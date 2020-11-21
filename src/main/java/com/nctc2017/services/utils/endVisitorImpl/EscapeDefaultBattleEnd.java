package com.nctc2017.services.utils.endVisitorImpl;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.services.utils.BattleEndVisitor;

@Component
public class EscapeDefaultBattleEnd implements BattleEndVisitor {
    private static final Logger LOG = Logger.getLogger(EscapeDefaultBattleEnd.class);

    @Override
    public void endCaseVisit(PlayerDao playerDao, ShipDao shipDao, BigInteger winnerShipId, BigInteger loserShipId,
            BigInteger winnerId, BigInteger loserId) {

        LOG.debug("Escaped");
    }

}
