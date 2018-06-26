package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Place;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.PlaceRepository;
import ge.boxwood.espace.services.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class PlaceServiceImpl implements PlaceService {
    @Autowired
    private PlaceRepository placeRespository;
    @Autowired
    private EntityManager em;

    @Override
    public Place getOne(Long id) {
        return placeRespository.findOne(id);
    }

    @Override
    public List<Place> getAll() {
        return placeRespository.findAll();
    }

    @Override
    public Place create(Place place) {
        place.setStatus(Status.ACTIVE);
        return placeRespository.save(place);
    }

    @Override
    public Place update(Place place, Long id) {
        place.setId(id);
        return placeRespository.save(place);
    }

    @Override
    public List<Place> getClosestPlaces(double lat, double lng) {
        Query q = em.createNativeQuery("SELECT pl.* , 111.045 * DEGREES(ACOS(COS(RADIANS(:lat))\n" +
                " * COS(RADIANS(latitude))\n" +
                " * COS(RADIANS(longitude) - RADIANS(:lng))\n" +
                " + SIN(RADIANS(:lat))\n" +
                " * SIN(RADIANS(latitude))))\n" +
                " AS distance_in_km\n" +
                "FROM espace.places AS pl WHERE pl.latitude IS NOT NULL AND pl.longitude IS NOT NULL \n" +
                "ORDER BY distance_in_km ASC\n" +
                "LIMIT 0,10", Place.class);

        q.setParameter("lat", lat);
        q.setParameter("lng", lng);
        return q.getResultList();
    }
}
