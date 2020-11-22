package com.nctc2017.dao.impl;

import com.nctc2017.bean.Mast;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.MastDao;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("mastDao")
public class MastDaoImpl implements MastDao {

    private static final Logger log = Logger.getLogger(MastDaoImpl.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    QueryExecutor queryExecutor;

    @Override
    public Mast findMast(@NotNull BigInteger mastId) {
        Mast pickedUpMast = queryExecutor.findEntity(mastId, DatabaseObject.MAST_OBJTYPE_ID,
                new EntityExtractor<>(mastId, new MastsVisitor()));
        if (pickedUpMast == null) {
            IllegalArgumentException e =
                    new IllegalArgumentException("Cannot find Mast,wrong mast object  id = " + mastId);
            log.log(Level.ERROR, "Exception: ", e);
            throw e;
        }
        return pickedUpMast;
    }

    private Mast findMastTemplate(@NotNull BigInteger mastTemplateId) {
        Mast pickedUpMast = queryExecutor.findEntity(mastTemplateId, DatabaseObject.MAST_TEMPLATE_OBJTYPE_ID,
                new EntityExtractor<>(mastTemplateId, new MastsTempVisitor()));
        if (pickedUpMast == null) {
            IllegalArgumentException e =
                    new IllegalArgumentException("Cannot find MastTemplate,wrong mastTemplateId = " + mastTemplateId);
            log.log(Level.ERROR, "Exception: ", e);
            throw e;
        }
        return pickedUpMast;
    }


    @Override
    public BigInteger createNewMast(BigInteger mastTemplateId, BigInteger containerOwnerId) {
        BigInteger newId = queryExecutor.getNextval();
        Mast template = findMastTemplate(mastTemplateId);

        PreparedStatementCreator psc = QueryBuilder
                .insert(DatabaseObject.MAST_OBJTYPE_ID, newId)
                .setParentId(containerOwnerId)
                .setSourceObjId(mastTemplateId)
                .setAttribute(DatabaseAttribute.ATTR_CURR_MAST_SPEED_ID,
                        String.valueOf(template.getMaxSpeed()))
                .build();
        jdbcTemplate.update(psc);
        return newId;
    }


    @Override
    public void deleteMast(BigInteger mastId) {
        int numberOfDelRow = 0;
        try {
            numberOfDelRow = jdbcTemplate.update(Query.DELETE_OBJECT,
                    new Object[]{JdbcConverter.toNumber(mastId), 
                            JdbcConverter.toNumber(DatabaseObject.MAST_OBJTYPE_ID)});
        } catch (ArithmeticException e) {
            log.log(Level.ERROR, "Arithmetical exception.Can not delete, id is to big: ", e);
            throw e;
        }

        if (numberOfDelRow == 0) {
            IllegalArgumentException ex = new IllegalArgumentException("Cant delete mast,wrong mastID = " + mastId);
            log.log(Level.ERROR, "Exception:", ex);
            throw ex;
        }

    }


    @Override
    public boolean updateCurMastSpeed(BigInteger mastId, int newMastSpeed) {
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(mastId)
                .setAttribute(DatabaseAttribute.ATTR_CURR_MAST_SPEED_ID, newMastSpeed)
                .build();
        jdbcTemplate.update(psc);
        return true;
    }


    private List<Mast> getShipMastsFromAnywhere(BigInteger containerID) {
        List<Mast> pickedUpMasts = queryExecutor
                .getEntitiesFromContainer(containerID,
                        DatabaseObject.MAST_OBJTYPE_ID,
                        new EntityListExtractor<>(new MastsVisitor()));
        if (pickedUpMasts == null) {
            log.log(Level.INFO, "Wrong containerID / Empty container");
        }
        return pickedUpMasts;
    }

    @Override
    public List<Mast> getShipMastsFromShip(BigInteger shipId) {
        return getShipMastsFromAnywhere(shipId);
    }

    @Override
    public List<Mast> getShipMastsFromStock(BigInteger stockId) {
        return getShipMastsFromAnywhere(stockId);
    }

    @Override
    public List<Mast> getShipMastsFromHold(BigInteger holdId) {
        return getShipMastsFromAnywhere(holdId);
    }

    @Override
    public int getTotalCurrentQuantity(BigInteger shipId){
        return queryExecutor.getTotalEntitiesCountByType(DatabaseObject.MAST_OBJTYPE_ID, shipId);
    }

    @Override
    public int getCurMastSpeed(BigInteger mastId) {
        int result = findMast(mastId).getCurSpeed();
        return result;
    }


    @Override
    public String getMastName(BigInteger mastId) {
        String result = findMast(mastId).getTemplateName();
        return result;
    }


    @Override
    public int getSailyards(int mastId) {
        return 0;
    }


    @Override
    public int getMaxSpeed(BigInteger mastId) {
        int result = findMast(mastId).getMaxSpeed();
        return result;
    }


    @Override
    public int getMastCost(BigInteger mastId) {
        int result = findMast(mastId).getCost();
        return result;
    }

    private class MastsTempVisitor implements ExtractingVisitor<Mast> {

        @Override
        public Mast visit(BigInteger entityId, Map<String, String> papamMap) {
            Mast mast = new Mast(Mast.QUANTITY,
                    entityId,
                    papamMap.remove(Mast.MAST_NAME),
                    Integer.valueOf(papamMap.remove(Mast.MAX_SPEED)),
                    JdbcConverter.parseInt(papamMap.remove(Mast.Cur_MAST_SPEED)),
                    Integer.valueOf(papamMap.remove(Mast.MAST_COST)));
            return mast;
        }
    }

    private class MastsVisitor extends MastsTempVisitor {

        @Override
        public Mast visit(BigInteger entityId, Map<String, String> papamMap) {
            Mast mast = super.visit(entityId, papamMap);
            mast.setTamplateId(queryExecutor.getTemplateId(entityId));
            return mast;
        }
    }

}