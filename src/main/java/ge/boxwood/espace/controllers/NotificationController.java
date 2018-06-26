package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.Notification;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.NotificationType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.NotificationService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {
    @Autowired
    UserService userService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    TokenHelper tokenHelper;

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, path = "/confirm-code", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> add(HttpServletRequest request, @RequestBody String code){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);

        Notification notification = notificationService.getOneByCodeAndUser(code, user);

        if (notification != null){
            NotificationType notificationType = notification.getNotificationType();
            if (notificationType == NotificationType.MAIL){
                user.setEmailActive(true);
                notification.setUsed(true);
            }
            if (notificationType == NotificationType.SMS){
                user.setSmsActive(true);
                notification.setUsed(true);
            }
            user.setStatus(Status.ACTIVE);
            userService.update(user, user.getId());
            notificationService.update(notification, notification.getId());
            return ResponseEntity.accepted().body(true);
        }
        return ResponseEntity.accepted().body(false);
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, path = "/resend-code", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> resendCode(HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        notificationService.resend(user);
        return ResponseEntity.ok(true);
    }
}
