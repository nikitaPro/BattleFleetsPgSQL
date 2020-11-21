package com.nctc2017.bean;

import java.math.BigInteger;

public class Mast extends AbstractThing {
    public static final String MAST_NAME = "MastName";
    public static final String MAX_SPEED = "Speed";
    public static final String Cur_MAST_SPEED = "CurMastSpeed";
    public static final String MAST_COST = "MastCost";
    public static final int QUANTITY = 1;

    protected int maxSpeed;
    protected int curSpeed;
    protected int cost;
    protected String templateName;

    public Mast(int quantity, BigInteger thingId, String templateName, int maxSpeed, int curSpeed, int cost) {
        super(quantity, thingId);
        this.templateName = templateName;
        this.maxSpeed = maxSpeed;
        this.curSpeed = curSpeed;
        this.cost = cost;
    }

    public String getTemplateName() {
        return templateName;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getCurSpeed() {
        return curSpeed;
    }

    public void setCurSpeed(int curSpeed) {
        this.curSpeed = curSpeed;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}