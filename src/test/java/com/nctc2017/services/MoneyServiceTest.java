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
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
public class MoneyServiceTest {
    private static Player steve;
    
    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private MoneyService moneyService;

    @BeforeClass
    public static void createPlayerSteve() {
        BigInteger playerId = BigInteger.TEN;
        BigInteger cityId = BigInteger.valueOf(11);

        String login = "Steve";
        String email = "Rogers@gmail.com";
        int money = 150;
        int points = 13;
        int lvl = 10;
        steve = new Player(playerId, login, email, money, points, lvl, 5,5,5);
    }

    @Before
    public void initMocks() {

        when(playerDao.getPlayerMoney(steve.getPlayerId())).thenReturn(150);
    }

    @Test
    public void addMoney() throws Exception {
        int money = steve.getMoney();
        steve.setMoney(moneyService.addMoney(steve.getPlayerId(),50));
        assertEquals(money+50, steve.getMoney());
        steve.setMoney(150);
    }

    @Test
    public void deductMoney() throws Exception {
        int money = steve.getMoney();
        steve.setMoney(moneyService.deductMoney(steve.getPlayerId(),50));
        assertEquals(money-50, steve.getMoney());
    }


    @Test
    public void isEnoughMoney() throws Exception {
       boolean isEnoughMoney = moneyService.isEnoughMoney(steve.getPlayerId(),100);
       assertTrue(isEnoughMoney);
    }

    @Test
    public void isEnoughMoneyFail() throws Exception {
        boolean isEnoughMoney = moneyService.isEnoughMoney(steve.getPlayerId(),300);
        assertFalse(isEnoughMoney);
    }

    @Test
    public void getPlayersMoney() throws Exception {
        int money1 = steve.getMoney();
        int money2 = moneyService.getPlayersMoney(steve.getPlayerId());
        assertEquals(money1, money2);
    }

}