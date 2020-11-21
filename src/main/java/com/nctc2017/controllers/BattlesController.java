package com.nctc2017.controllers;


import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nctc2017.bean.Player;
import com.nctc2017.bean.PlayerUserDetails;
import com.nctc2017.bean.Ship;
import com.nctc2017.exception.BattleEndException;
import com.nctc2017.services.BattleEndingService;
import com.nctc2017.services.BattlePreparationService;
import com.nctc2017.services.BattlePreparationService.ShipWrapper;
import com.nctc2017.services.BattleService;
import com.nctc2017.services.utils.endVisitorImpl.BoardingDefaultBattleEnd;
import com.nctc2017.services.utils.endVisitorImpl.DestroyDefaultBattleEnd;
import com.nctc2017.services.utils.endVisitorImpl.EscapeDefaultBattleEnd;
import com.nctc2017.services.utils.endVisitorImpl.PayoffDefaultBattleEnd;
import com.nctc2017.services.utils.endVisitorImpl.SurrenderDefaultBattleEnd;

@Controller
public class BattlesController {
    private static final int checkingCounter = 5; 
    private static final int checkingInterval = 2000;
    private static final Logger LOG = Logger.getLogger(BattlesController.class);
    
    @Autowired
    protected SurrenderDefaultBattleEnd surrenderDefaultBattleEnd;
    @Autowired
    protected BoardingDefaultBattleEnd boardingDefaultBattleEnd;
    @Autowired
    protected DestroyDefaultBattleEnd destroyDefaultBattleEnd;
    @Autowired
    protected PayoffDefaultBattleEnd payoffDefaultBattleEnd;
    @Autowired
    protected EscapeDefaultBattleEnd escapeDefaultBattleEnd;
    
    
    @Autowired
    private BattlePreparationService prepService;
    @Autowired
    private BattleService battleService;
    @Autowired
    private BattleEndingService battleEndServ;
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/battle_preparing", method = RequestMethod.GET)
    public ModelAndView battleWelcome(@AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request battle_preparing");
        List<Ship> enemyFleet;
        int time;
        Player player;
        try {
            try {
                if (battleService.isBattleStart(playerId)) {
                    return new ModelAndView("redirect:/battle");
                }
                enemyFleet = prepService.getEnemyShips(playerId);
                time = prepService.autoChoiceShipTimer(playerId);
                player = prepService.getEnemyInfo(playerId);
            } catch (BattleEndException e) {
                return new ModelAndView("redirect:/trip");
            }
            List<ShipWrapper> fleet = prepService.getShipsExtraInfo(playerId);
            ModelAndView model = new ModelAndView("BattlePreparingView");
            model.addObject("fleet", fleet);
            model.addObject("enemy_fleet", enemyFleet);
            model.addObject("timer", time < 0 ? 0 : time);
            model.addObject("enemy", player);
            return model;
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/get_auto_pick_time", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getAutoChoiceShipTime(@AuthenticationPrincipal PlayerUserDetails userDetails) throws JsonProcessingException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request get_auto_pick_time");
        try {
            int time = prepService.autoChoiceShipTimer(playerId);
            if (time == -1) {
                ResponseEntity.status(HttpStatus.LOCKED).build();
            }
            Map<String, Integer> body = Collections.singletonMap("time", time);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(body);
            return ResponseEntity.ok().body(json);
        } catch (BattleEndException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/escape", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public Map<String, Boolean> escape(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException, SQLException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request. Escape from battle");
        try {
            boolean success = battleEndServ.escapeBattleLocation(playerId, escapeDefaultBattleEnd);
            return Collections.singletonMap("success", success);
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/pick_ship", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public String pickShip(
            @AuthenticationPrincipal PlayerUserDetails userDetails,
            @RequestParam(value = "ship_id", required = true) String shipId) throws BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Ship picked request. Ship: " + shipId);
        try {
            prepService.chooseShip(playerId, new BigInteger(shipId));
            prepService.setReady(playerId);
            return "Wait for enemy pick...";
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/wait_for_enemy", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String waitForEnemy(
            @AuthenticationPrincipal PlayerUserDetails userDetails) 
                    throws BattleEndException, InterruptedException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Wait for enemy ready request");
        try {
            boolean ready = false;
            for (int i = 0; i < checkingCounter; i++) {
                ready = prepService.waitForEnemyReady(playerId);
                if (ready) break;
                Thread.sleep(checkingInterval);
            }
            
            return String.valueOf(ready);
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/battle", method = RequestMethod.GET)
    public ModelAndView getBattle(
            @AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request. Get battle page");
        try {
            ModelAndView model = new ModelAndView("BattleView");
            int payoff = battleEndServ.getPayOffPrice(playerId);
            boolean payoffAvailable = battleEndServ.isPayOffAvailable(payoff, playerId);
            model.addObject("payoff", payoff);
            model.addObject("payoffAvailable", payoffAvailable);
            model.setStatus(HttpStatus.OK);
            return model;
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/fire", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void fire(
            @AuthenticationPrincipal PlayerUserDetails userDetails,
            @RequestParam(value = "ammoCannon[]") int[] ammoCannon,
            @RequestParam(value = "dim") int dim,
            @RequestParam(value = "decrease") boolean decrease) throws SQLException, BattleEndException  {
        int[][] ammoCannon2 = new int[dim][];
        int k = 0;
        for (int i = 0; i < ammoCannon2.length; i++) {
            int rowDim = ammoCannon.length / dim;
            ammoCannon2[i] = new int[rowDim];
            for (int j = 0; j < ammoCannon2[i].length; j++) {
                ammoCannon2[i][j] = ammoCannon[k++];
            }
        }

        BigInteger playerId  = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Fire request with convergace dist: " + decrease);
        try {
            battleService.setConvergaceOfDist(playerId, decrease);
            battleService.calculateDamage(ammoCannon2, playerId, destroyDefaultBattleEnd);
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/fire_results", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<String> fireResults(
            @AuthenticationPrincipal PlayerUserDetails userDetails,
            @RequestParam(value = "forcibly", required = false) Boolean forcibly) 
                    throws JsonProcessingException, InterruptedException, BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request for getting fire result");
        try {
            if (forcibly == null) forcibly = false;
            for (int i = 0; i < checkingCounter; i++) {
                boolean avaliable = battleService.isStepResultAvalible(playerId);
                LOG.debug("Step result avaliable: " + avaliable);
    
                if (battleEndServ.isBattleFinish(playerId)) {
                    LOG.debug("Battle end news will return ");
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Location", "/is_battle_end");    
                    return new ResponseEntity<String>(headers,HttpStatus.SEE_OTHER);
                }
                
                if (avaliable || forcibly) {
                    
                    BattleService.ShipWrapper playerShip = battleService.getShipInBattle(playerId);
                    Ship enemyShip = battleService.getEnemyShipInBattle(playerId);
                    
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("enemy_ship", enemyShip);
                    infoMap.put("player_ship", playerShip);
                    int distance = battleService.getDistance(playerId);
                    infoMap.put("distance", distance);
                        
                    infoMap.put("madeStep", battleService.wasPalayerMadeStep(playerId));
                    
                    boolean escapeAvaliable = battleEndServ.isBattleLocationEscapeAvailable(playerId);
                    infoMap.put("escape_avaliable", escapeAvaliable);
                    
                    infoMap.put("try_later", false);
                    
                    infoMap.put("auto_step_time", battleService.getAutoStepTime(playerId));
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonShips = mapper.writeValueAsString(infoMap);
                    LOG.debug("Ship info will return");
                    
                    return ResponseEntity.ok(jsonShips);
                } else {
                    Thread.sleep(checkingInterval);
                }
            }
            
            Map<String, Object> shipMap = new HashMap<>();
            shipMap.put("try_later", true);
            ObjectMapper mapper = new ObjectMapper();
            String jsonShips = mapper.writeValueAsString(shipMap);
            return ResponseEntity.ok(jsonShips);
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/boarding", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void boarding(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException, SQLException {
        BigInteger playerId  = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        LOG.debug("Request boarding");
        try {
            battleService.boarding(playerId, boardingDefaultBattleEnd);
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/is_exit_available", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String isLeaveBattleFieldAvailable(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            LOG.debug("Request is_exit_available");
            boolean exit = battleEndServ.isLeaveBattleFieldAvailable(playerId);
            LOG.debug("Exit avaliable: " + exit);
            return String.valueOf(exit);
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/battlefield_exit", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String leaveBattleField(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            LOG.debug("Exit battlefield request");
            boolean exit = battleEndServ.leaveBattleField(playerId);
            LOG.debug("Exit battlefield done: " + exit);
            return String.valueOf(exit);
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/is_enemy_leave_battlefield", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String isEnemyLeaveBattleField(@AuthenticationPrincipal PlayerUserDetails userDetails) {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            LOG.debug("Request is_enemy_leave_battlefield ");
            boolean exit = battleEndServ.isEnemyLeaveBattlefield(playerId);
            LOG.debug("Enemy leave : " + exit);
            return String.valueOf(exit);
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/is_battle_end", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<String> isBattleEnd(@AuthenticationPrincipal PlayerUserDetails userDetails) throws JsonProcessingException, BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            boolean finish = battleEndServ.isBattleFinish(playerId);
            Map<String, String> resp = new HashMap<>();
            resp.put("end", String.valueOf(finish));
            if (finish) {
                if (battleEndServ.isPlayerWinner(playerId)) {
                    resp.put("title", String.valueOf("You Won!!!"));
                    resp.put("wonText", String.valueOf(battleEndServ.getWinnerMessage(playerId)));
                } else {
                    resp.put("title", String.valueOf("You Lose :("));
                    resp.put("wonText", String.valueOf(battleEndServ.getWinnerMessage(playerId)));
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonShips = mapper.writeValueAsString(resp);
            
            return ResponseEntity.ok(jsonShips);
        } finally {
            MDC.remove("userName");
        }
    }
    
    @Secured("ROLE_USER")
    @RequestMapping(value = "/payoff", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, Boolean> payoff(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            boolean success = battleEndServ.payoff(playerId, payoffDefaultBattleEnd);
            return Collections.singletonMap("success", success);
        } finally {
            MDC.remove("userName");
        }
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/surrender", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, Boolean> surrender(@AuthenticationPrincipal PlayerUserDetails userDetails) throws BattleEndException, SQLException {
        BigInteger playerId = userDetails.getPlayerId();
        MDC.put("userName", userDetails.getUsername());
        
        try {
            battleEndServ.surrender(playerId, surrenderDefaultBattleEnd);
            return Collections.singletonMap("success", true);
        } finally {
            MDC.remove("userName");
        }
    }

}