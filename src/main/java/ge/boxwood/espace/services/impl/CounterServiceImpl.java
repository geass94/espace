package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.Counter;
import ge.boxwood.espace.repositories.CounterRepository;
import ge.boxwood.espace.services.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterServiceImpl implements CounterService {
    @Autowired
    private CounterRepository counterRepository;
    @Override
    public List<Counter> getAllCounterByTrId(String trid) {
        return counterRepository.findAllByChargerTrId(trid);
    }
}
