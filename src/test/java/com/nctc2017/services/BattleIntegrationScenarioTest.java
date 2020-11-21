package com.nctc2017.services;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.nctc2017.bean.City;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.AmmoDao;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.CityDao;
import com.nctc2017.dao.GoodsDao;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.exception.BattleStartException;
import com.nctc2017.exception.DeadEndException;
import com.nctc2017.exception.PlayerNotFoundException;
import com.nctc2017.services.utils.BattleEndVisitor;
import com.nctc2017.services.utils.BattleManager;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
@Rollback(true)
@FixMethodOrder
public class BattleIntegrationScenarioTest {
    private static final Logger LOG = Logger.getLogger(BattleIntegrationScenarioTest.class);
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
    private  AmmoDao ammoDao;
    @Autowired
    private  CannonDao cannonDao;
    @Autowired
    private GoodsDao goodsDao;
    
    @Autowired
    private  TravelService travelService;
    @Autowired
    private  BattlePreparationService prepService;
    @Autowired
    private  BattleService battleService;
    @Autowired
    private  BattleEndingService battleEnd;
    @Autowired
    private ShipService shipService;
    
    private  BigInteger nikId;
    private  BigInteger steveId;
    private  BigInteger nikShipId;
    private  BigInteger steveShipId;
    private  BigInteger nikHoldId;
    private  BigInteger steveHoldId;
    
    private  BigInteger cannonballId;
    private  BigInteger buckshotId;
    private  BigInteger chainId;
    
    private final int defCount = 2;
    private final int defCannonballCount = 1;
    private final int[] mortarsCball = new int[] {defCannonballCount, 0, 0};
    private final int[] kulevrinsCball = new int[] {defCannonballCount, 0, 0};
    private final int[] bombardsCball = new int[] {defCannonballCount, 0, 0};
    
    private final int[] mortarsCB = new int[] {0, defCount, defCount};
    private final int[] kulevrinsCB = new int[] {0, defCount, defCount};
    private final int[] bombardsCB = new int[] {0, defCount, defCount};

    private final int[][] hullDamage = new int [][]{mortarsCball, kulevrinsCball, bombardsCball};
    
    private final int[][] mastAndCrewDamage = new int [][]{mortarsCB, kulevrinsCB, bombardsCB};
    
    private  Player newCombatant(String login, String email) {
        playerDao.addNewPlayer(login, "123", email);
        Player player = playerDao.findPlayerByLogin(login);
        
        return player;
    }
    
