package com.nctc2017.dao.impl;

import com.nctc2017.bean.Goods;
import com.nctc2017.constants.DatabaseAttribute;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.constants.Query;
import com.nctc2017.dao.GoodsDao;
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
@Qualifier("goodsDao")
public class GoodsDaoImpl implements GoodsDao {

    private static final Logger logger = Logger.getLogger(GoodsDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QueryExecutor queryExecutor;

    @Override
    public BigInteger createNewGoods(BigInteger goodsTemplateId, int quantity, int price) {
        BigInteger newObjId = queryExecutor.getNextval();

        PreparedStatementCreator psc = QueryBuilder.insert(DatabaseObject.GOODS_OBJTYPE_ID, newObjId)
                .setSourceObjId(goodsTemplateId)
                .setAttribute(DatabaseAttribute.GOODS_QUANTITY, quantity)
                .setAttribute(DatabaseAttribute.GOODS_PURCHASE_PRICE, price)
                .build();
        jdbcTemplate.update(psc);
        return newObjId;
    }

    @Override
    public Goods findById(@NotNull BigInteger goodsId) {
        Goods goods = queryExecutor.findEntity(goodsId,
                DatabaseObject.GOODS_OBJTYPE_ID,
                new EntityExtractor<>(goodsId, new GoodsVisitor()));
        if (goods == null) {
            RuntimeException e = new IllegalArgumentException("Wrong goods object id = " + goodsId);
            logger.log(Level.ERROR, "GoodsDao Exception while find by id ", e);
            throw e;
        }
        return goods;
    }

    @Override
    public void increaseGoodsQuantity(@NotNull BigInteger goodsId, int quantity) {
        Integer curQuantity = queryExecutor.getAttrValue(goodsId, 
                DatabaseAttribute.GOODS_QUANTITY, 
                Integer.class);
        if (curQuantity == null) {
            logger.log(Level.ERROR, "There is no quantity value for object with id = " + goodsId);
            return;
        }
        curQuantity = curQuantity + quantity;
        updateGoodsQuantity(goodsId, curQuantity);
    }

    @Override
    public void decreaseGoodsQuantity(@NotNull BigInteger goodsId, int quantity) {
        Integer curQuantity = queryExecutor.getAttrValue(goodsId, 
                DatabaseAttribute.GOODS_QUANTITY, 
                Integer.class);
        if (curQuantity == null) {
            logger.log(Level.ERROR, "There is no quantity value for object with id = " + goodsId);
            return;
        }
        curQuantity = curQuantity - quantity;
        updateGoodsQuantity(goodsId, curQuantity);
    }


    /**
     * set quantity value = quantity
     *
     * @param goodsId  goods which quantity is updating
     * @param quantity new quantity value to be set
     */
    @Override
    public void updateGoodsQuantity(@NotNull BigInteger goodsId, int quantity) {
        PreparedStatementCreator psc = QueryBuilder.updateAttributeValue(goodsId)
                .setAttribute(DatabaseAttribute.GOODS_QUANTITY, quantity)
                .build();
        jdbcTemplate.update(psc);
    }

    @Override
    public void deleteGoods(@NotNull BigInteger goodsId) {
        PreparedStatementCreator psc = QueryBuilder.delete(goodsId)
                .build();
        jdbcTemplate.update(psc);
    }

    /**
     * Get goods rarity coefficient for specified goods template
     *
     * @param goodsTemplateObjectId
     * @return goods rarity coefficient if query was successful
     * - 1 otherwise
     */
    @Override
    public int getGoodsRarity(@NotNull BigInteger goodsTemplateObjectId) {
        Integer coef = queryExecutor.getAttrValue(goodsTemplateObjectId,
                        DatabaseAttribute.TEMPLATE_GOODS_RARITY_COEF, 
                        Integer.class);
        if (coef == null) {
            logger.log(Level.ERROR, "There is no rarity value for object with id = " + goodsTemplateObjectId);
            return -1;
        } else
            return coef;
    }

    @Override
    public int getGoodsQuantity(@NotNull BigInteger goodsId) {
        Integer quantity = queryExecutor.getAttrValue(goodsId,
                DatabaseAttribute.GOODS_QUANTITY,
                Integer.class);
        if (quantity == null) {
            logger.log(Level.ERROR, "There is no quantity value for object with id = " + quantity);
            return -1;
        } else
            return quantity;
    }


    @Override
    public List<Goods> getAllGoodsFromStock(BigInteger stockId) {
        return getAllGoods(stockId);
    }

    @Override
    public List<Goods> getAllGoodsFromHold(BigInteger holdId) {
        return getAllGoods(holdId);
    }

    private List<Goods> getAllGoods(BigInteger containerId) {
        Object[] queryParams = new Object[]{JdbcConverter.toNumber(DatabaseObject.GOODS_OBJTYPE_ID),
                JdbcConverter.toNumber(containerId),
                JdbcConverter.toNumber(DatabaseObject.GOODS_OBJTYPE_ID),
                JdbcConverter.toNumber(containerId)};

        List<Goods> goodsList = jdbcTemplate.query(
                Query.GET_ENTITIES_FROM_CONTAINER, queryParams, new EntityListExtractor<>(new GoodsVisitor()));
        return goodsList;
    }
    
    private final class GoodsVisitor implements ExtractingVisitor<Goods> {

        @Override
        public Goods visit(BigInteger entityId, Map<String, String> papamMap) {
            Goods goods = new Goods(entityId,
                    papamMap.remove(Goods.NAME),
                    Integer.valueOf(papamMap.remove(Goods.QUANTITY)),
                    Integer.valueOf(papamMap.remove(Goods.PRICE)),
                    Integer.valueOf(papamMap.remove(Goods.RARITY)));
            goods.setTamplateId(queryExecutor.getTemplateId(entityId));
            return goods;
        }
        
    }

}