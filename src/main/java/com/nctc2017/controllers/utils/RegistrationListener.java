package com.nctc2017.controllers.utils;

import com.nctc2017.bean.Player;
import com.nctc2017.services.AuthRegService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<RegistrationListener.OnRegistrationCompleteEvent> {

    @Autowired
    private AuthRegService authRegService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        Player user = event.getPlayer();
        String token = authRegService.createVerificationToken(user);

        String address = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm?token=" + token;
        String message = "Dear " + user.getLogin() + "! \nYou registered successfully! Please confirm your account with the link below.";


        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(address);
        email.setFrom("registration_confirm@shipsbattle.com");
        email.setSubject(subject);
        email.setText(message + " \n" + confirmationUrl);

        mailSender.send(email);
    }

    public static class OnRegistrationCompleteEvent extends ApplicationEvent {
        private String appUrl;
        private Player player;

        public OnRegistrationCompleteEvent(Player player, String appUrl) {
            super(player);
            this.player = player;
            this.appUrl = appUrl;
        }

        public Player getPlayer() {
            return player;
        }

        public String getAppUrl() {
            return appUrl;
        }
    }
}



