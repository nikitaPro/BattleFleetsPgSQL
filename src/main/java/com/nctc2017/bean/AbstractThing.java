package com.nctc2017.bean;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

public abstract class AbstractThing implements Thing {

    protected BigInteger tamplateId;
    protected int quantity;
    protected BigInteger thingId;

    public AbstractThing(int quantity, BigInteger thingId) {
        super();
        this.quantity = quantity;
        this.thingId = thingId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public BigInteger getThingId() {
        return thingId;
    }

    @Override
    public void setThingId(BigInteger thingId) {
        this.thingId = thingId;
    }

    public BigInteger getTamplateId() {
        return tamplateId;
    }

    public void setTamplateId(BigInteger tamplateId) {
        this.tamplateId = tamplateId;
    }

}