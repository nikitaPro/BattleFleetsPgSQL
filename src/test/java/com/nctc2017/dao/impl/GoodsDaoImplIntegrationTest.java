package com.nctc2017.dao.impl;

import com.nctc2017.bean.Goods;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.GoodsDao;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
public class GoodsDaoImplIntegrationTest {

    @Autowired
    GoodsDao goodsDao;

    @Test
    public void findGoodsById(){
        BigInteger goodsId = goodsDao.createNewGoods(DatabaseObject.WOOD_TEMPLATE_ID,40,19);
        Goods expectedGoods = new Goods(goodsId, "Wood", 40  ,  19, 1);

        Goods resultGoods = goodsDao.findById(goodsId);

        assertEquals(expectedGoods.getQuantity(), resultGoods.getQuantity());
        assertEquals(expectedGoods.getPurchasePrice(), resultGoods.getPurchasePrice());
        assertEquals(expectedGoods.getName(), resultGoods.getName());
        assertEquals(expectedGoods.getRarity(), resultGoods.getRarity());
    }

    @Test
    public void createNewGoodsTest() {
        String expectedGoodName = "Gems";
        int expectedQuantity = 100;
        int expectedPrice = 600;

        BigInteger newObjectId = goodsDao.createNewGoods(DatabaseObject.GEMS_TEMPLATE_ID, expectedQuantity, expectedPrice);

        Goods resultGoods = goodsDao.findById(newObjectId);

        assertEquals(expectedGoodName, resultGoods.getName());
        assertEquals(expectedPrice, resultGoods.getPurchasePrice());
        assertEquals(expectedQuantity, resultGoods.getQuantity());
    }

    @Test (expected =  IllegalArgumentException.class)
    public void deleteGoods(){
        BigInteger objectId = new BigInteger("59");

        goodsDao.deleteGoods(objectId);

        Goods resultGoods = goodsDao.findById(objectId);
    }

    @Test
    public void increaseGoodsQuantityTest(){
        BigInteger goodsId = goodsDao.createNewGoods(DatabaseObject.TEA_TEMPLATE_ID,40,19);
        int increaseValue = 50;
        int expectedQuantity = 90;

        goodsDao.increaseGoodsQuantity(goodsId, increaseValue);

        int actualQuantity = goodsDao.getGoodsQuantity(goodsId);

        assertEquals(expectedQuantity, actualQuantity);
    }

    @Test
    public void decreaseGoodsQuantityTest(){
        BigInteger goodsId = goodsDao.createNewGoods(DatabaseObject.TEA_TEMPLATE_ID,200,19);
        int decreaseValue = 100;
        int expectedQuantity = 100;

        goodsDao.decreaseGoodsQuantity(goodsId, decreaseValue);

        int actualQuantity = goodsDao.getGoodsQuantity(goodsId);

        assertEquals(expectedQuantity, actualQuantity);
    }



}
