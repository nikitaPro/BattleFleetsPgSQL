package com.nctc2017.services;

import com.nctc2017.bean.*;
import com.nctc2017.bean.GoodsForSale;
import com.nctc2017.bean.GoodsForBuying.GoodsType;
import com.nctc2017.dao.*;

import com.nctc2017.dao.utils.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("cargoMovementService")
@Transactional
public class CargoMovementService {
    private static final Logger LOG = Logger.getLogger(CargoMovementService.class);

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private MastDao mastDao;
    @Autowired
    private AmmoDao ammoDao;
    @Autowired
    private CannonDao cannonDao;

    @Autowired
    private StockDao stockDao;
    @Autowired
    private HoldDao holdDao;
    @Autowired
    private ShipDao shipDao;

    @Autowired
    private ExecutorDao executorDao;

    public String moveCargoBetweenPlayers(BigInteger shipFromId, BigInteger shipToId) throws SQLException {
        return  executorDao.moveCargoToWinnerBoardingOSurrender(shipToId, shipFromId);

    }

    public void equipShip(BigInteger equipmentId, GoodsType type, BigInteger shipId){
        boolean result = false;
        if (type == GoodsType.CANNON){
            result = shipDao.setCannonOnShip(equipmentId, shipId);
        }
        if(type == GoodsType.MAST){
            result = shipDao.setMastOnShip(equipmentId, shipId);
        }
        if (!result){
            IllegalArgumentException e = new IllegalArgumentException("Can not equip the ship!");
            LOG.error("equipShip method exception while setting inventory");
            throw e;
        }

    }

    public BigInteger getPlayerStock(BigInteger playerId){
        return stockDao.findStockId(playerId);
    }

    public BigInteger getShipHold(BigInteger shipId){ return holdDao.findHold(shipId);}

    public String moveCargoTo(BigInteger cargoId, BigInteger destinationId, int quantity) {
        if(quantity < 1) {
            IllegalArgumentException e = new IllegalArgumentException("quantity is not valid");
            LOG.error("Exception while moving cargo, not valid quantity = " + quantity);
            throw e;
        }
        return executorDao.moveCargoTo(cargoId, destinationId, quantity);
    }

    public void moveCargoToStock(BigInteger cargoId, BigInteger playerId){
        stockDao.addCargo(cargoId, playerId);
    }

    public void moveCargoToHold(BigInteger cargoId, BigInteger holdId){
        holdDao.addCargo(cargoId, holdId);
    }
    
    public List<GoodsForSale> getCargoFromShip(BigInteger playerId, BigInteger shipId) {

        List<Mast> masts = mastDao.getShipMastsFromShip(shipId);
        List<Cannon> cannons = cannonDao.getAllCannonFromShip(shipId);
        
        return cargoConvert(null, cannons, null, masts);
    }
    
    public List<GoodsForSale> getCargoFromHold(BigInteger playerId, BigInteger shipId) {
        BigInteger holdId = holdDao.findHold(shipId);

        List<Goods> goods = goodsDao.getAllGoodsFromHold(holdId);
        List<Cannon> cannons = cannonDao.getAllCannonFromHold(holdId);
        List<Ammo> ammos = ammoDao.getAllAmmoFromHold(holdId);
        List<Mast> masts = mastDao.getShipMastsFromHold(holdId);
        
        return cargoConvert(goods, cannons, ammos, masts);
    }
    
    public List<GoodsForSale> getCargoFromStock(BigInteger playerId) {
        BigInteger stockId = stockDao.findStockId(playerId);
        
        List<Goods> goods = goodsDao.getAllGoodsFromStock(stockId);
        List<Cannon> cannons = cannonDao.getAllCannonFromStock(stockId);
        List<Ammo> ammos = ammoDao.getAllAmmoFromStock(stockId);
        List<Mast> masts = mastDao.getShipMastsFromStock(stockId);

        return cargoConvert(goods, cannons, ammos, masts);
    }
    
    private List<GoodsForSale> cargoConvert(List<Goods> goods,
                                            List<Cannon> cannons,
                                            List<Ammo> ammos,
                                            List<Mast> masts) {
        List<GoodsForSale> cargoInStock = new ArrayList<>();
        
        if (cannons != null) {
            for (Cannon cannon : cannons) {
                cargoInStock.add(new GoodsForSale(
                        cannon.getThingId(), 
                        cannon.getTamplateId(), 
                        cannon.getQuantity(), 
                        GoodsType.CANNON)
                    .setName(cannon.getName())
                    .setSalePrice(cannon.getCost()/2)
                    .appendDescription("damage " + cannon.getDamage())
                    .appendDescription("distance " + cannon.getDistance()));
            }
        }
        
        if (ammos != null) {
            for (Ammo ammo : ammos) {
                cargoInStock.add(new GoodsForSale(
                        ammo.getThingId(), 
                        ammo.getTamplateId(), 
                        ammo.getQuantity(), 
                        GoodsType.AMMO)
                    .setName(ammo.getName())
                    .setSalePrice(ammo.getCost()/2)
                    .appendDescription("For " + ammo.getDamageType()));
            }
        }
        
        if (masts != null) {
            for (Mast mast : masts) {
                cargoInStock.add(new GoodsForSale(
                        mast.getThingId(), 
                        mast.getTamplateId(), 
                        mast.getQuantity(), 
                        GoodsType.MAST)
                    .setName(mast.getTemplateName())
                    .setSalePrice(mast.getCost()/2)
                    .appendDescription("speed: " + mast.getCurSpeed() + "/" + mast.getMaxSpeed()));
            }
        }
        
        if (goods != null) {
            for (Goods goodsInst : goods) {
                cargoInStock.add(new GoodsForSale(
                        goodsInst.getThingId(), 
                        goodsInst.getTamplateId(), 
                        goodsInst.getQuantity(), 
                        GoodsType.GOODS)
                    .setName(goodsInst.getName())
                    .setSalePrice(0)
                    .appendDescription("Purchase price: " + goodsInst.getPurchasePrice()));
            }
        }
        
        return cargoInStock;
    }

}