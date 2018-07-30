package ge.boxwood.espace.ErrorStalker;

import ge.boxwood.espace.ErrorStalker.Model.StepLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StepLoggerService {
    void logStep(String entryPoint, String method, HashMap params);
    List<StepLogger> getAll();
}
