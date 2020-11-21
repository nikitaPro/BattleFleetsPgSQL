package com.nctc2017.bean;

import java.math.BigInteger;

public class City {
	public static final String NAME = "CityName";
	protected String cityName;
    protected Market market;
    protected BigInteger cityId;
	public City(String cityName, Market market, BigInteger cityId) {
		this.cityName = cityName;
		this.market = market;
		this.cityId=cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Market getMarket() {
		return market;
	}
	public void setMarket(Market market) {
		this.market = market;
	}

	public BigInteger getCityId() {
		return cityId;
	}

	public void setCityId(BigInteger cityId) {
		this.cityId = cityId;
	}
}