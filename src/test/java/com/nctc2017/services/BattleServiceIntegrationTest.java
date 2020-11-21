package com.nctc2017.services;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.nctc2017.bean.Ammo;
import com.nctc2017.bean.City;
import com.nctc2017.bean.Mast;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.AmmoDao;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.CityDao;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.MastDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.exception.BattleStartException;
import com.nctc2017.exception.PlayerNotFoundException;
import com.nctc2017.services.utils.BattleManager;
import com.nctc2017.services.utils.TravelManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
@Rollback(true)
public class BattleServiceIntegrationTest {
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ShipDao shipDao;
    @Autowired
    private HoldDao holdDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private AmmoDao ammoDao;
    @Autowired
    private MastDao mastDao;
    @Autowired
    private CannonDao cannonDao;
    
    @Autowired
    private TravelService travelService;
    @Autowired
    private BattlePreparationService prepService;
    @Autowired
    private BattleService battleService;
    @Autowired
    private ShipService shipService;
    
    private BigInteger nikId;
    private BigInteger steveId;
    private BigInteger nikShipId;
    private BigInteger steveShipId;
    private BigInteger nikHoldId;
    
    private Ship nikShipBefore;
    private Ship steveShipBefore;
    private List<Ammo> ammoInHoldBefore;
    
    private BigInteger cannonballId;
    private BigInteger buckshotId;
    private BigInteger chainId;
    
    private Player newCombatant(String login, String email) {
        playerDao.addNewPlayer(login, "123", email);
        Player player = playerDao.findPlayerByLogin(login);
        
        return player;
    }
    
    private  void getMoreCannons(BigInteger shipId) {
        for (int i = 0; i < 11; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.MORTAR_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
        for (int i = 0; i < 12; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.BOMBARD_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
        for (int i = 0; i < 12; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.KULEVRIN_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
    }
    
    @Before
    public void setUpCombatant() throws PlayerNotFoundException, BattleStartException, BattleEndException {
        BattleManager battleManager = (BattleManager)context.getBean("battleManagerPrototype");
        travelService = (TravelService)context.getBean("travelServicePrototype");
        ReflectionTestUtils.setField(travelService, "battleManager", battleManager);
        ReflectionTestUtils.setField(prepService, "battles", battleManager);
        ReflectionTestUtils.setField(battleService, "battles", battleManager);
        
        String loginNik = "Nik";
        String emailNik = "q@q.q";
        String loginSteve = "Steve";
        String emailSteve = "qqq@qq.qq";
        
        Player nik = newCombatant(loginNik, emailNik);
        Player steve = newCombatant(loginSteve, emailSteve);
        nikId = nik.getPlayerId();
        steveId = steve.getPlayerId();
        
        nikShipId = shipService.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, nikId);
        steveShipId = shipService.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, steveId);
        getMoreCannons(nikShipId);
        getMoreCannons(steveShipId);
        
        cannonballId = 
                ammoDao.createAmmo(DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID, 25);
        buckshotId = 
                ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, 25);
        chainId = 
                ammoDao.createAmmo(DatabaseObject.CHAIN_TEMPLATE_OBJECT_ID, 33);
        
        nikHoldId = holdDao.findHold(nikShipId);
        holdDao.addCargo(buckshotId, nikHoldId);
        holdDao.addCargo(cannonballId, nikHoldId);
        holdDao.addCargo(chainId, nikHoldId);
        
        BigInteger cannonballId = 
                ammoDao.createAmmo(DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID, 25);
        BigInteger buckshotId = 
                ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, 25);
        BigInteger chainId = 
                ammoDao.createAmmo(DatabaseObject.CHAIN_TEMPLATE_OBJECT_ID, 33);
        
        BigInteger steveHoldId = holdDao.findHold(steveShipId);
        holdDao.addCargo(buckshotId, steveHoldId);
        holdDao.addCargo(cannonballId, steveHoldId);
        holdDao.addCargo(chainId, steveHoldId);
        
        BigInteger cityIdNik = playerDao.getPlayerCity(nik.getPlayerId());
        BigInteger cityIdSteve = playerDao.getPlayerCity(steve.getPlayerId());
        List<City> cities = cityDao.findAll();
        
        travelService.relocate(nikId, cityIdNik.equals(cities.get(0).getCityId()) 
                ? cities.get(1).getCityId() 
                : cities.get(0).getCityId());
        travelService.relocate(steveId, cityIdSteve.equals(cities.get(0).getCityId()) 
                ? cities.get(1).getCityId() 
                : cities.get(0).getCityId());
        
