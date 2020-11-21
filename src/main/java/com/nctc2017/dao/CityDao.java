package com.nctc2017.dao;

import java.math.BigInteger;
import java.util.*;

import com.nctc2017.bean.City;

public interface CityDao {

    City find(BigInteger cityId);

    List<City> findAll();

}