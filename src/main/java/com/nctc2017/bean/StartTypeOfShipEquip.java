package com.nctc2017.bean;

import java.math.BigInteger;

public class StartTypeOfShipEquip {

    protected BigInteger shipTempId;
    protected String typeMastName;
    protected String typeCannonName;

    public static final String START_CANNON_TYPE = "StartCannonType";
    public static final String START_MAST_TYPE = "StartMastType";

    public StartTypeOfShipEquip(BigInteger shipTempId,String typeMastName, String typeCannonName) {
        this.shipTempId = shipTempId;
        this.typeMastName = typeMastName;
        this.typeCannonName = typeCannonName;
    }

    public String getTypeMastName() {
        return typeMastName;
    }

    public String getTypeCannonName() {
        return typeCannonName;
    }

    public void setTypeCannonName(String typeCannonName) {
        this.typeCannonName = typeCannonName;
    }

    public void setTypeMastName(String typeMastName) {
        this.typeMastName = typeMastName;
    }

    public BigInteger getShipTempId() {
        return shipTempId;
    }
}
