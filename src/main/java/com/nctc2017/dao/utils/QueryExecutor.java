package com.nctc2017.dao.utils;

import com.nctc2017.constants.Query;
//import oracle.sql.NUMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class QueryExecutor {

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * Finds entity which id is specified as {@code entityId} and type's id as {@code entityTypeId}.
     * @param entityId - id of entity
     * @param entityTypeId - id of type of entity
     * @param extractor - object that will extract results
     * */
    public <T> T findEntity(@NotNull BigInteger entityId, @NotNull BigInteger entityTypeId, @NotNull ResultSetExtractor<T> extractor) {
        return jdbcTemplate.query(Query.FIND_ANY_ENTITY,
                new Object[] { JdbcConverter.toNumber(entityTypeId), JdbcConverter.toNumber(entityId),
                        JdbcConverter.toNumber(entityTypeId), JdbcConverter.toNumber(entityId) },
                extractor);
    }

    public <T> T getAllEntitiesByType(@NotNull BigInteger entityTypeId, @NotNull ResultSetExtractor<T> extractor) {
        return jdbcTemplate.query(Query.FIND_ALL_ENTITIES_BY_TYPE, new Object[] { JdbcConverter.toNumber(entityTypeId)},extractor);
    }
    
    public <T> T getCountEntitiesByType(@NotNull BigInteger entityTypeId, @NotNull BigInteger containerId, @NotNull ResultSetExtractor<T> extractor) {
        return jdbcTemplate.query(Query.GET_COUNT_OF_ENTITY_FROM_CONTAINER, 
                new Object[] { 
                        JdbcConverter.toNumber(entityTypeId),
                        JdbcConverter.toNumber(containerId)},
                extractor);
    }

    public int getTotalEntitiesCountByType(@NotNull BigInteger entityTypeId, @NotNull BigInteger containerId) {
        return jdbcTemplate.queryForObject(Query.GET_COUNT_OF_ALL_ENTITY_FROM_CONTAINER,
                new Object[] {
                        JdbcConverter.toNumber(entityTypeId),
                        JdbcConverter.toNumber(containerId)},
                Integer.class);
    }

    /**
     * Deletes entity which id is specified as {@code entityId} and type's id as {@code entityTypeId}.
     * @param entityId - id of entity
     * @param entityTypeId - id of type of entity
     * */
    public int delete(@NotNull BigInteger entityId, @NotNull BigInteger entityTypeId) {
        int rowsAffected = jdbcTemplate.update(Query.DELETE_OBJECT,
                new Object[] {JdbcConverter.toNumber(entityId),
                        JdbcConverter.toNumber(entityTypeId)});
        return rowsAffected;
    }

    public int delete(QueryBuilder builder){
        int rowsAffected = jdbcTemplate.update(builder.build());

        return rowsAffected;
    }

    /**
     * This method allows finds and return all entities with specific type,
     * that contains in some container like Hold or Stock.
     * @param containerId - id of container
     * @param objTypeId - id of type of retrieving object
     * @param extractor - object that will extract results
     * */
    public <T> T getEntitiesFromContainer(@NotNull BigInteger containerId, @NotNull BigInteger objTypeId, @NotNull ResultSetExtractor<T> extractor) {
        return  jdbcTemplate.query(Query.GET_ENTITIES_FROM_CONTAINER,
                new Object[] {JdbcConverter.toNumber(objTypeId),
                        JdbcConverter.toNumber(containerId),
                        JdbcConverter.toNumber(objTypeId),
                        JdbcConverter.toNumber(containerId) },
                extractor);
    }

    /**
     * Finds and return container with specific type,
     * that belongs to some owner, like Hold belongs to Ship.
     * @param ownerId - id of owner
     * @param ownerTypeId - id of type of owner
     * @param containerTypeId- id of type of container
     * */
    public BigInteger findContainerByOwnerId(@NotNull BigInteger containerTypeId, @NotNull BigInteger ownerId, @NotNull BigInteger ownerTypeId) {
        return jdbcTemplate.queryForObject(Query.FIND_CONTAINER_BY_OWNER_ID,
                new Object[] { JdbcConverter.toNumber(containerTypeId),
                        JdbcConverter.toNumber(ownerId),
                        JdbcConverter.toNumber(ownerTypeId) },
                BigDecimal.class).toBigIntegerExact();
    }

    /**
     * This method allows insert object to container and returns 1 if operation is success.
     * If entity is containing in another container, it will be moved to container specified in this method.
     * @param containerId - id of container
     * @param entityId - id of entity
     * @param containerTypeId - id of type of container
     * @return 1 if operation is success
     * */
    public int putEntityToContainer(@NotNull BigInteger containerId, @NotNull BigInteger entityId, @NotNull BigInteger containerTypeId){
        Object containerIdNumber = JdbcConverter.toNumber(containerId);
        Object entityIdNumber = JdbcConverter.toNumber(entityId);
        return jdbcTemplate.update(Query.PUT_ENTITY_TO_CONTAINER,
                new Object[] {containerIdNumber,
                        entityIdNumber,
                        containerIdNumber,
                        containerIdNumber,
                        JdbcConverter.toNumber(containerTypeId)});
    }

    public List<BigInteger> findAllEntitiesInContainerByOwnerId(@NotNull BigInteger containerTypeId, @NotNull BigInteger ownerId, @NotNull BigInteger ownerTypeId){
        List<BigDecimal> entitiesId = jdbcTemplate.queryForList(Query.FIND_ALL_IN_CONTAINER_BY_OWNER_ID,
                new Object[] { JdbcConverter.toNumber(containerTypeId),
                        JdbcConverter.toNumber(ownerId),
                        JdbcConverter.toNumber(ownerTypeId) },
                BigDecimal.class);

        List<BigInteger> entitiesIdInt = new ArrayList<>(entitiesId.size());
        for (BigDecimal bigDecimal : entitiesId) {
            entitiesIdInt.add(bigDecimal.toBigIntegerExact());
        }
        return entitiesIdInt;
    }

    public <T> T getAttrValue(BigInteger entityId, BigInteger attrId,  Class<T> requiredType) {
        T value = jdbcTemplate.queryForObject(Query.GET_ATTR_VALUE,
                new Object[]{JdbcConverter.toNumber(entityId),
                        JdbcConverter.toNumber(attrId)},
                requiredType);
        return value;
    }

    public BigInteger getNextval() {
        return jdbcTemplate.queryForObject(Query.GET_NEXTVAL, BigDecimal.class).toBigIntegerExact();
    }

    public BigInteger createNewEntity(QueryBuilder builder) {
        if (!builder.isInsertOperation()) {
            throw new IllegalArgumentException("Wrong builder operation type. Insert operation expected.");
        }

        BigInteger newObjId = getNextval();
        PreparedStatementCreator psc = builder.setObjectId(newObjId)
                .build();
        jdbcTemplate.update(psc);

        return newObjId;
    }

    public int updateAttribute(QueryBuilder builder) {
        if (!builder.isUpdateAttrValueOperation()) {
            throw new IllegalArgumentException("Wrong builder operation type. Updete operation expected.");
        }
        PreparedStatementCreator psc = builder.build();
        return jdbcTemplate.update(psc);
    }

    public <T> T findAttrByRef(@NotNull BigInteger objectTypeId,BigInteger objectId, BigInteger objectTypeIdRef,
                               BigInteger attrId, Class<T> requireType) {
        T result = jdbcTemplate.queryForObject(Query.FIND_ATTR_BY_REF,
                requireType, new Object[] {JdbcConverter.toNumber(objectTypeId),
                        JdbcConverter.toNumber(objectId),JdbcConverter.toNumber(objectTypeIdRef),
                        JdbcConverter.toNumber(attrId)});
        return result;
    }

    public <T> T getSource(@NotNull BigInteger objectId,BigInteger objectTypeId,  Class<T> requireType) {
        T result = jdbcTemplate.queryForObject(Query.GET_SOURCE_ID,
                requireType, new Object[] {JdbcConverter.toNumber(objectId),
                        JdbcConverter.toNumber(objectTypeId)} );
        return result;
    }

    public <T> T getAttrsByRef(@NotNull BigInteger objectTypeId, @NotNull BigInteger objectTypeIdRef,
                               @NotNull BigInteger attrId,
                               @NotNull ResultSetExtractor<T> extractor) {
        return  jdbcTemplate.query(Query.FIND_ATTRS_BY_ATTR_ID,
                new Object[] {JdbcConverter.toNumber(objectTypeId),
                        JdbcConverter.toNumber(objectTypeIdRef),
                        JdbcConverter.toNumber(attrId)},
                extractor);
    }
    
    public BigInteger getTemplateId(@NotNull BigInteger objectId) {
        return jdbcTemplate.queryForObject(Query.GET_TEMPLATE_ID, 
                new Object[]{JdbcConverter.toNumber(objectId)}, 
                BigDecimal.class).toBigIntegerExact();
    }

}
