package com.nctc2017.dao.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.nctc2017.constants.Query;
import com.nctc2017.dao.impl.CannonDaoImpl;

@Component("validator")
public class Validator {
    
    private static Logger log = Logger.getLogger(Validator.class);
    
    /**
     * Checks whether the object <code>objId</code> belongs to type <code>objTypeId</code>
     * @param description - what is your object curName.
     * @param objId - id of object
     * @param objTypeId - id of type of object
     * */
    public static void dbInstanceOf(JdbcTemplate jdbcTemplate, @NotNull String description, @NotNull BigInteger objId, @NotNull BigInteger objTypeId){
        try{
            jdbcTemplate.queryForObject(Query.CHECK_OBJECT, 
                    new Object[] {JdbcConverter.toNumber(objId), 
                            JdbcConverter.toNumber(objTypeId)},  
                    BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex= new IllegalArgumentException("Wrong " + description + " id = " + objId, e);
            log.log(Level.ERROR, "Exception with " + description + ", while check valid object id with type id = " + objTypeId, ex);
            throw ex;
        }
    }
}
