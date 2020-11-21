package com.nctc2017.bean;

import java.math.BigInteger;

public class ShipTemplate {

    protected BigInteger templateId;
    protected String t_name;
    protected int maxHealth;
    protected int maxSailorsQuantity;
    protected int cost;
    protected int maxMastsQuantity;
    protected int maxCannonQuantity;
    protected int maxCarryingLimit;

    public static final String T_SHIPNAME = "ShipName";
    public static final String T_MAX_HEALTH = "HealthLimit";
    public static final String T_MAX_SAILORS_QUANTITY = "SailorLimit";
    public static final String T_MAX_COST = "ShipCost";
    public static final String MAX_MASTS_QUANTITY = "MastLimit";
    public static final String MAX_CANNON_QUANTITY = "CannonLimit";
    public static final String MAX_CARRYING_LIMIT = "CarryingLimit";

    public ShipTemplate(BigInteger templateId, String t_name, int maxHealth, int maxSailorsQuantity, int cost,
            int maxMastsQuantity, int maxCannonQuantity, int maxCarryingLimit) {
        this.templateId = templateId;
        this.t_name = t_name;
        this.maxHealth = maxHealth;
        this.maxSailorsQuantity = maxSailorsQuantity;
        this.cost = cost;
        this.maxMastsQuantity = maxMastsQuantity;
        this.maxCannonQuantity = maxCannonQuantity;
        this.maxCarryingLimit = maxCarryingLimit;

    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public String getTName() {
        return t_name;
    }

    public void setTName(String tName) {
        this.t_name = tName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxSailorsQuantity() {
        return maxSailorsQuantity;
    }

    public void setMaxSailorsQuantity(int maxSailorsQuantity) {
        this.maxSailorsQuantity = maxSailorsQuantity;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getMaxMastsQuantity() {
        return maxMastsQuantity;
    }

    public void setMaxMastsQuantity(int maxMastsQuantity) {
        this.maxMastsQuantity = maxMastsQuantity;
    }

    public int getMaxCannonQuantity() {
        return maxCannonQuantity;
    }

    public void setMaxCannonQuantity(int maxCannonQuantity) {
        this.maxCannonQuantity = maxCannonQuantity;
    }

    public int getMaxCarryingLimit() {
        return maxCarryingLimit;
    }

    public void setMaxCarryingLimit(int maxCarryingLimit) {
        this.maxCarryingLimit = maxCarryingLimit;
    }

}