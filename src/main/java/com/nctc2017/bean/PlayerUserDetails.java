package com.nctc2017.bean;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.util.Collection;

public class PlayerUserDetails extends User {
    private BigInteger playerId;

    public PlayerUserDetails(BigInteger playerId,
                             String username,
                             String password,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.playerId = playerId;
    }

    public BigInteger getPlayerId() {
        return playerId;
    }
}