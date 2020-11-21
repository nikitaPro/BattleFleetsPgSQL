package com.nctc2017.services;

import com.nctc2017.bean.City;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.Ship;
import com.nctc2017.dao.CityDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.dao.ShipDao;
import com.nctc2017.dao.StockDao;
import com.nctc2017.exception.BattleStartException;
import com.nctc2017.exception.PlayerNotFoundException;
import com.nctc2017.services.utils.BattleManager;
import com.nctc2017.services.utils.TravelManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional
public class TravelService {
    private static final Logger LOG = Logger.getLogger(TravelService.class);
    
    @Autowired
    private BattleManager battleManager;
    @Autowired
    private TravelManager travelManager;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private ShipDao shipDao;
    @Autowired
    private StockDao stockDao;
    

    public void relocate(BigInteger playerId, BigInteger cityId) {
        Player player = playerDao.findPlayerById(playerId);
        BigInteger curCityId = playerDao.getPlayerCity(playerId);
        if (curCityId.equals(cityId)) {
            RuntimeException ex = new IllegalArgumentException("You cannot move to the same city.");
            LOG.error("Exception while player " + playerId + " relocating from city with id = " + curCityId 
                    + " to city with id = " + cityId, ex);
            throw ex;
        }
        LOG.debug("Starting relocation");
        travelManager.startJourney(playerId, player.getLevel(), cityId);
    }

    public City getCurrentCity(BigInteger playerId) {
        BigInteger cityId = playerDao.getPlayerCity(playerId);
        City city=cityDao.find(cityId);
        return city;
    }

    public List<City> getCities() {
        List<City> allCity = cityDao.findAll();
        return allCity;
    }

    public int getRelocateTime(BigInteger playerId) {
        return travelManager.getRelocateTime(playerId);
    }
    
    public boolean isEnemyOnHorizon(BigInteger playerId) throws PlayerNotFoundException {
        boolean isEnemyOnHorizon = travelManager.getEnemyId(playerId) != null;
        boolean isFriendly = travelManager.isFriendly(playerId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("is enemy on horizon: " + isEnemyOnHorizon 
                    + " and is friendly: " + isFriendly);
            LOG.debug("Dialog appear: " + (isEnemyOnHorizon && !isFriendly));
        }
        return  isEnemyOnHorizon && !isFriendly;
    }

    public void confirmAttack(BigInteger playerId, Boolean decision) throws PlayerNotFoundException, BattleStartException {
        travelManager.confirmAttack(playerId, decision);
    }

    public int resumeRelocateTime(BigInteger playerId) throws PlayerNotFoundException {
        LOG.debug("Relocation timer resume.");
        int timeLeft = travelManager.continueTravel(playerId);
        LOG.debug("Time left: " + timeLeft);
        return timeLeft;
    }

    public boolean isBattleStart(BigInteger playerId) {
        return battleManager.isBattleStart(playerId);
    }
    
    public boolean isFleetSpeedOk(BigInteger playerId) {
        return playerDao.getFleetSpeed(playerId) > 1;
    }
    
    public boolean isHaveShip(BigInteger playerId) {
        List<BigInteger> shipsId = playerDao.findAllShip(playerId);
        return ! shipsId.isEmpty();
    }
    
    public boolean isSailorsEnough(BigInteger playerId) {
        List<BigInteger> shipsId = playerDao.findAllShip(playerId);
        if (shipsId.isEmpty()) return false;
        for (BigInteger shipId : shipsId) {
            Ship ship = shipDao.findShip(shipId);
            if (ship.getCurSailorsQuantity() < ship.getMaxSailorsQuantity() / 2) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEmptyStock(BigInteger playerId) {
        return stockDao.getOccupiedVolume(playerId) == 0;
    }
    
    public City getRelocationCity(BigInteger playerId) throws PlayerNotFoundException {
        BigInteger cityId = travelManager.getRelocationCity(playerId);
        return cityDao.find(cityId);
    }
    
    public boolean isDecisionAccept(BigInteger playerId) throws PlayerNotFoundException {
        return travelManager.isDecisionWasMade(playerId);
    }
    
    public boolean isPlayerInTravel(BigInteger playerId) {
        return travelManager.isPlayerInTravel(playerId);
    }

    public boolean isParticipated(BigInteger playerId) throws PlayerNotFoundException {
        return travelManager.isParticipated(playerId);
    }
    
    public void clearStock(BigInteger playerId) {
        stockDao.deleteStock(playerId);
        stockDao.createStock(playerId);
    }
    
}