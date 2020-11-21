package com.nctc2017.dao.impl;

import com.nctc2017.bean.*;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.*;
import com.nctc2017.services.ShipService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ApplicationConfig.class })
@Transactional
public class PlayerDaoImplTest {
    @Autowired
    PlayerDao playerDao;
    @Autowired
    CityDao cityDao;
    @Autowired
    ShipDao shipDao;
    @Autowired
    ShipService shipService;
    @Autowired
    HoldDao holdDao;
    @Autowired
    MastDao mastDao;
    @Autowired
    CannonDao cannonDao;

    @Test
    @Rollback(true)
    public void addNewPlayer() throws Exception {
        String succesResult = playerDao.addNewPlayer("qwe","1111","@FWF");
        assertNull(succesResult);
        String loginExist = playerDao.addNewPlayer("qwe","1111","@1234");
        assertEquals(loginExist, "Login exists, enter another login");
        String emailExist = playerDao.addNewPlayer("dvvdv","1111","@FWF");
        assertEquals(emailExist, "Email exists, enter another email");

    }

    @Test
    @Rollback
    public void getPlayerNextLevel()throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        int nxtLvl = playerDao.getNextPlayerLevel(player.getPlayerId());
        assertEquals(nxtLvl, player.getNextLevel());
    }

    @Test
    @Rollback
    public void updateNxtLvl()throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updateNxtLvl(player.getPlayerId(), 10);
        int nxtLvl = playerDao.getNextPlayerLevel(player.getPlayerId());
        assertEquals(nxtLvl, 10);
    }

    @Test
    @Rollback(true)
    public void findPlayerByLogin() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer1 = playerDao.findPlayerByLogin("Steve");
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",9000,0,1,5,5,5);
        assertEquals(topPlayer1.getLogin(), topPlayer2.getLogin());
        assertEquals(topPlayer1.getEmail(), topPlayer2.getEmail());
        assertEquals(topPlayer1.getLevel(), topPlayer2.getLevel());
        assertEquals(topPlayer1.getPoints(), topPlayer2.getPoints());
        assertEquals(topPlayer1.getMoney(), topPlayer2.getMoney());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void findPlayerByLoginNotExistPlayer() throws Exception {
        Player player = playerDao.findPlayerByLogin("Qwerty");
    }


    @Test
    @Rollback(true)
    public void updateLogin() throws Exception{
        String resultOfAdding = playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updateLogin(player.getPlayerId(),"Captain_America");
        String login = playerDao.findPlayerByLogin("Captain_America").getLogin();
        assertEquals("Captain_America",login);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updateLoginIncorrectId() throws Exception {
        playerDao.updateLogin(new BigInteger("100"),"Qwerty");
    }

    @Test
    @Rollback(true)
    public void updateLevel() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updateLevel(player.getPlayerId(),80);
        int level = playerDao.findPlayerByLogin("Steve").getLevel();
        assertEquals(80, level);
    }
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updateLevelIncorrectId() throws Exception {
        playerDao.updateLevel(new BigInteger("100"),80);
    }
    @Test
    @Rollback(true)
    public void updateEmail() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updateEmail(player.getPlayerId(),"80");
        String email = playerDao.findPlayerByLogin("Steve").getEmail();
        assertEquals("80", email);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updateEmailIncorrectId() throws Exception {
        playerDao.updateEmail(new BigInteger("100"),"80");
    }

    @Test
    @Rollback(true)
    public void updatePoints() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updatePoints(player.getPlayerId(),100);
        int points = playerDao.findPlayerByLogin("Steve").getPoints();
        assertEquals(100, points);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updatePointsIncorrectId() throws Exception {
        playerDao.updatePoints(new BigInteger("100"),80);
    }

    @Test
    @Rollback(true)
    public void updatePassword() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updatePassword(player.getPlayerId(),"qwerty");
        String password = playerDao.getPlayerPassword(player.getPlayerId());
        assertEquals("qwerty", password);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updatePasswordIncorrectId() throws Exception {
        playerDao.updatePassword(new BigInteger("100"),"80");
    }

    @Test
    @Rollback(true)
    public void updateMoney() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.updateMoney(player.getPlayerId(),300);
        int money = playerDao.findPlayerByLogin("Steve").getMoney();
        assertEquals(300, money);
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void updateMoneyIncorrectId() throws Exception {
        playerDao.updateMoney(new BigInteger("100"),80);
    }

    @Test
    @Rollback(true)
    public void updatePassiveIncome() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger playerId = player.getPlayerId();
        playerDao.updateLevel(playerId,5);
        playerDao.updatePassiveIncome(playerId,150);
        int money = playerDao.getCurrentPassiveIncome(playerId);
        assertEquals(150, money);
    }

    @Test
    @Rollback(true)
    public void updateMaxShips() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger playerId = player.getPlayerId();
        playerDao.updateLevel(playerId,5);
        playerDao.updateMaxShips(playerId,4);
        int ships = playerDao.getCurrentMaxShips(playerId);
        assertEquals(4, ships);
    }


    @Test
    @Rollback(true)
    public void findPlayerById() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        Player topPlayer1 = playerDao.findPlayerById(topPlayer.getPlayerId());
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",9000,0,1, 5,5,5);
        assertEquals(topPlayer2.getLogin(), topPlayer1.getLogin());
        assertEquals(topPlayer2.getEmail(), topPlayer1.getEmail());
        assertEquals(topPlayer2.getLevel(), topPlayer1.getLevel());
        assertEquals(topPlayer2.getPoints(), topPlayer1.getPoints());
        assertEquals(topPlayer2.getMoney(), topPlayer1.getMoney());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void findPlayerByIdNotExistPlayer() throws Exception {
        Player player = playerDao.findPlayerById(new BigInteger("53"));
    }


    @Test
    @Rollback(true)
    public void findAllPlayer() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        int j = 0;
        List<Player> players = playerDao.findAllPlayers();
        for(int i = 0; i < players.size(); i++)
        {
            if(players.get(i).getLogin().compareTo("Steve") == 0){
                j = i;
            }
        }
        Player topPlayer=players.get(j);
        assertEquals(topPlayer.getLogin(),"Steve");
        assertEquals(topPlayer.getEmail(),"Rogers@gmail.com");
        assertEquals(topPlayer.getLevel(),1);
        assertEquals(topPlayer.getPoints(),0);
        assertEquals(topPlayer.getMoney(),9000);
    }

    @Test
    @Rollback(true)
    @Ignore
    public void getCountPlayers() throws Exception{
        assertEquals(playerDao.getPlayersCount(),5);

    }

    @Test
    @Rollback(true)
    public void getPlayerLogin() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        String login = playerDao.getPlayerLogin(topPlayer.getPlayerId());
        Player topPlayer1 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",100,1,1, 5,5,5);
        assertEquals(login, topPlayer1.getLogin());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerLoginFailed() throws Exception{
        playerDao.getPlayerLogin(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerPassword() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        String password = playerDao.getPlayerPassword(topPlayer.getPlayerId());
        assertEquals(password,"1111");
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testGetPlayerPasswordFailed() throws Exception{
        playerDao.getPlayerPassword(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerEmail() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        String email = playerDao.getPlayerEmail(topPlayer.getPlayerId());
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",100,1,1, 5,5,5);
        assertEquals(email, topPlayer2.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerEmailFailed() throws Exception{
        playerDao.getPlayerEmail(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerMoney() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        int money = playerDao.getPlayerMoney(topPlayer.getPlayerId());
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",9000,1,1, 5,5,5);
        assertEquals(money, topPlayer2.getMoney());
    }
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerMoneyFailed() throws Exception{
        playerDao.getPlayerMoney(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerLevel() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        int lvl = playerDao.getPlayerLevel(topPlayer.getPlayerId());
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",100,0,1, 5,5,5);
        assertEquals(lvl, topPlayer2.getLevel());
    }
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerLevelFailed() throws Exception{
        playerDao.getPlayerLevel(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerPoints() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player topPlayer = playerDao.findPlayerByLogin("Steve");
        int points = playerDao.getPlayerPoints(topPlayer.getPlayerId());
        Player topPlayer2 = new Player(new BigInteger("1"),"Steve","Rogers@gmail.com",100,0,0, 5,5,5);
        assertEquals(points, topPlayer2.getPoints());
    }
    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerPointsFailed() throws Exception{
        playerDao.getPlayerPoints(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void getPlayerCity() throws Exception{
        playerDao.addNewPlayer("Steve", "1111", "Rogers@Gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger cityId = playerDao.getPlayerCity(player.getPlayerId());
        City city = cityDao.find(cityId);
        assertEquals(cityId, city.getCityId());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void getPlayerCityFailed() throws Exception{
        playerDao.getPlayerCity(new BigInteger("80"));
    }

    @Test
    @Rollback(true)
    public void addShip() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID,null);
        playerDao.addShip(player.getPlayerId(), shipId);
        List<BigInteger> ships = playerDao.findAllShip(player.getPlayerId());
        assertEquals(ships.get(0), shipId);

    }

    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void addShipWrongPlayerId() throws Exception{
        BigInteger shipId=shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID,null);
        playerDao.addShip(new BigInteger("100"), shipId);
    }

    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void addShipWrongShipId() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        playerDao.addShip(new BigInteger("100"), player.getPlayerId());
    }

    @Test
    @Rollback(true)
    public void deleteShip() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID,null);
        playerDao.addShip(player.getPlayerId(), shipId);
        playerDao.deleteShip(player.getPlayerId(), shipId);
        List<BigInteger> ships = playerDao.findAllShip(player.getPlayerId());
        assertEquals(0, ships.size());

    }

    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void deleteShipWrongPlayerId() throws Exception{
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, null);
        playerDao.deleteShip(new BigInteger("10"), shipId);
    }

    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void deleteShipWrongShipId() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID,null);
        playerDao.addShip(player.getPlayerId(), shipId);
        playerDao.deleteShip(new BigInteger("10"), player.getPlayerId());
    }

    @Test
    @Rollback(true)
    public void findAllShips() throws Exception{
        playerDao.addNewPlayer("Steve", "1111", "Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        shipDao.createNewShip(DatabaseObject.T_CARАССА_OBJECT_ID, player.getPlayerId());
        shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, player.getPlayerId());
        List<BigInteger> shipids = playerDao.findAllShip(player.getPlayerId());
        assertEquals(2, shipids.size());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void findAllShipsFailed() throws Exception{
        playerDao.findAllShip(new BigInteger("71"));
    }

    @Test
    @Rollback(true)
    public void movePlayerToCity() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        BigInteger playerId = playerDao.findPlayerByLogin("Steve").getPlayerId();
        BigInteger cityId = playerDao.getPlayerCity(playerId);
        if(cityId.intValue() > 69)
        {
            cityId = new BigInteger("69");
            playerDao.movePlayerToCity(playerId, cityId);
            assertEquals(playerDao.getPlayerCity(playerId).intValue(),69);
        }
        else{
            cityId=new BigInteger("73");
            playerDao.movePlayerToCity(playerId, cityId);
            assertEquals(playerDao.getPlayerCity(playerId).intValue(),73);

        }
    }
    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void movePlayerToCityFailed() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        playerDao.movePlayerToCity(playerDao.findPlayerByLogin("Steve").getPlayerId(), new BigInteger(Integer.toString(52)));

    }

    @Test
    @Rollback(true)
    public void getPasswordByEmail() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        Player player = playerDao.findPlayerByLogin("Steve");
        String password = playerDao.getPasswordByEmail(player.getEmail());
        assertEquals("1111", password);
    }

    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void getPasswordByEmailFailed() throws Exception{
        playerDao.getPasswordByEmail("qwerty");

    }

    @Test
    @Rollback(true)
    public void getCurrentPassiveIncome() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        BigInteger playerId = playerDao.findPlayerByLogin("Steve").getPlayerId();
        int pas_inc = playerDao.getCurrentPassiveIncome(playerId);
        assertEquals(100, pas_inc);
    }
    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void getCurrentPassiveIncomeFailed() throws Exception{
        playerDao.getCurrentPassiveIncome(new BigInteger("99"));
    }

    @Test
    @Rollback(true)
    public void getCurrentMaxShips() throws Exception{
        playerDao.addNewPlayer("Steve","1111","Rogers@gmail.com");
        BigInteger playerId = playerDao.findPlayerByLogin("Steve").getPlayerId();
        int ships = playerDao.getCurrentMaxShips(playerId);
        assertEquals(3, ships);
    }
    @Test(expected=IllegalArgumentException.class)
    @Rollback(true)
    public void getCurrentMaxShipsFailed() throws Exception{
        playerDao.getCurrentMaxShips(new BigInteger("99"));
    }
}
