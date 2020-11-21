package com.nctc2017.services;

import com.nctc2017.dao.ExecutorDao;
import com.nctc2017.dao.PlayerDao;
import com.nctc2017.exception.MoneyLackException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional
public class MoneyService {
    private static Logger log = Logger.getLogger(MoneyService.class);

    private static final BigInteger maxVal = BigInteger.valueOf(2147483647);

     @Autowired
     PlayerDao playerDao;


     public int addMoney(BigInteger playerId, int moneyAdd) {
        BigInteger newMoney = BigInteger.valueOf(getPlayersMoney(playerId)).add(BigInteger.valueOf(moneyAdd));
        if(newMoney.compareTo(maxVal)>0){
            playerDao.updateMoney(playerId, maxVal.intValue());
            return maxVal.intValue();
        }
        else{
            playerDao.updateMoney(playerId, newMoney.intValue());
            return newMoney.intValue();
        }

     }

     public Integer deductMoney(BigInteger playerId, int moneyDeduct) {
         if(moneyDeduct>0) {
             if (getPlayersMoney(playerId) >= moneyDeduct) {
                 return addMoney(playerId, -moneyDeduct);
             } else {
                 log.info("MoneyService Info not enough money while deduct money.");
                 return null;
             }
         }
         else{
             MoneyLackException ex = new MoneyLackException("Money less or equals zero");
             log.error("Money less or equals zero");
             throw ex;
         }
     }

     public boolean isEnoughMoney(BigInteger playerId, int money) {
        return (getPlayersMoney(playerId)>=money);
     }

     public int getPlayersMoney(BigInteger playerId) {
        return playerDao.getPlayerMoney(playerId);
    }

}