package com.nctc2017.services;

import com.nctc2017.bean.Player;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.PlayerDao;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
public class LevelUpServiceTest {
    private static Player steve;
    @Autowired
    private ApplicationContext context;
    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private LevelUpService levelUpService;

    @Mock
    private ScoreService score;

    @BeforeClass
    public static void createPlayerSteve() {
        BigInteger playerId = BigInteger.TEN;
        BigInteger cityId = BigInteger.valueOf(11);

        String login = "Steve";
        String email = "Rogers@gmail.com";
        int money = 150;
        int points = 200;
        int lvl = 10;
        steve = new Player(playerId, login, email, money, points, lvl,5,5,5);
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(playerDao.getPlayerLevel(steve.getPlayerId())).thenReturn(10).thenReturn(10).thenReturn(11);
        when(playerDao.getPlayerPoints(steve.getPlayerId())).thenReturn(200).thenReturn(61);
        when(playerDao.getCurrentPassiveIncome(steve.getPlayerId())).thenReturn(100).thenReturn(150);
        when(playerDao.getCurrentMaxShips(steve.getPlayerId())).thenReturn(3).thenReturn(4);
        when(score.getMaxLvl()).thenReturn(61);

        doNothing().when(playerDao).updatePoints(steve.getPlayerId(),200);
        doNothing().when(playerDao).updateLevel(steve.getPlayerId(),10);
        doNothing().when(playerDao).updatePassiveIncome(steve.getPlayerId(),200);
        doNothing().when(playerDao).updateMaxShips(steve.getPlayerId(),200);
    }


    @Test
    public void getCurrentLevel() throws Exception {
        int lvl = levelUpService.getCurrentLevel(steve.getPlayerId());
        assertEquals(lvl,steve.getLevel());
    }

    @Test
    public void levelUp() throws Exception {
        levelUpService.levelUp(steve.getPlayerId(),10);
        assertEquals(10,levelUpService.getCurrentLevel(steve.getPlayerId()));
    }

    @Test
    public void getCurrentPoints() throws Exception {
        int lvl = levelUpService.getCurrentPoints(steve.getPlayerId());
        assertEquals(lvl,steve.getPoints());
    }

    @Test
    public void PointsUp() throws Exception {
        levelUpService.pointsUp(steve.getPlayerId(),120);
        assertEquals(61,levelUpService.getCurrentPoints(steve.getPlayerId()));
    }

    @Test
    public void getPassiveIncome() throws Exception {
        int income = levelUpService.getPassiveIncome(steve.getPlayerId());
        assertEquals(income, 100);
    }

    @Test
    public void incomeUp() throws Exception {
        levelUpService.incomeUp(steve.getPlayerId());
        assertEquals(150,levelUpService.getPassiveIncome(steve.getPlayerId()));
    }

    @Test
    public void getMaxShips() throws Exception{
        int ships = levelUpService.getMaxShips(steve.getPlayerId());
        assertEquals(ships, 3);
    }

    @Test
    public void shipUp() throws Exception {
        levelUpService.shipUp(steve.getPlayerId());
        assertEquals(4,levelUpService.getMaxShips(steve.getPlayerId()));
    }

}
