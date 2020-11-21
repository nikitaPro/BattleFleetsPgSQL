package com.nctc2017.dao.impl;

import com.nctc2017.bean.GoodsForBuying;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.GoodsForSaleDao;
import com.nctc2017.dao.extractors.EntityListExtractor;
import com.nctc2017.dao.extractors.ExtractingVisitor;
import com.nctc2017.dao.utils.QueryExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("goodsForSaleDao")
public class GoodsForSaleDaoImpl implements GoodsForSaleDao {

    private static final Logger log = Logger.getLogger(GoodsForSaleDaoImpl.class);

    @Autowired
    private QueryExecutor queryExecutor;

    @Override
    public List<GoodsForBuying> findAllByTypeId(BigInteger templateTypeId, GoodsForBuying.GoodsType type) {
        List<GoodsForBuying> goodsList = queryExecutor.getAllEntitiesByType(templateTypeId,
                new EntityListExtractor<ArrayList<GoodsForBuying>, GoodsForBuying>(new GoodsForSaleVisitor(type)));
        return goodsList;
    }

    @Override
    public List<GoodsForBuying> findAll() {
        List<GoodsForBuying>  goods = new ArrayList<>();

        goods.addAll(findAllByTypeId(
                DatabaseObject.CANNON_TEMPLATE_TYPE_ID,
                GoodsForBuying.GoodsType.CANNON));

        goods.addAll(findAllByTypeId(
                DatabaseObject.MAST_TEMPLATE_OBJTYPE_ID,
                GoodsForBuying.GoodsType.MAST));

        goods.addAll(findAllByTypeId(
                DatabaseObject.GOODS_TEMPLATE_TYPE_ID,
                GoodsForBuying.GoodsType.GOODS));

        goods.addAll(findAllByTypeId(
                DatabaseObject.AMMO_TEMPLATE_TYPE_ID,
                GoodsForBuying.GoodsType.AMMO));

        return goods;
    }

    private final class GoodsForSaleVisitor implements ExtractingVisitor<GoodsForBuying> {

        private GoodsForBuying.GoodsType type;

        public GoodsForSaleVisitor(GoodsForBuying.GoodsType type) {
            this.type = type;
        }

        @Override
        public GoodsForBuying visit(BigInteger entityId, Map<String, String> papamMap) {
            String name;
            String description;
            GoodsForBuying goods;

            switch (type) {

                case GOODS:
                    name = papamMap.get("GoodsName");
                    description = "";
                    goods = new GoodsForBuying(entityId, name, description, type);
                    goods.setGoodsRarity(Integer.valueOf(papamMap.get("RarityCoef")));
                    break;

                case AMMO:
                    name = papamMap.get("AmmoName");
                    description = "Damage type: " + papamMap.get("DamageType");
                    goods = new GoodsForBuying(entityId, name, description, type);
                    goods.setBuyingPrice(Integer.valueOf(papamMap.get("AmmoCost")));
                    goods.setSalePrice(goods.getBuyingPrice()/2);
                    goods.setQuantity(Integer.MAX_VALUE);
                    break;

                case MAST:
                    name = papamMap.get("MastName");
                    description = "Sailyards count: " + papamMap.get("Sailyards") +
                            ", speed: " + papamMap.get("Speed");
                    goods = new GoodsForBuying(entityId, name, description, type);
                    goods.setBuyingPrice(Integer.valueOf(papamMap.get("MastCost")));
                    goods.setSalePrice(goods.getBuyingPrice()/2);
                    break;

                case CANNON:
                    name = papamMap.get("CanonName");
                    description = "Damage: " + papamMap.get("Damage") +
                            ", distance: " + papamMap.get("Distance");
                    goods = new GoodsForBuying(entityId, name, description, type);
                    goods.setBuyingPrice(Integer.valueOf(papamMap.get("CannonCost")));
                    goods.setSalePrice(goods.getBuyingPrice()/2);
                    break;

                default:
                    RuntimeException e = new IllegalArgumentException("Wrong object type " + type.name());
                    log.error("GoodsForSaleDao Exception while choosing object type", e);
                    throw e;

            }
            return goods;
        }
    }
}
