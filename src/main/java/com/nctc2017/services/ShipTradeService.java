package com.nctc2017.services;

import com.nctc2017.bean.Ship;
import com.nctc2017.bean.ShipTemplate;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service("shipTradeService")
@Transactional
public class ShipTradeService {

    private static Logger log = Logger.getLogger(ShipTradeService.class);
    @Autowired
    MoneyService moneyService;
    @Autowired
    LevelUpService levelUpService;
    @Autowired
    ShipRepairService shipRepairService;
    @Autowired
    ShipService shipService;
    @Autowired
    ShipDao shipDao;
    @Autowired
    PlayerDao playerDao;


    public String buyShip(BigInteger playerId, BigInteger shipTemplateId) {
        ShipTemplate shipTemplate = shipDao.findShipTemplate(shipTemplateId);
        int numberOfShips = playerDao.findAllShip(playerId) == null ? 0 : playerDao.findAllShip(playerId).size();
        if (levelUpService.getMaxShips(playerId) <= numberOfShips)
            return "You have complete fleet for your level!";
        Integer newMoney = moneyService.deductMoney(playerId, shipTemplate.getCost());
        if ( newMoney== null)
            return "Money is not enough to buy that ship";
        BigInteger createdId = shipService.createNewShip(shipTemplateId, playerId);
        return createdId.toString();
    }

    public List<Integer> getShipsCost(List<Ship> ships) {
        if (ships == null)
            return null;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < ships.size(); i++)
            result.add(costOfShip(ships.get(i)));
        return result;
    }

    public int costOfShip(Ship shipForSelling) {
        int halfOfCost = shipForSelling.getCost() / 2;
        if (shipForSelling == null)
            return 0;
        int costOfShip = halfOfCost - shipRepairService.countRepairCost(shipForSelling.getShipId());
        return costOfShip;
    }

    public String sellShip(BigInteger playerId, BigInteger shipId) {
        try {
            if (playerDao.findAllShip(playerId).size() == 1)
                return "You can not sell single ship!";
            Ship ship = shipDao.findShip(shipId);
            int halfOfCost = ship.getCost() / 2;
            int costOfShip = halfOfCost - shipRepairService.countRepairCost(shipId);
            moneyService.addMoney(playerId, costOfShip);
            shipDao.deleteShip(shipId);
            return "You sold your ship!";
        } catch (RuntimeException e) {
            RuntimeException ex = new IllegalArgumentException("Can not sell ship with that id: " + shipId);
            log.error("ShipTradeService Exception while selling a ship", ex);
            return "This ship is not ours, captain!";
        }
    }

}