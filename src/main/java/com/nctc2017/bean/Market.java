package com.nctc2017.bean;

import java.math.BigInteger;
import java.util.*;

public class Market {

    private static final int INITIAL_CAPACITY = 21;

    protected Map<BigInteger, GoodsForBuying> bar;

    public Market(List<GoodsForBuying> goodsForBuyings) {
        bar = new HashMap<>(INITIAL_CAPACITY);

        for (GoodsForBuying goods : goodsForBuyings) {
            if (goods != null) {
                bar.put(goods.getTemplateId(), goods);
            }
        }
    }

    public Market(Market market) {
        bar = new HashMap<>(INITIAL_CAPACITY);
        for (Map.Entry<BigInteger, GoodsForBuying> entry : market.bar.entrySet()){
            bar.put(entry.getKey(), new GoodsForBuying(entry.getValue()));
        }
    }

    public int getGoodsQuantity(BigInteger id) {
        return bar.get(id).getQuantity();
    }

    public GoodsForBuying.GoodsType getGoodsType(BigInteger id) {
        return bar.get(id).getType();
    }

    public int getGoodsBuyingPrice(BigInteger id) {
        return bar.get(id).getBuyingPrice();
    }

    public int getGoodsSalePrice(BigInteger id) {
        return bar.get(id).getSalePrice();
    }

    public GoodsForBuying getGoods(BigInteger id) {
        return bar.get(id);
    }

    public void updateGoodsQuantity(BigInteger id, int quantity) {
        GoodsForBuying goods = bar.get(id);
        goods.setQuantity(quantity);
    }

    public void updateGoodsBuyingPrice(BigInteger id, int newGoodsPrice) {
        bar.get(id).setBuyingPrice(newGoodsPrice);
    }

    public void updateGoodsSalePrice(BigInteger id, int newGoodsPrice) {
        bar.get(id).setSalePrice(newGoodsPrice);
    }

    public Set<BigInteger> getAllGoodsIds() {
        return bar.keySet();
    }

    public List<GoodsForBuying> getAllGoodsValues() {return new ArrayList<>(bar.values());}

    public Map<BigInteger, GoodsForBuying> getAllGoods(){
        return bar;
    }

}