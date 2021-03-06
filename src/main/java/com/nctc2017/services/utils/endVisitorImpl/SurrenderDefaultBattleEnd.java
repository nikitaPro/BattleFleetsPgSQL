package com.nctc2017.services.utils.endVisitorImpl;

import java.math.BigInteger;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.services.BattleEndingService;
import com.nctc2017.services.LevelUpService;
import com.nctc2017.services.ScoreService;
import com.nctc2017.services.utils.BattleEndVisitor;

@Component
public class SurrenderDefaultBattleEnd implements BattleEndVisitor {
    private static final Logger LOG = Logger.getLogger(SurrenderDefaultBattleEnd.class);
    @Autowired
    private BattleEndingService battleEndServ;
    @Autowired
    private LevelUpService levelUp;
    @Autowired 
    private ScoreService score;
    
    @Override
    public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, BigInteger loserShipId,
            BigInteger winnerId, BigInteger loserId) {
        
        LOG.debug("Pass goods to winner Player_" + winnerId + " ship because surrendered");
        try {
            battleEndServ.passSurrenderGoodsToWinner(winnerShipId, loserShipId);
        } catch (SQLException e) {
            LOG.fatal("Unexpected error when passing goods at the end of battle ", e);
            return;
        }
        
        int points = score.getScoreForSurrender(winnerId);
        points = score.calculateScores(winnerId, loserId, points);
        levelUp.pointsUp(winnerId, points);
    }

}
