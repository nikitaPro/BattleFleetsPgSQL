package com.nctc2017.services.utils;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nctc2017.dao.PlayerDao;
import com.nctc2017.exception.BattleStartException;
import com.nctc2017.exception.PlayerNotFoundException;

@Component
@Scope("prototype")
public class TravelManager {
    private static final Logger LOG = Logger.getLogger(TravelManager.class);
    
    @Autowired
    private PlayerDao playerDao;
    
    @Autowired
    @Qualifier("scheduledExecutorService")
    private ScheduledExecutorService executor;
    
    @Autowired
    private BattleManager battleManager;
    
    private static final int LVL_DIFF = 5;
    private static final int MAX_RELOC_TIME = 61000;
    private static final int MIN_RELOC_TIME = 60000;
    private static final long MANAGER_WAKE_UP = 2000;
    private static final long ENEMY_FIND_WAKE_UP = 10000;
    private static final int DELAY = 50000;
    
    private Map<BigInteger, TravelBook> journals = new ConcurrentHashMap<BigInteger, TravelBook>();
    private Random rand = new Random(new GregorianCalendar().getTimeInMillis());
    private Map<BigInteger, Thread> playerAutoDecision = new ConcurrentHashMap<>();
    
    private ScheduledFuture<?> managerTaskFuture;
    private ScheduledFuture<?> enemyFindTaskFuture;
    
    @PostConstruct
    public void invokeTravelManager(){
        Runnable managerTask = new TravelManagerTask();
        Runnable enemyFindTask = new EnemyFindTask();
        
        LOG.debug("TravelManager starting");
        managerTaskFuture = 
                executor.scheduleWithFixedDelay(managerTask, 0, MANAGER_WAKE_UP, TimeUnit.MILLISECONDS);
        LOG.debug("TravelManager running");
        
        LOG.debug("EnemyFind starting");
        enemyFindTaskFuture = 
                executor.scheduleWithFixedDelay(enemyFindTask, 0, ENEMY_FIND_WAKE_UP, TimeUnit.MILLISECONDS);
        LOG.debug("EnemyFind running");
    }
    
    @PreDestroy
    private void interruptManager() {
        managerTaskFuture.cancel(false);
        enemyFindTaskFuture.cancel(false);
    }
    
    public boolean prepareEnemyFor(BigInteger playerId) throws PlayerNotFoundException {
        LOG.debug("Find enemy");
        boolean isEnemyOnHorisont = false;
        TravelBook playerJornal = getPlayersJournal(playerId);

        if (playerJornal.isParticipated()) {
            LOG.debug("Already took part in the battle, does not need an enemy");
            return false;
        }
        int lvl = playerJornal.getPlayerLevel();
        LOG.debug("lvl = " + lvl);

        if (playerJornal.isFriendly()) {
            LOG.debug("Is friendly (prepareEnemy) ");
            return false;
        }

        if (playerJornal.getEnemyId() != null) {
            LOG.debug("Already have enemy");            
            return false;
        }

        for (Map.Entry<BigInteger, TravelBook> enemy : journals.entrySet()) {
            TravelBook enemyJornal = enemy.getValue();
            if (enemyJornal.getEnemyId() != null)
                continue;
            if (enemyJornal == playerJornal)
                continue;
            if (enemyJornal.isParticipated())
                continue;
            int enemyLvl = enemyJornal.getPlayerLevel();

            if (Math.abs(lvl - enemyLvl) <= LVL_DIFF) {
                playerJornal.setEnemyId(enemy.getKey());
                enemyJornal.setEnemyId(playerId);
                
                if (LOG.isDebugEnabled())
                    LOG.debug("Enemy for Player_" + playerId 
                            + " found - Player_" + playerJornal.getEnemyId());
                
                playerJornal.pause();
                enemyJornal.pause();

                playerJornal.setDecisionMade(false);
                isEnemyOnHorisont = true;
                break;
            }
        }
        return isEnemyOnHorisont;
    }
    
