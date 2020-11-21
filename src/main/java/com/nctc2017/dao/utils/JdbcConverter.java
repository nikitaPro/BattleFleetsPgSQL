package com.nctc2017.dao.utils;

import oracle.sql.NUMBER;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

public final class JdbcConverter {
    public static enum DB {ORACLE, POSTGRESQL};
    public static final DB currentDB = DB.POSTGRESQL;

    public static Object toNumber(BigInteger value) {
        if (value == null)
            return null;
        if (currentDB.equals(DB.ORACLE))
            return toOracleNumber(value);
        else if (currentDB.equals(DB.POSTGRESQL))
            return toPostgresqlNumeric(value);
        else
            throw new IllegalArgumentException("Unknown DB in JdbcConverter.currentDB");
       
    }
    
    private static NUMBER toOracleNumber(BigInteger value) {
        NUMBER numValue;
        try {
            numValue = new NUMBER(value);
        } catch (SQLException e) {
            throw new RuntimeException(e.getErrorCode() + " error code. " + e.getMessage());
        }
        return numValue;
    }
    
    private static BigDecimal toPostgresqlNumeric(BigInteger value) {
        BigDecimal numValue;
        numValue = new BigDecimal(value);
        return numValue;
    }

    /*This method should convert null to 0, instead of NumberFormatException*/
    public static int parseInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public static BigInteger parseBigIneger(String value) {
        try {
            return new BigInteger(value);
        } catch(Exception e) {
            return null;
        }
    }
}
