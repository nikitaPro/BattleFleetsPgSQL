package com.nctc2017.services.utils;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nctc2017.bean.Battle;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.exception.BattleStartException;
import com.nctc2017.services.BattleEndingService;
import com.nctc2017.services.utils.endVisitorImpl.SurrenderDefaultBattleEnd;

@Component
public class BattleManager {
    private static final Logger LOG = Logger.getLogger(BattleManager.class);

    private static final long AUTO_STEP_TIME = 180000;

    @Autowired
    protected SurrenderDefaultBattleEnd surrenderDefaultBattleEnd;
    @Autowired
    protected BattleEndingService battleEnd;
    
    private Map<BigInteger, Battle> battles = new HashMap<>();

    private Map<BigInteger, Long> autoStep = new ConcurrentHashMap<>();
    
    @PostConstruct
    private void init() {
        AutoStepManager mng = new AutoStepManager();
        Thread thread = new Thread(mng);
        thread.start();
    }
    
    public void newBattleBetween(BigInteger pl1, BigInteger pl2) throws BattleStartException {
        if (battles.get(pl1) != null) {
            BattleStartException ex = new BattleStartException("Player with id=" + pl1 + " already have a battle");
            LOG.warn("Players " + pl1 + " and " + pl2 + " cannon make a battlt ", ex);
            throw ex;
        }
        if (battles.get(pl2) != null) {
            BattleStartException ex = new BattleStartException("Player with id=" + pl2 + " already have a battle");
            LOG.warn("Players " + pl1 + " and " + pl2 + " cannon make a battlt ", ex);
            throw ex;
        }
        
        Battle newBattle = new Battle(pl1, pl2, new Visitor() {

            @Override
            public void visit() {
                setUpAutoStepTime(pl1, pl2);
            }
            
        });
        
        battles.put(pl1, newBattle);
        battles.put(pl2, newBattle);
    }
    
    public BigInteger getEnemyId (BigInteger playerId) {
        Battle battle = battles.get(playerId);
        if (battle == null) return null;
        BigInteger enemyId = battle.getEnemyId(playerId);
        return enemyId;
    }
    
    public Battle getBattle(BigInteger playerId) throws BattleEndException {
        Battle battle = battles.get(playerId);
        if (battle == null) {
            BattleEndException ex = new BattleEndException("Battle already end or wrong player id = " + playerId);
            LOG.warn("Player not found his battle", ex);
            throw ex;
        }
        return battle;
    }
    
    public boolean isBattleStart(BigInteger playerId) {
        Battle battle = battles.get(playerId);
        return battle != null;
    }

    public boolean endBattle(BigInteger playerId) {
        return battles.remove(playerId) == null ? false : true;
    }

    public void resetBattle(BigInteger playerId) {
        battles.get(playerId).resetAll();
    }
    
    public void clearAutoStepTime(BigInteger playerId) {
        autoStep.remove(playerId);
    }
    
    public void clearAutoStepTime(BigInteger playerId, BigInteger enemyId) {
        autoStep.remove(enemyId);
        autoStep.remove(playerId);
    }
    
    public int getAutoStepTime(BigInteger playerId) {
        Long timeInFuture = autoStep.get(playerId);
        if (timeInFuture == null) {
            return 0;
        }
        Long timeNow = new GregorianCalendar().getTimeInMillis();
        return (int)((timeInFuture - timeNow) / 1000L);
    }
    
    public void setUpAutoStepTime(BigInteger playerId, BigInteger enemyId) {
        LOG.debug("Auto Step need set up");
        if (autoStep.containsKey(playerId) || autoStep.containsKey(enemyId)) return;
        LOG.debug("     --==Write time " + AUTO_STEP_TIME + " to auto step==-- ");
        
        Long now = new GregorianCalendar().getTimeInMillis();
        long timeInFuture = now + AUTO_STEP_TIME;

        autoStep.put(playerId, timeInFuture);
        autoStep.put(enemyId, timeInFuture);
    }

    private class AutoStepManager implements Runnable {

        @Override
        public void run() {
            MDC.put("userName", "AutoStepManager");
            
            while (true) {
                
                Long min = AUTO_STEP_TIME;
                long buf;
                Long now = new GregorianCalendar().getTimeInMillis();
                
                for (Map.Entry<BigInteger,Long> time : autoStep.entrySet()) {
                    if (now >= time.getValue()) {
                        try {
                            LOG.debug("Player_" + time.getKey() + " TIME TO SURRENDER");
                            battleEnd.surrender(time.getKey(), surrenderDefaultBattleEnd);
                        } catch (BattleEndException e) {
                            LOG.fatal("Timer for auto step ended with excetion while try surrender player", e);
                        }
                    } else {
                        buf = time.getValue() - now;
                        if (min > buf) {
                            min = buf;
                        }
                    }
                }
                
                try {
                    LOG.debug("Sleep " + min + "ms");
                    Thread.sleep(min);
                } catch (InterruptedException e) {
                    LOG.error("Was interapted", e);
                    MDC.remove("userName");
                    return;
                }
                LOG.debug("Awoke");
            }
        }
        
    }
    
}
