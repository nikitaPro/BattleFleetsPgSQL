package com.nctc2017.services;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nctc2017.bean.Battle;
import com.nctc2017.dao.ExecutorDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.exception.PlayerNotFoundException;
import com.nctc2017.services.utils.BattleEndVisitor;
import com.nctc2017.services.utils.BattleManager;

@Service
@Transactional
public class BattleEndingService {
    private static final Logger LOG = Logger.getLogger(BattleEndingService.class);
    private static final int PAY_OFF_COEF = 500;
    private static final String SURRENDER_WINNER_MSG = "The enemy shamefully surrendered and gave you all his cargo.";
    private static final String SURRENDER_LOSER_MSG = "You shamefully surrendered, the enemy spared you, but you had to give him the whole cargo.";
    private static final String LEAVE_PLAYER_MSG = "Praise the sails you have come off the enemy.";
    private static final String LEAVE_MSG_FOR_ENEMY = "The enemy was able to slip away, our ship is too slow.";
    private static final String PAYOFF_WINNER_MSG = "The enemy exchanged gold for life. You became richer for ? doubloons.";
    private static final String PAYOFF_LOSER_MSG = "You paid off the enemy ? doubloons.";
    private static final int SPEED_DIFFERENCE_FOR_ESCAPE = 20;
    
    @Autowired
    private ShipDao shipDao;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ExecutorDao executorDao;
    @Autowired
    private BattlePreparationService prepService;
    @Autowired
    private TravelService travelService;
    
    @Autowired
    @Qualifier("battleManager")
    protected BattleManager battles;

    public String passCargoToWinnerAfterBoarding(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException {
        return executorDao.moveCargoToWinnerBoardingOSurrender(shipWinnerId, shipLoserId);
    }

    public String passDestroyGoodsToWinner(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException {
        return executorDao.moveCargoToWinnerDestroying(shipWinnerId, shipLoserId);
    }

    public boolean destroyShip(BigInteger shipId) {
        return shipDao.deleteShip(shipId);
    }

    public String passSurrenderGoodsToWinner(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException {
        return executorDao.moveCargoToWinnerBoardingOSurrender(shipWinnerId, shipLoserId);
    }
    
    private int getPayOff(int enemyLvl) {
        return enemyLvl * PAY_OFF_COEF;
    }

    public int getPayOffPrice(BigInteger playerId) {
        BigInteger enemyId = battles.getEnemyId(playerId);
        int enemyLvl = playerDao.getPlayerLevel(enemyId);
        int payOff = getPayOff(enemyLvl);
        return payOff;
    }
    
    public boolean isPayOffAvailable(int payoff, BigInteger playerId) {
        int money = playerDao.getPlayerMoney(playerId);
        return money >= payoff;
    }
    
    public int passPayOffToEnemy(BigInteger playerId) {
        BigInteger enemyId = battles.getEnemyId(playerId);
        int playerMoney = playerDao.getPlayerMoney(playerId);
        int enemyMoney = playerDao.getPlayerMoney(enemyId);
        int enemyLvl = playerDao.getPlayerLevel(enemyId);
        int payOff = getPayOff(enemyLvl);
        if (payOff <= playerMoney) {
            playerDao.updateMoney(enemyId, enemyMoney + payOff);
            playerDao.updateMoney(playerId, playerMoney - payOff);
            return payOff;
        }
        return -1;
    }
    
    public boolean isBattleFinish(BigInteger playerId) throws BattleEndException {
        return battles.getBattle(playerId).getShipId(playerId) == null;
    }
    
    private int fleetSpeed(BigInteger playerId) {
        return playerDao.getFleetSpeed(playerId);
    }
    
    private int fasterShipSpeed(BigInteger playerId) {
        return playerDao.getFasterShipSpeed(playerId);
    }
    
    public boolean isLeaveBattleFieldAvailable(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        List<BigInteger> playerShipsLeft = battle.getShipsLeftBattle(playerId);
        BigInteger enemyId = battle.getEnemyId(playerId);
        List<BigInteger> playerFactShips = playerDao.findAllShip(playerId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ships was in battle: " + playerShipsLeft.size() + " Ship all: " + playerFactShips.size());
        }
        int fasterEnemyShipSpeed = fasterShipSpeed(enemyId);
        int slowerPlayerShipSpeed = fleetSpeed(playerId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Faster enemy ship speed " + fasterEnemyShipSpeed + ". Slower player ship speed " + slowerPlayerShipSpeed);
        }
        return playerShipsLeft.size() >= playerFactShips.size() 
                || slowerPlayerShipSpeed - fasterEnemyShipSpeed >= SPEED_DIFFERENCE_FOR_ESCAPE;
    }
    
    public boolean leaveBattleField (BigInteger playerId) throws BattleEndException {
        if ( ! isLeaveBattleFieldAvailable(playerId)) return false;
        synchronized (battles) {
            BigInteger enemyId = battles.getEnemyId(playerId);
            
            if (! battles.endBattle(enemyId) || ! battles.endBattle(playerId))
                throw new BattleEndException("Battle already end. You automatically left.");
            prepService.stopAutoChooseTimer(enemyId);
            try {
                LOG.debug("Continue travel for enemy_" + enemyId);
                travelService.resumeRelocateTime(enemyId);
            } catch (PlayerNotFoundException e) {
                LOG.warn("Try resume relocate time for enemy fail when leaving battle field", e);
            }
        }
        prepService.stopAutoChooseTimer(playerId);
        try {
            LOG.debug("Continue travel for himself");
            travelService.resumeRelocateTime(playerId);
        } catch (PlayerNotFoundException e) {
            return true;
        }
        return true;
    }
    
    public boolean isPlayerWinner(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);

        return battle.isWinner(playerId);
    }
    
    public String getWinnerMessage(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);

        return battle.getWinMessage(playerId);
    }
    
    public boolean isEnemyLeaveBattlefield(BigInteger playerId) {
        BigInteger enemy = battles.getEnemyId(playerId);
        return enemy == null;
    }
    
    public void surrender(BigInteger playerId, BattleEndVisitor visitor) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        BigInteger winnerId = battle.getEnemyId(playerId);
        
        battles.clearAutoStepTime(playerId, winnerId);
        
        BigInteger winnerShipId = battle.getShipId(winnerId);
        BigInteger loserShipId = battle.getShipId(playerId);
        
        if (winnerShipId == null || loserShipId == null) return;
        
        battle.setWinner(winnerId, SURRENDER_WINNER_MSG, SURRENDER_LOSER_MSG);
        battle.resetAll();
        visitor.endCaseVisit(playerDao, shipDao, winnerShipId, loserShipId, winnerId, playerId);
    }
    
