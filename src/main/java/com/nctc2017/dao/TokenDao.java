package com.nctc2017.dao;

import com.nctc2017.bean.VerificationToken;

import java.math.BigInteger;

public interface TokenDao {

    void createToken(String token, long expireDate, BigInteger playerId);

    long getTokenExpireDate(String token);

    VerificationToken getToken(String token);
}
