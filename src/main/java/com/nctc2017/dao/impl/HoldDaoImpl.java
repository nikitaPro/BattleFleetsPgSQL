package com.nctc2017.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.HoldDao;
import com.nctc2017.dao.utils.JdbcConverter;
import com.nctc2017.dao.utils.QueryExecutor;

@Repository
@Qualifier("holdDao")
public class HoldDaoImpl implements HoldDao {

    private static Logger log = Logger.getLogger(HoldDaoImpl.class);
    private static String exceptionMessage = "HoldDAO Exception while adding cargo in hold";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QueryExecutor queryExecutor; 

    @Override
    public BigInteger findHold(BigInteger shipId) {
        try {
            BigInteger holdId = queryExecutor.findContainerByOwnerId(DatabaseObject.HOLD_OBJTYPE_ID, shipId, DatabaseObject.SHIP_OBJTYPE_ID);
            return holdId;
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Wrong ship object id to find Hold. Id = " + shipId);
            log.log(Level.ERROR, "HoldDAO Exception while finding hold by ship id.", ex);
            throw ex;
        }
    }

    @Override
    public int getOccupiedVolume(BigInteger shipId) {
        List<BigInteger> entitiesId = 
                queryExecutor.findAllEntitiesInContainerByOwnerId(DatabaseObject.HOLD_OBJTYPE_ID, 
                        shipId, 
                        DatabaseObject.SHIP_OBJTYPE_ID);
        //if (entitiesId == null) throwRuntimeException(new IllegalArgumentException("Wrong ship object id to find Hold. Id = " + shipId));
        Integer totalQuantityOfGoodsAndAmmo = 
                jdbcTemplate.queryForObject(Query.GET_OCUPATED_VOLUME_GOODS_AMMO, 
                        new Object[]{JdbcConverter.toNumber(shipId), 
                                JdbcConverter.toNumber(DatabaseObject.HOLD_OBJTYPE_ID)}, 
                        Integer.class); 
        return entitiesId.size() + totalQuantityOfGoodsAndAmmo;
    }

    @Override
    public BigInteger createHold() {        
        return createHold(null);
    }
    
    @Override
    
    public BigInteger createHold(BigInteger shipId) {   
        BigDecimal newId = jdbcTemplate.queryForObject(Query.GET_NEXTVAL,BigDecimal.class);
        
        int rowsAffected = jdbcTemplate.update(Query.CRATE_NEW_CONTAINER, 
                new Object[] {newId, 
                        JdbcConverter.toNumber(shipId),
                        JdbcConverter.toNumber(DatabaseObject.HOLD_OBJTYPE_ID), 
                        JdbcConverter.toNumber(DatabaseObject.HOLD_OBJTYPE_ID)});
        
        if (rowsAffected == 0) {
            RuntimeException ex = new IllegalStateException("No hold was created, one expected.");
            log.log(Level.ERROR, "HoldDAO Exception while creating new hold.", ex);
            throw ex;
        }
        
        return newId.toBigIntegerExact();
    }

    @Override
    public void deleteHold(BigInteger holdId) {
        int rowsAffected = queryExecutor.delete(holdId,  DatabaseObject.HOLD_OBJTYPE_ID);
        if (rowsAffected == 0) 
            log.log(Level.WARN,"No hold deleted with id = " + holdId + ", expected one.");
    }

    @Override
    public void addCargo(BigInteger cargoId, BigInteger holdId) {
        int rowsAffected = queryExecutor.putEntityToContainer(holdId, cargoId, DatabaseObject.HOLD_OBJTYPE_ID);
        if (rowsAffected == 0) {
            RuntimeException ex = new IllegalArgumentException("Some id might be wrong, cargoId = " + cargoId + ", or holdId = " + holdId);
            log.log(Level.ERROR, exceptionMessage + " and nothing was added.", ex);
            throw ex;
        } else if (rowsAffected > 1) {
            RuntimeException ex = new IllegalStateException("Cargo (id = " + cargoId + ") was put into several holds");
            log.log(Level.FATAL, exceptionMessage + " with id = " + holdId, ex);
            throw ex;
        }
    }

}