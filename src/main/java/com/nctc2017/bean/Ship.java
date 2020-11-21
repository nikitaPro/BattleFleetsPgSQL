package com.nctc2017.bean;

import java.math.BigInteger;

public class Ship extends ShipTemplate {

	protected BigInteger shipId;
	protected String curName;
	protected int curHealth;
	protected int curSailorsQuantity;
	protected int curCarryingLimit;
        protected int curDamage;
        protected int curSpeed;

	public static final String NAME = "CurShipName";
	public static final String CUR_HEALTH = "CurShipHealth";
	public static final String CUR_SAILORS_QUANTITY = "CurShipSailors";
    
	public Ship(ShipTemplate shipT, BigInteger shipId, String cur_name, 
	            int curHealth, int curSailorsQuantity, int curCarryingLimit) {
	    this(shipT, shipId, cur_name, curHealth, curSailorsQuantity, curCarryingLimit, 0, 0);
	}
	
        public Ship(ShipTemplate shipT, BigInteger shipId, String cur_name, 
            int curHealth, int curSailorsQuantity, int curCarryingLimit, 
            int curDamage, int curSpeed) {
		super(shipT.templateId,shipT.t_name, shipT.maxHealth, shipT.maxSailorsQuantity, shipT.cost, shipT.maxMastsQuantity,
				shipT.maxCannonQuantity, shipT.maxCarryingLimit);
		this.shipId = shipId;
		this.curName = cur_name;
		this.curHealth = curHealth;
		this.curSailorsQuantity = curSailorsQuantity;
		this.curCarryingLimit = curCarryingLimit;
		this.curDamage = curDamage;
		this.curSpeed = curSpeed;
	}

	public Ship(ShipTemplate shipT, BigInteger shipId, String cur_name) {
		this(shipT, shipId, cur_name, shipT.maxHealth, shipT.maxSailorsQuantity, shipT.maxCarryingLimit, 0, 0);
	}

	public BigInteger getShipId() {
		return shipId;
	}

	public void setShipId(BigInteger shipId) {
		this.shipId = shipId;
	}

	public String getCurName() {
		return curName;
	}

	public void setCurName(String curName) {
		this.curName = curName;
	}

	public int getCurHealth() {
		return curHealth;
	}

	public void setCurHealth(int curHealth) {
		this.curHealth = curHealth;
	}

	public int getCurSailorsQuantity() {
		return curSailorsQuantity;
	}

	public void setCurSailorsQuantity(int curSailorsQuantity) {
		this.curSailorsQuantity = curSailorsQuantity;
	}

	public int getCurCarryingLimit() {
		return curCarryingLimit;
	}

	public void setCurCarryingLimit(int curCarryingLimit) {
		this.curCarryingLimit = curCarryingLimit;
	}

    public int getCurDamage() {
        return curDamage;
    }

    public void setCurDamage(int curDamage) {
        this.curDamage = curDamage;
    }

    public int getCurSpeed() {
        return curSpeed;
    }

    public void setCurSpeed(int curSpeed) {
        this.curSpeed = curSpeed;
    }

}