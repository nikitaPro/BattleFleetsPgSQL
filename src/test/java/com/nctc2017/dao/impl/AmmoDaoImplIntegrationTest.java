package com.nctc2017.dao.impl;


import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.nctc2017.bean.Ammo;
import com.nctc2017.bean.Player;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.AmmoDao;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.StockDao;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
public class AmmoDaoImplIntegrationTest {
    
    @Autowired
    private AmmoDao ammoDao;
    
    @Autowired
    private HoldDao holdDao;
    
    @Autowired
    private StockDao stockDao;
    
    @Autowired
    private PlayerDao playerDao;
    
    @Test
    @Rollback(true)
    public void testCannonballCreateThenFindOk() {
        // Given
        int quantity = 13;
        // When
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID, quantity);
        // Then
        Ammo ammo = ammoDao.findById(ammoId);
        assertEquals(ammo.getQuantity(), quantity);
        assertEquals(ammo.getThingId(), ammoId);
        assertTrue(ammo.getDamageType().toLowerCase().indexOf("hull") != -1);
    }
    
    @Test
    @Rollback(true)
    public void testChainCreateThenFindOk() {
        // Given
        int quantity = 23;
        // When
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.CHAIN_TEMPLATE_OBJECT_ID, quantity);
        // Then
        Ammo ammo = ammoDao.findById(ammoId);
        assertEquals(ammo.getQuantity(), quantity);
        assertEquals(ammo.getThingId(), ammoId);
        assertTrue(ammo.getDamageType().toLowerCase().indexOf("mast") != -1);
    }
    
    @Test
    @Rollback(true)
    public void testBuckshotCreateThenFindOk() {
        // Given
        int quantity = 19;
        // When
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, quantity);
        // Then
        Ammo ammo = ammoDao.findById(ammoId);
        assertEquals(ammo.getQuantity(), quantity);
        assertEquals(ammo.getThingId(), ammoId);
        assertTrue(ammo.getDamageType().toLowerCase().indexOf("crew") != -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testFindFailWhenWrongId() {
        // Given
        BigInteger ammoId = BigInteger.ONE;
        // When
        ammoDao.findById(ammoId);
        // Then Exception
    }
    
    @Test
    @Rollback(true)
    public void testGetAmmoQuantitySuccess() {
        // Given
        int quantity = 17;
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, quantity);
        // When
        int quantityRes = ammoDao.getAmmoQuantity(ammoId);
        assertEquals(quantityRes, quantity);
        // Then Exception
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testGetAmmoQuantityFailWhenWrongId() {
        // Given
        BigInteger ammoId = BigInteger.ONE;
        // When
        ammoDao.getAmmoQuantity(ammoId);
        // Then Exception
    }
    
    @Test
    @Rollback(true)
    public void testIncreaseAmmoQuantitySuccess() {
        // When
        int quantity = 41;
        int increase = 9;
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, quantity);
        // Then
        boolean res = ammoDao.increaseAmmoQuantity(ammoId, increase);
        int quantityRes = ammoDao.getAmmoQuantity(ammoId);
        // Then
        assertTrue(res);
        assertEquals(quantity + increase, quantityRes);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testIncreaseAmmoQuantityFailWhenWrongId() {
        int increase = 9;
        // When
        BigInteger ammoId =BigInteger.ONE;
        // Then
        ammoDao.increaseAmmoQuantity(ammoId, increase);
        // Then Exception
    }
    
    @Test
    @Rollback(true)
    public void testDecreaseAmmoQuantitySuccess() {
        // When
        int quantity = 41;
        int decrease = 9;
        BigInteger ammoId = ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, quantity);
        // Then
        boolean res = ammoDao.decreaseAmmoQuantity(ammoId, decrease);
        int quantityRes = ammoDao.getAmmoQuantity(ammoId);
        // Then
        assertTrue(res);
        assertEquals(quantity - decrease, quantityRes);
    }
    
    @Test
    @Rollback(true)
    public void testGetAllAmmoFromHoldSuccess() {
        //Given
        int quantityC = 17;
        int quantityB = 28;
        BigInteger idHold = holdDao.createHold();
        BigInteger buckshotTemplateId = DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID;
        BigInteger cannonballTemplateId = DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID;
        BigInteger idB = ammoDao.createAmmo(buckshotTemplateId, quantityB);
        BigInteger idC = ammoDao.createAmmo(cannonballTemplateId, quantityC);
        holdDao.addCargo(idB, idHold);
        holdDao.addCargo(idC, idHold);
        // When
        List<Ammo> list = ammoDao.getAllAmmoFromHold(idHold);
        Ammo ammo1 = list.get(0);
        Ammo ammo2 = list.get(1);
        // Then
        assertNotEquals(ammo1, ammo2);
        assertTrue(ammo1.getThingId().equals(idB) || ammo1.getThingId().equals(idC));
        assertTrue(ammo2.getThingId().equals(idB) || ammo2.getThingId().equals(idC));
    }
    
    @Rollback(true)
    @Test(expected = IllegalArgumentException.class)
    public void testGetAmmoFromHoldWithStockId() {
        //Given
        int quantityB = 28;
        int quantityC = 27;
        
        String login = "qwe";
        String pass = "1111";
        String email = "qwe@qwe.qwe";
        
        BigInteger buckshotTemplateId = DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID;
        BigInteger idB = ammoDao.createAmmo(buckshotTemplateId, quantityB);
        BigInteger cannonballTemplateId = DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID;
        BigInteger idC = ammoDao.createAmmo(cannonballTemplateId, quantityC);
        
        playerDao.addNewPlayer(login, pass, email);
        Player player = playerDao.findPlayerByLogin(login);
        
        BigInteger holdId = holdDao.createHold();
        BigInteger stockId = stockDao.createStock(player.getPlayerId());
        // When
        holdDao.addCargo(idC, holdId);
        stockDao.addCargo(idB, stockId);
        ammoDao.getAllAmmoFromHold(stockId);
        // Then Exception
    }
}
