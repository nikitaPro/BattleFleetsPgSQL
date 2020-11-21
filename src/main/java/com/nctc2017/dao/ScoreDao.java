package com.nctc2017.dao;

public interface ScoreDao {

    int getScoreForDestroy();

    int getScoreForBoarding();

    int getScoreForSurrender();

    int getScoreForPayoff();

    int getMaxLvl();
}
