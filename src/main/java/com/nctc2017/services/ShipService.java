package com.nctc2017.services;

import com.nctc2017.bean.*;
import com.nctc2017.dao.*;
import com.nctc2017.exception.UpdateException;
import com.nctc2017.services.utils.CompBeans;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShipService {
    @Autowired
    private ShipDao shipDao;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private CannonDao cannonDao;
    @Autowired
    private MastDao mastDao;
    @Autowired
    private HoldDao holdDao;

    private static Logger log = Logger.getLogger(MoneyService.class);

    public List<ShipTemplate> getAllShipTemplates() {
        List<ShipTemplate> result = shipDao.findAllShipTemplates();
        result.sort(new CompBeans().new ShipTemplateCompare());
        return result;
    }

    public String getDefaultShipName(BigInteger shipTemplateId) {
        ShipTemplate result = shipDao.findShipTemplate(shipTemplateId);
        return  result.getTName();
    }


    public List<Ship> getAllPlayerShips(BigInteger playerId) {
        List<BigInteger> shipsId = playerDao.findAllShip(playerId);
        List<Ship> ships = shipDao.findAllShips(shipsId);
        return ships;
    }

    public List<StartTypeOfShipEquip> getTypeOfShipEquipment() {
        List<StartTypeOfShipEquip> result = shipDao.findStartShipsEqupMastType();
        result.sort(new CompBeans().new StartTypeCompare());
        List<StartTypeOfShipEquip> cannon = shipDao.findStartShipsEqupCannonType();
        cannon.sort(new CompBeans().new StartTypeCompare());
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setTypeCannonName(cannon.get(i).getTypeCannonName());
        }
        return result;
    }


    public int getSailorCost() {
        return shipDao.getSailorCost();
    }

    public List<ShipSpeed> getSpeedShips(List<Ship> ships) {
        List<ShipSpeed> result = new ArrayList<>();
        for (Ship ship : ships) {
            int maxShipSpeed = 0;
            int curShipSpeed = 0;
            for (Mast mast : mastDao.getShipMastsFromShip(ship.getShipId())) {
                maxShipSpeed += mast.getMaxSpeed();
                curShipSpeed += mast.getCurSpeed();
            }
            result.add(new ShipSpeed(maxShipSpeed, curShipSpeed));
        }
        return result;
    }

    public Ship findShip(BigInteger shipId) {
        return shipDao.findShip(shipId);
    }

    public boolean updateShipSailorsNumber(BigInteger shipId, int newSailorsNumber) throws UpdateException {
        // Ship ship = findShip(shipId);
        // if(ship.getCurSailorsQuantity()!=ship.getMaxSailorsQuantity()) {
        Ship ship = findShip(shipId);
        if(newSailorsNumber>ship.getMaxSailorsQuantity() || newSailorsNumber<=0)
        {
            UpdateException ex = new UpdateException("Incorrect number of sailors");
            log.error("Incorrect number of sailors",ex);
            throw ex;
        }
        else {
            return shipDao.updateShipSailorsNumber(shipId, newSailorsNumber);
        }
        //}
        //else{
        //  UpdateException ex = new UpdateException("Level greater then next level update");
        //log.error("Your current level should be greater or equal to level at which the update is possible",ex);
        //throw ex;
        //}
    }

    public boolean isAllShipsCompleted(BigInteger playerId) {
        int complete = 0;
        List<Ship> ships = getAllPlayerShips(playerId);
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).getMaxSailorsQuantity() == ships.get(i).getCurSailorsQuantity()) {
                complete++;
            }
        }
        if (ships.size() == complete) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isShipComplete(BigInteger shipId){
        if(findShip(shipId).getCurSailorsQuantity()==findShip(shipId).getMaxSailorsQuantity()){
            return true;
        }
        else{
            return false;
        }
    }

    public int numShipsCompleted(BigInteger playerId) {
        int complete = 0;
        List<Ship> ships = getAllPlayerShips(playerId);
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).getMaxSailorsQuantity() == ships.get(i).getCurSailorsQuantity()) {
                complete++;
            }
        }
        return complete;
    }

    public List<StartShipEquipment> getStartShipEquipment() {
        List<StartShipEquipment> result = shipDao.findStartShipsEqup();
        result.sort(new CompBeans().new StartShipEquipCompare());
        return result;
    }

    public int getSailorsNumber(BigInteger shipId) {
        return shipDao.getCurrentShipSailors(shipId);
    }

    public int getCurrentShipCannonsQuantity(BigInteger shipId){
        return cannonDao.getTotalCurrentQuantity(shipId);

    }

    public int getCurrentShipMastsQuantity(BigInteger shipId){
        return mastDao.getTotalCurrentQuantity(shipId);
    }


    public BigInteger createNewShip(BigInteger templateId, BigInteger playerId) {
        BigInteger shipId = shipDao.createNewShip(templateId, playerId);
        StartShipEquipment startShipEquipment = shipDao.findStartShipEquip(templateId);
        for (int i = 0; i < startShipEquipment.getStartNumCannon(); i++) {
            cannonDao.createCannon(startShipEquipment.getStartCannonType(), shipId);
        }
        for (int i = 0; i < startShipEquipment.getStartNumMast(); i++) {
            mastDao.createNewMast(startShipEquipment.getStartMastType(), shipId);
        }
        holdDao.createHold(shipId);
        return shipId;
    }

    public boolean setShipName(BigInteger shipId, String newShipName) {
        try {
            shipDao.updateShipName(shipId, newShipName);
            return true;
        } catch (Exception e) {
            RuntimeException ex = new IllegalArgumentException("ship does not exist. can not set name");
            log.error("ShipService Exception while set name.", ex);
            throw ex;
        }
    }

    public final class ShipSpeed {
        private final int maxSpeed;
        private final int curSpeed;

        public ShipSpeed(int maxSpeed, int curSpeed) {
            this.maxSpeed = maxSpeed;
            this.curSpeed = curSpeed;
        }

        public int getCurSpeed() {
            return curSpeed;
        }

        public int getMaxSpeed() {
            return maxSpeed;
        }
    }
}