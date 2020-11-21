package com.nctc2017.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.nctc2017.bean.Goods;

public interface GoodsDao {

    BigInteger createNewGoods(BigInteger goodsTemplateId, int quantity, int price);

    Goods findById(BigInteger goodsId);

    void increaseGoodsQuantity(BigInteger goodsId, int increaseValue);

    void decreaseGoodsQuantity(BigInteger goodsId, int decreaseValue);

    void updateGoodsQuantity(BigInteger goodsId, int quantity);

    void deleteGoods(BigInteger goodsId);

    int getGoodsRarity(BigInteger goodsTemplateId);

    int getGoodsQuantity(BigInteger goodsTemplateId);

    List<Goods> getAllGoodsFromStock(BigInteger stockId);

    List<Goods> getAllGoodsFromHold(BigInteger holdId);

}