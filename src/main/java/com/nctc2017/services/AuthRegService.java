package com.nctc2017.services;

import java.math.BigInteger;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.nctc2017.bean.Player;
import com.nctc2017.bean.VerificationToken;
import com.nctc2017.exception.PlayerValidationException;

public interface AuthRegService extends UserDetailsService {

    Player registration(String login, String password, String passwordConfirm, String email)
            throws PlayerValidationException;

    void confirmRegistration(BigInteger playerId);

    String createVerificationToken(Player player);

    String exit(String login);

    UserDetails loadUserByUsername(String login) throws UsernameNotFoundException;

    VerificationToken getVerificationToken(String token);

}