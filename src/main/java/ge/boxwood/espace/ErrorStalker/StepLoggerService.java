package ge.boxwood.espace.ErrorStalker;

import java.util.HashMap;
import java.util.Map;

public interface StepLoggerService {
    void logStep(String entryPoint, String method, HashMap params);
}
