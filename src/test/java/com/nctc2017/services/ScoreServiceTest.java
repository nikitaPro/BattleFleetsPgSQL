package com.nctc2017.services;

import com.nctc2017.bean.Player;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.PlayerDao;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
@Rollback(true)
@FixMethodOrder
public class ScoreServiceTest {
    private static Player steve;
    private static Player tony;
    private static Player redSkull;


    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private LevelUpService lvlUpService;


    private Player newPlayer(String login, String email) {
        playerDao.addNewPlayer(login, "123", email);
        Player player = playerDao.findPlayerByLogin(login);
        return player;
    }
    @Before
    public void createPlayerSteve() {
        steve = newPlayer("steve","1111");
        playerDao.updateLevel(steve.getPlayerId(), 7);
    }

    @Before
    public void createPlayerTony() {
        tony = newPlayer("tony","2222");
        playerDao.updateLevel(tony.getPlayerId(), 10);
    }

    @Before
    public void createPlayerRedSkull() {
        redSkull = newPlayer("redSkull","3333");
        playerDao.updateLevel(redSkull.getPlayerId(), 5);
    }

    @Test
    @Rollback
    public void calculateScoreWinLesLos() throws Exception {
       calculateScore(steve.getPlayerId(), tony.getPlayerId(), 30);
    }

    @Test
    @Rollback
    public void calculateScoreWinMoreLos() throws Exception {
        calculateScore(steve.getPlayerId(), tony.getPlayerId(), 30);
    }

    private void calculateScore(BigInteger playerWinId, BigInteger playerLoseId, int defaultScore){
        int winnerLvl = lvlUpService.getCurrentLevel(playerWinId);
        int loserLvl = lvlUpService.getCurrentLevel(playerLoseId);
        double xp;
        if(winnerLvl<=loserLvl)
        {
            xp = defaultScore*(1+0.05*(loserLvl-winnerLvl));
            assertEquals((int)xp, 34);
        }
        else
        {
            xp = defaultScore*(1-(winnerLvl-loserLvl)/10);
            assertEquals((int)xp, 24);
        }
    }


}
