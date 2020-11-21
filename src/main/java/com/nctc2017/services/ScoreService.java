package com.nctc2017.services;

import com.nctc2017.dao.ScoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional
public class ScoreService {
    @Autowired
    private ScoreDao scoreDao;
    @Autowired
    private LevelUpService lvlUpService;

    private final double growthRate = 1.05;

    public int getScoreForDestroy(BigInteger playerId) {
        int lvl = lvlUpService.getCurrentLevel(playerId);
        return (int)Math.floor(scoreDao.getScoreForDestroy()*Math.pow(growthRate,lvl-1));
    }

    public int getScoreForBoarding(BigInteger playerId) {
        int lvl = lvlUpService.getCurrentLevel(playerId);
        return (int)Math.floor(scoreDao.getScoreForBoarding()*Math.pow(growthRate,lvl-1));
    }

    public int getScoreForSurrender(BigInteger playerId) {
        int lvl = lvlUpService.getCurrentLevel(playerId);
        return (int)Math.floor(scoreDao.getScoreForSurrender()*Math.pow(growthRate,lvl-1));
    }

    public int getScoreForPayoff() {
        return scoreDao.getScoreForPayoff();
    }

    public int getMaxLvl(){
        return scoreDao.getMaxLvl();
    }

    public int calculateScores(BigInteger playerWinId, BigInteger playerLoseId, int defaultScore){
        int winnerLvl = lvlUpService.getCurrentLevel(playerWinId);
        int loserLvl = lvlUpService.getCurrentLevel(playerLoseId);
        double xp;
        if(winnerLvl<=loserLvl){
           xp = defaultScore*(1+0.05*(loserLvl-winnerLvl));
        }
        else{
            xp = defaultScore*(1-(winnerLvl-loserLvl)/10);
        }
        return (int)Math.floor(xp);
    }
}
