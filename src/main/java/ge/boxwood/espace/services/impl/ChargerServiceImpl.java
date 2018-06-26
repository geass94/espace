package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.config.utils.ChargerRequestUtils;
import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.repositories.ChargerRepository;
import ge.boxwood.espace.repositories.OrderRepository;
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
import java.util.List;
import java.util.Set;

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
        return null;
    }

    @Override
    public Charger update(Charger charger, Long id) {
        Charger raw = chargerRepository.findOne(id);
        if (raw.getAddress() != charger.getAddress() && charger.getAddress() != null){
            raw.setAddress(charger.getAddress());
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
    public ChargerInfo start(Long cID) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        User user = userService.getByUsername(username);
        Order order = orderRepository.findByChargerAndUserAndConfirmed(chargerRepository.findByChargerId(cID), user, false);

        Charger charger = this.getOneByCID(cID);
        if(order == null && charger.getStatus() != 0){
            try {
                ChargerInfo chargerInfo = chargerRequestUtils.start(cID);
                chargerInfo.setCharger(charger);
                if(chargerInfo.getResponseCode() >= 200 && chargerInfo.getResponseCode() < 250){
                    Order newOrder = new Order(user);
                    newOrder.setPaymentType(PaymentType.CREDITCARD);
                    newOrder.setCharger(chargerInfo.getCharger());
                    newOrder.setChargerTransactionId(Integer.parseInt(chargerInfo.getChargerTransactionId()));
                    chargerInfo.setOrder(newOrder);
                    orderRepository.save(newOrder);
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
                ChargerInfo chargerInfo = chargerRequestUtils.stop(cID, order.getChargerTransactionId());
                chargerInfo.setCharger(charger);
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
    public Charger info(Long cid) {
        Charger charger = this.getOneByCID(cid);
        if(charger == null)
        {
            charger = new Charger();
        }
        try {
            JSONObject jsonObj = new JSONObject(chargerRequestUtils.info(cid));
            charger.setId(Long.valueOf((Integer) jsonObj.getJSONArray("chargers").getJSONObject(0).get("id")));
            charger.setLatitude((Double) jsonObj.getJSONArray("chargers").getJSONObject(0).get("latitude") );
            charger.setLongitude((Double) jsonObj.getJSONArray("chargers").getJSONObject(0).get("longitude"));
            charger.setStatus((Integer) jsonObj.getJSONArray("chargers").getJSONObject(0).get("status"));
            charger.setType((Integer) jsonObj.getJSONArray("chargers").getJSONObject(0).get("type"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charger;
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
