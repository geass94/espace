package ge.boxwood.espace.services;

import ge.boxwood.espace.models.User;

public interface CommunicationService {
    void sendSms(User user, String message);

    void sendEmail(User user, String subject, String message);
}
