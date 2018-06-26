package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.config.providers.DeviceProvider;
import ge.boxwood.espace.config.providers.TimeProvider;
import ge.boxwood.espace.models.Notification;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.NotificationType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.NotificationRepository;
import ge.boxwood.espace.services.CommunicationService;
import ge.boxwood.espace.services.NotificationService;
import ge.boxwood.espace.services.UserService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.Set;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    TimeProvider timeProvider;
    @Autowired
    DeviceProvider deviceProvider;
    @Autowired
    private CommunicationService communicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Override
    public Notification create(Notification notification) {
        User user = notification.getUser();
        int preferedMethod = user.getActivationPreferedMethod();
        int addMinuteTime = 3;
        Date targetTime = timeProvider.now(); //now
        targetTime = DateUtils.addMinutes(targetTime, addMinuteTime);
        if(preferedMethod == 1){
            notification = new Notification();
            notification.setUser(user);
            notification.setStatus(Status.ACTIVE);
            notification.setNotificationType(NotificationType.SMS);
            notification.setActivationcode(this.generatePin(6));
            notification.setUsed(false);
            notification.setExpiresin(targetTime);
            communicationService.sendSms(user, "Activation code: " + notification.getActivationcode());
        }
        else{
            notification = new Notification();
            notification.setStatus(Status.ACTIVE);
            notification.setUser(user);
            notification.setNotificationType(NotificationType.MAIL);
            notification.setActivationcode(this.generatePin(4));
            notification.setUsed(false);
            notification.setExpiresin(targetTime);
            communicationService.sendEmail(user, "User activation", notification.getActivationcode());
        }
        notificationRepository.save(notification);
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Notification getOne(long id) {
        return notificationRepository.findOne(id);
    }

    @Override
    public Notification getOneByCodeAndUser(String code, User user) {
        return notificationRepository.findByActivationcodeAndUserAndExpiresinAfterAndUsedOrderByCreateDateDesc(code,user,new Date(),false);
    }

    @Override
    public Set<Notification> getAll() {
        return null;
    }

    @Override
    public Notification update(Notification notification, Long id) {
        notification.setId(id);
        notification = notificationRepository.save(notification);
        return  notification;
    }

    @Override
    public Notification getByCodeAndNotUsedAndType(String code, NotificationType notificationType) {
        return notificationRepository.findByActivationcodeAndUsedAndNotificationType(code, false, notificationType);
    }


    @Override
    public void resend(User user) {
        Notification notification = notificationRepository.findByUserAndUsed(user, false);
        if(notification != null){
            notification.setUsed(true);
            this.update(notification, notification.getId());
        }
        Notification notification1 = new Notification();
        notification1.setUser(user);
        notification1.setNotificationType(notification.getNotificationType());
        this.create(notification);
    }

    @Override
    public void recovery(User user) {
        Notification notif = notificationRepository.findByUserAndNotificationTypeAndUsed(user, NotificationType.RECOVERY, false);
        if(notif != null){
            notif.setUsed(true);
            notificationRepository.save(notif);
        }
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setUsed(false);
        notification.setStatus(Status.ACTIVE);
        notification.setNotificationType(NotificationType.RECOVERY);
        notification.setActivationcode(generatePin(5));
        user.setStatus(Status.LOCKED);
        userService.update(user, user.getId());
        notificationRepository.save(notification);
        if(user.getRecoveryPreferedMethod() == 1){
            communicationService.sendSms(user, notification.getActivationcode());
        }
        else{
            communicationService.sendEmail(user, "Password reset", notification.getActivationcode());
        }
    }

    @Override
    public void setTempPassword(User user) {
        String tempPassword = this.randomString(4);
        customUserDetailsService.setTempPassword(user.getUsername(), tempPassword);
        communicationService.sendSms(user, tempPassword);
    }

    private String generatePin(int digits){
        return String.valueOf(digits < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, digits - 1)) - 1)
                + (int) Math.pow(10, digits - 1));
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
