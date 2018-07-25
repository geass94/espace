package ge.boxwood.espace.ErrorStalker.Implmentation;

import ge.boxwood.espace.ErrorStalker.Model.StepLogger;
import ge.boxwood.espace.ErrorStalker.Repository.StepLoggerRepo;
import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@DependsOn(value = { "customUserDetailsService" })
public class StepLoggerServiceImpl implements StepLoggerService {
    @Autowired
    private StepLoggerRepo stepLoggerRepo;
    @Autowired
    private UserService userService;
    @Override
    public void logStep(String entryPoint, HashMap params) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null){
            String username = currentUser.getName();
            User user = userService.getByUsername(username);
            StepLogger stepLogger = new StepLogger();
            stepLogger.setEntryPoint(entryPoint);
            stepLogger.setParams(params);
            stepLogger.setUser(user);
            stepLogger.setTimestamp(new Date());
            stepLoggerRepo.save(stepLogger);
            System.out.println("=====STEP LOGGER BEGIN=====");
            System.out.println("EntryPoint: "+entryPoint);
            params.forEach((k,v)->{
                System.out.println(k + " : " + v);
            });
            System.out.println("=====STEP LOGGER END=====");
            System.out.println("--------------------------------");
        }
    }
}
