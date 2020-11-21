package com.nctc2017.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLDataException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.nctc2017.constants.Query;
import com.nctc2017.dao.ExecutorDao;
import com.nctc2017.dao.utils.JdbcConverter;


@Repository
@Qualifier("executorDao")
public class ExecutorDaoImpl implements ExecutorDao {
    private static final Logger LOG = Logger.getLogger(ExecutorDaoImpl.class);
    private static final String CREATE_CANNON_FUNCTION_NAME = "CREATE_CANNON";
    private static final String BOARDING_OR_SURRENDER_RESULT_FUNCTION_NAME = "BOARDING_OR_SURRENDER_RESULT";
    private static final String DESTROYING_RESULT_FUNCTION_NAME = "DESTROYING_RESULT";
    private static final String MOVE_CARGO_TO_FUNCTION_NAME = "MOVE_CARGO_TO";
    private static final String CREATE_CANNON_PARAMETER_NAME = "ObjectIdTemplate";
    private static final String CALCULATE_DAMAGE_FUNCTION_NAME = "CALCULATE_DAMAGE";
    private static final String PLAYER_SHIP_ID = "playerShipId";
    private static final String ENEMY_SHIP_ID = "enemyShipId";
    private static final String DIMENSION = "dimension_";
    private static final String DISTANCE = "distance";
    private static final String IN_LIST = "in_l";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean ifThingBelongToPlayer(BigInteger id, BigInteger idPerson) {
        // TODO implement here
        return false;
    }

    @Override
    public void calculateDamage(int[][] ammoCannon, BigInteger playerShipId, BigInteger idEnemyShip, int dist) throws SQLException {

        StringBuilder arrToStr = new StringBuilder();
        for (int i = 0; i < ammoCannon.length; i++) {
            for (int j = 0; j < ammoCannon[i].length; j++) {
                arrToStr.append(ammoCannon[i][j]);
                arrToStr.append(",");
            }
        }
        String arrInStr = arrToStr.deleteCharAt(arrToStr.length() - 1).toString();
        
        /*SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate.getDataSource())
                .withProcedureName(CALCULATE_DAMAGE_FUNCTION_NAME);
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue(IN_LIST, arrInStr)
                .addValue(PLAYER_SHIP_ID, JdbcConverter.toNumber(playerShipId))
                .addValue(ENEMY_SHIP_ID, JdbcConverter.toNumber(idEnemyShip))
                .addValue(DIMENSION, ammoCannon.length)
                .addValue(DISTANCE, dist);*/

        try {
            jdbcTemplate.queryForObject(Query.getCallFunctionQuery(CALCULATE_DAMAGE_FUNCTION_NAME, 5),  
                    new Object[]{ arrInStr.toString(), playerShipId, 
                            idEnemyShip, ammoCannon.length, dist}, Integer.class);
            //call.execute(in);
        } catch (UncategorizedSQLException e) {
            LOG.error("Mistake on client side, may be incorrect ratio of ammunition to cannons "
                    + "or ammunition to quantity in hold", e);
            throw e.getSQLException();
        }

    }

    @Override
    public BigInteger boarding(BigInteger idMyShip, BigInteger idEnemyShip) {
        // TODO implement here
        return null;
    }

    @Override
    public String moveCargoTo(BigInteger cargoId, BigInteger destinationId, int quantity) {
        /*SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withFunctionName(MOVE_CARGO_TO_FUNCTION_NAME);
        String result = call.executeFunction(String.class, cargoId, destinationId, quantity);*/
        String result=jdbcTemplate.queryForObject(Query.getCallFunctionQuery(MOVE_CARGO_TO_FUNCTION_NAME, 3), String.class, 
                new Object[] {cargoId, destinationId, quantity});
        if(result.endsWith("successfully!")) return result;
        else {
            IllegalArgumentException e = new IllegalArgumentException(result);
            LOG.error("Exception while moving cargo " + e);
            throw e;
        }
    }

    @Override
    public String moveCargoToWinnerBoardingOSurrender(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException {
        try {
            /*SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withFunctionName(BOARDING_OR_SURRENDER_RESULT_FUNCTION_NAME);
            String result = call.executeFunction(String.class, shipWinnerId, shipLoserId);*/
            String result=jdbcTemplate.queryForObject(Query.getCallFunctionQuery(BOARDING_OR_SURRENDER_RESULT_FUNCTION_NAME, 2), String.class, 
                    new Object[] {shipWinnerId, shipLoserId});
            return result;
        } catch (UncategorizedSQLException e) {
            LOG.error("Mistake on client side, may be you are trying to transfer goods not between ships", e);
            throw e.getSQLException();
        }
    }

    @Override
    public String moveCargoToWinnerDestroying(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException {
        try {
            /*SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withFunctionName(DESTROYING_RESULT_FUNCTION_NAME);
            String result = call.executeFunction(String.class, shipWinnerId, shipLoserId);*/
            String result=jdbcTemplate.queryForObject(Query.getCallFunctionQuery(DESTROYING_RESULT_FUNCTION_NAME, 2), String.class, 
                    new Object[] {shipWinnerId, shipLoserId});
            return result;
        } catch (UncategorizedSQLException e) {
            LOG.error("may be you are trying to transfer goods not between ships source ship: " + shipWinnerId + " target: " + shipLoserId, e);
            throw e.getSQLException();
        }

    }

    @Override
    public BigInteger createCannon(BigInteger templateId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withFunctionName(CREATE_CANNON_FUNCTION_NAME);
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue(CREATE_CANNON_PARAMETER_NAME,
                        JdbcConverter.toNumber(templateId));
        BigDecimal newCannonId = call.executeFunction(BigDecimal.class, in);
        if (newCannonId != null)
            return newCannonId.toBigInteger();
        return null;
    }

}