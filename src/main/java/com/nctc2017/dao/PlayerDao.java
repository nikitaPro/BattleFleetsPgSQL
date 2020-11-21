package com.nctc2017.dao;

import java.math.BigInteger;
import java.util.*;

import com.nctc2017.bean.City;


import com.nctc2017.bean.Player;

public interface PlayerDao {

    String addNewPlayer(String login, String password, String email);

    Player findPlayerByLogin(String login);

    void updateLogin(BigInteger playerId, String login);

    void updateLevel(BigInteger playerId, int level);

    void updatePassword(BigInteger playerId, String password);

    void updateEmail(BigInteger playerId, String email);

    void updatePoints(BigInteger playerId, int points);

    void updateMoney(BigInteger playerId, int money);

    Player findPlayerById(BigInteger playerId);

    List<Player> findAllPlayers();

    int getPlayersCount();

    String getPlayerLogin(BigInteger playerId);

    String getPlayerPassword(BigInteger playerId);

    String getPlayerEmail(BigInteger playerId);

    int getPlayerMoney(BigInteger playerId);

    int getPlayerLevel(BigInteger playerId);

    int getPlayerPoints(BigInteger playerId);

    BigInteger getPlayerCity(BigInteger playerId);

    boolean isAccountEnabled(BigInteger playerId);

    void setAccountEnabled(BigInteger playerId);

    void addShip(BigInteger playerId, BigInteger shipId);

    void deleteShip(BigInteger playerId, BigInteger shipId);

    List<BigInteger> findAllShip(BigInteger playerId);

    void movePlayerToCity(BigInteger playerId, BigInteger cityId);

    String getPasswordByEmail(String email);

    int getFleetSpeed(BigInteger playerId);

    int getCurrentPassiveIncome(BigInteger playerId);

    int getCurrentMaxShips(BigInteger playerId);

    void updatePassiveIncome(BigInteger playerId, int passiveIncome);

    void updateMaxShips(BigInteger playerId, int addShips);

    int getNextPlayerLevel(BigInteger playerId);

    void updateNxtLvl(BigInteger playerId, int lvl);

    int getFasterShipSpeed(BigInteger playerId);


}