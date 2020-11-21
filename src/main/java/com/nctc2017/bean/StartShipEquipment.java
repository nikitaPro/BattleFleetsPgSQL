package com.nctc2017.bean;

import java.math.BigInteger;

public class StartShipEquipment {

    protected BigInteger startCannonType;
    protected BigInteger startMastType;
    protected int startNumCannon;
    protected int startNumMast;
    protected BigInteger shipTId;

    public static final String START_CANNON_TYPE = "StartCannonType";
    public static final String START_MAST_TYPE = "StartMastType";
    public static final String START_NUM_CANNON = "StartNumCannon";
    public static final String START_NUM_MAST = "StartNumMast";

    public StartShipEquipment(BigInteger shipTId, BigInteger startCannonType, BigInteger startMastType,
                              int startNumCannon, int startNumMast) {
        this.shipTId = shipTId;
        this.startMastType = startMastType;
        this.startCannonType = startCannonType;
        this.startNumCannon = startNumCannon;
        this.startNumMast = startNumMast;
    }

    public BigInteger getShipTId() {
        return shipTId;
    }

    public BigInteger getStartCannonType() {
        return startCannonType;
    }

    public BigInteger getStartMastType() {
        return startMastType;
    }

    public int getStartNumCannon() {
        return startNumCannon;
    }

    public int getStartNumMast() {
        return startNumMast;
    }

    public void setStartCannonType(BigInteger startCannonType) {
        this.startCannonType = startCannonType;
    }

    public void setStartMastType(BigInteger startMastType) {
        this.startMastType = startMastType;
    }
}
