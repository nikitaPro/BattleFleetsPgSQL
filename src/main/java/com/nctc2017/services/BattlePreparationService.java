package com.nctc2017.services;

import com.nctc2017.bean.Ammo;
import com.nctc2017.bean.Battle;
import com.nctc2017.bean.Mast;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.dao.AmmoDao;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.MastDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.exception.PlayerNotFoundException;
import com.nctc2017.services.utils.AutoDecisionTask;
import com.nctc2017.services.utils.BattleManager;
import com.nctc2017.services.utils.Visitor;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
@Transactional
public class BattlePreparationService {
    private static final Logger LOG = Logger.getLogger(BattlePreparationService.class);
    private static final int DELAY = 60000;
    
    @Autowired
    @Qualifier("battleManager")
    protected BattleManager battles;    
    
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ShipDao shipDao;
    @Autowired
    private MastDao mastDao;
    @Autowired
    private AmmoDao ammoDao;
    @Autowired
    private HoldDao holdDao;
    @Autowired
    private CannonDao cannonDao;
    @Autowired
    private BattleEndingService battleEnd;
    
    private Random randomShip = new Random(System.currentTimeMillis());
    private Map<BigInteger, ThreadStorage> playerChoiceShipTimer = new ConcurrentHashMap<>();
    
    public List<ShipWrapper> getShipsExtraInfo(BigInteger playerId) {
        List<Ship> listShipsId = getShips(playerId);
        List<ShipWrapper> shipInfo = new ArrayList<ShipWrapper>();
        for (Ship ship : listShipsId) {
            shipInfo.add(new ShipWrapper(
                    ship, 
                    ammoDao.getAllAmmoFromHold(holdDao.findHold(ship.getShipId())),
                    cannonDao.getCurrentQuantity(ship.getShipId()),
                    mastDao.getShipMastsFromShip(ship.getShipId()),
                    shipDao.getMaxShotDistance(ship.getShipId())));
            if (ship.getCurSailorsQuantity() == 0) {
                ship.setShipId(null);
            }
        }
        return shipInfo;
    }
    
    private List<Ship> getShips(BigInteger playerId) {
        List<BigInteger> listShipsId = playerDao.findAllShip(playerId);
        List<Ship> listShips = shipDao.findAllShips(listShipsId);
        return listShips;
    }

    public List<Ship> getEnemyShips(BigInteger playerId) throws BattleEndException {
        BigInteger enemyId = battles.getEnemyId(playerId);
        if (enemyId == null) {
            stopAutoChooseTimer(playerId);
            BattleEndException ex = new BattleEndException("Enemy run away.");
            LOG.warn("Battle not found! ", ex);
            throw ex;
        }
        return getShips(enemyId);
    }
    
    public void stopAutoChooseTimer(BigInteger playerId) {
        ThreadStorage timer = playerChoiceShipTimer.get(playerId);
        if (timer != null && timer.decisionThread.isAlive()) {
            timer.decisionThread.interrupt();
            LOG.debug("Auto choose ship timer stoped. ");
        }
        playerChoiceShipTimer.remove(playerId);
    }

    public void chooseShip(BigInteger playerId, BigInteger shipId) throws BattleEndException {
        stopAutoChooseTimer(playerId);
        
        Battle battle = battles.getBattle(playerId);
        battle.setShipId(playerId, shipId);
        
        int maxDist = shipDao.getMaxShotDistance(shipId);
        if (battle.getDistance() < maxDist) {
            battle.setDistance(maxDist);
            LOG.debug("Reset distance " + maxDist);
        }
        LOG.debug("Chose ship " + shipId);
    }

    public void setReady(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        battle.setReady(playerId, true);
        LOG.debug("Ready to fight!");
    }
    
    public boolean waitForEnemyReady(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        boolean ready = battle.isEnemyReady(playerId);
        LOG.debug("Ask for enemy ready. Ready: " + ready);
        return ready;
    }

    public int autoChoiceShipTimer(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);

        ThreadStorage existing = playerChoiceShipTimer.get(playerId);
        if (existing != null) {
            return (int)(existing.decisionTask.getTimeLeft()/1000L);
        } else if (battle.getShipId(playerId) != null) {
            return -1;
        }
        AutoDecisionTask decisionTask = new AutoDecisionTask(new ShipVisitor(playerId), DELAY);
        Thread decisionThread = new Thread(decisionTask);
        ThreadStorage storage = new ThreadStorage(decisionTask, decisionThread);
        decisionThread.start();
        playerChoiceShipTimer.put(playerId, storage);
        LOG.debug("Auto choose ship timer started");
        return DELAY/1000;
    }
    
    public List<BigInteger> getShipsLeftBattle(BigInteger playerId) throws BattleEndException {
        return battles.getBattle(playerId).getShipsLeftBattle(playerId);
    }
    
    public Player getEnemyInfo(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        BigInteger enemyId = battle.getEnemyId(playerId);
        return playerDao.findPlayerById(enemyId);
    }
    
    private class ThreadStorage {
        public final AutoDecisionTask decisionTask;
        public final Thread decisionThread;
        
        public ThreadStorage(AutoDecisionTask decisionTask, Thread decisionThread) {
            this.decisionTask = decisionTask;
            this.decisionThread = decisionThread;
        }
    }
    
    private class ShipVisitor implements Visitor{
        
        private BigInteger playerId;
        private Object userName;
        
        public ShipVisitor(BigInteger playerId) {
            this.playerId = playerId;
            this.userName = MDC.get("userName");
        }

        @Override
        public void visit() {
            MDC.put("userName", userName);
            try {
                LOG.debug("Ship choosing TIMEOUT");
                List<BigInteger> ships = playerDao.findAllShip(playerId);
                
                ships.removeAll(getShipsLeftBattle(playerId));
    
                LOG.debug("Has ships: " + ships.size());
                if (ships.size() == 0) {
                    LOG.debug("Has no ships");
                    battleEnd.leaveBattleField(playerId);
                    return;
                } 
                
                BigInteger shipId = ships.get(randomShip.nextInt(ships.size()));
                LOG.debug("Choose ship with id = " + shipId);
                chooseShip(playerId, shipId);
                setReady(playerId);
            } catch (BattleEndException e) {
                stopAutoChooseTimer(playerId);
                LOG.debug("When TIMEOUT the enemy already leave", e);
            } finally {
                MDC.remove("userName");
            }
        }
        
    }
    
    public class ShipWrapper {
        private Ship ship;
        private Map<String, String> cannons;
        private List<Ammo> ammo;
        private List<Mast> masts;
        private int maxShotDistance;
        public ShipWrapper(Ship ship, List<Ammo> ammo, Map<String, String> cannons, List<Mast> curMasts, int dist) {
            this.ship = ship;
            this.cannons = cannons;
            this.masts = curMasts;
            this.maxShotDistance = dist;
            this.ammo = ammo;
        }
        
        public Ship getShip() {
            return ship;
        }
        
        public Map<String, String> getCannons() {
            return cannons;
        }

        public List<Mast> getMasts() {
            return masts;
        }

        public int getMaxShotDistance() {
            return maxShotDistance;
        }

        public List<Ammo> getAmmo() {
            return ammo;
        }
        
    }

}