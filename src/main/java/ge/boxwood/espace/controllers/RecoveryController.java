package ge.boxwood.espace.controllers;

import ge.boxwood.espace.ErrorStalker.Model.StepLogger;
import ge.boxwood.espace.ErrorStalker.StepLoggerService;
import ge.boxwood.espace.models.Notification;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.UserTokenState;
import ge.boxwood.espace.models.enums.NotificationType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.NotificationService;
import ge.boxwood.espace.services.UserService;
import ge.boxwood.espace.services.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recovery")
public class RecoveryController {
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenHelper tokenHelper;
    @Autowired
    private StepLoggerService stepLoggerService;

    @PostMapping("/start-recovery")
    public ResponseEntity<?> getUserByEmail(@RequestBody Map<String, String> phone){
        User user = userService.getByPhone(phone.get("phoneNumber"));
        notificationService.setTempPassword(user);
        HashMap responseData = new HashMap();
        responseData.put("username", user.getUsername());
        stepLoggerService.logStep("RecoveryController", "getUserByEmail", responseData);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping
    public ResponseEntity<?> sendRecoveryCode(@RequestBody HashMap<String, String> requestData){
        String phoneNumber = requestData.get("phoneNumber");
        String email = requestData.get("email");
        String username = requestData.get("username");
        int recoveryPreferedMethod = Integer.parseInt(requestData.get("recoveryPreferedMethod"));
        User user = userService.getByUsername(username);
        user.setRecoveryPreferedMethod(recoveryPreferedMethod);
        stepLoggerService.logStep("RecoveryController", "sendRecoveryCode", requestData);
        if(user.getEmail().equals(email) && user.getPhoneNumber().equals(phoneNumber)){
            notificationService.recovery(user);
            return ResponseEntity.ok(true);
        }
        throw new RuntimeException("Bad credentials");
    }

    @PostMapping("/init-password-reset")
    public ResponseEntity<?> resetPassword(@RequestBody String code, Device device){
        Notification notification = notificationService.getByCodeAndNotUsedAndType(code, NotificationType.RECOVERY);
        if(notification != null){
            notification.setUsed(true);
            notificationService.update(notification, notification.getId());
            // Perform the security
            final Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            notification.getUser(),
                            null,
                            notification.getUser().getAuthorities()
                    );

            // Inject into security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // token creation
            User user1 = (User)authentication.getPrincipal();
            String jws = tokenHelper.generateToken(user1, device);
            String jwsr = tokenHelper.generateRefreshToken(user1, device);

            int expiresIn = tokenHelper.getExpiredIn(device);
            // Return the token
            return ResponseEntity.ok(new UserTokenState(jws, jwsr, expiresIn, user1.getSmsActive() || user1.getEmailActive()));
        }else{
            throw new RuntimeException("Bad code or already used");
        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody HashMap<String, String> requestData, HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        String newPassword = requestData.get("newPassword");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus(Status.ACTIVE);
        user = userService.update(user, user.getId());
        stepLoggerService.logStep("RecoveryController", "changePassword", requestData);
        return ResponseEntity.accepted().body(user);
    }
}