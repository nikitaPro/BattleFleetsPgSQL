package com.nctc2017.services.utils;

import com.nctc2017.bean.City;
import com.nctc2017.bean.Market;
import com.nctc2017.constants.DatabaseObject;
import com.nctc2017.dao.CityDao;
import com.nctc2017.dao.GoodsForSaleDao;
import com.nctc2017.dao.PlayerDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Component
public class MarketManager {

    private static Logger log = Logger.getLogger(MarketManager.class);
    private static final int INITIAL_CAPACITY = 5;

    protected final Map<BigInteger, Market> markets;

    @Autowired
    CityDao cityDao;

    @Autowired
    PlayerDao playerDao;

    @Autowired
    GoodsForSaleDao goodsForSaleDao;

    public MarketManager() {
        markets = new HashMap<>(INITIAL_CAPACITY);
    }

    @PostConstruct
    private void init() {
        log.debug("init post construct begin");
        List<City> cities = cityDao.findAll();
        Market template = initMarket();
        for (City city : cities) {
            markets.put(city.getCityId(), new Market(template));
        }

        Runnable marketManagerTask = new MarketManagerTask();
        Thread managerThread = new Thread(marketManagerTask);

        log.debug(" MarketManagerTask is starting");
        managerThread.start();
        log.debug(" MarketManagerTask is working");
    }

    public Market findMarketByCityId(BigInteger cityId) {
        return markets.get(cityId);
    }

    public void increaseGoodsQuantity(BigInteger cityId, BigInteger goodsId, int deltaQuantity) {
        int oldQuantity = markets.get(cityId).getGoodsQuantity(goodsId);
        markets.get(cityId).updateGoodsQuantity(goodsId, oldQuantity + deltaQuantity);
    }

    public void decreaseGoodsQuantity(BigInteger cityId, BigInteger goodsId, int deltaQuantity) {
        increaseGoodsQuantity(cityId, goodsId, -deltaQuantity);
    }

    public boolean isActualBuyingPrice(BigInteger cityId, BigInteger goodsId, int price) {
        return price == markets.get(cityId).getGoodsBuyingPrice(goodsId);
    }

    public boolean isActualSalePrice(BigInteger cityId, BigInteger goodsId, int price) {
        return price == markets.get(cityId).getGoodsSalePrice(goodsId);
    }

    public boolean isActualBuyingQuantity(BigInteger cityId, BigInteger goodsId, int quantity)
    {
        return quantity <= markets.get(cityId).getGoodsQuantity(goodsId);
    }

    private void generateMarketForCity(Market market) {
        Set<BigInteger> goodsId = market.getAllGoodsIds();
        int playersCount = playerDao.getPlayersCount();
        for (BigInteger id : goodsId) {

            switch (market.getGoodsType(id)) {
                case GOODS:
                    updateGoods(id, playersCount, market);
                    break;
                case MAST:
                    updateInventory(id, playersCount, market);
                    break;
                case CANNON:
                    updateInventory(id, playersCount, market);
                    break;
            }
        }
    }

    private Market initMarket() {
        return new Market(goodsForSaleDao.findAll());
    }

    private void updateGoods(BigInteger goodsId, int playersCount, Market market) {
        int rarity = market.getGoods(goodsId).getGoodsRarity();
        int buyingPrice = generateNewBuyingPriceForGoods(rarity);
        int salePrice = generateNewSalePriceForGoods(buyingPrice);
        int quantity = generateNewQuantityForGoods(rarity, playersCount);

        market.updateGoodsBuyingPrice(goodsId, buyingPrice);
        market.updateGoodsSalePrice(goodsId, salePrice);
        market.updateGoodsQuantity(goodsId, quantity);
    }

    private void updateInventory(BigInteger goodsId, int playersCount, Market market) {
        int randomCoef = ThreadLocalRandom.current().nextInt(3, 8 + 1);
        int newQuantity = playersCount * randomCoef;
        market.updateGoodsQuantity(goodsId, newQuantity);
    }

    private int generateNewBuyingPriceForGoods(int rarity) {
        return ThreadLocalRandom.current().nextInt(rarity * 50, rarity * 100 + 1);
    }

    private int generateNewSalePriceForGoods(int buyingPrice) {
        int randomCoef = ThreadLocalRandom.current().nextInt(8, 20 + 1);
        return buyingPrice - randomCoef;
    }

    private int generateNewQuantityForGoods(int rarity, int playersCount) {
        int randomCoef = ThreadLocalRandom.current().nextInt(100, 200 + 1);
        double raritiCoef = 1.0 / (double) rarity;
        return (int) Math.rint(raritiCoef * playersCount * randomCoef);
    }


    private class MarketManagerTask implements Runnable {

        private static final long DELAY = 21600000L; // 6h
        private static final long SLEEP_TIME = 1800000L; // 30min

        private long nextUpdateTime = 0L;

        @Override
        public void run() {
            while (true) {

                long currentTime = new GregorianCalendar().getTimeInMillis();

                if (currentTime >= nextUpdateTime) {
                    for (Map.Entry<BigInteger, Market> entity : markets.entrySet()) {
                        Market market = entity.getValue();
                        synchronized (market) {
                            generateMarketForCity(market);
                        }
                    }
                    nextUpdateTime = currentTime + DELAY;
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error(" MarketManagerTask is interrupted!");
                    Thread managerThread = new Thread(this);
                    log.debug(" MarketManagerTask is restarting...");
                    managerThread.start();
                    log.debug(" MarketManagerTask is working");
                }
            }
        }
    }
}
