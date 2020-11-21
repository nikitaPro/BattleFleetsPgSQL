package com.nctc2017.dao.utils;

import oracle.jdbc.OracleTypes;
import oracle.sql.NUMBER;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryBuilder {

    private static final Logger logger = Logger.getLogger(QueryBuilder.class);

    private static final SqlParameter NUMERIC_PARAM = new SqlParameter(OracleTypes.NUMERIC);
    public static final SqlParameter VARCHAR_PARAM = new SqlParameter(OracleTypes.VARCHAR);
    public static final SqlParameter NULL_PARAM = new SqlParameter(OracleTypes.NULL);

    private static final String OBJECT_TYPE_ID = "objectTypeId";
    private static final String OBJECT_ID = "objectId";
    private static final String PARENT_ID = "parentId";
    private static final String SOURCE_OBJECT_ID = "sourceObjId";

    private final Operation queryOperation;
    private Map<String, BigInteger> objectColumnsValues;
    private Map<BigInteger, String> attributes;
    private Map<BigInteger, String> dateAttributes;

    private enum Operation {
        INSERT, UPDATE_OBJECT_PARENT_ID, UPDATE_OBJECT_ATTRIBUTE_VALUE, DELETE
    }

    private QueryBuilder(Operation operation) {
        queryOperation = operation;
        objectColumnsValues = new HashMap<>();
        attributes = new HashMap<>();
        dateAttributes = new HashMap<>();
    }

    private QueryBuilder(Operation operation, BigInteger objectId) {
        this(operation);
        this.setObjectIdPrivate(objectId);
    }

    /**
     * Creates builder for inserting object and its attributes values.
     *
     * @return builder
     */
    public static QueryBuilder insert(@NotNull BigInteger objectTypeId, @NotNull BigInteger objectId) {
        QueryBuilder builder = new QueryBuilder(Operation.INSERT, objectId);
        builder.setObjectTypeId(objectTypeId);

        return builder;
    }

    public static QueryBuilder insert(@NotNull BigInteger objectTypeId) {
        QueryBuilder builder = new QueryBuilder(Operation.INSERT);
        builder.setObjectTypeId(objectTypeId);

        return builder;
    }

    /**
     * Creates builder for updating object parent_id
     *
     * @param objectId - the object for which you want to update parent_id
     * @param parentId - id of new parent object
     * @return builder
     */
    public static QueryBuilder updateParent(@NotNull BigInteger objectId, BigInteger parentId) {
        QueryBuilder builder = new QueryBuilder(Operation.UPDATE_OBJECT_PARENT_ID, objectId);
        builder.setParentId(parentId);

        return builder;
    }

    /**
     * Creates builder for updating object attributes values. Can update several attributes at once.
     *
     * @param objectId - the object for which you want to update attributes values
     * @return builder
     */
    public static QueryBuilder updateAttributeValue(@NotNull BigInteger objectId) {
        return new QueryBuilder(Operation.UPDATE_OBJECT_ATTRIBUTE_VALUE, objectId);
    }

    /**
     * Creates builder for delete specified object
     *
     * @param objectId - the object which you want to delete
     * @return builder
     */
    public static QueryBuilder delete(@NotNull BigInteger objectId) {
        return new QueryBuilder(Operation.DELETE, objectId);
    }


    private void setObjectIdPrivate(BigInteger id) {
        objectColumnsValues.put(OBJECT_ID, id);
    }

    public QueryBuilder setObjectId(BigInteger id) {
        objectColumnsValues.put(OBJECT_ID, id);
        return this;
    }

    /**
     * Set object type id for new object
     *
     * @param id - object type id of object that will be updated in query
     * @return builder
     */
    public QueryBuilder setObjectTypeId(BigInteger id) {
        if (objectColumnsValues.containsKey(OBJECT_TYPE_ID)) {
            objectColumnsValues.replace(OBJECT_TYPE_ID, id);
        } else {
            objectColumnsValues.put(OBJECT_TYPE_ID, id);
        }
        return this;
    }

    /**
     * Set parent id
     *
     * @param id - parent id for object that will be updated in query
     * @return builder
     */
    public QueryBuilder setParentId(BigInteger id) {
        if (objectColumnsValues.containsKey(PARENT_ID)) {
            objectColumnsValues.replace(PARENT_ID, id);
        } else {
            objectColumnsValues.put(PARENT_ID, id);
        }
        return this;
    }

    /**
     * Set source object id
     *
     * @param id - source object id for object that will be updated in query
     * @return builder
     */
    public QueryBuilder setSourceObjId(BigInteger id) {
        if (objectColumnsValues.containsKey(SOURCE_OBJECT_ID)) {
            objectColumnsValues.replace(SOURCE_OBJECT_ID, id);
        } else {
            objectColumnsValues.put(SOURCE_OBJECT_ID, id);
        }
        return this;
    }

    /**
     * Set new value for attribute with id = attributeId
     *
     * @param attributeId    - id of attribute that will be updated in query
     * @param attributeValue - attribute value that will be set in query
     * @return builder
     */
    public QueryBuilder setAttribute(BigInteger attributeId, String attributeValue) {
        if (attributes.containsKey(attributeId)) {
            attributes.replace(attributeId, attributeValue);
        } else {
            attributes.put(attributeId, attributeValue);
        }
        return this;
    }

    /**
     * Set new value for attribute with id = attributeId
     *
     * @param attributeId    - id of attribute that will be updated in query
     * @param attributeValue - attribute value that will be set in query
     * @return builder
     */
    public QueryBuilder setAttribute(BigInteger attributeId, int attributeValue) {
        setAttribute(attributeId, String.valueOf(attributeValue));
        return this;
    }

    /**
     * Set new date value for attribute with id = attributeId
     *
     * @param attributeId        - id of attribute that will be updated in query
     * @param dateAttributeValue - attribute date value that will be set in query
     * @return builder
     */
    public QueryBuilder setDateAttribute(BigInteger attributeId, String dateAttributeValue) {
        if (dateAttributes.containsKey(attributeId)) {
            dateAttributes.replace(attributeId, dateAttributeValue);
        } else {
            dateAttributes.put(attributeId, dateAttributeValue);
        }
        return this;
    }

    /**
     * Build the update query
     *
     * @return preparedStatement with query
     */
    public PreparedStatementCreator build() throws IllegalArgumentException {

        if (objectColumnsValues.get(OBJECT_ID) == null) {
            logger.log(Level.ERROR, "QueryBuilder has null object_id");
            throw new IllegalArgumentException("QueryBuilder Exception. Null object_id, cannot perform any query");
        }

        switch (queryOperation) {

            case INSERT:
                return insertQuery();

            case UPDATE_OBJECT_PARENT_ID:
                return updateParentQuery();

            case UPDATE_OBJECT_ATTRIBUTE_VALUE:
                return updateAttributeValueQuery();

            case DELETE:
                return deleteQuery();

            default:
                return null;
        }

    }

    private PreparedStatementCreator insertQuery() {
        ArrayList<SqlParameter> declaredParams = new ArrayList<>();
        Object newObjectId = JdbcConverter.toNumber(objectColumnsValues.get(OBJECT_ID));

        StringBuilder attributesQuery = new StringBuilder();
        String oneAttributeQuery = "INSERT INTO attributes_value(attr_id, object_id, value, date_value) VALUES ( ? , ?, ?, ? ); ";
        for (int i = 0; i < attributes.size() + dateAttributes.size(); i++) {
            attributesQuery.append(oneAttributeQuery);
        }

        String insertObjectQuery = "INSERT INTO objects (object_id, parent_id, object_type_id, source_id, name)" +
                " values (?, ?, ?, ?, ";

        String objectNameQuery;
        if (objectColumnsValues.containsKey(SOURCE_OBJECT_ID)) {
            objectNameQuery = "( SELECT name FROM objects WHERE object_id = ?)); ";
        } else {
            objectNameQuery = "( SELECT name FROM objtype WHERE object_type_id = ?)); ";
        }

        String selectQuery = "";//" SELECT 1 FROM dual ";

        ArrayList<Object> paramsInsert = new ArrayList<>();

        paramsInsert.add(newObjectId);
        declaredParams.add(NUMERIC_PARAM);
        paramsInsert.add(JdbcConverter.toNumber(objectColumnsValues.getOrDefault(PARENT_ID, null)));
        declaredParams.add(NUMERIC_PARAM);
        paramsInsert.add(JdbcConverter.toNumber(objectColumnsValues.getOrDefault(OBJECT_TYPE_ID, null)));
        declaredParams.add(NUMERIC_PARAM);
        paramsInsert.add(JdbcConverter.toNumber(objectColumnsValues.getOrDefault(SOURCE_OBJECT_ID, null)));
        declaredParams.add(NUMERIC_PARAM);
        paramsInsert.add(JdbcConverter.toNumber(
                objectColumnsValues.getOrDefault(SOURCE_OBJECT_ID, objectColumnsValues.get(OBJECT_TYPE_ID))));
        declaredParams.add(NUMERIC_PARAM);

        for (Map.Entry<BigInteger, String> cursor : attributes.entrySet()) {
            paramsInsert.add(JdbcConverter.toNumber(cursor.getKey()));
            declaredParams.add(NUMERIC_PARAM);
            paramsInsert.add(newObjectId);
            declaredParams.add(NUMERIC_PARAM);
            paramsInsert.add(cursor.getValue());
            declaredParams.add(VARCHAR_PARAM);
            paramsInsert.add(null); // date_value is always null here
            declaredParams.add(NULL_PARAM);
        }
        for (Map.Entry<BigInteger, String> cursor : dateAttributes.entrySet()) {
            paramsInsert.add(JdbcConverter.toNumber(cursor.getKey()));
            declaredParams.add(NUMERIC_PARAM);
            paramsInsert.add(newObjectId);
            declaredParams.add(NUMERIC_PARAM);
            paramsInsert.add(null); // value is always null here
            declaredParams.add(NULL_PARAM);
            paramsInsert.add(cursor.getValue());
            declaredParams.add(VARCHAR_PARAM);
        }

        PreparedStatementCreatorFactory stmtInsert = new PreparedStatementCreatorFactory(
                insertObjectQuery + objectNameQuery + attributesQuery.toString() + selectQuery,
                declaredParams);
        return stmtInsert.newPreparedStatementCreator(paramsInsert);
    }

    private PreparedStatementCreator updateParentQuery() {
        ArrayList<SqlParameter> declaredParams = new ArrayList<>();

        String updateObjectQuery = "UPDATE objects SET parent_id = ? WHERE object_id = ?";

        ArrayList<Object> paramsUpdateParent = new ArrayList<>();
        paramsUpdateParent.add(JdbcConverter.toNumber(objectColumnsValues.getOrDefault(PARENT_ID, null)));
        declaredParams.add(NUMERIC_PARAM);
        paramsUpdateParent.add(JdbcConverter.toNumber(objectColumnsValues.get(OBJECT_ID)));
        declaredParams.add(NUMERIC_PARAM);

        PreparedStatementCreatorFactory stmtUpdateParent =
                new PreparedStatementCreatorFactory(updateObjectQuery, declaredParams);
        return stmtUpdateParent.newPreparedStatementCreator(paramsUpdateParent);
    }

    private PreparedStatementCreator updateAttributeValueQuery() {
        ArrayList<SqlParameter> declaredParams = new ArrayList<>();

        StringBuilder updateAttrQuery = new StringBuilder(/*"BEGIN; "*/"");

        String oneAttrForUpdateQuery = "UPDATE attributes_value SET value = ? WHERE object_id = ? and attr_id = ? ;";
        for (int i = 0; i < attributes.size(); i++) {
            updateAttrQuery.append(oneAttrForUpdateQuery);
        }

        String oneDateAttrForUpdateQuery = "UPDATE attributes_value SET date_value = ? " +
                "WHERE object_id = ? and attr_id = ? ;";
        for (int i = 0; i < dateAttributes.size(); i++) {
            updateAttrQuery.append(oneDateAttrForUpdateQuery);
        }

        ArrayList<Object> paramsUpdateAttr = new ArrayList<>();

        for (Map.Entry<BigInteger, String> cursor : attributes.entrySet()) {
            paramsUpdateAttr.add(cursor.getValue());
            declaredParams.add(VARCHAR_PARAM);
            paramsUpdateAttr.add(JdbcConverter.toNumber(objectColumnsValues.get(OBJECT_ID)));
            declaredParams.add(NUMERIC_PARAM);
            paramsUpdateAttr.add(JdbcConverter.toNumber(cursor.getKey()));
            declaredParams.add(NUMERIC_PARAM);
        }

        for (Map.Entry<BigInteger, String> cursor : dateAttributes.entrySet()) {
            paramsUpdateAttr.add(cursor.getValue());
            declaredParams.add(VARCHAR_PARAM);
            paramsUpdateAttr.add(JdbcConverter.toNumber(objectColumnsValues.get(OBJECT_ID)));
            declaredParams.add(NUMERIC_PARAM);
            paramsUpdateAttr.add(JdbcConverter.toNumber(cursor.getKey()));
            declaredParams.add(NUMERIC_PARAM);
        }

        PreparedStatementCreatorFactory stmtUpdateAttr =
                new PreparedStatementCreatorFactory(updateAttrQuery.toString() /*+ " COMMIT;"*/, declaredParams);
        return stmtUpdateAttr.newPreparedStatementCreator(paramsUpdateAttr);
    }

    private PreparedStatementCreator deleteQuery() {
        ArrayList<SqlParameter> declaredParams = new ArrayList<>();

        String deleteObjectQuery = "delete from objects where object_id = ?";

        ArrayList<Object> paramsDeleteObject = new ArrayList<>();
        paramsDeleteObject.add(JdbcConverter.toNumber(objectColumnsValues.get(OBJECT_ID)));
        declaredParams.add(NUMERIC_PARAM);

        PreparedStatementCreatorFactory stmtDeleteObject =
                new PreparedStatementCreatorFactory(deleteObjectQuery, declaredParams);
        return stmtDeleteObject.newPreparedStatementCreator(paramsDeleteObject);
    }

    public boolean isInsertOperation() {
        return queryOperation.equals(Operation.INSERT);
    }

    public boolean isUpdateAttrValueOperation() {
        return queryOperation.equals(Operation.UPDATE_OBJECT_ATTRIBUTE_VALUE);
    }

    public boolean isDeleteOperation() {
        return queryOperation.equals(Operation.DELETE);
    }

    public boolean isUpdateParentIdOperation() {
        return queryOperation.equals(Operation.UPDATE_OBJECT_PARENT_ID);
    }

}