    public boolean isBattleLocationEscapeAvailable(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        BigInteger enemyShipId = battle.getEnemyShipId(playerId);
        BigInteger playerShipId = battle.getShipId(playerId);
        int playerShipSpeed = shipDao.getSpeed(playerShipId);
        int enemyShipSpeed = shipDao.getSpeed(enemyShipId);
        return playerShipSpeed - enemyShipSpeed >= SPEED_DIFFERENCE_FOR_ESCAPE;
    }
    
    public boolean escapeBattleLocation(BigInteger playerId, BattleEndVisitor visitor) throws BattleEndException {
        if (! isBattleLocationEscapeAvailable(playerId)) return false;
        Battle battle = battles.getBattle(playerId);
        BigInteger loserId = battle.getEnemyId(playerId);
        
        battles.clearAutoStepTime(playerId, loserId);
        
        BigInteger loserShipId = battle.getShipId(loserId);
        BigInteger winnerShipId = battle.getShipId(playerId);
        battle.setWinner(playerId, LEAVE_PLAYER_MSG, LEAVE_MSG_FOR_ENEMY);
        battle.resetAll();
        visitor.endCaseVisit(playerDao, shipDao, winnerShipId, loserShipId, playerId, loserId);
        return true;
    }
    
    public boolean payoff(BigInteger playerId, BattleEndVisitor visitor) throws BattleEndException {
        Battle battle = battles.getBattle(playerId);
        BigInteger winnerId = battle.getEnemyId(playerId);
        
        battles.clearAutoStepTime(playerId, winnerId);
        
        BigInteger winnerShipId = battle.getShipId(winnerId);
        BigInteger loserShipId = battle.getShipId(playerId);
        if (winnerShipId == null || loserShipId == null) return false;
        int payoff = passPayOffToEnemy(playerId);
        if (payoff == -1) return false;
        battle.setWinner(winnerId, 
                PAYOFF_WINNER_MSG.replace("?", String.valueOf(payoff)), 
                PAYOFF_LOSER_MSG.replace("?", String.valueOf(payoff)));
        battle.resetAll();
        visitor.endCaseVisit(playerDao, shipDao, winnerShipId, loserShipId, winnerId, playerId);
        return true;
    }
}