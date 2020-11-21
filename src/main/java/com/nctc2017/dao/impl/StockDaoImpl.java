package com.nctc2017.dao.impl;

import com.nctc2017.bean.GoodsForBuying;
import com.nctc2017.bean.GoodsForSale;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.StockDao;
import com.nctc2017.dao.utils.JdbcConverter;
import com.nctc2017.dao.utils.QueryBuilder;
import com.nctc2017.dao.utils.QueryExecutor;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Qualifier("stockDao")
public class StockDaoImpl implements StockDao {

    private static final Logger log = Logger.getLogger(StockDaoImpl.class);
    private static final String CHECK_EXISTENCE_QUERY = "SELECT count(*) FROM objects " +
            "WHERE object_id = ? " +
            "AND source_id = ? " +
            "AND parent_id = ?";

    private static final String GET_GOODS_FOR_SALE =
            "SELECT obj.object_id, obj.source_id, quantity.value, price.value " +
                    " FROM objects obj, attributes_value quantity, attributes_value price " +
                    " WHERE obj.object_type_id = ? " +
                    " AND obj.parent_id = ?" +
                    " AND obj.object_id = quantity.object_id " +
                    " AND obj.object_id = price.object_id " +
                    " AND quantity.attr_id = ? " +
                    " AND price.attr_id = ?";

    private static final String GET_AMMO_FOR_SALE =
            "SELECT obj.object_id, obj.source_id, quantity.value " +
                    " FROM objects obj, attributes_value quantity " +
                    " WHERE obj.object_type_id = ? " +
                    " AND obj.parent_id = ?" +
                    " AND obj.object_id = quantity.object_id " +
                    " AND quantity.attr_id = ?";

    private static final String GET_CANNON_FOR_SALE =
            "SELECT obj.object_id, obj.source_id" +
                    " FROM objects obj " +
                    " WHERE obj.object_type_id = ? " +
                    " AND obj.parent_id = ?";

    private static final String GET_MAST_FOR_SALE =
            "SELECT obj.object_id, obj.source_id" +
                    " FROM objects obj, attributes_value cur_speed, objects obj_temp, attributes_value max_speed " +
                    " WHERE obj.object_type_id = ? " +
                    " AND obj.parent_id = ? " +
                    " AND obj.object_id = cur_speed.object_id " +
                    " AND cur_speed.attr_id = ? " +
                    " AND cur_speed.value = max_speed.value " +
                    " AND obj.source_id = obj_temp.object_id " +
                    " AND obj_temp.object_type_id = ? " +
                    " AND obj_temp.object_id = max_speed.object_id " +
                    " AND max_speed.attr_id = ?";


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QueryExecutor queryExecutor;


    @Override
    public BigInteger findStockId(BigInteger playerId) {
        try {
            BigInteger stockId = queryExecutor.findContainerByOwnerId(DatabaseObject.STOCK_OBJTYPE_ID, playerId, DatabaseObject.PLAYER_OBJTYPE_ID);
            return stockId;

        } catch (EmptyResultDataAccessException e) {
            RuntimeException ex = new IllegalArgumentException("Wrong player object id to find Stock. Id = " + playerId);
            log.log(Level.ERROR, "StockDao Exception while finding stock by player id.", ex);
            throw ex;
        }
    }

    @Override
    public int getOccupiedVolume(BigInteger playerId) {
        List<BigInteger> entitiesId =
                queryExecutor.findAllEntitiesInContainerByOwnerId(DatabaseObject.STOCK_OBJTYPE_ID,
                        playerId,
                        DatabaseObject.PLAYER_OBJTYPE_ID);
        Integer totalQuantityOfGoodsAndAmmo =
                jdbcTemplate.queryForObject(Query.GET_OCUPATED_VOLUME_GOODS_AMMO,
                        new Object[]{JdbcConverter.toNumber(playerId),
                                JdbcConverter.toNumber(DatabaseObject.STOCK_OBJTYPE_ID)},
                        Integer.class);
        return entitiesId.size() + totalQuantityOfGoodsAndAmmo;
    }

    @Override
    public BigInteger createStock(BigInteger playerId) {
        if (stockExists(playerId)) {
            RuntimeException e = new IllegalStateException("Stock already exists. One player cannot have more than 1 stock at the time.");
            log.log(Level.ERROR, "StockDAO Exception while creating new stock.", e);
            throw e;
        }

        BigInteger newObjId = jdbcTemplate.queryForObject(Query.GET_NEXTVAL, BigDecimal.class).toBigInteger();

        PreparedStatementCreator psc = QueryBuilder.insert(DatabaseObject.STOCK_OBJTYPE_ID, newObjId)
                .setParentId(playerId)
                .build();

        int rowsAffected = jdbcTemplate.update(psc);
        if (rowsAffected == 0) {
            RuntimeException e = new IllegalStateException("No stock was created, one expected.");
            log.log(Level.ERROR, "StockDAO Exception while creating new stock.", e);
            throw e;
        }
        return newObjId;
    }

    @Override
    public void deleteStock(BigInteger playerId) {
        BigInteger stockId = findStockId(playerId);

        PreparedStatementCreator psc = QueryBuilder.delete(stockId).build();

        int rowsAffected = jdbcTemplate.update(psc);
        if (rowsAffected == 0) {
            RuntimeException e = new IllegalStateException("No stock was deleted, one expected.");
            log.log(Level.ERROR, "StockDAO Exception while deleting stock.", e);
            throw e;
        }

    }

