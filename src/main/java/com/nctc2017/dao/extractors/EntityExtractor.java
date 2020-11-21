package com.nctc2017.dao.extractors;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;


public class EntityExtractor<E> implements ResultSetExtractor<E> {
    
    private BigInteger entityId;
    private ExtractingVisitor<E> visitor;

    public EntityExtractor(@NotNull BigInteger entityId, @NotNull ExtractingVisitor<E> visitor) {
        this.entityId = entityId;
        this.visitor = visitor;
    }

    @Override
    public E extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (!rs.isBeforeFirst()) return null;
        
        Map<String, String> papamMap = new HashMap<>(4);
        while (rs.next()) {
            papamMap.put(rs.getString(1), rs.getString(2));
        }
        
        return visitor.visit(entityId, papamMap);
    }

}
