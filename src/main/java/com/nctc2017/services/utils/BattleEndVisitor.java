package com.nctc2017.services.utils;

import java.math.BigInteger;
import java.sql.SQLException;

import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;

public interface BattleEndVisitor {
    
    public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, 
            BigInteger loserShipId, BigInteger winnerId, BigInteger loserId );
}
