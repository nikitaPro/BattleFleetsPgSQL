package com.nctc2017.dao;

import java.math.BigInteger;
import java.util.List;

import com.nctc2017.bean.Mast;

public interface MastDao {

    Mast findMast(BigInteger mastId);

    BigInteger createNewMast(BigInteger mastTemplateId, BigInteger containerOwnerID);

    void deleteMast(BigInteger mastId);

    boolean updateCurMastSpeed(BigInteger mastId, int newMastSpeed);

    List<Mast> getShipMastsFromShip(BigInteger shipId);

    List<Mast> getShipMastsFromStock(BigInteger stockId);

    List<Mast> getShipMastsFromHold(BigInteger holdId);

    int getTotalCurrentQuantity(BigInteger shipId);

    int getCurMastSpeed(BigInteger mastId);

    String getMastName(BigInteger mastId);

    int getSailyards(int mastId);

    int getMaxSpeed(BigInteger mastId);

    int getMastCost(BigInteger mastId);

}