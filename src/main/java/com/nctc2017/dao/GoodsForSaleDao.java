package com.nctc2017.dao;

import com.nctc2017.bean.GoodsForBuying;

import java.math.BigInteger;
import java.util.List;

public interface GoodsForSaleDao {

    List<GoodsForBuying> findAllByTypeId(BigInteger templateTypeId, GoodsForBuying.GoodsType type);

    List<GoodsForBuying> findAll();
}