    private  void getMoreCannons(int count, BigInteger shipId) {
        for (int i = 0; i < count / 2; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.MORTAR_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
        for (int i = 0; i < count / 2; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.BOMBARD_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
        for (int i = 0; i < count / 2; i++) {
            BigInteger id = 
                    cannonDao.createCannon(DatabaseObject.KULEVRIN_TEMPLATE_ID, shipId);
            shipDao.setCannonOnShip(id, shipId);
        }
    }
    
    private void putNewGoodsInHold(BigInteger holdId) {
        BigInteger goodsId_1 = 
                goodsDao.createNewGoods(DatabaseObject.GRAIN_TEMPLATE_ID, 10, 10);
        holdDao.addCargo(goodsId_1, holdId);
    }
    
    @Before
    public void setUpCombatant() throws PlayerNotFoundException, BattleStartException, BattleEndException {
        BattleManager battleManager = (BattleManager)context.getBean("battleManagerPrototype");
        travelService = (TravelService)context.getBean("travelServicePrototype");
        ReflectionTestUtils.setField(battleEnd, "travelService", travelService);
        ReflectionTestUtils.setField(battleEnd, "battles", battleManager);
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
        assertTrue(shipDao.getSpeed(nikShipId) > 0);
        assertTrue(shipDao.getSpeed(steveShipId) > 0);
        getMoreCannons(24, nikShipId);
        getMoreCannons(24, steveShipId);
        assertTrue(shipDao.getCurrentShipSailors(nikShipId) > 0);
        assertTrue(shipDao.getCurrentShipSailors(steveShipId) > 0);
        shipDao.updateShipHealth(nikShipId, shipDao.getCurrentShipHealth(nikShipId) / 3);
        shipDao.updateShipHealth(steveShipId, shipDao.getCurrentShipHealth(steveShipId) / 3);
        cannonballId = 
                ammoDao.createAmmo(DatabaseObject.CANNONBALL_TEMPLATE_OBJECT_ID, 80);
        buckshotId = 
                ammoDao.createAmmo(DatabaseObject.BUCKSHOT_TEMPLATE_OBJECT_ID, 180);
        chainId = 
                ammoDao.createAmmo(DatabaseObject.CHAIN_TEMPLATE_OBJECT_ID, 80);
        
        nikHoldId = holdDao.findHold(nikShipId);
        steveHoldId = holdDao.findHold(steveShipId);

        holdDao.addCargo(cannonballId, nikHoldId);
        holdDao.addCargo(buckshotId, steveHoldId);
        holdDao.addCargo(chainId, steveHoldId);
        putNewGoodsInHold(steveHoldId);
        putNewGoodsInHold(nikHoldId);
       
        BigInteger cityIdNik = playerDao.getPlayerCity(nik.getPlayerId());
        BigInteger cityIdSteve = playerDao.getPlayerCity(steve.getPlayerId());
        List<City> cities = cityDao.findAll();
        
        travelService.relocate(nikId, cityIdNik.equals(cities.get(0).getCityId()) 
                ? cities.get(1).getCityId() 
                : cities.get(0).getCityId());
        travelService.relocate(steveId, cityIdSteve.equals(cities.get(0).getCityId()) 
                ? cities.get(1).getCityId() 
                : cities.get(0).getCityId());
        

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

    }
    
    @Test(expected = IllegalStateException.class)
    public void testBoardingWithBigDist() throws DeadEndException, BattleEndException, SQLException {
        battleService.boarding(nikId, null);
    }
    
    private int totalCurrSpeed(BigInteger shipId) {
        int currSpeed = shipDao.getSpeed(shipId);
        return currSpeed;
    }
    
    @Test
    public void testBattleWithShipDestroy() throws DeadEndException, BattleEndException, SQLException {

        Ship steveShipBefore;
        Ship nikShipAfter;
        Ship steveShipAfter;
        while(true) {
            steveShipBefore = shipDao.findShip(steveShipId);
            
            battleService.setConvergaceOfDist(nikId, true);
            battleService.setConvergaceOfDist(steveId, true);
            
            battleService.calculateDamage(hullDamage, nikId, null);
            battleService.calculateDamage(mastAndCrewDamage, steveId, new DefaultDestroyBattleEnd());
            
            nikShipAfter = shipDao.findShip(nikShipId);
            try {
                steveShipAfter = shipDao.findShip(steveShipId);
            } catch (IllegalArgumentException e) {
                break;
            }
            assertTrue(nikShipAfter.getCurSailorsQuantity() >= 0);
            int currSpeed = totalCurrSpeed(steveShipId);
            assertTrue(currSpeed >= 1);
    
            assertTrue(steveShipAfter.getCurHealth() < steveShipBefore.getCurHealth());
            
            assertTrue(battleService.getDistance(nikId) >= 0);
        }
        assertTrue(battleEnd.isBattleFinish(nikId));
        assertTrue(battleEnd.isBattleFinish(steveId));
        assertTrue(battleEnd.isLeaveBattleFieldAvailable(steveId));
        assertTrue(battleEnd.isLeaveBattleFieldAvailable(nikId));
        assertTrue(battleEnd.leaveBattleField(nikId));
        try {
            battleEnd.leaveBattleField(steveId);
        } catch (BattleEndException e1) {
            return;
        }
        fail("BattleEndException expected");
        return;
        
    }
    
    @Test
    public void testBoarding() throws BattleEndException, SQLException {

        Ship nikShipBefore = shipDao.findShip(nikShipId);
        Ship steveShipBefore = shipDao.findShip(steveShipId);
        Ship nikShipAfter;
        Ship steveShipAfter;
        int dist;
        int[] mortars = new int[] {0, 5, 0};
        int[] kulevrins = new int[] {0, 5, 0};
        int[] bombards = new int[] {0, 5, 0};

        int[][] ammoCannons = new int [][]{mortars, kulevrins, bombards};
        int[][] ammoCannonsNik = new int [][]{{0, 0, 0},{0, 0, 0},{0, 0, 0}};
        while(true) {
            dist = battleService.getDistance(nikId);
            battleService.setConvergaceOfDist(nikId, true);
            battleService.setConvergaceOfDist(steveId, true);
            battleService.decreaseOfDistance(nikId);
            battleService.decreaseOfDistance(steveId);
            
            battleService.calculateDamage(ammoCannonsNik, nikId, null);
            battleService.calculateDamage(ammoCannons, steveId, null);
            int currSteveSailors = shipDao.getCurrentShipSailors(steveShipId);
            int currNikSailors = shipDao.getCurrentShipSailors(nikShipId);
            assertEquals("" + currSteveSailors + " = " + steveShipBefore.getCurSailorsQuantity(), 
                    currSteveSailors, steveShipBefore.getCurSailorsQuantity());
            assertTrue(currNikSailors < nikShipBefore.getCurSailorsQuantity());
            assertTrue(battleService.getDistance(nikId) >= 0);
            assertTrue(battleService.getDistance(nikId) < dist);
            
            if (battleService.getDistance(nikId) == 0) {
                holdDao.deleteHold(nikHoldId);
                holdDao.deleteHold(steveHoldId);
                
                nikHoldId = holdDao.createHold(nikShipId);
                steveHoldId = holdDao.createHold(steveShipId);
                
                putNewGoodsInHold(steveHoldId);
                putNewGoodsInHold(nikHoldId);
                
                currSteveSailors = shipDao.getCurrentShipSailors(steveShipId);
                assertEquals(steveShipBefore.getCurSailorsQuantity(), currSteveSailors);
                battleService.boarding(nikId, new DefaultBoardingBattleEnd());
                break;
            }
        }
        nikShipAfter = shipDao.findShip(nikShipId);
        steveShipAfter = shipDao.findShip(steveShipId);
        assertEquals(nikShipAfter.getCurSailorsQuantity(), 0);
        assertTrue(nikShipAfter.getCurSailorsQuantity() < nikShipBefore.getCurSailorsQuantity());
        int steveCurSailors = steveShipAfter.getCurSailorsQuantity();
        assertTrue("steve crew after battle: " + steveCurSailors + " > 0", steveCurSailors > 0);
        assertTrue(steveShipAfter.getCurSailorsQuantity() < steveShipBefore.getCurSailorsQuantity());
        assertTrue(battleService.getDistance(nikId) == 0);
       
        assertTrue(battleEnd.isBattleFinish(nikId));
        assertTrue(battleEnd.isBattleFinish(steveId));
        assertTrue(battleEnd.isLeaveBattleFieldAvailable(steveId));
        assertTrue(battleEnd.isLeaveBattleFieldAvailable(nikId));
        assertTrue(battleEnd.leaveBattleField(nikId));
        try {
            battleEnd.leaveBattleField(steveId);
        } catch (BattleEndException e1) {
            return;
        }
        fail("BattleEndException expected");
        return;
        
    }
    
    private class DefaultDestroyBattleEnd implements BattleEndVisitor {

        @Override
        public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, BigInteger loserShipId,
                BigInteger winnerId, BigInteger loserId) {
            int loserVolumeBefore = holdDao.getOccupiedVolume(loserShipId);
            int winnerVolumeBefore = holdDao.getOccupiedVolume(winnerShipId);
            try {
                battleEnd.passDestroyGoodsToWinner(winnerShipId, loserShipId);
            } catch (SQLException e) {
                LOG.error(e);
                fail(e.getMessage());
            }
            int loserVolumeAfter = holdDao.getOccupiedVolume(loserShipId);
            int winnerVolumeAfter = holdDao.getOccupiedVolume(winnerShipId);
            assertTrue("loser: Volume After: " 
                    + loserVolumeAfter
                    + " Volume Before: " 
                    + loserVolumeBefore, 
                    loserVolumeBefore > loserVolumeAfter);
            assertTrue("winner: Volume After: " 
                    + winnerVolumeAfter
                    + " Volume Before: " 
                    + winnerVolumeBefore, winnerVolumeBefore < winnerVolumeAfter);
            shipDao.deleteShip(loserShipId);
        }
        
    }
    
    private class DefaultBoardingBattleEnd implements BattleEndVisitor {

        @Override
        public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, BigInteger loserShipId,
                BigInteger winnerId, BigInteger loserId) {

            int currSteveSailors = shipDao.getCurrentShipSailors(steveShipId);
            int currNikSailors = shipDao.getCurrentShipSailors(nikShipId);
            //assertTrue(currSteveSailors + " > " + currNikSailors, currSteveSailors > currNikSailors);
            int loserVolumeBefore = holdDao.getOccupiedVolume(loserShipId);
            int winnerVolumeBefore = holdDao.getOccupiedVolume(winnerShipId);
            try {
                battleEnd.passCargoToWinnerAfterBoarding(winnerShipId, loserShipId);
            } catch (SQLException e) {
                LOG.error(e);
                fail(e.getMessage());
            }
            int loserVolumeAfter = holdDao.getOccupiedVolume(loserShipId);
            int winnerVolumeAfter = holdDao.getOccupiedVolume(winnerShipId);
            
            assertTrue("loser: Volume After: " 
                    + loserVolumeAfter
                    + " Volume Before: " 
                    + loserVolumeBefore, 
                    loserVolumeBefore > loserVolumeAfter);
            assertTrue("winner: Volume After: " 
                    + winnerVolumeAfter
                    + " Volume Before: " 
                    + winnerVolumeBefore, winnerVolumeBefore < winnerVolumeAfter);
        }
        
    }
}
