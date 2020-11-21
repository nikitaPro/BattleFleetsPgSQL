package com.nctc2017.bean;

import java.math.BigInteger;

public class Ammo extends AbstractThing {
    public static final String COST = "AmmoCost";
    public static final String NAME = "AmmoName";
    public static final String NUM = "AmmoNum";
    public static final String TYPE = "DamageType";
    protected String name;
    protected String damageType;
    protected int cost;

    public Ammo(BigInteger thingId, String name, String damageType, int quantity, int cost) {
        super(quantity, thingId);
        this.name = name;
        this.damageType = damageType;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }
    
    

}