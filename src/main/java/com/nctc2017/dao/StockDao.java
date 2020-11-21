package com.nctc2017.dao;

import com.nctc2017.bean.GoodsForSale;

import java.math.BigInteger;
import java.util.Map;

public interface StockDao {

    BigInteger findStockId(BigInteger playerId);

    BigInteger createStock(BigInteger playerId);

    void deleteStock(BigInteger playerId);

    void addCargo(BigInteger cargoId, BigInteger playerId);

    boolean isSuchCargoInStock(BigInteger cargoId, BigInteger cargoTemplateId, BigInteger stockId);

    int getOccupiedVolume(BigInteger playerId);

    Map<BigInteger, GoodsForSale> getAllPlayersGoodsForSale(BigInteger playerId);
}