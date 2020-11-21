package com.nctc2017.services;

import com.nctc2017.bean.*;
import com.nctc2017.dao.*;
import com.nctc2017.exception.GoodsLackException;
import com.nctc2017.exception.MoneyLackException;
import com.nctc2017.services.utils.MarketManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Transactional
@Service("tradeService")
public class TradeService {

    private static final Logger log = Logger.getLogger(TradeService.class);

    @Autowired
    private MoneyService moneyService;
    @Autowired
    private MarketManager marketManager;

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private StockDao stockDao;

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private AmmoDao ammoDao;
    @Autowired
    private CannonDao cannonDao;
    @Autowired
    private MastDao mastDao;

    /**
     * Gets goods which player can sell (goods that lays in his stock)
     * @param playerId player whose goods we are getting
     * @return
     */
    public List<GoodsForSale> getPlayersGoodsForSale(BigInteger playerId){
        Map<BigInteger, GoodsForSale> goods = stockDao.getAllPlayersGoodsForSale(playerId);

        BigInteger cityId = playerDao.getPlayerCity(playerId);
        Market market = marketManager.findMarketByCityId(cityId);

        for(Map.Entry<BigInteger, GoodsForSale> entry : goods.entrySet()){
            BigInteger templateId = entry.getValue().getGoodsTemplateId();
            GoodsForBuying templateObj = market.getGoods(templateId);
            entry.getValue().appendDescription(templateObj.getGoodsDescription());
            entry.getValue().setName(templateObj.getName());
            entry.getValue().setSalePrice(templateObj.getSalePrice());
        }

        return new ArrayList<GoodsForSale>(goods.values());
    }

    /**
     * Gets market goods for player by getting city in which player is now.
     * @param playerId - player id, for whom is market getting
     * @return list of goods in market for current player's city
     */
    public List<GoodsForBuying> getMarketGoodsByPlayerId(BigInteger playerId){
        BigInteger cityId = playerDao.getPlayerCity(playerId);
        Map<BigInteger, GoodsForBuying> market = marketManager.findMarketByCityId(cityId).getAllGoods();
        return new ArrayList<GoodsForBuying>(market.values());
    }

    public String buy(BigInteger playerId, BigInteger goodsTemplateId, int price, int quantity) {
        int totalCost = price * quantity;

        if (quantity<=0) {
            RuntimeException e = new IllegalArgumentException("The quantity can not be negative or zero!");
            log.error("TradeService Exception while buying", e);
            throw e;
        }
        if (!moneyService.isEnoughMoney(playerId, totalCost)) {
            MoneyLackException e = new MoneyLackException("Not enough money to pay " + totalCost + "!");
            log.error("TradeService Exception while buying goods", e);
            throw e;
        }
        BigInteger cityId = playerDao.getPlayerCity(playerId);
        if (!marketManager.isActualBuyingPrice(cityId, goodsTemplateId, price)) {
            RuntimeException e = new IllegalArgumentException("Not actual price for goods!");
            log.error("TradeService Exception while buying", e);
            throw e;
        }

        if(!marketManager.isActualBuyingQuantity(cityId, goodsTemplateId, quantity)){
            GoodsLackException e = new GoodsLackException("Try to buy more goods than there is in the market!");
            log.error("TradeService Exception while buying", e);
            throw e;
        }


        switch (marketManager.findMarketByCityId(cityId).getGoodsType(goodsTemplateId)) {

            case GOODS:
                buyGoods(playerId, goodsTemplateId, price, quantity);
                marketManager.decreaseGoodsQuantity(cityId, goodsTemplateId, quantity);
                break;

            case AMMO:
                buyAmmo(playerId, goodsTemplateId, quantity);
                break;

            case MAST:
                buyMast(goodsTemplateId, stockDao.findStockId(playerId), quantity);
                marketManager.decreaseGoodsQuantity(cityId, goodsTemplateId, quantity);
                break;

            case CANNON:
                buyCannon(goodsTemplateId, stockDao.findStockId(playerId), quantity);
                marketManager.decreaseGoodsQuantity(cityId, goodsTemplateId, quantity);
                break;
        }
        moneyService.deductMoney(playerId, totalCost);

        return "Success!";
    }

