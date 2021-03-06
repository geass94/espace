package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Category;
import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.ChargerInfoDTO;
import ge.boxwood.espace.models.Order;

import java.util.HashMap;
import java.util.List;

public interface ChargerService {
    Charger create(Charger charger);
    void importChargers(List<Charger> chargers);
    Charger update(Charger charger, Long id);
    void delete(Long id);
    Charger getOne(Long id);
    Charger getOneByCID(Long id);
    Charger getOneByCode(String code);
    List<Charger> getAll();
    List<Charger> getClosestChargers(Double latitude, Double longitude);
    void refreshChargers();
    Long freeChargers();
    HashMap preStart(Long cID, Long conID, Long cardID, Float targetPrice);
    ChargerInfoDTO start(Long cID, Long conID, String paymentUUID);
    HashMap stop(Long cID);
    Charger info(Long cid);
    ChargerInfoDTO transaction(Long trid);
    List<Category> categories();
    ChargerInfoDTO finish(Long trId);
}
