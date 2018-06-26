package ge.boxwood.espace.services.impl;

import ge.boxwood.espace.models.User;
import ge.boxwood.espace.services.CommunicationService;
import ge.boxwood.espace.services.MailService;
import ge.boxwood.espace.services.smsservice.GeoSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunicationServiceImpl implements CommunicationService {
    @Autowired
    private MailService mailService;
    @Autowired
    private GeoSmsService geoSmsService;

    @Override
    public void sendSms(User user, String message) {
        geoSmsService.sendSingle(user.getPhoneNumber(), message);
    }

    @Override
    public void sendEmail(User user, String subject, String message) {
        mailService.sendMail("oplaapp2018@gmail.com", user.getEmail(), subject, message);
    }
}
