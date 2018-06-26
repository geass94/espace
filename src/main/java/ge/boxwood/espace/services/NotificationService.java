package ge.boxwood.espace.services;

import ge.boxwood.espace.models.Notification;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.NotificationType;

import java.util.Set;

public interface NotificationService {
    Notification create(Notification notification);
    void delete(Long id);
    Notification getOne(long id);
    Notification getOneByCodeAndUser(String code,User user);
    Set<Notification> getAll();
    Notification update(Notification notification, Long id);
    Notification getByCodeAndNotUsedAndType(String code, NotificationType notificationType);
    void resend(User user);
    void recovery(User user);
    void setTempPassword(User user);
}