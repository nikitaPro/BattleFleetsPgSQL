package com.nctc2017.dao.impl;

import com.nctc2017.bean.VerificationToken;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.TokenDao;
import com.nctc2017.dao.utils.JdbcConverter;
import com.nctc2017.dao.utils.QueryBuilder;
import com.nctc2017.dao.utils.QueryExecutor;
import org.apache.jasper.tagplugins.jstl.core.If;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Repository
@Qualifier("tokenDao")
public class TokenDaoImpl implements TokenDao {

    private static Logger log = Logger.getLogger(TokenDaoImpl.class);

    @Autowired
    private QueryExecutor queryExecutor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createToken(String token, long expireDate, BigInteger playerId) {
        BigInteger newObjId = queryExecutor.getNextval();
        QueryBuilder builder = QueryBuilder.insert(DatabaseObject.TOKEN_OBJTYPE_ID, newObjId)
                .setAttribute(DatabaseAttribute.VERIFICATION_TOKEN_TOKEN, token)
                .setAttribute(DatabaseAttribute.VERIFICATION_TOKEN_EXPIRE_DATE, String.valueOf(expireDate))
                .setParentId(playerId);
        queryExecutor.createNewEntity(builder);
    }

    @Override
    public long getTokenExpireDate(String token) {
        String query = "SELECT date_value.value FROM objects o, " +
                "attributes_value token_value, " +
                "attributes_value date_value " +
                "WHERE o.object_type_id = ? AND " +
                " token_value.attr_id = ? AND " +
                " date_value.attr_id = ? AND " +
                " o.object_id = token_value.object_id AND " +
                " o.object_id = date_value.object_id AND " +
                " token_value.value = ?";

        try {
            return jdbcTemplate.queryForObject(query,
                    new Object[]{DatabaseObject.TOKEN_OBJTYPE_ID, DatabaseAttribute.VERIFICATION_TOKEN_TOKEN,
                            DatabaseAttribute.VERIFICATION_TOKEN_EXPIRE_DATE, token},
                    Long.class);
        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Invalid token = " + token, e);
            log.error("TokenDao Exception while getting token expire date", ex);
            throw ex;
        }
    }

    @Override
    public VerificationToken getToken(String token) {
        String query = "SELECT date_value.value, o.parent_id FROM objects o, " +
                "attributes_value token_value, " +
                "attributes_value date_value " +
                "WHERE o.object_type_id = ? AND " +
                " token_value.attr_id = ? AND " +
                " date_value.attr_id = ? AND " +
                " o.object_id = token_value.object_id AND " +
                " o.object_id = date_value.object_id AND " +
                " token_value.value = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(query,
                    new Object[]{JdbcConverter.toNumber(DatabaseObject.TOKEN_OBJTYPE_ID),
                            JdbcConverter.toNumber(DatabaseAttribute.VERIFICATION_TOKEN_TOKEN),
                            JdbcConverter.toNumber(DatabaseAttribute.VERIFICATION_TOKEN_EXPIRE_DATE), token},
                    new int[]{java.sql.Types.NUMERIC, java.sql.Types.NUMERIC, java.sql.Types.NUMERIC, java.sql.Types.VARCHAR});

            if (!result.next()) {
                RuntimeException ex = new IllegalArgumentException("Invalid token = " + token);
                log.error("TokenDao Exception while getting token", ex);
                throw ex;
            }

            String date = result.getString(1);
            BigDecimal userId = result.getBigDecimal(2);
            return new VerificationToken(userId.toBigInteger(), Long.valueOf(date), token);

        } catch (EmptyResultDataAccessException e){
            RuntimeException ex = new IllegalArgumentException("Invalid token = " + token, e);
            log.error("TokenDao Exception while getting token", ex);
            throw ex;
        }
    }
}
