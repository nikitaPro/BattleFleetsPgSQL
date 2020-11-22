package com.nctc2017.dao.impl;

import com.nctc2017.bean.Player;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.PlayerDao;

import com.nctc2017.dao.ShipDao;
import com.nctc2017.dao.extractors.EntityExtractor;
import com.nctc2017.dao.extractors.EntityListExtractor;
import com.nctc2017.dao.extractors.ExtractingVisitor;
import com.nctc2017.dao.utils.JdbcConverter;
import com.nctc2017.dao.utils.QueryBuilder;
import com.nctc2017.dao.utils.QueryExecutor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;


@Repository
@Qualifier("playerDao")
public class PlayerDaoImpl implements PlayerDao{
    public static final String createPlayerFunctionName = "CREATE_PLAYER";
    public static final String queryForPlayerAttributesByLogin ="SELECT OBJECT_ID FROM OBJECTS WHERE OBJECT_TYPE_ID=? AND NAME=?";
    public static final String queryForPasswordByEmail = "SELECT pass.VALUE FROM ATTRIBUTES_VALUE pass, ATTRIBUTES_VALUE email " +
            "WHERE email.VALUE=? " +
            "AND pass.ATTR_ID=? " +
            "AND email.OBJECT_ID=pass.OBJECT_ID";
    private static Logger log = Logger.getLogger(PlayerDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QueryExecutor queryExecutor;
    @Autowired
    private ShipDao shipDao;

    @Override
    public String addNewPlayer(@NotNull String login,@NotNull String password,@NotNull String email) {
        /*SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withFunctionName(createPlayerFunctionName);
        String result = call.executeFunction(String.class,login,password,email);*/
        String result=jdbcTemplate.queryForObject(Query.getCallFunctionQuery(createPlayerFunctionName, 3), String.class, 
                new Object[] {login, password, email});
        return result;
    }

    @Override
    public Player findPlayerByLogin(@NotNull String login) {
        try {
            BigInteger playerId = jdbcTemplate.queryForObject(queryForPlayerAttributesByLogin, BigInteger.class,
                    DatabaseObject.PLAYER_OBJTYPE_ID.longValueExact(),
                    login);
            return findPlayerById(playerId);
        }
        catch (Exception e){
            RuntimeException ex = new IllegalArgumentException("Wrong player login = " + login);
            log.error("PlayerDAO Exception while find by login.", e);
            throw ex;
        }
    }

    @Override
    public void updateLogin(@NotNull BigInteger playerId,@NotNull String login) {
            findPlayerById(playerId);
            jdbcTemplate.update("UPDATE OBJECTS SET NAME =? WHERE OBJECT_ID=?", login, JdbcConverter.toNumber(playerId));
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.LOGIN_ATR_ID, login)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updateLevel(@NotNull BigInteger playerId,@NotNull int level) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.LEVEL_ATR_ID, level)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updatePassword(@NotNull BigInteger playerId,@NotNull String password) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.PASSWORD_ATR_ID, password)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updateEmail(@NotNull BigInteger playerId,@NotNull String email) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.EMAIL_ATR_ID, email)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updatePoints(@NotNull BigInteger playerId,@NotNull int points) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.POINTS_ATR_ID, points)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updateMoney(@NotNull BigInteger playerId,@NotNull int money) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.MONEY_ATR_ID, money)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void updatePassiveIncome(@NotNull BigInteger playerId,@NotNull int passiveIncome) {
            PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                    .setAttribute(DatabaseAttribute.PASSIVE_INCOME_ATR_ID, passiveIncome)
                    .build();
            jdbcTemplate.update(psc);
    }

    @Override
    public void updateMaxShips(@NotNull BigInteger playerId,@NotNull int maxShips) {
            PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                    .setAttribute(DatabaseAttribute.MAX_SHIPS_ATR_ID, maxShips)
                    .build();
            jdbcTemplate.update(psc);
    }

    @Override
    public Player findPlayerById(@NotNull BigInteger playerId) {
        Player player = queryExecutor.findEntity(playerId,DatabaseObject.PLAYER_OBJTYPE_ID,
                new EntityExtractor<>(playerId, new PlayerVisitor()));
        if (player == null){
            RuntimeException ex = new IllegalArgumentException("Wrong player object id = " + playerId);
            log.error("PlayerDAO Exception while find by id.", ex);
            throw ex;
        }
        return player;
    }



    @Override
    public List<Player> findAllPlayers() {
        List<Player> players = queryExecutor.getAllEntitiesByType(DatabaseObject.PLAYER_OBJTYPE_ID,
                new EntityListExtractor<>( new PlayerVisitor()));
        return players;   
    }

    @Override
    public int getPlayersCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(object_id) FROM objects WHERE object_type_id = ?",
                new Object[] {JdbcConverter.toNumber(DatabaseObject.PLAYER_OBJTYPE_ID)}, Integer.class);
    }

    @Override
    public String getPlayerLogin(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.LOGIN_ATR_ID, String.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player login.", ex);
            throw ex;
        }
    }

    @Override
    public String getPlayerPassword(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.PASSWORD_ATR_ID, String.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player password.", ex);
            throw ex;
        }
    }


    @Override
    public String getPlayerEmail(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.EMAIL_ATR_ID, String.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player email.", ex);
            throw ex;
        }

    }

    @Override
    public int getPlayerMoney(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.MONEY_ATR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player money.", ex);
            throw ex;
        }
    }

    @Override
    public int getPlayerLevel(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.LEVEL_ATR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player level.", ex);
            throw ex;
        }

    }

    @Override
    public int getPlayerPoints(@NotNull BigInteger playerId) {
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.POINTS_ATR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player points.", ex);
            throw ex;
        }
    }

    @Override
    public BigInteger getPlayerCity(@NotNull BigInteger playerId) {
        try {
            return jdbcTemplate.queryForObject("SELECT PARENT_ID FROM OBJECTS WHERE OBJECT_ID=? AND OBJECT_TYPE_ID=?",
                    new Object[]{JdbcConverter.toNumber(playerId),
                            JdbcConverter.toNumber(DatabaseObject.PLAYER_OBJTYPE_ID)}, BigInteger.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player city.", ex);
            throw ex;
        }
    }

    @Override
    public boolean isAccountEnabled(BigInteger playerId) {
        try {
            Integer isEnabled = queryExecutor.getAttrValue(playerId,
                    DatabaseAttribute.ENABLED_USER_ACC,
                    Integer.class);
            return isEnabled == 1;
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid player id = " + playerId, e);
            log.error("PlayerDAO Exception while getting player account status.", ex);
            throw ex;
        }
    }

    @Override
    public void setAccountEnabled(BigInteger playerId){
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.ENABLED_USER_ACC, 1)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void addShip(@NotNull BigInteger playerId,@NotNull BigInteger shipId) {
        findPlayerById(playerId);
        shipDao.findShip(shipId);
        PreparedStatementCreator psc = QueryBuilder.updateParent(shipId, playerId).build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void deleteShip(@NotNull BigInteger playerId,@NotNull BigInteger shipId) {
        findPlayerById(playerId);
        shipDao.findShip(shipId);
        jdbcTemplate.update("UPDATE OBJECTS SET PARENT_ID=? WHERE OBJECT_ID=? AND PARENT_ID=?",
                null, JdbcConverter.toNumber(shipId), JdbcConverter.toNumber(playerId));
    }

    @Override
    public List<BigInteger> findAllShip(@NotNull BigInteger playerId) {
        findPlayerById(playerId);
        List<BigInteger> ships = jdbcTemplate.queryForList("SELECT OBJECT_ID FROM OBJECTS WHERE PARENT_ID=? AND OBJECT_TYPE_ID=?",
                BigInteger.class, JdbcConverter.toNumber(playerId),
                JdbcConverter.toNumber(DatabaseObject.SHIP_OBJTYPE_ID));
        return ships;
    }
    
    @Override
    public void movePlayerToCity(@NotNull BigInteger playerId, @NotNull BigInteger cityId) {
        int res = queryExecutor.putEntityToContainer(cityId, playerId, DatabaseObject.CITY_OBJTYPE_ID);
        if (res != 1) {
            RuntimeException ex = new IllegalArgumentException("Wrong city object id = " + cityId);
            log.log(Level.ERROR, "PlayerDAO Exception while moving player with id = " + playerId, ex);
            throw ex;
        }
    }

    @Override
    public String getPasswordByEmail(@NotNull String email){
        try {
            return jdbcTemplate.queryForObject(queryForPasswordByEmail,
                    new Object[]{email, JdbcConverter.toNumber(DatabaseAttribute.PASSWORD_ATR_ID)},
                    String.class);
        } catch (Exception e) {
            RuntimeException ex = new IllegalArgumentException("Invalid email = " + email, e);
            log.error("PlayerDAO Exception while getting player password.", ex);
            throw ex;
        }

    }
    
    @Override
    public int getFleetSpeed(BigInteger playerId) {
        Integer speed = jdbcTemplate.queryForObject(Query.GET_FLEET_SPEED,
                new Object[]{JdbcConverter.toNumber(playerId),
                        JdbcConverter.toNumber(DatabaseObject.MAST_OBJTYPE_ID),
                        JdbcConverter.toNumber(DatabaseAttribute.ATTR_CURR_MAST_SPEED_ID)},
                Integer.class);
        if (speed == null) return 0;
        return speed;
    }
    
    @Override
    public int getFasterShipSpeed(BigInteger playerId) {
        Integer speed = jdbcTemplate.queryForObject(Query.GET_HIGHEST_SPEED,
                new Object[]{JdbcConverter.toNumber(playerId),
                        JdbcConverter.toNumber(DatabaseObject.MAST_OBJTYPE_ID),
                        JdbcConverter.toNumber(DatabaseAttribute.ATTR_CURR_MAST_SPEED_ID)},
                Integer.class);
        if (speed == null) return 0;
        return speed;
    }

    @Override
    public int getCurrentPassiveIncome(@NotNull BigInteger playerId){
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.PASSIVE_INCOME_ATR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player passive income.", ex);
            throw ex;
        }
    }

    @Override
    public int getCurrentMaxShips(@NotNull BigInteger playerId){
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.MAX_SHIPS_ATR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player max ships.", ex);
            throw ex;
        }
    }

    @Override
    public int getNextPlayerLevel(BigInteger playerId){
        try {
            return queryExecutor.getAttrValue(playerId, DatabaseAttribute.NEXT_LVL_ATTR_ID, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid playerId = " + playerId, e);
            log.error("PlayerDAO Exception while getting player nextLevel.", ex);
            throw ex;
        }
    }

    @Override
    public void updateNxtLvl(BigInteger playerId, int lvl) {
        findPlayerById(playerId);
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(playerId)
                .setAttribute(DatabaseAttribute.NEXT_LVL_ATTR_ID, lvl)
                .build();
        jdbcTemplate.update(psc);
    }

    private final class PlayerVisitor implements ExtractingVisitor<Player> {

        @Override
        public Player visit(BigInteger entityId, Map<String, String> papamMap) {
            return new Player(entityId,
                    papamMap.get(Player.LOGIN),
                    papamMap.get(Player.EMAIL),
                    Integer.valueOf(papamMap.get(Player.MONEY)),
                    Integer.valueOf(papamMap.get(Player.POINTS)),
                    Integer.valueOf(papamMap.get(Player.LEVEL)),
                    Integer.valueOf(papamMap.get(Player.NEXT_LEVEL)),
                    Integer.valueOf(papamMap.get(Player.INCOME)),
                    Integer.valueOf(papamMap.get(Player.MAX_SHIPS)));
        }
    }
}
