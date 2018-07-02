package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.config.utils.ChargerRequestUtils;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.repositories.*;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.PricingService;
import ge.boxwood.espace.services.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ChargerServiceImpl implements ChargerService {
    @Autowired
    private ChargerRepository chargerRepository;
    @Autowired
    private ChargerRequestUtils chargerRequestUtils;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager em;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private PricingService pricingService;
    @Override
    public Charger create(Charger charger) {

        Charger charger1 = chargerRepository.save(charger);

        return charger1;
    }

    @Override
    public void importChargers(List<Charger> chargers) {
        for (Charger charger:chargers) {
            Charger chrg = chargerRepository.save(charger);
            List<Connector> connectorList = new ArrayList<>();
            if(charger.getConnectors() != null){
                for(Connector connector:charger.getConnectors()){
                    connector.setCharger(chrg);
                    Connector cntr = connectorRepository.save(connector);
                    connectorList.add(cntr);
                }
                chrg.setConnectors(connectorList);
            }
            chargerRepository.flush();
        }
    }

    @Override
    public Charger update(Charger charger, Long id) {
        Charger raw = chargerRepository.findOne(id);
        if (raw.getDescription() != charger.getDescription() && charger.getDescription() != null){
            raw.setDescription(charger.getDescription());
        }
        if (raw.getLatitude() != charger.getLatitude() && charger.getLatitude() != null){
            raw.setLatitude(charger.getLatitude());
        }
        if (raw.getLongitude() != charger.getLongitude() && charger.getLongitude() != null){
            raw.setLongitude(charger.getLongitude());
        }
        if (raw.getCode() != charger.getCode() && charger.getCode() != null){
            raw.setCode(charger.getCode());
        }
        return chargerRepository.save(raw);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Charger getOne(Long id) {
        return chargerRepository.findOne(id);
    }

    @Override
    public Charger getOneByCID(Long id) {
        return chargerRepository.findByChargerId(id);
    }

    @Override
    public List<Charger> getAll() {
        return chargerRepository.findAll();
    }

    @Override
    public List<Charger> getClosestChargers(Double latitude, Double longitude) {
        Query q = em.createNativeQuery("SELECT ch.* , 111.045 * DEGREES(ACOS(COS(RADIANS(:lat))\n" +
                " * COS(RADIANS(latitude))\n" +
                " * COS(RADIANS(longitude) - RADIANS(:lng))\n" +
                " + SIN(RADIANS(:lat))\n" +
                " * SIN(RADIANS(latitude))))\n" +
                " AS distance_in_km\n" +
                "FROM espace.chargers AS ch WHERE ch.latitude IS NOT NULL AND ch.longitude IS NOT NULL \n" +
                "ORDER BY distance_in_km ASC\n" +
                "LIMIT 0,10", Charger.class);

        q.setParameter("lat", latitude);
        q.setParameter("lng", longitude);
        return q.getResultList();
    }





    @Override
    public ChargerInfoDTO start(Long cID, Long conID, Long cardID) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        User user = userService.getByUsername(username);
        Order order = orderRepository.findByChargerAndUserAndConfirmed(chargerRepository.findByChargerId(cID), user, false);
        Charger charger = this.info(cID);

        if(order == null && charger.getStatus() == 0){
            try {
                ChargerInfo chargerInfo = new ChargerInfo();
                JSONObject chargerStart = chargerRequestUtils.start(cID, conID);
                chargerInfo.setChargerTransactionId(chargerStart.get("data") != null && !chargerStart.get("data").equals(null) ? chargerStart.get("data").toString() : "0");
                chargerInfo.setCharger(charger);
                chargerInfo.setResponseCode((Integer) chargerStart.get("responseCode"));

                if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 250){
                    Order newOrder = new Order(user);
                    newOrder.setPaymentType(PaymentType.CREDITCARD);
                    newOrder.setCharger(chargerInfo.getCharger());
                    newOrder.setChargerTransactionId(Long.valueOf(chargerInfo.getChargerTransactionId()));
                    newOrder = orderRepository.save(newOrder);
                    chargerInfo.setOrder(newOrder);
                    Payment payment = new Payment(0f, newOrder);
                    if(cardID != null && cardID > 0){
                        CreditCard creditCard = creditCardRepository.findOne(cardID);
                        payment.setCreditCard(creditCard);
                    }
                    payment = paymentRepository.save(payment);
                    newOrder.setPayments(Collections.singletonList(payment));
                    paymentRepository.flush();
                    orderRepository.flush();
                    ChargerInfoDTO dto = this.transaction(Long.valueOf(chargerInfo.getChargerTransactionId()));
                    charger.setStatus(1);
                    dto.setChargerStatus(charger.getStatus());
                    chargerRepository.save(charger);
                    return dto;
                }else
                {
                    throw new RuntimeException("Something wrong with charger");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new RuntimeException("Some order is pending confirmation or charger is offline/busy");
        }
    }


    @Override
    public ChargerInfoDTO stop(Long cID) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        User user = userService.getByUsername(username);
        Charger charger = this.info(cID);
        Order order = orderRepository.findByChargerAndUserAndConfirmed(charger, user, false);
        if(order != null){
            try {
                JSONObject stopInfo = chargerRequestUtils.stop(cID, order.getChargerTransactionId());
                ChargerInfo chargerInfo = new ChargerInfo();
                chargerInfo.setResponseCode((Integer) stopInfo.get("responseCode"));
                if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 300){
                    order.confirm();
                    chargerInfo.setOrder(order);
                    orderRepository.save(order);
                    ChargerInfoDTO dto = this.transaction(order.getChargerTransactionId());

                    return dto;
                }else
                {
                    throw new RuntimeException("Something wrong with charger");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new RuntimeException("Some order is pending confirmation");
        }
    }

    @Override
    public Charger info(Long cid) {
        try {
            JSONObject info = chargerRequestUtils.info(cid);
            JSONObject chrgInfo = info.getJSONObject("data");
            Long chargerId = Long.valueOf(chrgInfo.get("id").toString());
            Charger charger = chargerRepository.findByChargerId(chargerId);
            charger.setStatus( Integer.valueOf(chrgInfo.get("status").toString()) );
            charger.setCode( chrgInfo.get("code").toString() );
            chargerRepository.save(charger);
            return charger;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChargerInfoDTO transaction(Long trid) {
        ChargerInfoDTO dto = new ChargerInfoDTO();
        try {
            JSONObject transactionInfo = chargerRequestUtils.transaction(trid);
            ChargerInfo chargerInfo = new ChargerInfo();
            chargerInfo.setResponseCode((Integer) transactionInfo.get("responseCode"));
            if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 300){
                JSONObject transaction = transactionInfo.getJSONObject("data");
                Charger charger = this.getOneByCID(Long.valueOf(transaction.get("id").toString()));
                Order order = orderRepository.findByChargerAndChargerTransactionIdAndConfirmed(charger, trid, false);
                chargerInfo.setCharger(charger);
                chargerInfo.setOrder(order != null ? order : new Order());
                chargerInfo.setTransStart(transaction.get("transStart") != null && !transaction.get("transStart").equals(null) ? (long) transaction.get("transStart") : 0L);
                chargerInfo.setTransStop(transaction.get("transStop") != null && !transaction.get("transStop").equals(null) ? (long)transaction.get("transStop") : 0L);
                chargerInfo.setMeterStart(transaction.get("meterStart") != null && !transaction.get("meterStart").equals(null) ? (long)(int)transaction.get("meterStart") : 0L);
                chargerInfo.setMeterStop(transaction.get("meterStop") != null && !transaction.get("meterStop").equals(null) ? (long)(int)transaction.get("meterStop") : 0L);
                chargerInfo.setChargingPower(transaction.get("kiloWattHour") != null && !transaction.get("kiloWattHour").equals(null) ? Double.valueOf(transaction.get("kiloWattHour").toString()) : 0d);
                chargerInfo.setChargeTime(transaction.get("chargingTime") != null && !transaction.get("chargingTime").equals(null) ? (long)(int)transaction.get("chargingTime") : 0L);
                chargerInfo.setChargerTransactionId(String.valueOf(trid));
                chargerInfo.setStartUUID(transaction.get("uuidStart") != null && !transaction.get("uuidStart").equals(null) ? transaction.get("uuidStart").toString() : "");
                chargerInfo.setStopUUID(transaction.get("uuidEnd") != null && !transaction.get("uuidEnd").equals(null) ? transaction.get("uuidEnd").toString() : "");
                chargerInfo.setConsumedPower(transaction.get("consumed") != null && !transaction.get("consumed").equals(null) ? Long.valueOf(transaction.get("consumed").toString()) : 0L);

                dto.setChargerId(chargerInfo.getCharger().getChargerId());
                dto.setChargePower(chargerInfo.getChargingPower());
                dto.setChargeTime(chargerInfo.getChargeTime());
                dto.setChargerStatus(chargerInfo.getCharger().getStatus());
                dto.setChargerType(chargerInfo.getCharger().getType());
                dto.setOrderUUID(chargerInfo.getOrder().getUuid());
                dto.setPaymentUUID(chargerInfo.getOrder().getPayments().get(0).getUuid());
                dto.setChargerTrId(chargerInfo.getChargerTransactionId());
                dto.setConsumedPower(chargerInfo.getConsumedPower());
                Long seconds = TimeUnit.MILLISECONDS.toSeconds(dto.getChargeTime());
                Float p = (pricingService.getPriceForChargingPower(dto.getChargePower()) * (seconds / 60 / 60));
                DecimalFormat df = new DecimalFormat("#.##");
                float price = Float.parseFloat(df.format(p));
                System.out.println(price);
                dto.setCurrentPrice(price);
                dto.setConsumedPower(chargerInfo.getConsumedPower());
                order.setPrice(price);
                Payment payment = paymentRepository.findByOrderAndConfirmed(order, false);
                payment.setPrice(price);
                orderRepository.save(order);
                paymentRepository.save(payment);
                if(!chargerInfo.getStopUUID().isEmpty()){
                    dto.setChargingFinished(true);
                    chargerRequestUtils.stop(dto.getChargerId(), Long.valueOf(dto.getChargerTrId()));
                }
                else{
                    dto.setChargingFinished(false);
                }
                return dto;
            }else
            {
                throw new RuntimeException("Something wrong with charger");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long freeChargers() {
        return chargerRepository.countAllByStatus(-1);
    }
}
