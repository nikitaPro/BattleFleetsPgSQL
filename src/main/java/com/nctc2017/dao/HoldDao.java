package com.nctc2017.dao;

import java.math.BigInteger;

public interface HoldDao {

    BigInteger findHold(BigInteger shipId);

    int getOccupiedVolume(BigInteger shipId);

    BigInteger createHold();

    void deleteHold(BigInteger holdId);

    void addCargo(BigInteger cargoId, BigInteger holdId);

    BigInteger createHold(BigInteger shipId);

}