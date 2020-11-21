package com.nctc2017.dao.impl;

import com.nctc2017.bean.Player;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.dao.StockDao;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
public class HoldDaoImplIntegrationTest {
    
    @Autowired
    private HoldDao holdDao;
    
    @Autowired
    private CannonDao cannonDao;
    
    @Autowired
    private StockDao stockDao;
    
    @Autowired
    private PlayerDao playerDao;
    
    @Autowired
    private ShipDao shipDao;
    
    
    @Test
    @Rollback(true)
    public void testCreateHold() {
        // When
        BigInteger id = holdDao.createHold();
        
        // Then
        assertNotNull(id);
        assertTrue(id.longValueExact() > 0L);
    }
    
    @Test
    @Rollback(true)
    public void testCreateHoldForShip() {
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, null);
        // When
        BigInteger id = holdDao.createHold(shipId);
        int volume = holdDao.getOccupiedVolume(shipId);
        // Then
        assertNotNull(id);
        assertTrue(id.longValueExact() > 0L);
        assertTrue(volume == 0);
    }
    
    @Test
    @Rollback(true)
    public void testDeleteHold() {
        // Given
        BigInteger id = holdDao.createHold();
        
        // When
        holdDao.deleteHold(id);
        // Then okay
    }
    
    @Test
    @Rollback(true)
    public void testDeleteHoldFail() {
        // Given
        BigInteger id = BigInteger.ONE;
        
        // When
        holdDao.deleteHold(id);
        // Then okay
    }
    
    @Test
    @Rollback(true)
    public void testAddCargo() {
        // Given
        BigInteger cargoId = cannonDao.createCannon(DatabaseObject.MORTAR_TEMPLATE_ID);
        BigInteger holdId = holdDao.createHold();
        
        // When
        holdDao.addCargo(cargoId, holdId);
        // Then
        // no exceptions;
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testAddCargoToInvalidHold() {
        // Given
        BigInteger cargoId = cannonDao.createCannon(DatabaseObject.MORTAR_TEMPLATE_ID);
        BigInteger holdId = BigInteger.ONE;
        
        // When
        holdDao.addCargo(cargoId, holdId);
        // Then 
        // exception;
    }
    
    @Test
    @Rollback(true)
    public void testGetOccupiedVolume() {
        // Given
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, null);
        BigInteger holdId = holdDao.createHold(shipId);
        BigInteger cargoId1 = cannonDao.createCannon(DatabaseObject.KULEVRIN_TEMPLATE_ID, null);
        BigInteger cargoId2 = cannonDao.createCannon(DatabaseObject.BOMBARD_TEMPLATE_ID, null);
        
        // When
        int emptyShipVolume = holdDao.getOccupiedVolume(shipId);
        holdDao.addCargo(cargoId1, holdId);
        holdDao.addCargo(cargoId2, holdId);
        int volume = holdDao.getOccupiedVolume(shipId);
        // Then 
        assertEquals(emptyShipVolume + 2, volume);
    }
    
    @Test
    @Rollback(true)
    public void testGetOccupiedVolumeAddTheSame() {
        // Given
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, null);
        BigInteger holdId = holdDao.createHold(shipId);
        //first adding
        BigInteger cargoId1 = cannonDao.createCannon(DatabaseObject.KULEVRIN_TEMPLATE_ID, holdId);
        BigInteger cargoId2 = cannonDao.createCannon(DatabaseObject.BOMBARD_TEMPLATE_ID, holdId);
        
        // When
        int strtShipVolume = holdDao.getOccupiedVolume(shipId);
        //second adding
        holdDao.addCargo(cargoId1, holdId);
        holdDao.addCargo(cargoId2, holdId);
        int volume = holdDao.getOccupiedVolume(shipId);
        // Then 
        assertEquals(strtShipVolume, volume);
    }
    
    @Test
    @Rollback(true)
    public void testGetOccupiedVolumeInvalidShipId() {
        // Given
        BigInteger invalidShipId = BigInteger.ONE;
        // When
        int volume = holdDao.getOccupiedVolume(invalidShipId);
        // Then ? 
        assertTrue(volume == 0);
    }
    
    @Test
    @Rollback(true)
    public void testFindHold() {
        // Given
        BigInteger shipId = shipDao
                .createNewShip(DatabaseObject.T_FREGATA_OBJECT_ID, null);
        BigInteger holdId = holdDao.createHold(shipId);
        // When
        BigInteger fondHoldId = holdDao.findHold(shipId);
        // Then
        assertEquals(holdId, fondHoldId);
    }

    @Rollback(true)
    @Test(expected = IllegalArgumentException.class)
    public void testFindHoldByInvalidShipId() {
        // Given
        BigInteger invalidShipId = BigInteger.ONE;
        // When
        holdDao.findHold(invalidShipId);
        // Then 
    }
    
    @Rollback(true)
    @Test(expected = IllegalArgumentException.class)
    public void testAddCargoToHoldWithStockId() {
        //Given
        String login = "qwe";
        String pass = "1111";
        String email = "qwe@qwe.qwe";

        BigInteger cargoId = cannonDao.createCannon(DatabaseObject.MORTAR_TEMPLATE_ID);
        
        playerDao.addNewPlayer(login, pass, email);
        Player player = playerDao.findPlayerByLogin(login);

        BigInteger stockId = stockDao.createStock(player.getPlayerId());
        // When
        holdDao.addCargo(cargoId, stockId);
        // Then Exception
    }
}