    public void startJourney(BigInteger playerId, int lvl, BigInteger city) {
        TravelBook cityTime = journals.get(playerId);
        long timeNow = System.currentTimeMillis();
        long timeToArrival;
        if (cityTime != null) {
            timeToArrival = cityTime.getTime() - timeNow;
            throw new IllegalAccessError("Player already in travel to city with id = " 
                    + cityTime.getCityId() + ". Time to arrival: " 
                    + timeToArrival/60000 + " min");
        }
        
        long timeLeft = MIN_RELOC_TIME + rand.nextInt(MAX_RELOC_TIME - MIN_RELOC_TIME);
        timeToArrival = timeNow + timeLeft;
        cityTime = new TravelBook(city, timeToArrival, lvl);
        journals.put(playerId, cityTime);
        if (LOG.isDebugEnabled())
            LOG.debug("Journey started. Time Left: " + timeLeft);
    }

    public int getRelocateTime(BigInteger playerId) {
        TravelBook travelBook = journals.get(playerId);
        if (travelBook == null) return Integer.MIN_VALUE;
        
        long timeToArrival = travelBook.getTime();
        GregorianCalendar clock = new GregorianCalendar();
        long timeNow = clock.getTimeInMillis();
        return (int) (timeToArrival - timeNow) / 1000;
    }
    
    public BigInteger getEnemyId(BigInteger playerId) throws PlayerNotFoundException {
        return getPlayersJournal(playerId).getEnemyId();
    }

    public void friendly(BigInteger playerId) throws PlayerNotFoundException {
        LOG.debug("Became friendly");
        TravelBook playerBook = getPlayersJournal(playerId);
        
        BigInteger enemyId = playerBook.getEnemyId();
        playerBook.resume();
        if (enemyId == null) {
            LOG.warn(".friendly() was called when no enemy was written down in travel book.");
            playerBook.setFriendly(false);
            playerBook.setDecisionMade(false);
            return;
        }
        
        TravelBook enemyBook = journals.get(enemyId);
        if (enemyBook == null) {
            PlayerNotFoundException ex = new PlayerNotFoundException("Enemy_" + enemyId + " already left travel.");
            LOG.warn("Enemy not found in travel", ex);
            throw ex;
        }

        if (enemyBook.isFriendly()) {
            LOG.debug(" and Player_" + enemyId + " - Both Friendly");
            playerBook.setEnemyId(null);
            playerBook.setFriendly(false);
            
            enemyBook.setEnemyId(null);
            enemyBook.setFriendly(false);
        } else {
            playerBook.setFriendly(true);
            LOG.debug("Is friendly now.");
        }
    }
    
    public boolean isFriendly(BigInteger playerId) throws PlayerNotFoundException {
        return getPlayersJournal(playerId).isFriendly();
    }
    
    public int continueTravel(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook playerBook = getPlayersJournal(playerId);
        playerBook.resume();
        GregorianCalendar clock = new GregorianCalendar();
        long now = clock.getTimeInMillis();
        return (int) (playerBook.getTime() - now);
    }

    public BigInteger getRelocationCity(BigInteger playerId) throws PlayerNotFoundException {
        LOG.debug("Getting his relocation city ");
        return getPlayersJournal(playerId).getCityId();
    }
    
    public void decisionWasMade(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook playerBook = getPlayersJournal(playerId);
        playerBook.setDecisionMade(true);
    }
    
    public boolean isDecisionWasMade(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook playerBook = getPlayersJournal(playerId);
        boolean wasMade = playerBook.isDecisionWasMade();
        playerBook.setDecisionMade(false);
        return wasMade;
    }

    public void setParticipated(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook playerBook = getPlayersJournal(playerId);
        playerBook.setParticipated(true);
    }
    
    public boolean isPlayerInTravel(BigInteger playerId) {
        return journals.get(playerId) != null;
    }

    public boolean isParticipated(BigInteger playerId) throws PlayerNotFoundException {
        return getPlayersJournal(playerId).isParticipated();
    }

    public void stopRelocationTimer(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook travelBook = getPlayersJournal(playerId);
        travelBook.pause();
    }
    
