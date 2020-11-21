package com.nctc2017.controllers;

import com.nctc2017.bean.Player;
import com.nctc2017.bean.PlayerUserDetails;
import com.nctc2017.services.LevelUpService;
import com.nctc2017.services.PlayerService;
import com.nctc2017.services.ScoreService;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HeaderController {
    @Autowired
    private LevelUpService lvlUpService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private PlayerService playerService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/addHeader",method = RequestMethod.GET)
    public ModelAndView header(@AuthenticationPrincipal PlayerUserDetails userDetails, 
            ModelAndView model) {
        BigInteger playerId = userDetails.getPlayerId();
        Player player = playerService.findPlayer(playerId);
        String login = player.getLogin();
        int money = player.getMoney();
        int level = player.getLevel();
        int points = player.getPoints();
        int nextLevel = player.getNextLevel();
        int maxShips = player.getMaxShips();
        int income = player.getIncome();
        int pointsToNxtLvl = lvlUpService.getPointsToNxtLevel(playerId);
        int nextImprove = player.getNextLevel();
        int maxLvl = scoreService.getMaxLvl();
        List<BigInteger> currShips = playerService.findAllShipsId(playerId);
        model.addObject("login", login);
        model.addObject("money", money);
        model.addObject("points", points);
        model.addObject("level", level);
        model.addObject("nextLevel", nextLevel);
        model.addObject("currShips", currShips.size());
        model.addObject("maxShips", maxShips);
        model.addObject("income", income);
        model.addObject("toNxtLevel", pointsToNxtLvl);
        model.addObject("nextImprove",nextImprove);
        model.addObject("maxLvl",maxLvl);
        model.setViewName("fragment/header");
        return model;
    }
}
