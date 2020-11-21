package com.nctc2017.bean;

import java.math.BigInteger;

public class Goods extends AbstractThing {
    public static final String NAME = "GoodsName";
    public static final String QUANTITY = "GoodsNum";
    public static final String PRICE = "PurchasePrice";
    public static final String RARITY = "RarityCoef";

    protected String name;
    protected int rarity;
    protected int purchasePrice;

    public Goods(BigInteger thingId, String name, int quantity, int purchasePrice, int rarity) {
        super(quantity, thingId);
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }
}