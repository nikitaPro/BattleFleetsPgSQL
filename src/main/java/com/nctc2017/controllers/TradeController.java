package com.nctc2017.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nctc2017.bean.PlayerUserDetails;
import com.nctc2017.bean.View;
import com.nctc2017.exception.GoodsLackException;
import com.nctc2017.exception.MoneyLackException;
import com.nctc2017.services.MoneyService;
import com.nctc2017.services.TradeService;
import com.nctc2017.services.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.method.annotation.*;

import java.math.BigInteger;
import java.net.URI;

@Controller
public class TradeController {

    @Autowired
    MoneyService moneyService;

    @Autowired
    TradeService tradeService;

    @Autowired
    TravelService travelService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market", method = RequestMethod.GET)
    public ModelAndView marketWelcome(
            @RequestParam(value = "city", required = false) String city,
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        if(travelService.isPlayerInTravel(userDetails.getPlayerId()))  return new ModelAndView("redirect:/trip");
        ModelAndView model = new ModelAndView();
        model.addObject("msg", "This is protected page - Only for Users!");
        model.addObject("city", city);
        model.setViewName("MarketView");
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market/buy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> buy(@RequestParam(value = "goodsTemplateId") BigInteger goodsTemplateId,
                                      @RequestParam(value = "price") int price,
                                      @RequestParam(value = "quantity") int quantity,
                                      @AuthenticationPrincipal PlayerUserDetails userDetails) {
        try{
            if(travelService.isPlayerInTravel(userDetails.getPlayerId())){
                return ResponseEntity.status(HttpStatus.LOCKED).header("Location","/trip")
                        .body("Go to trip");
            }
            return ResponseEntity.status(HttpStatus.OK).body(tradeService
                        .buy(userDetails.getPlayerId(), goodsTemplateId, price, quantity));
        }
        catch(GoodsLackException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(MoneyLackException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market/sell", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sell(@RequestParam(value = "goodsId") BigInteger goodsId,
                     @RequestParam(value = "goodsTemplateId") BigInteger goodsTemplateId,
                     @RequestParam(value = "price") int price,
                     @RequestParam(value = "quantity") int quantity,
                     @AuthenticationPrincipal PlayerUserDetails userDetails) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(tradeService.sell(userDetails
                    .getPlayerId(),goodsId, goodsTemplateId,price,quantity));
        }
        catch(GoodsLackException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market/market-goods", method = RequestMethod.GET)
    @ResponseBody
    public String getAllGoodsForBuying(@AuthenticationPrincipal PlayerUserDetails userDetails)
            throws JsonProcessingException {
        //moneyService.addMoney(userDetails.getPlayerId(),999000000);
        return new ObjectMapper().writerWithView(View.Buy.class)
                    .writeValueAsString(tradeService.getMarketGoodsByPlayerId(userDetails.getPlayerId()));
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market/stock-goods", method = RequestMethod.GET)
    @ResponseBody
    public String getAllGoodsForSelling(@AuthenticationPrincipal PlayerUserDetails userDetails)
    throws JsonProcessingException{
        return new ObjectMapper().writerWithView(View.Sell.class)
                .writeValueAsString(tradeService.getPlayersGoodsForSale(userDetails.getPlayerId()));
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/market/my-money", method = RequestMethod.GET)
    @ResponseBody
    public String getMoney(@AuthenticationPrincipal PlayerUserDetails userDetails){
        int money = moneyService.getPlayersMoney(userDetails.getPlayerId());
        return String.valueOf(money);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleBadParameters(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity must be a natural number!");
    }

}
