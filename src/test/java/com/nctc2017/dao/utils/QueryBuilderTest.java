package com.nctc2017.dao.utils;

import com.nctc2017.bean.Cannon;
import com.nctc2017.bean.Goods;
import com.nctc2017.bean.Player;
import com.nctc2017.configuration.ApplicationConfig;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.CannonDao;
import com.nctc2017.dao.GoodsDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
public class QueryBuilderTest {

    @Autowired
    private CannonDao cannonDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private ShipDao shipDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Rollback(true)
    public void testInsertOnlyObject() {
        BigInteger objectId = jdbcTemplate.queryForObject(Query.GET_NEXTVAL, BigDecimal.class).toBigInteger();
        Cannon expectedCannon = new Cannon(objectId, "Bombard", 4, 150, 1000);

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.insert(DatabaseObject.CANNON_OBJTYPE_ID, objectId)
                .setSourceObjId(DatabaseObject.BOMBARD_TEMPLATE_ID)
                .build();
        jdbcTemplate.update(actualPreparedStmt);

        Cannon resultCannon = cannonDao.findById(objectId);

        assertEquals(expectedCannon.getThingId(), resultCannon.getThingId());
        assertEquals(expectedCannon.getCost(), resultCannon.getCost());
        assertEquals(expectedCannon.getDamage(), resultCannon.getDamage());
        assertEquals(expectedCannon.getDistance(), resultCannon.getDistance());
        assertEquals(expectedCannon.getName(), resultCannon.getName());
    }

    @Test
    @Rollback(true)
    public void insertObjectWithAttributesValues() {

        BigInteger objectId = jdbcTemplate.queryForObject(Query.GET_NEXTVAL, BigDecimal.class).toBigInteger();
        Goods expectedGoods = new Goods(objectId, "Wood", 350, 40, 1);

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.insert(DatabaseObject.GOODS_OBJTYPE_ID, objectId)
                .setSourceObjId(DatabaseObject.WOOD_TEMPLATE_ID)
                .setAttribute(DatabaseAttribute.GOODS_PURCHASE_PRICE, "40")
                .setAttribute(DatabaseAttribute.GOODS_QUANTITY, "350")
                .build();

        jdbcTemplate.update(actualPreparedStmt);

        Goods resultGoods = goodsDao.findById(objectId);
        assertEquals(expectedGoods.getThingId(), resultGoods.getThingId());
        assertEquals(expectedGoods.getQuantity(), resultGoods.getQuantity());
        assertEquals(expectedGoods.getPurchasePrice(), resultGoods.getPurchasePrice());
        assertEquals(expectedGoods.getName(), resultGoods.getName());
        assertEquals(expectedGoods.getRarity(), resultGoods.getRarity());
    }

    @Test (expected = IllegalArgumentException.class)
    @Rollback(true)
    public void insertObjectWithNullObjectId() {
        PreparedStatementCreator actualPreparedStmt = QueryBuilder.insert(DatabaseObject.GOODS_OBJTYPE_ID, null)
                .build();

        jdbcTemplate.update(actualPreparedStmt);
    }

    @Test
    @Rollback(true)
    public void testUpdateParentId() {
       playerDao.addNewPlayer("Steve", "1111", "Rogers@gmail.com");
       playerDao.addNewPlayer("Tony", "2222", "Stark@gmail.com");
       Player steve = playerDao.findPlayerByLogin("Steve");
       Player tony = playerDao.findPlayerByLogin("Tony");
       BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, steve.getPlayerId());

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.updateParent(shipId, tony.getPlayerId()).build();
        jdbcTemplate.update(actualPreparedStmt);

        BigInteger resultParentId = jdbcTemplate.queryForObject("select parent_id from objects where object_id = ?", BigDecimal.class,
                shipId.longValueExact()).toBigInteger();

        assertEquals(tony.getPlayerId(), resultParentId);
    }

    @Test
    @Rollback(true)
    public void testUpdateParentIdToNull() {
        playerDao.addNewPlayer("Steve", "1111", "Rogers@gmail.com");
        Player steve = playerDao.findPlayerByLogin("Steve");
        BigInteger shipId = shipDao.createNewShip(DatabaseObject.T_CARAVELLA_OBJECT_ID, steve.getPlayerId());
        BigInteger parentId = null;

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.updateParent(shipId, parentId).build();
        jdbcTemplate.update(actualPreparedStmt);

        BigDecimal idValue = jdbcTemplate.queryForObject("select parent_id from objects where object_id = ?", BigDecimal.class,
                shipId.longValueExact());
        BigInteger resultParentId = (idValue == null) ? null : idValue.toBigInteger();

        assertNull(resultParentId);
    }

    @Test
    @Rollback(true)
    public void testUpdateAttrValues() {
        BigInteger objectId = goodsDao.createNewGoods(DatabaseObject.WOOD_TEMPLATE_ID, 20, 40);
        Goods expectedGoods = new Goods(objectId, "Wood", 350, 50, 1);

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.updateAttributeValue(objectId)
                .setAttribute(DatabaseAttribute.GOODS_PURCHASE_PRICE, "40")
                .setAttribute(DatabaseAttribute.GOODS_QUANTITY, "350")
                .setAttribute(DatabaseAttribute.GOODS_PURCHASE_PRICE, "50")
                .build();
        jdbcTemplate.update(actualPreparedStmt);

        Goods resultGoods = goodsDao.findById(objectId);
        assertEquals(expectedGoods.getThingId(), resultGoods.getThingId());
        assertEquals(expectedGoods.getQuantity(), resultGoods.getQuantity());
        assertEquals(expectedGoods.getPurchasePrice(), resultGoods.getPurchasePrice());
        assertEquals(expectedGoods.getName(), resultGoods.getName());
        assertEquals(expectedGoods.getRarity(), resultGoods.getRarity());
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback(true)
    public void testDeleteObject() {
        BigInteger objectId = goodsDao.createNewGoods(DatabaseObject.WOOD_TEMPLATE_ID, 20, 40);

        PreparedStatementCreator actualPreparedStmt = QueryBuilder.delete(objectId).build();
        jdbcTemplate.update(actualPreparedStmt);

        Goods resultGoods = goodsDao.findById(objectId);
    }

}