        //testIsEnemyOnHorizon(nik);
        battleManager.newBattleBetween(nikId, steveId);
        //travelService.confirmAttack(nikId, true);
        
        boolean start = travelService.isBattleStart(nikId);
        assertTrue(start); 
        start = travelService.isBattleStart(steveId);
        assertTrue(start); 
            
        prepService.chooseShip(nikId, nikShipId);
        prepService.chooseShip(steveId, steveShipId);
        prepService.setReady(nikId);
        prepService.setReady(steveId);
        try {
            assertTrue(prepService.waitForEnemyReady(nikId));
        } catch (BattleEndException e) {
            fail();
        }
        try {
            assertTrue(prepService.waitForEnemyReady(steveId));
        } catch (BattleEndException e) {
            fail();
        }

        nikShipBefore = shipDao.findShip(nikShipId);
        steveShipBefore = shipDao.findShip(steveShipId);
        ammoInHoldBefore = ammoDao.getAllAmmoFromHold(nikHoldId);
    }
    
    private void testIsEnemyOnHorizon(Player player) {
        boolean ret = false;
        try {
            ret = travelService.isEnemyOnHorizon(player.getPlayerId());
        } catch (PlayerNotFoundException e) {
            fail("Player must be in travel");
        }
        assertTrue(ret);
    }
    
    public void calculateDamage(int[][] cannonAmmo) throws SQLException, BattleEndException {
        // When
            battleService.calculateDamage(cannonAmmo, nikId, null);
            battleService.calculateDamage(new int[][]{{0,0,0},
                                                      {0,0,0},
                                                      {0,0,0}}, steveId, null);
        // Then
    }
    
    private void tesstMastNotDamaged(BigInteger playerId) {
        List<Mast> masts = mastDao.getShipMastsFromShip(playerId);
        for (Mast mast : masts) {
            assertEquals(mast.getCurSpeed(), mast.getMaxSpeed());
        }
    }
    
    private void testSailorsAlive(Ship shipBefor, Ship shipAfter) {
        assertEquals(shipAfter.getCurSailorsQuantity(), 
                shipBefor.getCurSailorsQuantity());
    }
    
    private void ammoWasNotSpend(List<Ammo> ammoInHold, Ammo ammoBefore) {
        for (Ammo ammo : ammoInHold) {
            if (ammo.getThingId().equals(ammoBefore.getThingId())) {
                assertEquals(ammo.getName() + " " + ammo.getDamageType(), 
                        ammo.getQuantity(), 
                        ammoBefore.getQuantity());
                break;
            }
        }
    }
    
    private void testAmmoWasSpend(List<Ammo> ammoInHold, BigInteger id, int spendCount) {
        for (Ammo ammoBefore : ammoInHoldBefore) {
            for (Ammo ammo : ammoInHold) {
                if (ammo.getThingId().equals(id) && ammoBefore.getThingId().equals(id)) {
                    assertEquals(ammoBefore.getQuantity() - spendCount,
                            ammo.getQuantity());
                    break;
                }
            }
        }
    }
    
    @Test
    public void calculateDamageCannonballs() throws SQLException, BattleEndException {
        calculateDamageCannonballs(8);
    }
    
    @Test(expected = SQLException.class)
    public void cannonballsGraterThenHave() throws SQLException, BattleEndException {
        calculateDamageCannonballs(9);
    }
    
    private void calculateDamageCannonballs(int defCount) throws SQLException, BattleEndException {
        // Given
        int[][] cannonAmmo = new int [3][];
        int[] mortars = new int[] {defCount, 0, 0};
        int[] kulevrins = new int[] {defCount, 0, 0};
        int[] bombards = new int[] {defCount, 0, 0};
        
        cannonAmmo[0] = mortars;
        cannonAmmo[1] = kulevrins;
        cannonAmmo[2] = bombards;
        
        // When
        calculateDamage(cannonAmmo);
        
        // Then
        Ship nikShip = shipDao.findShip(nikShipId);
        Ship steveShip = shipDao.findShip(steveShipId);
        
        assertEquals(nikShip.getCurHealth(), nikShipBefore.getCurHealth());
        assertTrue(steveShip.getCurHealth() < steveShipBefore.getCurHealth());
        
        List<Ammo> ammoInHold = ammoDao.getAllAmmoFromHold(nikHoldId);

        testAmmoWasSpend(ammoInHold, cannonballId, defCount * 3);
        
        for (Ammo ammoBefore : ammoInHoldBefore) {
            if ( ! ammoBefore.getThingId().equals(cannonballId))
                ammoWasNotSpend(ammoInHold, ammoBefore);
        }
        
        testSailorsAlive(nikShipBefore, nikShip);
        testSailorsAlive(steveShipBefore, steveShip);
        
        tesstMastNotDamaged(nikShipId);
        tesstMastNotDamaged(steveShipId);
    }
    
    @Test
    public void calculateDamageBuckshot() throws SQLException, BattleEndException {
        calculateDamageBuckshot(8);
    }
    
    @Test(expected = SQLException.class)
    public void buckshotGraterThenHave() throws SQLException, BattleEndException {
        calculateDamageBuckshot(9);
    }
    
    private void calculateDamageBuckshot(int defCount) throws SQLException, BattleEndException {
        // Given
        int[][] cannonAmmo = new int [3][];
        int[] mortars = new int[] {0, defCount, 0};
        int[] kulevrins = new int[] {0, defCount, 0};
        int[] bombards = new int[] {0, defCount, 0};
        
        cannonAmmo[0] = mortars;
        cannonAmmo[1] = kulevrins;
        cannonAmmo[2] = bombards;
        
        // When
        calculateDamage(cannonAmmo);
        
        // Then
        Ship nikShip = shipDao.findShip(nikShipId);
        Ship steveShip = shipDao.findShip(steveShipId);
        
        assertEquals(nikShip.getCurHealth(), nikShipBefore.getCurHealth());
        assertEquals(steveShip.getCurHealth(), steveShipBefore.getCurHealth());
        
        List<Ammo> ammoInHold = ammoDao.getAllAmmoFromHold(nikHoldId);
        
        testAmmoWasSpend(ammoInHold, buckshotId, defCount * 3);

        for (Ammo ammoBefore : ammoInHoldBefore) {
            if ( ! ammoBefore.getThingId().equals(buckshotId))
                ammoWasNotSpend(ammoInHold, ammoBefore);
        }
        
        testSailorsAlive(nikShipBefore, nikShip);
        assertTrue(steveShip.getCurSailorsQuantity() < steveShipBefore.getCurSailorsQuantity());
        
        tesstMastNotDamaged(nikShipId);
        tesstMastNotDamaged(steveShipId);
    }
 
    @Test
    public void calculateDamageChains() throws SQLException, BattleEndException {
        calculateDamageChains(11);
    }
    
    @Test(expected = SQLException.class)
    public void chainsGraterThenHave() throws SQLException, BattleEndException {
        calculateDamageChains(12);
    }
    
    private void calculateDamageChains(int defCount) throws SQLException, BattleEndException {
        // Given
        int[][] cannonAmmo = new int [3][];
        int[] mortars = new int[] {0, 0, defCount};
        int[] kulevrins = new int[] {0, 0, defCount};
        int[] bombards = new int[] {0, 0, defCount};
        
        cannonAmmo[0] = mortars;
        cannonAmmo[1] = kulevrins;
        cannonAmmo[2] = bombards;
        
        // When
        calculateDamage(cannonAmmo);
        
        // Then
        Ship nikShip = shipDao.findShip(nikShipId);
        Ship steveShip = shipDao.findShip(steveShipId);
        
        assertEquals(nikShip.getCurHealth(), nikShipBefore.getCurHealth());
        assertEquals(steveShip.getCurHealth(), steveShipBefore.getCurHealth());
        
        List<Ammo> ammoInHold = ammoDao.getAllAmmoFromHold(nikHoldId);

        testAmmoWasSpend(ammoInHold, chainId, defCount * 3);
        
        for (Ammo ammoBefore : ammoInHoldBefore) {
            if ( ! ammoBefore.getThingId().equals(chainId))
                ammoWasNotSpend(ammoInHold, ammoBefore);
        }
        
        testSailorsAlive(nikShipBefore, nikShip);
        testSailorsAlive(steveShipBefore, steveShip);
        
        tesstMastNotDamaged(nikShipId); 
        
        int maxSpeed = 0; 
        int currSpeed = 0; 
        List<Mast> steveMasts = mastDao.getShipMastsFromShip(steveShipId);
        for (Mast mast : steveMasts) {
            currSpeed += mast.getCurSpeed();
            maxSpeed += mast.getMaxSpeed();
        }
        assertNotEquals(maxSpeed, currSpeed);
    }
}
