package com.nctc2017.bean;

import java.math.BigInteger;

public class Cannon extends AbstractThing{
    public static final String NAME = "CanonName";
    public static final String DAMAGE = "Damage";
    public static final String DISTANCE = "Distance";
    public static final String COST = "CannonCost";
    protected String name;
    protected int damage;
    protected int distance;
    protected int cost;

    public Cannon(BigInteger id, String name, int damage, int distance, int cost) {
        super(1,id);
        this.name = name;
        this.damage = damage;
        this.distance = distance;
        this.cost = cost;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cannon id= ");
        builder.append(thingId);
        builder.append(", curName= ");
        builder.append(name);
        builder.append(", damage= ");
        builder.append(damage);
        builder.append(", distance= ");
        builder.append(distance);
        builder.append(", cost= ");
        builder.append(cost);
        return builder.toString();
    }
}
