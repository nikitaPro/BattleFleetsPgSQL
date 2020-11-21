package com.nctc2017.dao.extractors;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class EntityListExtractor<L extends List<E>, E> implements ResultSetExtractor<L> {

    private ExtractingVisitor<E> visitor;
    
    public EntityListExtractor(@NotNull ExtractingVisitor<E> visitor) {
        this.visitor = visitor;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public L extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, String> papamMap;
        Map<BigDecimal, Map<String, String>> entityMap = new HashMap<>();
        BigDecimal entityId;
        while (rs.next()) {
            entityId = rs.getBigDecimal(1);
            papamMap = entityMap.get(entityId);
            if (papamMap == null) {
                papamMap = new HashMap<>(4);
                papamMap.put(rs.getString(2), rs.getString(3));
                entityMap.put(entityId, papamMap);
            } else {
                papamMap.put(rs.getString(2), rs.getString(3));
            }
        }
        
        List<E> entityList = new ArrayList<>(entityMap.size());
        E nextEntity;
        Map<String, String> nextParamMap;
        for (Entry<BigDecimal, Map<String, String>> entry : entityMap.entrySet()) {
            nextParamMap = entry.getValue();
            nextEntity = visitor.visit(entry.getKey().toBigInteger(), 
                    nextParamMap);
            entityList.add(nextEntity);
        }
        return (L) entityList;
    }
    
}
