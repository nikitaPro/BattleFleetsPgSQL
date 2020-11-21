package com.nctc2017.dao.impl;


import com.nctc2017.bean.Goods;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.GoodsDao;
import com.nctc2017.dao.StockDao;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
public class StockDaoImplIntegrationTest {

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    StockDao stockDao;

    @Test
    public void createStockTest(){
        BigInteger playerId = new BigInteger("44");

        stockDao.deleteStock(playerId);
        BigInteger resultStockId = stockDao.createStock(playerId);

        assertNotNull(resultStockId);
        assertFalse(resultStockId.toString().equals("56"));
    }

    @Test
    @Ignore
    public void findStockTest(){
        BigInteger playerId = new BigInteger("44");
        BigInteger expectedStockId = new BigInteger("56");

        BigInteger actualStockId = stockDao.findStockId(playerId);

        assertEquals(expectedStockId, actualStockId);
    }

    @Test
    public void addCargoTest(){
        BigInteger playerId = new BigInteger("44");
        stockDao.deleteStock(playerId);
        BigInteger stockId = stockDao.createStock(playerId);
        BigInteger cargoId = goodsDao.createNewGoods(DatabaseObject.WOOD_TEMPLATE_ID, 100, 80);

        stockDao.addCargo(cargoId, playerId);
        List<Goods> actualGoodsAtStock = goodsDao.getAllGoodsFromStock(stockId);
        Goods actualGoods = actualGoodsAtStock.get(0);

        assertEquals(1, actualGoodsAtStock.size());
        assertEquals(cargoId, actualGoods.getThingId());
        assertEquals("Wood", actualGoods.getName());
        assertEquals(1, actualGoods.getRarity());
        assertEquals(100, actualGoods.getQuantity());
        assertEquals(80, actualGoods.getPurchasePrice());

    }
}
