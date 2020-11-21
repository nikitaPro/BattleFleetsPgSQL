package com.nctc2017.services;

import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.bean.ShipTemplate;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.MastDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

;
;

@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
public class ShipTradeServiceTest {

    @Mock
    private ShipDao shipDao;
    @Mock
    private MastDao mastDao;

    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private ShipTradeService shipTradeService;
    @InjectMocks
    private MoneyService moneyService;
    @Mock
    private ShipRepairService shipRepairService;
    @Mock
    private LevelUpService levelUpService;
    @Mock
    private ShipService shipService;

    private static Player steve;
    private static ShipTemplate t_BlackPerl;
    private static Ship blackPerl;
    private static int money;


    @BeforeClass
    public static void createPlayerSteve() {
        BigInteger playerId = BigInteger.TEN;
        BigInteger cityId = BigInteger.valueOf(11);

        String login = "Steve";
        String email = "Rogers@gmail.com";
        int money = 1150;
        int points = 13;
        int lvl = 10;
        steve = new Player(playerId, login, email, money, points, lvl,5,5,5);
    }


    @BeforeClass
    public static void createShipTemplate() {
        String t_name = "Full-rigged ship";
        BigInteger shipTemplId = BigInteger.ONE;
        int maxHealth = 100;
        int maxSailorsQuantity = 100;
        int cost = 300;
        int maxMastsQuantity = 5;
        int maxCannonQuantity = 30;
        int maxCarryingLimit = 90;
        int curSailorsQuantity = 80;
        int curCarryingLimit = 60;

        BigInteger shipId = new BigInteger("2");
        String curName = "Black Perl";
        int curHealth = 80;

        t_BlackPerl = new ShipTemplate(shipTemplId, t_name, maxHealth, maxSailorsQuantity,
                cost, maxMastsQuantity, maxCannonQuantity, maxCarryingLimit);
        blackPerl = new Ship(t_BlackPerl, shipId, curName, curHealth, curSailorsQuantity, curCarryingLimit);
    }

    @Before
    public void initMocks() {
        shipTradeService.moneyService = moneyService;
        ArrayList<BigInteger> allships = new ArrayList<BigInteger>();
        allships.add(blackPerl.getShipId());
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                steve.setMoney((int) args[1]);
                return null;
            }
        }).when(playerDao).updateMoney(any(), anyInt());

        when(levelUpService.getMaxShips(steve.getPlayerId())).thenReturn(10);
        when(shipService.createNewShip(any(), any())).thenReturn(new BigInteger("1"));
        when(shipDao.findShipTemplate(t_BlackPerl.getTemplateId())).thenReturn(t_BlackPerl);
        when(playerDao.getPlayerMoney(steve.getPlayerId())).thenReturn(steve.getMoney());
        when(playerDao.findAllShip(steve.getPlayerId())).thenReturn(allships);
    }

    @Test
    public void buyShipTest() throws Exception {
        money = steve.getMoney();
        BigInteger shipTempId = t_BlackPerl.getTemplateId();
        shipTradeService.buyShip(steve.getPlayerId(), shipTempId);
        assertEquals(money - t_BlackPerl.getCost(), steve.getMoney());
    }

    @Test
    public void soldShipTest() throws Exception {
        money = steve.getMoney();
        playerDao.addShip(steve.getPlayerId(), blackPerl.getShipId());

        assertEquals("You can not sell single ship!",shipTradeService.sellShip(steve.getPlayerId(), blackPerl.getShipId()));
        assertEquals(money , steve.getMoney());
    }

    @Test
    public void getShipCosts() throws Exception {
        shipTradeService.getShipsCost(null);
        playerDao.addShip(steve.getPlayerId(), blackPerl.getShipId());

        assertEquals("You can not sell single ship!",shipTradeService.sellShip(steve.getPlayerId(), blackPerl.getShipId()));
    }
}
