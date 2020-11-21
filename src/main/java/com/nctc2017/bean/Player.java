package com.nctc2017.bean;

import java.math.BigInteger;

public class Player {
    public static final String LOGIN = "Login";
    public static final String EMAIL = "Email";
    public static final String MONEY = "Money";
    public static final String POINTS = "Points";
    public static final String LEVEL = "Level";
    public static final String NEXT_LEVEL = "NextLevel";
    public static final String INCOME = "Passive income";
    public static final String MAX_SHIPS = "Max ships";

    protected BigInteger playerId;

    protected String login;

    protected String email;

    protected int money;

    protected int points;

    protected int level;

    protected int nextLevel;

    protected int income;

    protected int maxShips;

    public Player(BigInteger playerId, String login, String email, int money, int points, int level, int nextLevel, int income, int maxShips) {
        this.playerId = playerId;
        this.login = login;
        this.email = email;
        this.money = money;
        this.points = points;
        this.level = level;
        this.nextLevel = nextLevel;
        this.income = income;
        this.maxShips = maxShips;
    }

    public BigInteger getPlayerId() {
        return playerId;
    }

    public void setPlayerId(BigInteger playerId) {
        this.playerId = playerId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(int nextLevel) {
        this.nextLevel = nextLevel;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getMaxShips() {
        return maxShips;
    }

    public void setMaxShips(int maxShips) {
        this.maxShips = maxShips;
    }
}