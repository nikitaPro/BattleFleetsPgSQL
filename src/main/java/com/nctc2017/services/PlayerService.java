package com.nctc2017.services;

import com.nctc2017.bean.Player;
import com.nctc2017.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerDao playerDao;

    public Player findPlayer(BigInteger playerId){
        return playerDao.findPlayerById(playerId);
    }
    
    public List<BigInteger> findAllShipsId(BigInteger playerId){
        return playerDao.findAllShip(playerId);
    }
}
