package com.nctc2017.dao.impl;

import com.nctc2017.bean.Ammo;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.AmmoDao;
import com.nctc2017.dao.extractors.EntityExtractor;
import com.nctc2017.dao.extractors.EntityListExtractor;
import com.nctc2017.dao.extractors.ExtractingVisitor;
import com.nctc2017.dao.utils.QueryBuilder;
import com.nctc2017.dao.utils.QueryExecutor;
import com.nctc2017.dao.utils.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("ammoDao")
public class AmmoDaoImpl implements AmmoDao {
    
    private static Logger log = Logger.getLogger(AmmoDaoImpl.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private QueryExecutor queryExecutor;

    @Override
    public Ammo findById(BigInteger ammoId) {
        Ammo ammo = queryExecutor.findEntity(ammoId, 
                DatabaseObject.AMMO_OBJTYPE_ID, 
                new EntityExtractor<>(ammoId, new AmmoVisitor()));
        if (ammo == null){
            RuntimeException ex = new IllegalArgumentException("Wrong ammo object id = " + ammoId);
            log.error("AmmoDAO Exception while find by id.", ex);
            throw ex;
        }
        return ammo;
    }

    @Override
    public String getAmmoName(BigInteger ammoId) {
        return queryExecutor.getAttrValue(ammoId, DatabaseAttribute.AMMO_NAME, String.class);
    }

    @Override
    public String getAmmoDamageType(BigInteger ammoId) {
        return queryExecutor.getAttrValue(ammoId, DatabaseAttribute.AMMO_DAMAGE_TYPE, String.class);
    }

    @Override
    public int getAmmoCost(BigInteger ammoId) {
        return queryExecutor.getAttrValue(ammoId, DatabaseAttribute.AMMO_COST, Integer.class);
    }

    @Override
    public int getAmmoQuantity(BigInteger ammoId) {
        try {
            return queryExecutor.getAttrValue(ammoId, DatabaseAttribute.AMMO_NUM, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid ammo id = " + ammoId, e);
            log.error("AmmoDAO Exception while getting ammo quantity.", ex);
            throw ex;
        }
    }

    @Override
    public boolean increaseAmmoQuantity(BigInteger ammoId, int increaseNumber) {
        Integer curQuantity;
        try {
            curQuantity = queryExecutor.getAttrValue(ammoId, 
                    DatabaseAttribute.AMMO_NUM, 
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid ammo id = " + ammoId, e);
            log.error("AmmoDAO Exception while increasing ammo quantity.", ex);
            throw ex;
        }
        
        QueryBuilder builder = QueryBuilder.updateAttributeValue(ammoId)
                .setAttribute(DatabaseAttribute.AMMO_NUM, curQuantity + increaseNumber);
        int res = queryExecutor.updateAttribute(builder);
        return res == 1;
    }

    @Override
    public boolean decreaseAmmoQuantity(BigInteger ammoId, int decreaseNumber) {
        return increaseAmmoQuantity(ammoId, -decreaseNumber);
    }

    @Override
    public BigInteger createAmmo(BigInteger ammoTemplateId, int quantity) {
        QueryBuilder builder = QueryBuilder.insert(DatabaseObject.AMMO_OBJTYPE_ID)
        .setSourceObjId(ammoTemplateId)
        .setAttribute(DatabaseAttribute.AMMO_NUM, quantity);
        
        BigInteger newObjId = queryExecutor.createNewEntity(builder);
        return newObjId;
    }

    @Override
    public void deleteAmmo(BigInteger ammoId) {
        QueryBuilder builder = QueryBuilder.delete(ammoId);

        queryExecutor.delete(builder);
    }

    @Override
    public List<Ammo> getAllAmmoFromStock(BigInteger idStock) {
        Validator.dbInstanceOf(jdbcTemplate, "stock", idStock, DatabaseObject.STOCK_OBJTYPE_ID);
        return getAllAmmoFromAnywhere(idStock);
    }

    @Override
    public List<Ammo> getAllAmmoFromHold(BigInteger idHold) {
        Validator.dbInstanceOf(jdbcTemplate, "hold", idHold, DatabaseObject.HOLD_OBJTYPE_ID);
        return getAllAmmoFromAnywhere(idHold);
    }
    
    private List<Ammo> getAllAmmoFromAnywhere(BigInteger containerId) {
        List<Ammo> ammoList = getAllAmmoFromAnywhere(containerId, new AmmoVisitor());
        return ammoList;
    }
    
    private List<Ammo> getAllAmmoFromAnywhere(BigInteger containerId, ExtractingVisitor<Ammo> visitor) {
        List<Ammo> ammoList = queryExecutor
                .getEntitiesFromContainer(containerId, 
                        DatabaseObject.AMMO_OBJTYPE_ID, 
                        new EntityListExtractor<>(visitor));
        return ammoList;
    }
    
    private final class AmmoVisitor implements ExtractingVisitor<Ammo> {

        @Override
        public Ammo visit(BigInteger entityId, Map<String, String> papamMap) {
            Ammo ammo = new Ammo(entityId,
                    papamMap.get(Ammo.NAME),
                    papamMap.get(Ammo.TYPE),
                    Integer.valueOf(papamMap.get(Ammo.NUM)), 
                    Integer.valueOf(papamMap.get(Ammo.COST)));
            ammo.setTamplateId(queryExecutor.getTemplateId(entityId));
            return ammo;
        }
        
    }

}