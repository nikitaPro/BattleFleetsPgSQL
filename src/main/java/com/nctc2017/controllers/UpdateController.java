package com.nctc2017.controllers;

import com.nctc2017.bean.PlayerUserDetails;
import com.nctc2017.dao.ScoreDao;
import com.nctc2017.exception.UpdateException;
import com.nctc2017.services.LevelUpService;
import com.nctc2017.services.MoneyService;
import com.nctc2017.services.ScoreService;
import com.nctc2017.services.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;

import static java.lang.Integer.parseInt;

@Controller
public class UpdateController {

    @Autowired
    private LevelUpService lvlUpService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private TravelService travelService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndView update(@AuthenticationPrincipal PlayerUserDetails userDetails){
        BigInteger playerId = userDetails.getPlayerId();
        if(travelService.isPlayerInTravel(playerId)){
            return new ModelAndView("redirect:/trip");
        }
        ModelAndView model = new ModelAndView();
        model.setViewName("UpdateView");
        int nextImprove = lvlUpService.getNextLevel(playerId);
        int lvl = lvlUpService.getCurrentLevel(playerId);
        int maxLvl = scoreService.getMaxLvl();
        model.addObject("lvl", lvl);
        model.addObject("nextImprove", nextImprove);
        model.addObject("maxLvl", maxLvl);
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/incomeUp", method = RequestMethod.GET)
    @ResponseBody
    public String[] incomeUp(@AuthenticationPrincipal PlayerUserDetails userDetails) throws UpdateException {
        lvlUpService.incomeUp(userDetails.getPlayerId());
        lvlUpService.updateNxtLvl(userDetails.getPlayerId());
        int curIncome = lvlUpService.getPassiveIncome(userDetails.getPlayerId());
        int nxtLvlImprove = lvlUpService.getNextLevel(userDetails.getPlayerId());
        String[] results = new String[3];
        results[0] = "Your income grew";
        results[1] = String.valueOf(curIncome);
        results[2] = String.valueOf(nxtLvlImprove);
        return results;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/shipUp", method = RequestMethod.GET)
    @ResponseBody
    public String[] shipUp(@AuthenticationPrincipal PlayerUserDetails userDetails) throws UpdateException {
        lvlUpService.shipUp(userDetails.getPlayerId());
        lvlUpService.updateNxtLvl(userDetails.getPlayerId());
        int curMaxShips = lvlUpService.getMaxShips(userDetails.getPlayerId());
        int nxtLvlImprove = lvlUpService.getNextLevel(userDetails.getPlayerId());
        String[] results = new String[3];
        results[0] = "Your max number of ships grew";
        results[1] = String.valueOf(curMaxShips);
        results[2] = String.valueOf(nxtLvlImprove);
        return results;
    }

    /*@ExceptionHandler(UpdateException.class)
    public ModelAndView updateException(Exception ex){
        ModelAndView model = new ModelAndView();
        model.setViewName("ErrorView");
        return model;
    }*/
}