    private BigInteger isSuchGoods(BigInteger playerId, BigInteger goodsTemplateId, int price) {
        BigInteger stockId = stockDao.findStockId(playerId);
        List<Goods> goodsList = goodsDao.getAllGoodsFromStock(stockDao.findStockId(playerId));
        for (Goods goods : goodsList) {
            boolean isSuchGoods = stockDao.isSuchCargoInStock(goods.getThingId(), goodsTemplateId, stockId);

            if (isSuchGoods && goods.getPurchasePrice() == price) {
                return goods.getThingId();
            }
        }
        return null;
    }

    private BigInteger isSuchAmmo(BigInteger playerId, BigInteger goodsTemplateId) {
        BigInteger stockId = stockDao.findStockId(playerId);
        List<Ammo> ammoList = ammoDao.getAllAmmoFromStock(stockDao.findStockId(playerId));
        for (Ammo ammo : ammoList) {
            boolean isSuchAmmo = stockDao.isSuchCargoInStock(ammo.getThingId(), goodsTemplateId, stockId);

            if (isSuchAmmo) {
                return ammo.getThingId();
            }
        }
        return null;
    }

    private void buyGoods(BigInteger playerId, BigInteger goodsTemplateId, int price, int quantity) {
        BigInteger existingGoodId = isSuchGoods(playerId, goodsTemplateId, price);
        if (existingGoodId == null) {
            BigInteger newGoodId = goodsDao.createNewGoods(goodsTemplateId, quantity, price);
            stockDao.addCargo(newGoodId, playerId);
        } else {
            goodsDao.increaseGoodsQuantity(existingGoodId, quantity);
        }
    }

    private void buyAmmo(BigInteger playerId, BigInteger goodsTemplateId, int quantity) {
        BigInteger existingAmmoId = isSuchAmmo(playerId, goodsTemplateId);
        if (existingAmmoId == null) {
            BigInteger newGoodId = ammoDao.createAmmo(goodsTemplateId, quantity);
            stockDao.addCargo(newGoodId, playerId);
        } else {
            ammoDao.increaseAmmoQuantity(existingAmmoId, quantity);
        }
    }

    private void buyCannon(BigInteger goodsTemplateId, BigInteger playersStock, int quantity) {
        for (int i = 0; i < quantity; i++) {
            cannonDao.createCannon(goodsTemplateId, playersStock);
        }
    }

    private void buyMast(BigInteger goodsTemplateId, BigInteger playersStock, int quantity) {
        for (int i = 0; i < quantity; i++) {
            mastDao.createNewMast(goodsTemplateId, playersStock);
        }
    }


