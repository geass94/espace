package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.config.utils.ChargerRequestUtils;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.*;
import ge.boxwood.espace.services.*;
import ge.boxwood.espace.services.gc.GCPaymentService;
import org.json.JSONArray;
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
    private static int finisher = 0;
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
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CounterRepository counterRepository;
    @Autowired
    private GCPaymentService gcPaymentService;
    @Autowired
    private StepLoggerService stepLoggerService;
    @Autowired
    private SettingsService settingsService;

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
            if (charger.getCategoryId() != null){
                Category cat = categoryRepository.findOne(charger.getCategoryId());
                chrg.setCategory(cat);
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
        Charger charger = chargerRepository.findOne(id);
        if(charger != null){

            return charger;
        }else{
            HashMap params = new HashMap();
            params.put("id", id);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]getOne", params);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }

    @Override
    public Charger getOneByCID(Long id) {
        Charger charger = chargerRepository.findByChargerId(id);
        if(charger != null){
            stepLoggerService.logStep("ChargerService", "getOneByCID", charger.getHashMap());
            return charger;
        }else{
            HashMap params = new HashMap();
            params.put("chargerId", id);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]getOneByCID", params);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }

    @Override
    public Charger getOneByCode(String code) {
        Charger charger = chargerRepository.findByCode(code);
        if(charger != null){
            charger = this.info(charger.getChargerId());
            stepLoggerService.logStep("ChargerService", "getOneByCode", charger.getHashMap());
            return charger;
        }
        else{
            HashMap params = new HashMap();
            params.put("code", code);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]getOneByCode", params);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }

    @Override
    public List<Charger> getAll() {
        return chargerRepository.findAll();
    }

    @Override
    public List<Charger> getClosestChargers(Double latitude, Double longitude) {
        HashMap params = new HashMap();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        stepLoggerService.logStep("ChargerService", "getClosestChargers", params);
        this.refreshChargers();
        Query q = em.createNativeQuery("SELECT ch.* , 111.045 * DEGREES(ACOS(COS(RADIANS(:lat))\n" +
                " * COS(RADIANS(latitude))\n" +
                " * COS(RADIANS(longitude) - RADIANS(:lng))\n" +
                " + SIN(RADIANS(:lat))\n" +
                " * SIN(RADIANS(latitude))))\n" +
                " AS distance_in_km\n" +
                "FROM espace.chargers AS ch WHERE ch.latitude IS NOT NULL AND ch.longitude IS NOT NULL \n" +
                "ORDER BY distance_in_km ASC\n" +
                "LIMIT 0,20", Charger.class);

        q.setParameter("lat", latitude);
        q.setParameter("lng", longitude);
        return q.getResultList();
    }

    @Override
    public void refreshChargers() {
        try {
            JSONObject obj = chargerRequestUtils.all();
            JSONObject data = obj.getJSONObject("data");
            JSONArray chargers = data.getJSONArray("chargers");
            for (int i = 0; i < chargers.length(); i++) {
                JSONObject jsonCharger = chargers.getJSONObject(i);
                JSONArray jsonChargerConnectors = jsonCharger.getJSONArray("connectors");
                Charger charger = chargerRepository.findByChargerId(Long.valueOf(jsonCharger.get("id").toString()));
                if(charger == null) {
                    charger = new Charger();
                }
                    List<Connector> connectors = new ArrayList<>();
                    charger.setChargerId( Long.valueOf( jsonCharger.get("id").toString() ) );
                    charger.setLatitude( Double.valueOf( jsonCharger.get("latitude").toString() ) );
                    charger.setLongitude( Double.valueOf( jsonCharger.get("longitude").toString() ) );
                    charger.setStatus( Integer.valueOf( jsonCharger.get("status").toString() ) );
                    charger.setType( Integer.valueOf( jsonCharger.get("type").toString() ) );
                    charger.setCode( jsonCharger.get("code").toString() );
                    charger.setDescription( jsonCharger.get("description").toString() );
                    charger.setCategory( categoryRepository.findOne(Long.valueOf( jsonCharger.get("category").toString() ) ) );
                    charger = chargerRepository.save(charger);
                    for(int x = 0; x < jsonChargerConnectors.length(); x++){
                        JSONObject connector =  jsonChargerConnectors.getJSONObject(x);
                        Connector conn = connectorRepository.findByChargerAndConnectorId(charger, Long.valueOf( connector.get("id").toString() ) );
                        if(conn == null){
                            conn = new Connector();
                        }
                        conn.setConnectorId( Long.valueOf( connector.get("id").toString() ) );
                        conn.setType( connector.get("type").toString() );
                        conn.setCharger(charger);
                        conn = connectorRepository.save(conn);
                        connectors.add(conn);
                    }
                    charger.setConnectors(connectors);
                    chargerRepository.flush();
                    connectorRepository.flush();
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap preStart(Long cID, Long conID, Long cardID, Float targetPrice) {
        Charger charger = this.info(cID);
        HashMap params = charger.getHashMap();
        params.put("chargerId", cID);
        params.put("connectorID", conID);
        params.put("cardID", cardID);
        params.put("targetPrice", targetPrice);
        stepLoggerService.logStep("ChargerService", "preStart", params);
        if(charger != null){
            Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
            String username = currentUser.getName();
            User user = userService.getByUsername(username);
            if(charger.getStatus() != 0 && charger != null){
                throw new RuntimeException("CHARGER_OFFLINE_BUSY");
            }
            else{
                Order order = new Order(user);
                order.setCharger(charger);
                order.setTargetPrice(targetPrice);
                order.setMustPay(Float.valueOf(settingsService.getByKey("defaultPriceForCharging").getValue().toString()));
                order.setPaymentType(PaymentType.CREDITCARD);
                order = orderRepository.save(order);

                Payment payment = new Payment(order.getMustPay(), order);

                if(cardID != null && cardID > 0){
                    CreditCard creditCard = creditCardRepository.findOne(cardID);
                    payment.setCreditCard(creditCard);
                }
                payment.setOrder(order);
                payment = paymentRepository.save(payment);
                orderRepository.flush();
                paymentRepository.flush();
                HashMap ret = new HashMap();
                ret.put("paymentUUID", payment.getUuid());
                ret.put("chargerId", order.getCharger().getChargerId());
                ret.put("connectorId", conID);
                return ret;
            }
        }else{
            HashMap params1 = new HashMap();
            params.put("chargerId", cID);
            params.put("connectorID", conID);
            params.put("cardID", cardID);
            params.put("targetPrice", targetPrice);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]preStart", params1);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }

    // როდესაც ეშვება start მეთოდი პირველ რიგში მოწმდება არსებობს თუ არა დაუსრულებელი ორდერი ამ ჩარჯერეზე.
    //    შემდეგ ეშვება info მეთოდი და ახლდება ჩარჯერის ინფორმაცია და ინახება ჩემთან ბაზაში.
    //    იქმნება ორდერი, ფეიმენტი და ინახება როგორც დაუსულებელი გადახდა.
    @Override
    public ChargerInfoDTO start(Long cID, Long conID, String paymentUUID) {
        Charger charger = this.info(cID);
        HashMap params = charger.getHashMap();
        params.put("chargerId", cID);
        params.put("connectorId", conID);
        params.put("paymentUUID", paymentUUID);
        stepLoggerService.logStep("ChargerService", "start", params);
        if (charger != null){
            Payment payment = paymentRepository.findByUuid(paymentUUID);
            Order order = payment.getOrder();

            this.finisher = 0;
//        თუ ჩარჯერი თავისუფალია,
//        თუ შეკვეთა ძალაშია
//        თუ წინასწარ გადასახდელი თანხა გადახდილია
//        მაშინ დავიწყოთ დატენვა
            if(charger.getStatus() != 0){
                throw new RuntimeException("CHARGER_OFFLINE_BUSY");
            }

            if(order.getStatus() == Status.ORDERED && payment.isConfirmed()){
                try {
                    ChargerInfo chargerInfo = new ChargerInfo();
                    JSONObject chargerStart = chargerRequestUtils.start(cID, conID);
                    chargerInfo.setChargerTransactionId(chargerStart.get("data") != null && !chargerStart.get("data").equals(null) ? chargerStart.get("data").toString() : "0");
                    chargerInfo.setCharger(charger);
                    chargerInfo.setResponseCode((Integer) chargerStart.get("responseCode"));
                    chargerInfo.setOrder(order);
                    if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 250){
                        order.setChargerTransactionId(Long.valueOf(chargerInfo.getChargerTransactionId()));
                        order = orderRepository.save(order);
                        Payment payment1 = new Payment(order.getMustPay(), order);
                        payment1.setCreditCard(payment.getCreditCard());
                        paymentRepository.save(payment1);
                        orderRepository.flush();
                        charger.setStatus(1);
                        charger = chargerRepository.save(charger);
                        chargerRepository.flush();
                        Counter counter = new Counter();
                        counter.setChargerId(charger.getChargerId());
                        counter.setChargePower(0d);
                        counter.setChargerTrId(chargerInfo.getChargerTransactionId());
                        counter.setCurrentPrice(0f);
                        counter.setChargeTime(0L);
                        counter.setConsumedPower(0L);
                        counter.setLastUpdate(Calendar.getInstance().getTimeInMillis());
                        counter.setStartTime(Calendar.getInstance().getTimeInMillis());
                        counter.setPricing(0f);
                        counterRepository.save(counter);
                        ChargerInfoDTO dto = this.transaction(Long.valueOf(chargerInfo.getChargerTransactionId()));
                        dto.setChargerStatus(charger.getStatus());
                        return dto;
                    }else
                    {
                        throw new RuntimeException("CHARGER_CONNECTION_ERROR");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                throw new RuntimeException("PAYMENT_ERROR");
            }
        }else{
            HashMap params1 = new HashMap();
            params.put("chargerId", cID);
            params.put("connectorID", conID);
            params.put("paymentUUID", paymentUUID);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]start", params1);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }


    @Override
    public HashMap stop(Long cID) {
        Charger charger = this.info(cID);
        HashMap params = charger.getHashMap();
        params.put("chargerId", cID);
        stepLoggerService.logStep("ChargerService", "stop", params);
        if (charger != null){
            Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
            String username = currentUser.getName();
            User user = userService.getByUsername(username);
            Order order = orderRepository.findByUserAndChargerAndStatus(user, charger, Status.ORDERED);
            HashMap ret = new HashMap();
            ret.put("chargerId", cID);
            if(order != null){
                try {
                    JSONObject stopInfo = chargerRequestUtils.stop(cID, order.getChargerTransactionId());
                    ChargerInfo chargerInfo = new ChargerInfo();
                    chargerInfo.setResponseCode((Integer) stopInfo.get("responseCode"));

                    Payment successfulPayment = paymentRepository.findByOrderAndConfirmed(order, true);
                    Payment pendingPayment = paymentRepository.findByOrderAndConfirmed(order, false);

                    System.out.println("PENDING UUID: "+pendingPayment.getUuid());

                    if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 300){

                        System.out.println("succeesPayment: "+successfulPayment.getPrice());
                        System.out.println("pendingPayemtn: "+pendingPayment.getPrice());

                        if(pendingPayment.getPrice() - successfulPayment.getPrice() > 0){
                            System.out.println("PAY MORE BIATCH");
                            pendingPayment.setPrice( pendingPayment.getPrice() - successfulPayment.getPrice() );
                            paymentRepository.save(pendingPayment);
                            ret.put("CODE", 2);
                            ret.put("DESC", "OVERDOSE");
                            ret.put("MESSAGE", "You spent more thanyou specified at the begining. Please pay!");
                            ret.put("paymentUUID", pendingPayment.getUuid());
                        }
                        else if ( pendingPayment.getPrice() - successfulPayment.getPrice() == 0 ){
                            System.out.println("ALL GOOD");
                            pendingPayment.setPrice( pendingPayment.getPrice() - successfulPayment.getPrice() );
                            pendingPayment.confirm();
                            paymentRepository.save(pendingPayment);
                            order.confirm();
                            order.setStatus(Status.PAID);
                            order.setPrice(successfulPayment.getPrice());
                            orderRepository.save(order);
                            ret.put("CODE", 1);
                            ret.put("DESC", "SUCCESSFUL_PAYMENT");
                            ret.put("MESSAGE", "All payments went through successfully. You are good to go!");
                        }
                        else if( pendingPayment.getPrice() - successfulPayment.getPrice() < 0){
                            System.out.println("REFUNDING");
                            pendingPayment.setPrice( successfulPayment.getPrice() - pendingPayment.getPrice() );
                            pendingPayment.setTrxId(successfulPayment.getTrxId());
                            pendingPayment.setPrrn(successfulPayment.getPrrn());
                            pendingPayment = paymentRepository.save(pendingPayment);
                            String code = gcPaymentService.makeRefund(pendingPayment.getUuid(), pendingPayment.getPrice(), pendingPayment.getTrxId(), pendingPayment.getPrrn());
                            if(code.equals("1")){
                                ret.put("CODE", 1);
                                ret.put("DESC", "SUCCESSFULLY_REFUNDED");
                                ret.put("MESSAGE", "Refund was successful. You are goodto go!");
                            }else{
                                ret.put("CODE", 0);
                                ret.put("DESC", "REFUND_FAILED");
                                ret.put("MESSAGE", "Something went wrong while refuning. Saving to pending refunds!");
                            }
                        }
                        else{
                            ret.put("CODE", -1);
                            ret.put("DESC", "UNKNOWN_STATEMENT");
                            ret.put("MESSAGE", "UNKNOWN_ERROR");
                        }
                        stepLoggerService.logStep("ChargerService", "stop", ret);
                        return ret;
                    }else
                    {
                        throw new RuntimeException("CHARGER_CONNECTION_ERROR");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                throw new RuntimeException("PAYMENT_ERROR");
            }
        }else{
            HashMap params1 = new HashMap();
            params.put("chargerId", cID);
            stepLoggerService.logStep("ChargerService", "[EXCEPTION]stop", params1);
            throw new RuntimeException("CHARGER_NOT_FOUND");
        }
    }

// როდესაც ეშვება info მეთოდი, მოწმდება ჩარჯერთან მისი ახლანდელი ინფორმაცია და ჩემთან ბაზაში ახლდება ჩანაწერი იმ კონკრეტული ჩარჯერის ID-ზე.
    @Override
    public Charger info(Long cid) {
        HashMap params = new HashMap();
        params.put("chargerId", cid);
        stepLoggerService.logStep("ChargerService", "info", params);
        try {
            JSONObject info = chargerRequestUtils.info(cid);
            JSONObject chrgInfo = info.getJSONObject("data");
            Long chargerId = Long.valueOf(chrgInfo.get("id").toString());
            Charger charger = chargerRepository.findByChargerId(chargerId);
            charger.setStatus( Integer.valueOf(chrgInfo.get("status").toString()) );
            charger.setCode( chrgInfo.get("code").toString() );
            try {
                JSONArray jArray = chrgInfo.getJSONArray("connectors");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jb = jArray.getJSONObject(i);
                    String type = jb.getString("type");
                    Long connectorId = jb.getLong("id");
                    Connector connector = connectorRepository.findByChargerAndConnectorId(charger, connectorId);
                    connector.setType(type);
                    connectorRepository.save(connector);
                }

            } catch (Exception e) {
                throw new RuntimeException("UNKNOWN_ERROR");
            }
            chargerRepository.flush();
            return charger;
        } catch (Exception e) {
            throw new RuntimeException("CHARGER_CONNECTION_ERROR");
        }
    }

//    ეს მეთოდი ამომწმებს ჩარჯერის სტატუს, მის მეტერებს და ინფორმაციას დატენვაზე.
//    თუ არსებობს uuidEnd სტრიქონი ეს იმას ნიშნავს რომ ჩარჯერი გამოაერთეს მანქანიდან და დატენვის პროცესი დასრულდა.
//    ამ შემთხვევაში ეშვება ჩემთამ stop მეთოდი და ბრუნდება ფეიმენტის ID რაზეც უკვე ხორციელდება გადახდა საბანკო ბარათით
    @Override
    public ChargerInfoDTO transaction(Long trid) {
        HashMap params = new HashMap();
        params.put("trId", trid);
        stepLoggerService.logStep("ChargerService", "transaction", params);
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();
        User user = userService.getByUsername(username);
        ChargerInfoDTO dto = new ChargerInfoDTO();
        try {
            JSONObject transactionInfo = chargerRequestUtils.transaction(trid);
            ChargerInfo chargerInfo = new ChargerInfo();
            chargerInfo.setResponseCode((Integer) transactionInfo.get("responseCode"));
            if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 300){
                JSONObject transaction = transactionInfo.getJSONObject("data");
                Order order = orderRepository.findByUserAndChargerTransactionIdAndStatus(user, Long.valueOf(transaction.get("id").toString()), Status.ORDERED);
                Payment payment = paymentRepository.findByOrderAndConfirmed(order, false);
                Charger charger = order.getCharger();
                chargerInfo.setCharger(charger != null ? charger : new Charger());
                chargerInfo.setOrder(order != null ? order : new Order());
                chargerInfo.setChargerTransactionId(String.valueOf(trid));

                parseChargerInfo(transaction, chargerInfo);

                dto.setChargerId(chargerInfo.getCharger().getChargerId());
                dto.setChargePower(chargerInfo.getChargingPower());
                dto.setChargeTime(chargerInfo.getChargeTime());
                dto.setChargerStatus(chargerInfo.getCharger().getStatus());
                dto.setChargerType(chargerInfo.getCharger().getType());
                dto.setChargerTrId(chargerInfo.getChargerTransactionId());
                dto.setConsumedPower(chargerInfo.getConsumedPower());

                List<Counter> counters = counterRepository.findAllByChargerIdAndChargerTrId(charger.getChargerId(), trid.toString());
                float price = this.calculatePrice(counters);
                Counter counter = new Counter();
                counter.setConsumedPower(dto.getConsumedPower());
                counter.setLastUpdate(Calendar.getInstance().getTimeInMillis());
                counter.setChargerTrId(trid.toString());
                counter.setChargePower(dto.getChargePower());
                counter.setChargerId(dto.getChargerId());
                counter.setChargeTime(dto.getChargeTime());
                counter.setPricing(pricingService.getPriceForChargingPower(dto.getChargePower()));
                counter.setCurrentPrice(price);
                counter = counterRepository.save(counter);
                counters.add(counter);
                price = this.calculatePrice(counters);
                dto.setCurrentPrice(counter.getCurrentPrice());
                dto.setConsumedPower(chargerInfo.getConsumedPower());
                dto.setChargingFinished(false);
                dto.setChargerPulledOut(false);
                order.setPrice(price);
                payment.setPrice(price);

                payment = paymentRepository.save(payment);
                dto.setPaymentUUID(payment.getUuid());
                if(!chargerInfo.getStopUUID().isEmpty() && msToMinutes(chargerInfo.getTransStop()) > 0){
                    dto.setChargerPulledOut(true);
                    dto.setChargingFinished(true);
                    this.finisher = 6;
                }
                if(order.getTargetPrice() - price <= 0 && order.getTargetPrice() > 0 && dto.getChargerPulledOut() == false){
                    dto.setChargingFinished(true);
                    this.finisher = 6;
                }

                this.finisher ++;
                return dto;
            }else
            {
                throw new RuntimeException("CHARGER_CONNECTION_ERROR");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Category> categories() {
        return categoryRepository.findAll();
    }

    @Override
    public ChargerInfoDTO finish(Long trId) {
        HashMap params = new HashMap();
        params.put("trId", trId);
        stepLoggerService.logStep("ChargerService", "finish", params);
        Order order = orderRepository.findByChargerTransactionId(trId);
        Charger charger = order.getCharger();
        List<Counter> counters = counterRepository.findAllByChargerIdAndChargerTrId(charger.getChargerId(), trId.toString());
        double chargePower = 0;
        for (Counter counter:counters) {
            chargePower+=counter.getChargePower();
        }
        Counter last = counters.get( counters.size() - 1 );
        chargePower = chargePower / counters.size();
        ChargerInfoDTO dto = new ChargerInfoDTO();
        dto.setChargePower(chargePower);
        dto.setChargeTime( Long.valueOf(msToMinutes( Long.valueOf( last.getChargeTime().toString() ) ).toString() ) );
        dto.setConsumedPower(last.getConsumedPower());
        dto.setChargerStatus(charger.getStatus());
        return dto;
    }

    @Override
    public Long freeChargers() {
        return chargerRepository.countAllByStatus(-1);
    }


    private Float calculatePrice(List<Counter> counterList){
        DecimalFormat df = new DecimalFormat("##.##");
        Float price = 0f;
        int last;
        int prev;
        if (counterList.size() - 1 <= 0){
            System.out.println("counterList IF");
            last = 0;
            prev = 0;
        }else{
            System.out.println("counterList ELSE");
            last = counterList.size() - 1;
            prev = last - 1;
        }

        Counter lastCounter = counterList.get(last);
        Counter prevCounter = counterList.get(prev);
        if ( !prevCounter.equals(null) && !lastCounter.equals(null) ){
            if(msToHours( lastCounter.getLastUpdate() - prevCounter.getLastUpdate()) > 0){
                price = prevCounter.getCurrentPrice() + (msToHours( lastCounter.getLastUpdate() - prevCounter.getLastUpdate()) * lastCounter.getPricing());
            }
        }

        if (msToMinutes( counterList.get( counterList.size() - 1 ).getLastUpdate() - counterList.get( 0 ).getLastUpdate()) <= 2){
            price = 0f;
        }

        String formattedPrice = df.format(price);
        float finalPrice = Float.valueOf(formattedPrice);
        HashMap params = new HashMap();
        params.put("calcualtedPrice", finalPrice);
        stepLoggerService.logStep("ChargerService", "calcualtePrice", params);
        return finalPrice;
    }

    private Float msToHours(Long ms){
        DecimalFormat df = new DecimalFormat("##.#####");
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        Float minutes = Float.valueOf(seconds.toString()) / 60;
        Float hours = minutes / 60;
        String formatted = df.format(Math.abs(hours));
        HashMap params = new HashMap();
        params.put("msToHours", formatted);
        stepLoggerService.logStep("ChargerService", "msToHours", params);
        return Float.valueOf(formatted);
    }

    private Float msToMinutes(Long ms){
        DecimalFormat df = new DecimalFormat("##.#####");
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        Float minutes = Float.valueOf(seconds.toString()) / 60;
        String formatted = df.format(Math.abs(minutes));
        HashMap params = new HashMap();
        params.put("msToMinutes", formatted);
        stepLoggerService.logStep("ChargerService", "msToMinutes", params);
        return Float.valueOf(formatted);
    }

    private ChargerInfo parseChargerInfo(JSONObject transaction, ChargerInfo chargerInfo){
        chargerInfo.setTransStart(transaction.get("transStart") != null && !transaction.get("transStart").equals(null) ? (long) transaction.get("transStart") : 0L);
        chargerInfo.setTransStop(transaction.get("transStop") != null && !transaction.get("transStop").equals(null) ? (long)transaction.get("transStop") : 0L);
        chargerInfo.setMeterStart(transaction.get("meterStart") != null && !transaction.get("meterStart").equals(null) ? (long)(int)transaction.get("meterStart") : 0L);
        chargerInfo.setMeterStop(transaction.get("meterStop") != null && !transaction.get("meterStop").equals(null) ? (long)(int)transaction.get("meterStop") : 0L);
        chargerInfo.setChargingPower(transaction.get("kiloWattHour") != null && !transaction.get("kiloWattHour").equals(null) ? Double.valueOf(transaction.get("kiloWattHour").toString()) : 0d);
        chargerInfo.setChargeTime(transaction.get("chargingTime") != null && !transaction.get("chargingTime").equals(null) ? (long)(int)transaction.get("chargingTime") : 0L);
        chargerInfo.setStartUUID(transaction.get("uuidStart") != null && !transaction.get("uuidStart").equals(null) ? transaction.get("uuidStart").toString() : "");
        chargerInfo.setStopUUID(transaction.get("uuidEnd") != null && !transaction.get("uuidEnd").equals(null) ? transaction.get("uuidEnd").toString() : "");
        chargerInfo.setConsumedPower(transaction.get("consumed") != null && !transaction.get("consumed").equals(null) ? Long.valueOf(transaction.get("consumed").toString()) : 0L);
        HashMap params = new HashMap();
        params.put("transStart", chargerInfo.getTransStart());
        params.put("transStop", chargerInfo.getTransStop());
        params.put("meterStart", chargerInfo.getMeterStart());
        params.put("meterStop", chargerInfo.getMeterStop());
        params.put("chargingPower", chargerInfo.getChargingPower());
        params.put("chargingTime", chargerInfo.getChargeTime());
        params.put("startUUID", chargerInfo.getStartUUID());
        params.put("stopUUID", chargerInfo.getStopUUID());
        params.put("consumedPower", chargerInfo.getConsumedPower());
        stepLoggerService.logStep("ChargerService", "parseChargerInfo", params);
        return chargerInfo;
    }
}
