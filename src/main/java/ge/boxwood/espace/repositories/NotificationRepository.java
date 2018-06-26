package ge.boxwood.espace.repositories;

import ge.boxwood.espace.models.Notification;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Notification findByActivationcodeAndUserAndExpiresinAfterAndUsedOrderByCreateDateDesc(String activationcode, User user, Date expiresin, boolean used);
    Notification findByUserAndUsed(User user, boolean used);
    Notification findByActivationcodeAndUsedAndNotificationType(String code, Boolean used, NotificationType notificationType);
    Notification findByUserAndNotificationTypeAndUsed(User user, NotificationType notificationType, Boolean used);
}