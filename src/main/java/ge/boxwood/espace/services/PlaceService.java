package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Place;

import java.util.List;

public interface PlaceService {
    Place getOne(Long id);
    List<Place> getAll();
    Place create(Place place);
    Place update(Place place, Long id);
    List<Place> getClosestPlaces(double lat, double lng);
}