    public String sell(BigInteger playerId, BigInteger goodsId, BigInteger goodsTemplateId, int price, int quantity) {

        if (quantity<=0) {
            RuntimeException e = new IllegalArgumentException("The quantity can not be negative or zero!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        BigInteger cityId = playerDao.getPlayerCity(playerId);
        if (!marketManager.isActualSalePrice(cityId, goodsTemplateId, price)) {
            RuntimeException e = new IllegalArgumentException("Not actual price for goods!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        GoodsForBuying.GoodsType type = marketManager.findMarketByCityId(cityId).getGoodsType(goodsTemplateId);

        switch (type) {

            case GOODS:
                sellGoods(playerId, goodsId, cityId, goodsTemplateId, quantity, price);
                break;

            case AMMO:
                sellAmmo(playerId, goodsId, quantity, price);
                break;

            case MAST:
                sellMast(playerId, goodsId, cityId, goodsTemplateId, price);
                break;

            case CANNON:
                sellCannon(playerId, goodsId, cityId, goodsTemplateId, price);
                break;
        }
        return "Success!";
    }

    private void sellGoods(BigInteger playerId, BigInteger goodsId,
                           BigInteger cityId, BigInteger goodsTemplateId,
                           int sellingQuantity, int price) {

        List<Goods> things = goodsDao.getAllGoodsFromStock(stockDao.findStockId(playerId));
        Goods sellingGoods = null;
        for (Goods thing : things) {
            if (Objects.equals(thing.getThingId(), goodsId)) {
                sellingGoods = thing;
                break;
            }
        }

        if (sellingGoods == null) {
            RuntimeException e = new IllegalArgumentException("Not valid goods id!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        int actualQuantity = sellingGoods.getQuantity();

        if (actualQuantity < sellingQuantity) {
            GoodsLackException e = new GoodsLackException("Trying to sell more goods than have!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        if (actualQuantity == sellingQuantity) {
            goodsDao.deleteGoods(sellingGoods.getThingId());
        }

        if (actualQuantity > sellingQuantity) {
            goodsDao.decreaseGoodsQuantity(sellingGoods.getThingId(), sellingQuantity);
        }
        marketManager.increaseGoodsQuantity(cityId, goodsTemplateId, sellingQuantity);
        moneyService.addMoney(playerId, sellingQuantity * price);
    }

    private void sellAmmo(BigInteger playerId, BigInteger ammoId, int sellingQuantity, int price) {
        List<Ammo> things = ammoDao.getAllAmmoFromStock(stockDao.findStockId(playerId));
        Ammo sellingGoods = null;
        for (Ammo thing : things) {
            if (Objects.equals(thing.getThingId(), ammoId)) {
                sellingGoods = thing;
                break;
            }
        }

        if (sellingGoods == null) {
            RuntimeException e = new IllegalArgumentException("Not valid goods id!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        int actualQuantity = sellingGoods.getQuantity();

        if (actualQuantity < sellingQuantity) {
            GoodsLackException e = new GoodsLackException("Trying to sell more goods than have!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        if (actualQuantity == sellingQuantity) {
            ammoDao.deleteAmmo(sellingGoods.getThingId());
        }

        if (actualQuantity > sellingQuantity) {
            ammoDao.decreaseAmmoQuantity(sellingGoods.getThingId(), sellingQuantity);
        }

        moneyService.addMoney(playerId, sellingQuantity * price);
    }

    private void sellMast(BigInteger playerId, BigInteger goodsId,
                          BigInteger cityId, BigInteger goodsTemplateId, int price) {

        List<Mast> things = mastDao.getShipMastsFromStock(stockDao.findStockId(playerId));
        Mast sellingGoods = null;
        for (Mast thing : things) {
            if (Objects.equals(thing.getThingId(), goodsId)) {
                sellingGoods = thing;
                break;
            }
        }

        if (sellingGoods == null) {
            RuntimeException e = new IllegalArgumentException("Not valid goods id!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        mastDao.deleteMast(sellingGoods.getThingId());
        marketManager.increaseGoodsQuantity(cityId, goodsTemplateId, 1);
        moneyService.addMoney(playerId, price);
    }

    private void sellCannon(BigInteger playerId, BigInteger goodsId,
                            BigInteger cityId, BigInteger goodsTemplateId, int price) {

        List<Cannon> things = cannonDao.getAllCannonFromStock(stockDao.findStockId(playerId));
        Cannon sellingGoods = null;
        for (Cannon thing : things) {
            if (Objects.equals(thing.getThingId(), goodsId)) {
                sellingGoods = thing;
                break;
            }
        }

        if (sellingGoods == null) {
            RuntimeException e = new IllegalArgumentException("Not valid goods id!");
            log.error("TradeService Exception while selling", e);
            throw e;
        }

        cannonDao.deleteCannon(sellingGoods.getThingId());
        marketManager.increaseGoodsQuantity(cityId, goodsTemplateId, 1);
        moneyService.addMoney(playerId, price);
    }
}