    public void confirmAttack(BigInteger playerId, boolean decision) throws PlayerNotFoundException, BattleStartException {
        stopAutoDecisionTimer(playerId);
        decisionWasMade(playerId);
        LOG.debug("Made decision. Confirm Attack - " + decision);
        if (decision) {
            BigInteger enemyId = getEnemyId(playerId);
            stopAutoDecisionTimer(enemyId);
            battleManager.newBattleBetween(playerId, enemyId);
            LOG.debug("Stop relocation timer for enemy");
            stopRelocationTimer(enemyId);
            setParticipated(playerId);
            setParticipated(enemyId);
            LOG.debug("     --==battle created!==--");
        } else {
            LOG.debug("Attack rejecting ...");
            friendly(playerId);
            LOG.debug("Attack rejected.");
        }
    }
    
    private TravelBook getPlayersJournal(BigInteger playerId) throws PlayerNotFoundException {
        TravelBook travelBook = journals.get(playerId);
        if (travelBook == null) {
            PlayerNotFoundException ex = new PlayerNotFoundException("May be player already arrived");
            LOG.warn("Player not found in trip", ex);
            throw ex;
        }
        return travelBook;
    }
    
    private void autoDecisionTimer(BigInteger playerId) {
        if (playerAutoDecision.get(playerId) != null) return;
        Runnable decisionTask = new AutoDecisionTask(new DecisionVisitor(playerId), DELAY);
        Thread decisionThread = new Thread(decisionTask);
        decisionThread.start();
        playerAutoDecision.put(playerId, decisionThread);
        LOG.debug("Auto decision timer started");
    }
    
    private void stopAutoDecisionTimer(BigInteger playerId) {
        Thread timer = playerAutoDecision.get(playerId);
        LOG.debug("Auto Decision timer stoping. timer = null ? " + (timer == null));
        if (timer != null) {
            timer.interrupt();
            playerAutoDecision.remove(playerId);
            LOG.debug("Auto Decision timer stoped");
        }
    }
    
    private class TravelBook {
        
        private BigInteger cityId;
        private long arrivalTime;
        private int lvl;
        private BigInteger enemyId;
        private boolean pause;
        private long pauseTime;
        private boolean friendly = false;
        private boolean decisionMade = false;
        private boolean participated = false;
        
        public TravelBook(BigInteger cityId, Long time, int lvl) {
            this.cityId = cityId;
            this.arrivalTime = time;
            this.lvl = lvl;
            this.enemyId = null;
            this.pause = false;
            this.pauseTime = 0L;
        }
        
        public boolean isParticipated() {
            return participated;
        }

        public void setParticipated(boolean participated) {
            this.enemyId = null;
            this.decisionMade = false;
            this.participated = participated;
        }

        public void setDecisionMade(boolean made) {
            decisionMade = made;
        }
        
        public boolean isDecisionWasMade() {
            return decisionMade;
        }

        public void pause() {
            if (this.pause) {
                LOG.debug("Pause already on");
                return;
            }
            this.pause = true;
            GregorianCalendar clock = new GregorianCalendar();
            pauseTime = clock.getTimeInMillis();
            if(LOG.isDebugEnabled())
                LOG.debug("Pause when time left: " + (arrivalTime - pauseTime));
        }
        
        public void resume() {
            if (!this.pause) {
                LOG.debug("Pause already off");
                return;
            }
            GregorianCalendar clock = new GregorianCalendar();
            long now = clock.getTimeInMillis();
            long timeLeft = arrivalTime - pauseTime;
            if(LOG.isDebugEnabled())
                LOG.debug("Resume - time left: " + timeLeft);
            arrivalTime = now + timeLeft;
            pauseTime = 0L;
            this.pause = false;
        }
        public BigInteger getCityId() {
            return cityId;
        }
        
        public Long getTime() {
            if (pause) {
                GregorianCalendar clock = new GregorianCalendar();
                long now = clock.getTimeInMillis();
                return now + (arrivalTime - pauseTime);
            } else {
                return arrivalTime;
            }
        }
        
        public int getPlayerLevel() {
            return lvl;
        }

        public BigInteger getEnemyId() {
            return enemyId;
        }

        public void setEnemyId(BigInteger enemyId) {
            this.enemyId = enemyId;
        }

        public boolean isFriendly() {
            return friendly;
        }

