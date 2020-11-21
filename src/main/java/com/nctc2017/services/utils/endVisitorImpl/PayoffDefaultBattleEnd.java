package com.nctc2017.services.utils.endVisitorImpl;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.services.LevelUpService;
import com.nctc2017.services.ScoreService;
import com.nctc2017.services.utils.BattleEndVisitor;

@Component
public class PayoffDefaultBattleEnd implements BattleEndVisitor {
    private static final Logger LOG = Logger.getLogger(PayoffDefaultBattleEnd.class);
    @Autowired
    private LevelUpService levelUp;
    @Autowired 
    private ScoreService score;

    @Override
    public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, BigInteger loserShipId,
            BigInteger winnerId, BigInteger loserId) {
        LOG.debug("Pass money to winner Player_" + winnerId);
        
        int points = score.getScoreForPayoff();
        points = score.calculateScores(winnerId, loserId, points);
        levelUp.pointsUp(winnerId, points);
    }

}
