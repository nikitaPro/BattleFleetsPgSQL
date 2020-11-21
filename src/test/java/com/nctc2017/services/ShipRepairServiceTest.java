package com.nctc2017.services;

import com.nctc2017.bean.Mast;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.bean.ShipTemplate;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.dao.MastDao;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
public class ShipRepairServiceTest {

    private static List<Mast> masts = new ArrayList<>();
    private static ShipTemplate flyingDutchmanTemp;


    private static Ship flyingDutchman;

    private static Player steve;

    //@Resource(name="shipServicePrototype")
    //private ShipService shipService;
    @InjectMocks
    private ShipRepairService shipRepairService;

    @Autowired
    private ApplicationContext context;

    /*@Mock
    PlayerDao playerDao;

    @Mock
    ShipDao shipDao;

    @Mock
    MastDao mastDao;*/

    @Mock
    private ShipDao mockShipDao;

    @Mock
    private MastDao mockMastDao;



    @BeforeClass
    public static void createPlayerSteve() {
        BigInteger playerId = BigInteger.TEN;

        String login = "Steve";
        String email = "Rogers@gmail.com";
        int money = 11000;
        int points = 13;
        int lvl = 10;
        steve = new Player(playerId, login, email, money, points, lvl,5, 5, 5);
    }

    @BeforeClass
    public static void createMasts(){
        Mast mast1 = new Mast(1, BigInteger.ONE, "mast1", 100, 70, 50);
        Mast mast2 = new Mast(1, BigInteger.ZERO, "mast2", 100, 70, 50);
        masts.add(mast1);
        masts.add(mast2);
    }

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
        int curHealth = 70;
        int curSailorsQuantity = 80;
        int curCarryingLimit = 60;

        flyingDutchmanTemp = new ShipTemplate(shipTemplId,t_name, maxHealth, maxSailorsQuantity,
                cost, maxMastsQuantity, maxCannonQuantity, maxCarryingLimit);
        flyingDutchman = new Ship(flyingDutchmanTemp,shipId,curName,curHealth,
                curSailorsQuantity,curCarryingLimit);
    }

    @Before
    public void initMocks() {
        shipRepairService = (ShipRepairService) this.context.getBean("shipRepairService");
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(shipRepairService, "mastDao", mockMastDao);
        ReflectionTestUtils.setField(shipRepairService, "shipDao", mockShipDao);
        when(mockMastDao.getShipMastsFromShip(flyingDutchman.getShipId())).thenReturn(masts);
        when(mockShipDao.getShipCost(flyingDutchman.getShipId())).thenReturn(flyingDutchman.getCost());
        when(mockShipDao.getHealthLimit(flyingDutchman.getShipId())).thenReturn(flyingDutchmanTemp.getMaxHealth());
        when(mockShipDao.getCurrentShipHealth(flyingDutchman.getShipId())).thenReturn(flyingDutchman.getCurHealth());

    }


    @Test
    public void checkRepairCost() throws Exception {
        int repairCost = shipRepairService.countRepairCost(flyingDutchman.getShipId());
        assertEquals(69,repairCost);
    }




}
