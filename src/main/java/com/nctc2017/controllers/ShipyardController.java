package com.nctc2017.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nctc2017.bean.*;
import com.nctc2017.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Controller
public class ShipyardController {
    private static final Logger LOG = Logger.getLogger(ShipyardController.class);

    @Autowired
    private ShipService shipService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TravelService travelService;

    @Autowired
    private CargoMovementService cargoMovementService;

    @Autowired
    private ShipTradeService shipTradeService;

    @Autowired
    private ShipRepairService shipRepairService;

    @Secured("ROLE_USER")
    @RequestMapping(value = "/shipyard", method = RequestMethod.GET)
    public ModelAndView shipyardWelcome(
            @RequestParam(value = "city", required = false) String city,
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();

        if (travelService.isPlayerInTravel(playerId)) {
            return new ModelAndView("redirect:/trip");
        }

        ModelAndView model = new ModelAndView();
        model.addObject("msg", "This is protected page - Only for Users!");
        model.addObject("city", city);
        model.setViewName("ShipyardView");
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    @ResponseBody
    public String buyShip(@RequestParam(value = "shipTemplateId") BigInteger shipTemplateId,
                          @RequestParam(value = "shipName") String shipName,
                          @RequestParam(value = "defaultName", required = false) String defaultName,
                          @AuthenticationPrincipal PlayerUserDetails userDetails) {
        int maxShipNameLength = 20;
        BigInteger debugPlayerId = userDetails.getPlayerId();
        String result = shipTradeService.buyShip(debugPlayerId, shipTemplateId);
        if (ShipyardController.isNumeric(result)) {
            BigInteger createdShipId = new BigInteger(result);
            if (shipName.length() > maxShipNameLength || shipName.equals("") || ShipyardController.isNotEnglSymbol(shipName)) {
                shipName = shipService.getDefaultShipName(shipTemplateId);
            }
            shipService.setShipName(createdShipId, shipName);
            result = "Congratulation! One more ship is already armed.";
        }
        return result;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/buyShip", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllShipTemplates() {

        List<ShipTemplate> shipTemplates = shipService.getAllShipTemplates();
        List<StartShipEquipment> shipEquipments = shipService.getStartShipEquipment();
        List<StartTypeOfShipEquip> startTypeOfShipEquips = shipService.getTypeOfShipEquipment();

        ModelAndView model = new ModelAndView();
        model.addObject("startTypeOfShipEquips", startTypeOfShipEquips);
        model.addObject("shipTemplates", shipTemplates);
        model.addObject("shipEquipments", shipEquipments);
        model.setViewName("fragment/shiptable");
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/sellShip", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllPlayerShips(@AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        List<Ship> playerShips = shipService.getAllPlayerShips(playerId);
        List<Integer> shipCosts = shipTradeService.getShipsCost(playerShips);
        String action = "Sell";

        ModelAndView model = new ModelAndView();
        model.addObject("action", action);
        model.addObject("shipCosts", shipCosts);
        model.addObject("playerShips", playerShips);
        model.setViewName("fragment/playerships");
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    @ResponseBody
    public String sellShip(@RequestParam(value = "shipId") BigInteger shipId,
                            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        return shipTradeService.sellShip(playerId, shipId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/repairShip", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView repairShip(@AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        List<Ship> playerShips = shipService.getAllPlayerShips(playerId);
        List<Integer> shipCosts = shipTradeService.getShipsCost(playerShips);
        List<ShipService.ShipSpeed> shipsSpeed = shipService.getSpeedShips(playerShips);
        String action = "Repair";

        ModelAndView model = new ModelAndView();
        model.addObject("action", action);
        model.addObject("shipsSpeed", shipsSpeed);
        model.addObject("shipCosts", shipCosts);
        model.addObject("playerShips", playerShips);
        model.setViewName("fragment/playerships");
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/repair", method = RequestMethod.GET)
    @ResponseBody
    public boolean repair(@RequestParam(value = "shipId") BigInteger shipId,
                          @AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        return shipRepairService.repairShip(playerId, shipId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/setShipName", method = RequestMethod.GET)
    @ResponseBody
    public boolean setShipName(@RequestParam(value = "shipId") BigInteger shipId,
                               @AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        return shipRepairService.repairShip(playerId, shipId);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/stock", method = RequestMethod.GET)
    public ModelAndView distributeResources(@AuthenticationPrincipal PlayerUserDetails userDetails,
                                            @RequestParam(value = "city", required = false) String city,
                                            @RequestParam(value = "page", required = false) String page) throws JsonProcessingException {
        BigInteger playerId = userDetails.getPlayerId();
        if (travelService.isPlayerInTravel(playerId)) {
            return new ModelAndView("redirect:/trip");
        }
        ModelAndView model = new ModelAndView("StockView");

        LOG.debug("stock welcome " + city);

        ObjectMapper mapper = new ObjectMapper();
        model.addObject("playerShips", mapper.writeValueAsString(shipService.getAllPlayerShips(playerId)));
        model.addObject("playerStock", mapper.writeValueAsString(cargoMovementService.getCargoFromStock(playerId)));
        model.addObject("city", city);
        model.addObject("page", page);
        return model;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/shipresources", method = RequestMethod.POST)
    @ResponseBody
    public String getShipResources(@AuthenticationPrincipal PlayerUserDetails userDetails,
                                   @RequestParam(value = "shipId") BigInteger shipId) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("hold", cargoMovementService.getCargoFromHold(userDetails.getPlayerId(), shipId));
        hashMap.put("inventory", cargoMovementService.getCargoFromShip(userDetails.getPlayerId(), shipId));
        hashMap.put("curCarryingLimit", shipService.findShip(shipId).getCurCarryingLimit());
        hashMap.put("curCannons", shipService.getCurrentShipCannonsQuantity(shipId));
        hashMap.put("curMasts", shipService.getCurrentShipMastsQuantity(shipId));
        return mapper.writeValueAsString(hashMap);
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/tostock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> moveCargoToStock(@RequestParam(value = "cargoId") BigInteger cargoId,
                                                   @RequestParam(value = "cargoQuantity") int quantity,
                                                   @RequestParam(value = "shipId") BigInteger shipId,
                                                   @RequestParam(value = "source") String source,
                                                   @AuthenticationPrincipal PlayerUserDetails userDetails) throws JsonProcessingException {
        try {
            String result;
            Container src = Container.valueOf(source.toUpperCase());
            if (src == Container.INVENTORY) {
                cargoMovementService.moveCargoToStock(cargoId, userDetails.getPlayerId());
                result = "Equipment is transferred successfully!";
            } else {
                result = cargoMovementService.moveCargoTo(cargoId,
                        cargoMovementService.getPlayerStock(userDetails.getPlayerId()), quantity);
            }
            return ResponseEntity.ok(
                    successResponseData(shipId,
                            userDetails.getPlayerId(),
                            src,
                            Container.STOCK, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(failureResponseData("Something go wrong. Please, try again later."));
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/tohold", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> moveCargoToHold(@RequestParam(value = "cargoId") BigInteger cargoId,
                                                  @RequestParam(value = "cargoQuantity") int quantity,
                                                  @RequestParam(value = "shipId") BigInteger shipId,
                                                  @RequestParam(value = "source") String source,
                                                  @AuthenticationPrincipal PlayerUserDetails userDetails) throws JsonProcessingException {
        try {
            String result;
            Container src = Container.valueOf(source.toUpperCase());
            if (src == Container.INVENTORY) {
                cargoMovementService.moveCargoToHold(cargoId, cargoMovementService.getShipHold(shipId));
                result = "Equipment is transferred successfully!";
            } else {
                result = cargoMovementService.moveCargoTo(cargoId, cargoMovementService.getShipHold(shipId), quantity);
            }
            return ResponseEntity.ok(
                    successResponseData(shipId,
                            userDetails.getPlayerId(),
                            src,
                            Container.HOLD, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(failureResponseData("Something go wrong. Please, try again later."));
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/toinventory", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> moveCargoToInventory(@RequestParam(value = "cargoId") BigInteger cargoId,
                                                       @RequestParam(value = "shipId") BigInteger shipId,
                                                       @RequestParam(value = "cargoType") String cargoType,
                                                       @RequestParam(value = "source") String source,
                                                       @AuthenticationPrincipal PlayerUserDetails userDetails) throws JsonProcessingException {

        try {
            cargoMovementService.equipShip(cargoId, GoodsForBuying.GoodsType.valueOf(cargoType.toUpperCase()), shipId);
            LOG.debug("successful equip");
            return ResponseEntity.ok(
                    successResponseData(shipId,
                            userDetails.getPlayerId(),
                            Container.valueOf(source.toUpperCase()),
                            Container.INVENTORY,
                            "Ship was equipped successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(failureResponseData("Something go wrong. Please, try again later."));
        }


    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleBadParameters(MethodArgumentTypeMismatchException e) throws JsonProcessingException {
        return ResponseEntity.badRequest()
                .body(failureResponseData("Quantity must be a natural number!"));
    }

    private enum Container {
        STOCK, HOLD, INVENTORY
    }

    private String successResponseData(BigInteger shipId,
                                       BigInteger playerId,
                                       Container source,
                                       Container destination,
                                       String result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msg", result);
        if (source == Container.STOCK || destination == Container.STOCK) {
            hashMap.put("stock", cargoMovementService.getCargoFromStock(playerId));
        }
        if (source == Container.HOLD || destination == Container.HOLD) {
            hashMap.put("hold", cargoMovementService.getCargoFromHold(playerId, shipId));
        }
        if (source == Container.INVENTORY || destination == Container.INVENTORY) {
            hashMap.put("inventory", cargoMovementService.getCargoFromShip(playerId, shipId));
        }
        hashMap.put("curCarryingLimit", shipService.findShip(shipId).getCurCarryingLimit());
        hashMap.put("curCannons", shipService.getCurrentShipCannonsQuantity(shipId));
        hashMap.put("curMasts", shipService.getCurrentShipMastsQuantity(shipId));
        return mapper.writeValueAsString(hashMap);
    }

    private String failureResponseData(String result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msg", result);
        return mapper.writeValueAsString(hashMap);
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isNotEnglSymbol(String str) {
        return str.matches("[^A-z,0-9,\\s,_]");
    }

}