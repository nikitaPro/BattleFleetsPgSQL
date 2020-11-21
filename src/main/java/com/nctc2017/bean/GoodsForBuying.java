package com.nctc2017.bean;

import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigInteger;

public class GoodsForBuying {

    protected final BigInteger templateId;

    protected final String name;

    protected final String goodsDescription;

    protected final GoodsType type;

    @JsonView(View.No.class)
    protected int goodsRarity;

    protected volatile int buyingPrice;

    protected volatile int quantity;

    @JsonView(View.Sell.class)
    protected volatile int salePrice;

    @JsonView(View.No.class)
    public enum GoodsType{
        GOODS, AMMO, MAST, CANNON
    }

    public GoodsForBuying(BigInteger templateId, String name, String goodsDescription, GoodsType type) {
        this.templateId = templateId;
        this.name = name;
        this.goodsDescription = goodsDescription;
        this.type = type;
    }

    public GoodsForBuying(GoodsForBuying origin){
        this(origin.templateId, origin.name, origin.goodsDescription, origin.type);
        goodsRarity = origin.getGoodsRarity();
        buyingPrice = origin.getBuyingPrice();
        salePrice = origin.getSalePrice();
        quantity = origin.getQuantity();
    }

    public String getName() {
        return name;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getGoodsRarity() {
        return goodsRarity;
    }

    public void setGoodsRarity(int goodsRarity) {
        this.goodsRarity = goodsRarity;
    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public GoodsType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GoodsForBuying{" +
                "templateId=" + templateId +
                ", name='" + name + '\'' +
                ", goodsDescription='" + goodsDescription + '\'' +
                ", type=" + type +
                ", goodsRarity=" + goodsRarity +
                ", buyingPrice=" + buyingPrice +
                ", quantity=" + quantity +
                ", salePrice=" + salePrice +
                '}';
    }
}