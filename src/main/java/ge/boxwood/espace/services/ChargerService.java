package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Charger;
import ge.boxwood.espace.models.ChargerInfoDTO;

import java.util.List;

public interface ChargerService {
    Charger create(Charger charger);
    void importChargers(List<Charger> chargers);
    Charger update(Charger charger, Long id);
    void delete(Long id);
    Charger getOne(Long id);
    Charger getOneByCID(Long id);
    List<Charger> getAll();
    List<Charger> getClosestChargers(Double latitude, Double longitude);
    List<Charger> getAllChargersByPlace(Long id);
    List<Charger> getAllChargersByClosestPlaces(List<Place> places);

    Long freeChargers();

    ChargerInfoDTO start(Long cID, Long conID, Long cardID);
    ChargerInfoDTO stop(Long cID);
    Charger info(Long cid);
    ChargerInfoDTO transaction(Long trid);
}