        public void setFriendly(boolean friendly) {
            this.friendly = friendly;
        }
        
    }
    
    private void forEachPlayerInTravel(ForOnePlayerVisitor visitor) {
            
        Iterator<Entry<BigInteger, TravelBook>> mapIterator = journals.entrySet().iterator();
        Map.Entry<BigInteger, TravelBook> player;
            
        while (mapIterator.hasNext()) {
            player = mapIterator.next();
            TravelBook travelBook = player.getValue();
            
            boolean remove = visitor.doForPlayer(player.getKey(), travelBook);
            
            if (remove) mapIterator.remove();
        }
    }
    
    private interface ForOnePlayerVisitor {
        /** return true if want to delete player from map*/
        boolean doForPlayer(BigInteger playerId, TravelBook travelBook);
    }
    
    private class TravelManagerTask implements Runnable{
        
        @Override
        public void run() {
            MDC.put("userName", "TravelManager");
            LOG.trace("Awoke");
            
            GregorianCalendar clock = new GregorianCalendar();
            long now = clock.getTimeInMillis();
            
            forEachPlayerInTravel(new ForOnePlayerVisitor() {

                @Override
                public boolean doForPlayer(BigInteger playerId, TravelBook travelBook) {
                    long timeToLeft = travelBook.getTime();
                    if (now >= timeToLeft) {
                        playerDao.movePlayerToCity(playerId, travelBook.getCityId());
                        BigInteger enemyId = travelBook.getEnemyId();
                        if (enemyId != null) {
                            new Thread(new EnemyJournalFixTask(enemyId, playerId)).start();
                        }
                        return true;
                    }
                    return false;
                }
                
            });
            
            LOG.trace("Sleep");
            MDC.remove("userName");
        }
    }
    
    private class EnemyJournalFixTask implements Runnable{
        private BigInteger enemyId;
        private BigInteger playerId;
        
        public EnemyJournalFixTask(BigInteger enemyId, BigInteger playerId) {
            LOG.debug("EnemyJournalFixTask will run");
            this.enemyId = enemyId;
            this.playerId = playerId;
        }

        @Override
        public void run() {
            TravelBook travelBook = journals.get(enemyId);
            if (travelBook == null) return;
            synchronized (travelBook) {
                if (playerId.equals(travelBook.getEnemyId())) {
                    travelBook.setEnemyId(null);
                    travelBook.setFriendly(false);
                    travelBook.setDecisionMade(false);
                }
            }
        }
        
    }
    
    private class EnemyFindTask implements Runnable{
        @Override
        public void run() {
            MDC.put("userName", "EnemyFindTask");
            LOG.trace("Awoke");
            
            long now = System.currentTimeMillis();
            
            forEachPlayerInTravel(new ForOnePlayerVisitor() {

                @Override
                public boolean doForPlayer(BigInteger playerId, TravelBook travelBook) {
                    long timeToLeft = travelBook.getTime();
                    if (timeToLeft - now > 5000) {
                        try {
                            if(prepareEnemyFor(playerId)) {
                                LOG.debug("Two player's decision timers start if not started yet.");
                                autoDecisionTimer(playerId);
                                BigInteger enemyId = getEnemyId(playerId);
                                autoDecisionTimer(enemyId);
                            }
                        } catch (PlayerNotFoundException e) {
                            if (LOG.isDebugEnabled())
                                LOG.debug("Task cannot find a player with id = " + playerId);
                        }
                    }
                    return false;
                }
                
            });
        }
    }
    
    private class DecisionVisitor implements Visitor{
        BigInteger playerId;
        Object userName;
        
        public DecisionVisitor(BigInteger playerId) {
            this.playerId = playerId;
            this.userName = MDC.get("userName");
        }

        @Override
        public void visit() {
            MDC.put("userName", userName);
            
            LOG.debug("Reject attack by TIMEOUT Player_" + playerId);
            try {
                confirmAttack(playerId, false);
            } catch (PlayerNotFoundException | BattleStartException e) {
                LOG.warn("Timer could not do rejecting attack", e);
                return;
                
            } finally {
                MDC.remove("userName");
            }
        }
        
    }
}
