package com.nctc2017.dao.impl;

import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.ScoreDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
public class ScoreDaoImplTest {
    @Autowired
    private ScoreDao scoreDao;

    @Test
    @Rollback
    public void getScoreForDestroy() throws Exception {
        assertEquals(scoreDao.getScoreForDestroy(),30);
    }

    @Test
    @Rollback
    public void getScoreForBoarding() throws Exception {
        assertEquals(scoreDao.getScoreForBoarding(),40);
    }

    @Test
    @Rollback
    public void getScoreForSurrender() throws Exception {
        assertEquals(scoreDao.getScoreForSurrender(),20);
    }

    @Test
    @Rollback
    public void getScoreForPayoff() throws Exception {
        assertEquals(scoreDao.getScoreForPayoff(),0);
    }

    @Test
    @Rollback
    public void getMaxLvl() throws Exception {
        assertEquals(scoreDao.getMaxLvl(),100);
    }
}