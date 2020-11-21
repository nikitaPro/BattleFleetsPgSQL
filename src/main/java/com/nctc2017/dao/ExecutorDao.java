package com.nctc2017.dao;

import java.math.BigInteger;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

public interface ExecutorDao {

    boolean ifThingBelongToPlayer(BigInteger id, BigInteger idPerson);

    void calculateDamage(int[][] ammoCannon, BigInteger idMyShip, BigInteger idEnemyShip, int dist) throws SQLException;

    BigInteger boarding(BigInteger idMyShip, BigInteger idEnemyShip);

    String moveCargoTo(BigInteger cargoId, BigInteger destinationId, int quantity);

    String moveCargoToWinnerBoardingOSurrender(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException;

    String moveCargoToWinnerDestroying(BigInteger shipWinnerId, BigInteger shipLoserId) throws SQLException;


    BigInteger createCannon(BigInteger templateId);

}