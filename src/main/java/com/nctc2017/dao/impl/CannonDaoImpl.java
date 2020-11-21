package com.nctc2017.dao.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nctc2017.bean.Cannon;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.extractors.EntityExtractor;
import com.nctc2017.dao.extractors.EntityListExtractor;
import com.nctc2017.dao.extractors.ExtractingVisitor;
import com.nctc2017.dao.utils.JdbcConverter;
import com.nctc2017.dao.utils.QueryExecutor;
import com.nctc2017.dao.utils.Validator;

@Repository
@Qualifier("cannonDao")
public class CannonDaoImpl implements CannonDao {
    
    private static Logger log = Logger.getLogger(CannonDaoImpl.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QueryExecutor queryExecutor; 

    @Override
    public Cannon findById(@NotNull BigInteger cannonId) {
        Cannon pickedUpCannon = queryExecutor.findEntity(cannonId, 
                DatabaseObject.CANNON_OBJTYPE_ID,
                new EntityExtractor<>(cannonId, new CannonVisitor()));
        
        if (pickedUpCannon == null){
            RuntimeException ex = new IllegalArgumentException("Wrong cannon object id = " + cannonId);
            log.error("CannonDAO Exception while find by id.", ex);
            throw ex;
        }
        return pickedUpCannon;
    }
    
    @Override
    public Map<String, String> getCurrentQuantity(BigInteger shipId) {
        Map<String, String> cannonTypeCount = queryExecutor.getCountEntitiesByType(DatabaseObject.CANNON_OBJTYPE_ID,
                shipId,
                new EntityExtractor<>(shipId, new CannonCountVisitor()));
        return cannonTypeCount;
    }

    @Override
    public int getTotalCurrentQuantity(BigInteger shipId){
        return queryExecutor.getTotalEntitiesCountByType(DatabaseObject.CANNON_OBJTYPE_ID, shipId);
    }

    @Override
    public String getName(int cannonId) {
        // TODO implement here
        return "";
    }

    @Override
    public int getCost(int cannonId) {
        // TODO implement here
        return 0;
    }

    @Override
    public int getDistance(@NotNull BigInteger cannonTemplateId) {
        return queryExecutor.getAttrValue(cannonTemplateId, DatabaseAttribute.CANNON_DISTANCE, Integer.class);
    }

    @Override
    public int getDamage(@NotNull BigInteger cannonTemplateId) {
        return queryExecutor.getAttrValue(cannonTemplateId, DatabaseAttribute.CANNON_DAMAGE, Integer.class);
    }

    @Override
    public List<Cannon> getAllCannonFromStock(@NotNull BigInteger stockId) {
        Validator.dbInstanceOf(jdbcTemplate, "stock", stockId, DatabaseObject.STOCK_OBJTYPE_ID);      
        return getAllCannonsFromAnywhere(stockId);
    }

    @Override
    public List<Cannon> getAllCannonFromHold(@NotNull BigInteger holdId) {
        Validator.dbInstanceOf(jdbcTemplate, "hold", holdId, DatabaseObject.HOLD_OBJTYPE_ID);
        return getAllCannonsFromAnywhere(holdId);
    }

    @Override
    public List<Cannon> getAllCannonFromShip(@NotNull BigInteger shipId) {
        Validator.dbInstanceOf(jdbcTemplate, "ship", shipId, DatabaseObject.SHIP_OBJTYPE_ID);
        return getAllCannonsFromAnywhere(shipId);
    }
    
    @Override
    public BigInteger createCannon(@NotNull BigInteger cannonTemplateId) {
        return createCannon(cannonTemplateId, null); 
    }
    
    @Override
    public BigInteger createCannon(@NotNull BigInteger cannonTemplateId, BigInteger containerId) {
        Validator.dbInstanceOf(jdbcTemplate,
                "cannon template", 
                cannonTemplateId, 
                DatabaseObject.CANNON_TEMPLATE_TYPE_ID);      
        
        BigInteger newId = queryExecutor.getNextval();
        
        int rowsAffected = jdbcTemplate.update(Query.CREATE_NEW_ENTITY, 
                new Object[] {JdbcConverter.toNumber(newId), 
                        JdbcConverter.toNumber(containerId),
                        JdbcConverter.toNumber(DatabaseObject.CANNON_OBJTYPE_ID), 
                        JdbcConverter.toNumber(cannonTemplateId), 
                        JdbcConverter.toNumber(cannonTemplateId),
                        JdbcConverter.toNumber(DatabaseAttribute.CANNON_NAME_ID)});
        if (rowsAffected == 0){
            RuntimeException ex = new IllegalStateException("No cannon created, expected one new cannon");
            log.error("CannonDAO Exception while creating new entity of cannon.", ex);
            throw ex;
        }
        
        return newId;
    }

    @Override
    public void deleteCannon(@NotNull BigInteger cannonId) {
        int rowsAffected = queryExecutor.delete(cannonId, DatabaseObject.CANNON_OBJTYPE_ID);
        if (rowsAffected == 0) 
            log.warn("No cannon deleted with id = " + cannonId + ", expected one.");
    }

    private List<Cannon> getAllCannonsFromAnywhere(@NotNull BigInteger containerId) {
        return getAllCannonsFromAnywhere(containerId, new CannonVisitor());
    }
    
    private List<Cannon> getAllCannonsFromAnywhere(@NotNull BigInteger containerId, ExtractingVisitor<Cannon> visitor) {
        List<Cannon> pickedUpCannons = queryExecutor
                .getEntitiesFromContainer(containerId, 
                        DatabaseObject.CANNON_OBJTYPE_ID, 
                        new EntityListExtractor<>(visitor));
        return pickedUpCannons;
    }
    
    private final class CannonVisitor implements ExtractingVisitor<Cannon> {

        @Override
        public Cannon visit(BigInteger entityId, Map<String, String> papamMap) {
            Cannon cannon = new Cannon(entityId, 
                    papamMap.remove(Cannon.NAME), 
                    Integer.valueOf(papamMap.remove(Cannon.DAMAGE)),
                    Integer.valueOf(papamMap.remove(Cannon.DISTANCE)), 
                    Integer.valueOf(papamMap.remove(Cannon.COST)));
            cannon.setTamplateId(queryExecutor.getTemplateId(entityId));
            return cannon;
        }
        
    }
    
    private final class CannonCountVisitor implements ExtractingVisitor<Map<String, String>> {

        @Override
        public Map<String, String> visit(BigInteger entityId, Map<String, String> papamMap) {
            return papamMap;
        }
        
    }
    
}