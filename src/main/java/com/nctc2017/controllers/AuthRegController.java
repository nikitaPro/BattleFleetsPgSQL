package com.nctc2017.controllers;

import com.nctc2017.bean.Player;
import com.nctc2017.bean.VerificationToken;
import com.nctc2017.controllers.utils.RegistrationListener;
import com.nctc2017.exception.PlayerValidationException;
import com.nctc2017.services.AuthRegService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.Date;

@Controller
public class AuthRegController {

    private static Logger log = Logger.getLogger(AuthRegController.class);

    @Autowired
    private AuthRegService authRegService;

    @Autowired
    private RegistrationListener listener;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    @ResponseBody
    public String registration(@RequestParam(value = "username_reg") String login,
                               @RequestParam(value = "email") String email,
                               @RequestParam(value = "password_reg") String password,
                               @RequestParam(value = "password_confirm") String passwordConfirm,
                               final HttpServletRequest request) {
        try {
            Player player = authRegService.registration(login, password, passwordConfirm, email);
            String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            listener.onApplicationEvent(new RegistrationListener.OnRegistrationCompleteEvent
                    (player, appUrl));

        } catch (PlayerValidationException e) {
            log.error(e.getMessage());
            return "<div class = \"errorText\" >" + e.getMessage() + "</div>";
        } catch (MailException e) {
            log.error("MailException while sending confirmation email" + e.getMessage());
            return "<div class = \"errorText\" >Oops, sorry! Something go wrong while sending your confirmation email! Please, try again later!</div>";
        }
        return "<div class = \"successText\" >" + "Congratulations! " +
                "Your registration is almost complete! \n " +
                "Please, check your email for confirmation link!" + "</div>";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView protectedPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username or password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("RegAuthView");

        return model;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration
            (WebRequest request, @RequestParam("token") String token) {

        ModelAndView model = new ModelAndView();
        model.setViewName("RegAuthView");
        VerificationToken verificationToken;

        try {
            verificationToken = authRegService.getVerificationToken(token);
        } catch (IllegalArgumentException e) {
            model.addObject("error", "Your token is invalid!");
            return model;
        }

        BigInteger userId = verificationToken.getUserId();
        long currentDate = new Date().getTime();
        if (verificationToken.getExpireDate() - currentDate <= 0) {
            model.addObject("error", "Your confirmation link is expired!\nPlease, sign up again.");
            return model;
        }

        authRegService.confirmRegistration(userId);
        model.addObject("msg", "Congratulations!\nYour registration is complete!");
        return model;
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView appError() {
        ModelAndView view = new ModelAndView("error");
        return view;
    }

}