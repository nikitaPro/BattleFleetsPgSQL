package com.nctc2017.dao.impl;

import com.nctc2017.bean.City;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.CityDao;

import com.nctc2017.dao.extractors.EntityExtractor;
import com.nctc2017.dao.extractors.EntityListExtractor;
import com.nctc2017.dao.extractors.ExtractingVisitor;
import com.nctc2017.dao.utils.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

@Repository
@Qualifier("cityDao")
public class CityDaoImpl implements CityDao {
    private static Logger log = Logger.getLogger(CityDaoImpl.class);
    @Autowired
    private QueryExecutor queryExecutor;
    @Override
    public City find(@NotNull BigInteger cityId) {
        City city = queryExecutor.findEntity(cityId,DatabaseObject.CITY_OBJTYPE_ID,
                new EntityExtractor<>(cityId, new CityVisitor()));
        if (city == null){
            RuntimeException ex = new IllegalArgumentException("Wrong city object id = " + cityId);
            log.error("CityDAO Exception while find by id.", ex);
            throw ex;
        }
        return city;
    }


    @Override
    public List<City> findAll() {
        List<City> cities = queryExecutor.getAllEntitiesByType(DatabaseObject.CITY_OBJTYPE_ID,
                new EntityListExtractor<>( new CityVisitor()));
        return cities;
    }
    private final class CityVisitor implements ExtractingVisitor<City> {

        @Override
        public City visit(BigInteger entityId, Map<String, String> cityMap) {
            return new City(cityMap.get(City.NAME),null, entityId );
        }

    }
}
