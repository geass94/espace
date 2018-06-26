package ge.boxwood.espace.services;

public interface MailService {
    void sendMail(String from, String to, String subject, String body);
}
