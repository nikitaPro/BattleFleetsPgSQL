package com.nctc2017.controllers;

import java.math.BigInteger;

import com.nctc2017.services.LevelUpService;
import com.nctc2017.services.MoneyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nctc2017.bean.City;
import com.nctc2017.bean.PlayerUserDetails;
import com.nctc2017.services.TravelService;

@Controller
public class CityActionsController {
    private static final Logger LOG = Logger.getLogger(CityActionsController.class);

    @Autowired
    private TravelService travelService;
    @Autowired
    private LevelUpService lvlUpService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/travel", method = RequestMethod.GET)
    public ModelAndView travelWelcome(@AuthenticationPrincipal PlayerUserDetails userDetails) {
       // travelService
        ModelAndView model = new ModelAndView();
        BigInteger playerId = userDetails.getPlayerId();
        
        model.setViewName("fragment/message");        
        model.setStatus(HttpStatus.LOCKED);
        
        boolean haveShip =travelService.isHaveShip(playerId);
        if (!haveShip) {
            model.addObject("errorMes", "You need a ship");
            return model;
        }
        
        boolean fleetSpeedOk = travelService.isFleetSpeedOk(playerId);
        if (!fleetSpeedOk) {
            model.addObject("errorMes", 
                    "The masts are damaged, the ship can not leave the port.");
            model.addObject("errTitle", "Thousand devils!!!");
            return model;
        }
        
        boolean sailorsEnough = travelService.isSailorsEnough(playerId);
        if (!sailorsEnough) {
            model.addObject("errorMes", "You need to hire more sailors");
            return model;
        }
        
        boolean emptyStock = travelService.isEmptyStock(playerId);
        if (!emptyStock) {
            model.setStatus(HttpStatus.ACCEPTED);
            model.addObject("errorMes", "You forgot something in stock. Do you want to come back?");
            model.addObject("errTitle", "Things in stock");
            return model;
        }
        model.setStatus(HttpStatus.OK);
        return model;
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = {"/","/city**"}, method = RequestMethod.GET)
    public ModelAndView getCity(@AuthenticationPrincipal PlayerUserDetails userDetails) {
        ModelAndView model = new ModelAndView();
        BigInteger playerId =userDetails.getPlayerId();
        
        while (true) {
            int time = travelService.getRelocateTime(playerId);
            if (time == Integer.MIN_VALUE) break;
            else if (time > 0) return new ModelAndView("redirect:/trip");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("User thread was interrupted while entering in new city", e);
            }
        }
        
        City currCity = travelService.getCurrentCity(playerId);
        model.setViewName("CityView");
        model.addObject("city", currCity.getCityName());
        model.addObject("level",lvlUpService.getCurrentLevel(playerId));
        model.addObject("nextLevel",lvlUpService.getNextLevel(playerId));
        return model;
    }
}