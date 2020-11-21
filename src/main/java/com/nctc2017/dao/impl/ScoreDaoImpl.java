package com.nctc2017.dao.impl;

import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.ScoreDao;
import com.nctc2017.dao.utils.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("scoreDao")
public class ScoreDaoImpl implements ScoreDao{
    @Autowired
    private QueryExecutor queryExecutor;


    @Override
    public int getScoreForDestroy() {
        return queryExecutor.getAttrValue(DatabaseObject.SCORE_DESTROYING_ID, DatabaseAttribute.SCORE_NUM_ATTR_ID, Integer.class);

    }

    @Override
    public int getScoreForBoarding() {
        return queryExecutor.getAttrValue(DatabaseObject.SCORE_BOARDING_ID, DatabaseAttribute.SCORE_NUM_ATTR_ID, Integer.class);
    }

    @Override
    public int getScoreForSurrender() {
        return queryExecutor.getAttrValue(DatabaseObject.SCORE_SURRENDER_ID, DatabaseAttribute.SCORE_NUM_ATTR_ID, Integer.class);
    }

    @Override
    public int getScoreForPayoff() {
        return queryExecutor.getAttrValue(DatabaseObject.SCORE_PAYOFF_ID, DatabaseAttribute.SCORE_NUM_ATTR_ID, Integer.class);
    }

    @Override
    public int getMaxLvl() {
        return queryExecutor.getAttrValue(DatabaseObject.MAX_LVL, DatabaseAttribute.SCORE_NUM_ATTR_ID, Integer.class);
    }
}
