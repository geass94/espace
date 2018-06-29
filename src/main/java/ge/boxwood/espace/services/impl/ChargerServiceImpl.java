package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.config.utils.ChargerRequestUtils;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.repositories.*;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.PlaceService;
import ge.boxwood.espace.services.UserService;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

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
    private PlaceService placeService;
    @Autowired
    private ConnectorRepository connectorRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Override
    public Charger create(Charger charger) {
        if (charger.getPlace() != null){
            Place place = placeService.create(charger.getPlace());
            charger.setPlace(place);
        }
        Charger charger1 = chargerRepository.save(charger);
        if (charger1 != null && charger.getPlace() != null){
            Place place1 = charger1.getPlace();
            place1.getChargers().add(charger1);
            placeService.update(place1, place1.getId());
        }
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
        if (raw.getPlace() != null && raw.getPlace() != charger.getPlace() && charger.getPlace() != null){
            raw.setPlace(charger.getPlace());
        }
        if(raw.getPlace() == null && charger.getPlace() != null){
            Place place = placeService.create(charger.getPlace());
            raw.setPlace(place);
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
    public ChargerInfo start(Long cID, Long conID, Long cardID) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        User user = userService.getByUsername(username);
        Order order = orderRepository.findByChargerAndUserAndConfirmed(chargerRepository.findByChargerId(cID), user, false);

        Charger charger = this.getOneByCID(cID);
        if(order == null && charger.getStatus() != 0){
            try {
                ChargerInfo chargerInfo = new ChargerInfo();
                JSONObject chargerStart = chargerRequestUtils.start(cID, conID);
                chargerInfo.setChargerTransactionId(chargerStart.get("data") != null && !chargerStart.get("data").equals(null) ? chargerStart.get("data").toString() : "0");
                JSONObject transactionInfo = chargerRequestUtils.transaction(Long.valueOf(chargerInfo.getChargerTransactionId()));
                JSONObject transaction = transactionInfo.getJSONObject("data");
                chargerInfo.setCharger(charger);
                chargerInfo.setTransStart(transaction.get("transStart") != null && !transaction.get("transStart").equals(null) ? (long) transaction.get("transStart") : 0L);
                chargerInfo.setTransStop(transaction.get("transStop") != null && !transaction.get("transStop").equals(null) ? (long)transaction.get("transStop") : 0L);
                chargerInfo.setMeterStart(transaction.get("meterStart") != null && !transaction.get("meterStart").equals(null) ? (long)(int)transaction.get("meterStart") : 0L);
                chargerInfo.setMeterStop(transaction.get("meterStop") != null && !transaction.get("meterStop").equals(null) ? (long)(int)transaction.get("meterStop") : 0L);
                chargerInfo.setChargingPower(transaction.get("kiloWattHour") != null && !transaction.get("kiloWattHour").equals(null) ? Double.valueOf(transaction.get("kiloWattHour").toString()) : 0d);
                chargerInfo.setChargeTime(transaction.get("chargingTime") != null && !transaction.get("chargingTime").equals(null) ? (long)(int)transaction.get("chargingTime") : 0L);
                chargerInfo.setResponseCode((Integer) chargerStart.get("responseCode"));

                if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 250){
                    Order newOrder = new Order(user);
                    newOrder.setPaymentType(PaymentType.CREDITCARD);
                    newOrder.setCharger(chargerInfo.getCharger());
                    newOrder.setChargerTransactionId(Long.valueOf(chargerInfo.getChargerTransactionId()));
                    chargerInfo.setOrder(newOrder);
                    newOrder = orderRepository.save(newOrder);
                    Payment payment = new Payment(0f, newOrder);
                    if(cardID != null && cardID > 0){
                        CreditCard creditCard = creditCardRepository.findOne(cardID);
                        payment.setCreditCard(creditCard);
                    }

                    paymentRepository.save(payment);
                    order.setPayments(Collections.singletonList(payment));
                    orderRepository.flush();
                    return chargerInfo;
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
    public ChargerInfo stop(Long cID) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        User user = userService.getByUsername(username);
        Charger charger = this.getOneByCID(cID);
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
                    return chargerInfo;
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
    public ChargerInfo info(Long cid) {
        ChargerInfo chargerInfo = new ChargerInfo();
        try {
            JSONObject info = new JSONObject(chargerRequestUtils.info(cid));
            JSONObject chrgInfo = info.getJSONObject("data");
            Long chargerId = Long.valueOf(chrgInfo.get("id").toString());
            Charger charger = chargerRepository.findByChargerId(chargerId);
            charger.setStatus( Integer.valueOf(chrgInfo.get("status").toString()) );
            charger.setCode( chrgInfo.get("code").toString() );
            chargerRepository.save(charger);
            chargerInfo.setCharger(charger);
            return chargerInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChargerInfo transaction(Long trid) {
        try {
            JSONObject transactionInfo = chargerRequestUtils.transaction(trid);
            ChargerInfo chargerInfo = new ChargerInfo();
            chargerInfo.setResponseCode((Integer) transactionInfo.get("responseCode"));
            if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 300){
                JSONObject transaction = transactionInfo.getJSONObject("data");
                Charger charger = this.getOneByCID(Long.valueOf(transaction.get("id").toString()));
                Order order = orderRepository.findByChargerAndChargerTransactionId(charger, trid);
                chargerInfo.setCharger(charger);
                chargerInfo.setOrder(order);
                chargerInfo.setTransStart(transaction.get("transStart") != null && !transaction.get("transStart").equals(null) ? (long) transaction.get("transStart") : 0L);
                chargerInfo.setTransStop(transaction.get("transStop") != null && !transaction.get("transStop").equals(null) ? (long)transaction.get("transStop") : 0L);
                chargerInfo.setMeterStart(transaction.get("meterStart") != null && !transaction.get("meterStart").equals(null) ? (long)(int)transaction.get("meterStart") : 0L);
                chargerInfo.setMeterStop(transaction.get("meterStop") != null && !transaction.get("meterStop").equals(null) ? (long)(int)transaction.get("meterStop") : 0L);
                chargerInfo.setChargingPower(transaction.get("kiloWattHour") != null && !transaction.get("kiloWattHour").equals(null) ? Double.valueOf(transaction.get("kiloWattHour").toString()) : 0d);
                chargerInfo.setChargeTime(transaction.get("chargingTime") != null && !transaction.get("chargingTime").equals(null) ? (long)(int)transaction.get("chargingTime") : 0L);
                chargerInfo.setChargerTransactionId(String.valueOf(trid));
                return chargerInfo;
            }else
            {
                throw new RuntimeException("Something wrong with charger");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Charger> getAllChargersByPlace(Long id) {
        return chargerRepository.findAllByPlace( placeService.getOne(id) );
    }

    @Override
    public List<Charger> getAllChargersByClosestPlaces(List<Place> places) {
        return chargerRepository.findAllByPlaceIn(places);
    }

    @Override
    public Long freeChargers() {
        return chargerRepository.countAllByStatus(-1);
    }
}
