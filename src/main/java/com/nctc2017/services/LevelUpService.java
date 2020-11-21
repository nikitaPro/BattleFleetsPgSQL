package com.nctc2017.services;


import com.nctc2017.dao.PlayerDao;
import com.nctc2017.exception.UpdateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional
public class LevelUpService {
    private static Logger log = Logger.getLogger(LevelUpService.class);

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ScoreService scoreService;

    private static final int upPassiveIncome = 50;
    private static final int upMaxShips = 1;
    private static final int upNxtLvl = 5;
    private static final int factor = 100;
    private static final double ratio = 1.1;
    private static final int updatelvl = 1;
    private static final int zero = 0;


    public int getCurrentLevel(BigInteger playerId) {
        return playerDao.getPlayerLevel(playerId);
    }

    public void levelUp(BigInteger playerId,int level) {
        playerDao.updateLevel(playerId, level);
    }

    public int getCurrentPoints(BigInteger playerId) {
        return playerDao.getPlayerPoints(playerId);
    }

    public int getPointsToNxtLevel(BigInteger playerId){
        int curLvl = getCurrentLevel(playerId);
        double points = Math.floor(factor*Math.pow(ratio, curLvl-1));
        return (int)(points);
    }

    public void pointsUp(BigInteger playerId, int points) {
       double diff;
       int curLvl = getCurrentLevel(playerId);
       if(curLvl!=scoreService.getMaxLvl()) {
           int newPoints = points + getCurrentPoints(playerId);
           double maxCurPoints = Math.ceil(factor * Math.pow(ratio, curLvl - 1));
           if (newPoints < maxCurPoints) {
               playerDao.updatePoints(playerId, newPoints);
           } else {
               diff = newPoints - maxCurPoints;
               levelUp(playerId, curLvl + updatelvl);
               playerDao.updatePoints(playerId, zero);
               if (diff != 0) {
                   pointsUp(playerId, (int) diff);
               }
           }
       }
    }

    public int getPassiveIncome(BigInteger playerId){
        return playerDao.getCurrentPassiveIncome(playerId);
    }

    public void incomeUp(BigInteger playerId) throws UpdateException {
       int curPass = playerDao.getCurrentPassiveIncome(playerId);
       int lvl = getCurrentLevel(playerId);
       int next = getNextLevel(playerId);
       if(lvl>=next) {
           playerDao.updatePassiveIncome(playerId, curPass + upPassiveIncome);
       }
       else{
           UpdateException ex = new UpdateException("Level greater then next level update");
           log.error("Your current level should be greater or equal to level at which the update is possible",ex);
           throw ex;
       }
    }

    public int getMaxShips(BigInteger playerId){
        return playerDao.getCurrentMaxShips(playerId);
    }

    public void shipUp(BigInteger playerId) throws UpdateException {
        int curMaxShips = playerDao.getCurrentMaxShips(playerId);
        int lvl = getCurrentLevel(playerId);
        int next = getNextLevel(playerId);
        if(lvl>=next) {
            playerDao.updateMaxShips(playerId, curMaxShips + upMaxShips);
        }
        else{
            UpdateException ex = new UpdateException("Level greater then next level update");
            log.error("Your current level should be greater or equal to level at which the update is possible",ex);
            throw ex;
        }
    }

    public int getNextLevel(BigInteger playerId){
        return playerDao.getNextPlayerLevel(playerId);
    }

    public void updateNxtLvl(BigInteger playerId){
        playerDao.updateNxtLvl(playerId,getNextLevel(playerId)+upNxtLvl);
    }

    public String getLogin(BigInteger playerId){
        return playerDao.getPlayerLogin(playerId);
    }

}