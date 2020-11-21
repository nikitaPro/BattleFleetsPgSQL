package com.nctc2017.bean;

import java.math.BigInteger;

public class VerificationToken {
    private BigInteger userId;
    private long expireDate;
    private String token;

    public VerificationToken(BigInteger userId, long expireDate, String token) {
        this.userId = userId;
        this.expireDate = expireDate;
        this.token = token;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public String getToken() {
        return token;
    }
}
