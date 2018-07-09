package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Counter;

import java.util.List;

public interface CounterService {
    List<Counter> getAllCounterByTrId(String trid);
}