    private boolean stockExists(BigInteger playerId) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM objects " +
                        "WHERE parent_id = ? and object_type_id = ?",
                new Object[]{JdbcConverter.toNumber(playerId),
                        JdbcConverter.toNumber(DatabaseObject.STOCK_OBJTYPE_ID)}, Integer.class);
        return count != 0;
    }

    @Override
    public void addCargo(BigInteger cargoId, BigInteger playerId) {
        BigInteger stockId = findStockId(playerId);

        PreparedStatementCreator psc = QueryBuilder.updateParent(cargoId, stockId).build();

        int rowsAffected = jdbcTemplate.update(psc);
        if (rowsAffected == 0) {
            RuntimeException e = new IllegalStateException("No cargo was added, one expected.");
            log.log(Level.ERROR, "StockDAO Exception while adding cargo.", e);
            throw e;
        }

    }

    @Override
    public boolean isSuchCargoInStock(BigInteger cargoId, BigInteger cargoTemplateId, BigInteger stockId) {
        int count = jdbcTemplate.queryForObject(CHECK_EXISTENCE_QUERY,
                new Object[]{JdbcConverter.toNumber(cargoId),
                        JdbcConverter.toNumber(cargoTemplateId),
                        JdbcConverter.toNumber(stockId)},
                Integer.class);
        return count != 0;
    }

    @Override
    public Map<BigInteger, GoodsForSale> getAllPlayersGoodsForSale(BigInteger playerId) {
        Map<BigInteger, GoodsForSale> goods = new HashMap<>();
        BigInteger stockId = findStockId(playerId);

        getGoods(goods, stockId);
        getAmmo(goods, stockId);
        getCannon(goods, stockId);
        getMast(goods, stockId);

        return  goods;
    }

    private void getGoods(Map<BigInteger, GoodsForSale> goods, BigInteger stockId) {
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(GET_GOODS_FOR_SALE,
                    JdbcConverter.toNumber(DatabaseObject.GOODS_OBJTYPE_ID),
                    JdbcConverter.toNumber(stockId),
                    JdbcConverter.toNumber(DatabaseAttribute.GOODS_QUANTITY),
                    JdbcConverter.toNumber(DatabaseAttribute.GOODS_PURCHASE_PRICE));

            while (result.next()) {
                GoodsForSale goodsForSale = new GoodsForSale(
                        result.getBigDecimal(1).toBigInteger(),
                        result.getBigDecimal(2).toBigInteger(),
                        result.getInt(3), GoodsForBuying.GoodsType.GOODS);
                goodsForSale.appendDescription("Purchase price: " + result.getInt(4) + ".\n");
                goods.put(goodsForSale.getGoodsId(), goodsForSale);
            }
        } catch (DataAccessException e) {
            log.warn("DataAccessException while trying to get players goods. " +
                    "May be a normal situation if user don't have any goods. Stock id =" + stockId);
        }
    }

    private void getAmmo(Map<BigInteger, GoodsForSale> goods, BigInteger stockId) {
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(GET_AMMO_FOR_SALE,
                    JdbcConverter.toNumber(DatabaseObject.AMMO_OBJTYPE_ID),
                    JdbcConverter.toNumber(stockId),
                    JdbcConverter.toNumber(DatabaseAttribute.AMMO_NUM));

            while (result.next()) {
                GoodsForSale goodsForSale = new GoodsForSale(
                        result.getBigDecimal(1).toBigInteger(),
                        result.getBigDecimal(2).toBigInteger(),
                        result.getInt(3), GoodsForBuying.GoodsType.AMMO);
                goods.put(goodsForSale.getGoodsId(), goodsForSale);
            }
        } catch (DataAccessException e) {
            log.warn("DataAccessException while trying to get players ammo. " +
                    "May be a normal situation if user don't have any ammo. Stock id =" + stockId);
        }
    }

    private void getCannon(Map<BigInteger, GoodsForSale> goods, BigInteger stockId) {
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(GET_CANNON_FOR_SALE,
                    JdbcConverter.toNumber(DatabaseObject.CANNON_OBJTYPE_ID),
                    JdbcConverter.toNumber(stockId));

            while (result.next()) {
                GoodsForSale goodsForSale = new GoodsForSale(
                        result.getBigDecimal(1).toBigInteger(),
                        result.getBigDecimal(2).toBigInteger(),
                        1, GoodsForBuying.GoodsType.CANNON);
                goods.put(goodsForSale.getGoodsId(), goodsForSale);
            }
        } catch (DataAccessException e){
            log.warn("DataAccessException while trying to get players cannon. " +
                    "May be a normal situation if user don't have any cannon. Stock id =" + stockId);
        }
    }

    private void getMast(Map<BigInteger, GoodsForSale> goods, BigInteger stockId) {
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(GET_MAST_FOR_SALE,
                    JdbcConverter.toNumber(DatabaseObject.MAST_OBJTYPE_ID),
                    JdbcConverter.toNumber(stockId),
                    JdbcConverter.toNumber(DatabaseAttribute.ATTR_CURR_MAST_SPEED_ID),
                    JdbcConverter.toNumber(DatabaseObject.MAST_TEMPLATE_OBJTYPE_ID),
                    JdbcConverter.toNumber(DatabaseAttribute.ATTR_MAX_MAST_SPEED_ID));

            while (result.next()) {
                GoodsForSale goodsForSale = new GoodsForSale(
                        result.getBigDecimal(1).toBigInteger(),
                        result.getBigDecimal(2).toBigInteger(),
                        1, GoodsForBuying.GoodsType.MAST);
                goods.put(goodsForSale.getGoodsId(), goodsForSale);
            }
        } catch (DataAccessException e){
            log.warn("DataAccessException while trying to get players mast. " +
                    "May be a normal situation if user don't have any mast. Stock id =" + stockId);
        }

    }


}