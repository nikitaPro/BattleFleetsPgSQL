package com.nctc2017.services;

import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.bean.ShipTemplate;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
public class ShipServiceTest {

    private static ShipTemplate flyingDutchmanTemp;
    private static ShipTemplate blackPerlTemp;
    private static ShipTemplate queenAnneRevengeTemp;
    private static List<ShipTemplate> shipTemplates = new ArrayList<>();


    private static Ship flyingDutchman;
    private static Ship blackPerl;
    private static Ship queenAnneRevenge;
    private static List<Ship> ships = new ArrayList<>();
    private static List<BigInteger> shipsId = new ArrayList<>();

    private static Player steve;

    @Mock
    private ShipDao shipDao;

    @Mock
    private PlayerDao playerDao;

    @InjectMocks
    private ShipService shipService;

    @BeforeClass
    public static void createShipFlyingDutchman(){
        BigInteger shipTemplId = BigInteger.ZERO;
         String t_name = "Ghost ship";
         int maxHealth = 100;
         int maxSailorsQuantity = 100;
         int cost = 300;
         int maxMastsQuantity = 5;
         int maxCannonQuantity = 30;
         int maxCarryingLimit = 90;
         BigInteger shipId = new BigInteger("1");
         String curName = "Flying Dutchman";
         int curHealth = 80;
         int curSailorsQuantity = 80;
         int curCarryingLimit = 60;

         flyingDutchmanTemp = new ShipTemplate(shipTemplId,t_name, maxHealth, maxSailorsQuantity,
                cost, maxMastsQuantity, maxCannonQuantity, maxCarryingLimit);
        shipTemplates.add(flyingDutchmanTemp);
        flyingDutchman = new Ship(flyingDutchmanTemp,shipId,curName,curHealth,
                 curSailorsQuantity,curCarryingLimit);
        ships.add(flyingDutchman);
        shipsId.add(flyingDutchman.getShipId());
    }

    @BeforeClass
    public static void createShipBlackPerl(){
        BigInteger shipTemplId = BigInteger.TEN;
        String t_name = "Fregata";
        int maxHealth = 100;
        int maxSailorsQuantity = 100;
        int cost = 300;
        int maxMastsQuantity = 5;
        int maxCannonQuantity = 30;
        int maxCarryingLimit = 90;
        BigInteger shipId = new BigInteger("2");
        String curName = "Black Perl";
        int curHealth = 80;
        int curSailorsQuantity = 80;
        int curCarryingLimit = 60;

        blackPerlTemp = new ShipTemplate(shipTemplId,t_name, maxHealth, maxSailorsQuantity,
                cost, maxMastsQuantity, maxCannonQuantity, maxCarryingLimit);
        shipTemplates.add(blackPerlTemp);
        blackPerl = new Ship(blackPerlTemp, shipId, curName, curHealth,
                curSailorsQuantity, curCarryingLimit);
        ships.add(blackPerl);
        shipsId.add(blackPerl.getShipId());
    }

    @BeforeClass
    public static void createShipTemplateQueenAnneRevenge(){
        BigInteger shipTemplId = BigInteger.ONE;
        String t_name = "Full-rigged ship";
        int maxHealth = 100;
        int maxSailorsQuantity = 100;
        int cost = 300;
        int maxMastsQuantity = 5;
        int maxCannonQuantity = 30;
        int maxCarryingLimit = 90;
        BigInteger shipId = new BigInteger("3");
        String curName = "Quenn Anne's Revenge";
        int curHealth = 80;
        int curSailorsQuantity = 80;
        int curCarryingLimit = 60;

        queenAnneRevengeTemp = new ShipTemplate(shipTemplId,t_name, maxHealth, maxSailorsQuantity,
                cost, maxMastsQuantity, maxCannonQuantity, maxCarryingLimit);
        shipTemplates.add(queenAnneRevengeTemp);
        queenAnneRevenge = new Ship(queenAnneRevengeTemp, shipId, curName, curHealth,
                curSailorsQuantity, curCarryingLimit);
        ships.add(queenAnneRevenge);
        shipsId.add(queenAnneRevenge.getShipId());
    }

    @BeforeClass
    public static void createPlayerSteve() {
        BigInteger playerId = BigInteger.TEN;
        BigInteger cityId = BigInteger.valueOf(11);

        String login = "Steve";
        String email = "Rogers@gmail.com";
        int money = 150;
        int points = 13;
        int lvl = 10;
        steve = new Player(playerId, login, email, money, points, lvl,5,5,5);
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(shipDao.findAllShipTemplates()).thenReturn(shipTemplates);
        when(playerDao.findAllShip(steve.getPlayerId())).thenReturn(shipsId);
        when(shipDao.findAllShips(shipsId)).thenReturn(ships);


    }

    @Test
    public void getAllShipTemplates() throws Exception {
        List<ShipTemplate> shipTemplates = shipService.getAllShipTemplates();
        assertEquals(shipTemplates.get(2), blackPerlTemp);
        assertEquals(shipTemplates.get(1), queenAnneRevengeTemp);
        assertEquals(shipTemplates.get(0), flyingDutchmanTemp);
    }

    @Test
    public void getAllPlayerShips() throws Exception {
        List<Ship> ships = shipService.getAllPlayerShips(steve.getPlayerId());
        assertEquals(ships.get(0), blackPerl);
        assertEquals(ships.get(1), queenAnneRevenge);
        assertEquals(ships.get(2), flyingDutchman);
    }

}