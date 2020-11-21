package com.nctc2017.dao;

import java.math.BigInteger;
import java.util.List;

import com.nctc2017.bean.Ammo;

public interface AmmoDao {

    Ammo findById(BigInteger ammoId);

    String getAmmoName(BigInteger ammoId);

    String getAmmoDamageType(BigInteger ammoId);

    int getAmmoCost(BigInteger ammoId);

    int getAmmoQuantity(BigInteger ammoId);

    boolean increaseAmmoQuantity(BigInteger ammoId, int increaseNumber);

    boolean decreaseAmmoQuantity(BigInteger ammoId, int decreaseNumber);

    List<Ammo> getAllAmmoFromStock(BigInteger stockId);

    List<Ammo> getAllAmmoFromHold(BigInteger holdId);

    BigInteger createAmmo(BigInteger ammoTemplateId, int quantity);

    void deleteAmmo(BigInteger ammoId);

}