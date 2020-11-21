
function hoverConveyor() {
    hoverInit("fire");
    hoverInit("escape");
    hoverInit("payoff");
    hoverInit("surrender");
}

var old_enemy_ship;
var old_player_ship

function tabResultFilling (ships) {
    var tab = $("#info_tab");
    var battle_info =$(".battle_info");
    
    battle_info.slideUp("slow");
    
    var cur_player_health = ships.player_ship.ship.curHealth;
    var cur_enemy_health = ships.enemy_ship.curHealth;
    var cur_player_crew = ships.player_ship.ship.curSailorsQuantity;
    var cur_enemy_crew = ships.enemy_ship.curSailorsQuantity;
    var cur_player_speed = ships.player_ship.ship.curSpeed;
    var cur_enemy_speed= ships.enemy_ship.curSpeed;
        
    tab.find("#P_health")
    .html(cur_player_health +"/" + ships.player_ship.ship.maxHealth);
    tab.find("#E_health")
    .html(cur_enemy_health +"/" + ships.enemy_ship.maxHealth);
    tab.find("#P_crew")
    .html(cur_player_crew + "/" + ships.player_ship.ship.maxSailorsQuantity);
    tab.find("#E_crew")
    .html(cur_enemy_crew + "/" + ships.enemy_ship.maxSailorsQuantity);
    tab.find("#P_speed")
    .html(cur_player_speed);
    tab.find("#E_speed")
    .html(cur_enemy_speed);
    tab.find("#P_damage")
    .html(ships.player_ship.ship.curDamage);
    tab.find("#E_damage")
    .html(ships.enemy_ship.curDamage);
    tab.find("#distance")
    .html(ships.distance);
    if (ships.distance == "0") {
        boardingAllow();
    }
    
    battle_info.slideDown("slow");
    
    ammoCannonQuantity(ships.player_ship);
    if (old_player_ship !== undefined && old_enemy_ship !== undefined) {
        var p_dec = $("#p_dec");
        var e_dec = $("#e_dec");
        p_dec.find("#p_health_dec").html(cur_player_health - old_player_ship.curHealth);
        e_dec.find("#e_health_dec").html(cur_enemy_health - old_enemy_ship.curHealth);
        
        p_dec.find("#p_crew_dec").html(cur_player_crew - old_player_ship.curSailorsQuantity);
        e_dec.find("#e_crew_dec").html(cur_enemy_crew - old_enemy_ship.curSailorsQuantity);

        p_dec.find("#p_speed_dec").html(cur_player_speed - old_player_ship.curSpeed);
        e_dec.find("#e_speed_dec").html(cur_enemy_speed - old_enemy_ship.curSpeed);
        
        animateLostParam();
    }
    old_player_ship = ships.player_ship.ship;
    old_enemy_ship = ships.enemy_ship;
}

var mortars;
var bombards;
var kulevrins;
var cannonballs;
var buckshots;
var chains;
var cannons_limit;
var ammo_limit;

function isCorrectAmmoCannon(ammoCannon, dim) {
    var arr2 = new Array(dim);
    var k = 0;
    for (i=0; i < dim; i++) {
        arr2[i] = new Array(ammoCannon.length / dim);
        for(j=0; j < ammoCannon.length / dim; j++) {
            arr2[i][j] = ammoCannon[k++];
        }
    }
        
    console.log(arr2);
    var sum = 0;
    for (i = 0; i < dim; i++) {
	for (j = 0; j < ammoCannon.length / dim; j++) {
	    sum += arr2[i][j];
	}
	if (sum > cannons_limit[i]) {
	    var warn = "You cannot shoot ammo greater than cannons.\n" + sum
		    + " > " + cannons_limit[i]
	    warningMsg(warn);
	    $("#r" + (i + 1)).effect( "bounce", "slow" );
	    return false;
	}
	console.log("horizontal: " + sum);
	sum = 0;
    }
        

    sum = 0;
    for (i = 0; i < ammoCannon.length / dim; i++) {
	for (j = 0; j < dim; j++) {
	    sum += arr2[j][i];
	}
	if (sum > ammo_limit[i]) {
	    var warn = "You cannot shoot ammo greater than you have.\n" + sum
		    + " > " + ammo_limit[i]
	    warningMsg(warn);
	    $(".c" + (i + 1)).effect( "bounce", "slow" );
	    return false;
	}
	console.log("vertical: " + sum);
	sum = 0;
    }
    return true;
}

function ammoCannonQuantity(shipWrapper) {
    var ammo_cannon = $(".ammo_cannon");

    ammo_cannon.slideUp("slow");

    if (shipWrapper.cannons != null) {
	    mortars = shipWrapper.cannons.Mortar;
	    bombards = shipWrapper.cannons.Bombard;
	    kulevrins = shipWrapper.cannons.Kulevrin;
    }

    mortars = isNaN(mortars) ? 0 : mortars;
    bombards = isNaN(bombards) ? 0 : bombards;
    kulevrins = isNaN(kulevrins) ? 0 : kulevrins;

    cannonballs = shipWrapper.ammo.Cannonball;
    cannonballs = isNaN(cannonballs) ? 0 : cannonballs;

    buckshots = shipWrapper.ammo.Buckshot;
    buckshots = isNaN(buckshots) ? 0 : buckshots;

    chains = shipWrapper.ammo.Chain;
    chains = isNaN(chains) ? 0 : chains;
    
    var cannons = {mortars:mortars, bombards:bombards, kulevrins:kulevrins};
    var ammos = {cannonballs:cannonballs, buckshots:buckshots, chains:chains};
    ammoCannonsTabDisableByCount(cannons, ammos);

    cannons_limit = [ mortars, bombards, kulevrins ];
    ammo_limit = [ cannonballs, buckshots, chains ];
    var tab = $("#ammo_tab");
    tab.find("#mortar").html(mortars);
    tab.find("#bombard").html(bombards);
    tab.find("#kulevrin").html(kulevrins);
    
    tab.find("#kulevrin, .icon_kulevrin").attr({title: "Distance: " + shipWrapper.cannonsDist.Kulevrin});
    tab.find("#bombard, .icon_bombard").attr({title: "Distance: " + shipWrapper.cannonsDist.Bombard});
    tab.find("#mortar, .icon_mortar").attr({title: "Distance: " + shipWrapper.cannonsDist.Mortar});
    
    tab.find("#cball").html(cannonballs);
    tab.find("#bshot").html(buckshots);
    tab.find("#chains").html(chains);

    ammo_cannon.slideDown("slow");
}

function animateLostParam() {
    var divs = $("#p_dec, #e_dec");
    divs.removeAttr("hidden");
    
    $(".dec_val").fadeIn(0);
    divs.css("top", "200px");
    divs.animate({top: "-100px"},5000);
    $(".dec_val").fadeOut(6000);
}

function animateWait() {
    var wait = $(".wait")
    wait.removeAttr("hidden");
    wait.animate({opacity: '0.1'}, 2000);
    wait.animate({opacity: '1.0'}, 2000);
}

function animateTask(animFunction, time) {
    console.log("Animating timer start ");
    var anId = setInterval(function() {
	animFunction();
    }, time);
    return anId;
}
    
var waitId;

function animateWaitTask() {
    console.log("Wait Task start");
    waitReset();
    waitId = animateTask(animateWait, 4000);
    animateWait();
}
    
function waitReset() {
    console.log("wait animation stop");
    clearInterval(waitId);
    $(".wait").attr("hidden", "true");
}

function dialogBattleEnd(title_msg, msg) {
    var dialog = $("#dialog");
    dialog.html(msg);
    dialog.dialog({
        title : title_msg,
        modal : true,
        buttons : {
            Ok : function() {
                $(this).dialog("close");
                window.location.href = "/battle_preparing";
            }
        }
    });
}

var fireResultId;
function fireResultTask(audioPlay) {
    console.log("Task fire result timer start ");
    fireResultId = setInterval(function() {
        infoTabUpdate(false, audioPlay);
    }, 2000);
}

var auto_step;
var $surrender_time;
function autoStepTimer() {
    auto_step = auto_step - 1;
    timeUpdate();
    if (auto_step == 0) {
        console.log("Surrender timer stop");
        clearInterval(timerId);
        $surrender_time.html("Timeout!");
    }
};

function timeUpdate() {
    var sec = auto_step % 60;
    var min = auto_step / 60 ^ 0;
    $surrender_time.html(min + ":" + (sec < 10 ? "0" + sec : sec));
}

var timerId;
function autoStepTask() {
    clearInterval(timerId);
    console.log("Surrender timer start ");
    timerId = setInterval(function() {
        autoStepTimer();
    }, 1000);
};

function enableSpinnerNotZero(selector, checkedData) {
    if (checkedData == 0) {
        var elem = $( selector );
        elem.spinner({ disabled: true });
        elem.spinner("value", 0);
    }
}

function ammoCannonsTabDisableByCount(cannons, ammos) {
    $( ".spinner" ).spinner({ disabled: false });
    enableSpinnerNotZero(".s_mortar", cannons.mortars);
    enableSpinnerNotZero(".s_bombard", cannons.bombards);
    enableSpinnerNotZero(".s_kulevrin", cannons.kulevrins);

    enableSpinnerNotZero(".s_ball", ammos.cannonballs);
    enableSpinnerNotZero(".s_bshot", ammos.buckshots);
    enableSpinnerNotZero(".s_chains", ammos.chains);
}

function ammoCannonsTabDisableByDist(cannonsDist, dist) {
    if (cannonsDist.Mortar < dist) {
        $( ".s_mortar" ).spinner({ disabled: true });
    }
    if (cannonsDist.Bombard < dist) {
        $( ".s_bombard" ).spinner({ disabled: true });
    }
    if (cannonsDist.Kulevrin < dist) {
        $( ".s_kulevrin" ).spinner({ disabled: true });
    }
}

function infoTabUpdate(forcibly_, audioPlay) {
    clearInterval(fireResultId);
    console.log("request for fire result");
    $.get("/fire_results", {
        forcibly : forcibly_
    }).done(function(response, status, xhr) {
        console.log("start get result - ship info " + response
                + " status " + xhr.status);
        
        var n = response.search(/<html>/i);
        if (n != -1) window.location.href = "/login";
        
        var json_obj = JSON.parse(response);

        if (json_obj.end) {
            dialogBattleEnd(json_obj.title, json_obj.wonText);
            return;
        }
        if (json_obj.try_later) {
            fireResultTask(audioPlay);
            return;
        } else {
            if (audioPlay) {
                enemyShotSoundPaly();
            }
        }

        $surrender_time = $("#surrender_time");
        auto_step = json_obj.auto_step_time;
        if (auto_step > 0) {
            autoStepTask();
        }
        timeUpdate();

        tabResultFilling(json_obj);

        if (json_obj.escape_avaliable) {
            enable("escape");
        } else {
            disable("escape");
        }
                
        ammoCannonsTabDisableByDist(json_obj.player_ship.cannonsDist, json_obj.distance);

        console.log("page reloaded after step was made " + json_obj.madeStep);
        if (json_obj.madeStep) {
            infoTabUpdate(true);
            animateWaitTask();
        } else {
            enable("fire");
            enable("surrender");
            if (payoffAvailable) {
                enable("payoff");
            }
            waitReset();
        }
    }).fail(function(xhr, status, error) {
        console.log("getting result ship info FAIL " + xhr.status + " "
                + status);
        if (xhr.status == 405) {
            window.location.href = "/battle_preparing";
        } else {
            $(".wait").html("Server error")
        }
        //waitReset();
    });
}

function warningMsg(msg) {
    console.log("#warning_info " + msg);
    var warn = $("#warning_info");
    warn.html(msg);
    warn.removeAttr("hidden");
}

function isBattleEnd() {
    console.log("is battle end request");
    $.get("/is_battle_end").done(function(response, status, xhr) {
        console.log("is battle end response: " + response);

        var n = response.search(/<html>/i);
        if (n != -1) window.location.href = "/login";
        
        var json_obj = JSON.parse(response);
        if (json_obj.end == "true") {
            clearInterval(battleEndId);
            clearInterval(timerId);
            dialogBattleEnd(json_obj.title, json_obj.wonText);
            disableAllButtons();
        }
        
    }).fail(function(xhr, status, error) {
        console.log("is_battle_end checking FAIL" + error + " " + xhr.status);
        if (xhr.status == 405) {
            window.location.href = "/battle_preparing";
        }
        // window.location.href = "/error";
    });
}

function boarding() {
    console.log("boarding request");
    $("#warning_info").attr("hidden", "true");
    animateWaitTask();
    $.get("/boarding").done(function(response, status, xhr) {
        console.log("boarding response " + response);

        var n = response.search(/<html>/i);
        if (n != -1) window.location.href = "/login";
        boardingSoundPaly();
        isBattleEnd();
    }).fail(function(xhr, status, error) {
        console.log("boarding FAIL" + error + " " + xhr.status);
        if (xhr.status == 405) {
            isBattleEnd();
        }
        // window.location.href = "/error";
    });
}

function boardingAllow() {
    console.log("boarding allow now");
    var boarding_button = $("#boarding");
    enable("boarding");
    boarding_button.click(function() {
        boarding();
    });
}

var battleEndId;

function battleEndTask() {
    console.log("BattleEnd timer start ");
    battleEndId = setInterval(function() {
        isBattleEnd();
    }, 2000);
}

function disableAllButtons() {
    var ids = $(".button_pick");
    for(var i = 0; i < ids.length; i++) {
        disable($(ids[i]).attr("id"));
    }
}

function fire() {
    disable("fire");
    disable("surrender");
    
    $("#warning_info").attr("hidden", "true");
    console.log("fire start !!!");
    animateWaitTask();
    var spinner = $(".spinner");
    var dimensional = $("#ammo_tab tr").length - 2;
    var ammoCannon = new Array(spinner.length);
    for (i = 0; i < spinner.length; i++) {
        ammoCannon[i] = parseInt(spinner[i].value);
        if (spinner[i].value.match(/\D/g) != null 
                || isNaN(ammoCannon[i]) 
                || ammoCannon[i] < 0) {
            var warn = spinner[i].value + " is not allowed value";
            $(spinner[i]).effect( "bounce", "slow" );
            warningMsg(warn);
            waitReset();
            enable("fire");
            enable("surrender");
            return;
        }
        console.log(ammoCannon[i]);
    }
    if (!isCorrectAmmoCannon(ammoCannon, dimensional)) {
        waitReset();
        enable("fire");
        enable("surrender");
        return;
    }
    var convergence = checkBox.prop("checked");
    console.log("Convergence: " + convergence);
    console.log("Data ammoCannon: " + ammoCannon);
    $.post("/fire", {
        "ammoCannon" : ammoCannon,
        "dim" : dimensional,
        "decrease" : convergence
    }).done(function(response, status, xhr) {
        console.log("fire done, now need update table");

        var n = response.search(/<html>/i);
        if (n != -1) window.location.href = "/login";

        disableAllButtons();
        infoTabUpdate(false, true);
        isBattleEnd();
        clearInterval(timerId);
        playerShotSoundPaly();
    }).fail(function(xhr, status, error) {
        console.log("fire FAIL" + error + " " + xhr.status);
        if (xhr.status == 405) {
            // not true decision because battle ended normaly and won message
            // expect.
            // isBattleEnd();
        } else {
            // window.location.href = "/error";
        }
        waitReset();
    });
}

function soundPaly(selector) {
    $(selector).each(function(){
        this.currentTime = 0; // Reset time
        this.play();
    }); 
}
    
function playerShotSoundPaly() {
    soundPaly('#p_shot_audio');
}

function enemyShotSoundPaly() {
    soundPaly('#e_shot_audio');
}

function boardingSoundPaly() {
    soundPaly('#boarding_audio');
}

function anotherEndCase(end_link) {
    console.log(end_link + " request");
    $.get(end_link).done(function(response, status, xhr) {
        console.log(end_link + " response " + response);
        
        var n = xhr.responseText.search(/<html>/i);
        if (n != -1) window.location.href = "/login";
        
        if (response.success) {
            isBattleEnd();
        } else {
             window.location.href = "/error";
        }
    }).fail(function(xhr, status, error) {
        console.log(end_link + " FAIL " + error + " " + xhr.status);
        if (xhr.status == 405) {
            isBattleEnd();
        }
        window.location.href = "/error?reason=" + end_link + " " + xhr.status;
    });
}

function payoff() {
    console.log("payoff request");
    anotherEndCase("/payoff");
}

var checkBox;
var payoffAvailable;
$(document).ready(function() {
    animateWaitTask();
    var spinner = $(".spinner").spinner();
    spinner.click(function() {
        $("#warning_info").attr("hidden", "true");
    });
    spinner.spinner("value", 0);
    spinner.spinner('option', 'min', 0);

    checkBox = $("#check").checkboxradio({
        icon : false
    });

    hoverConveyor();
    $("#fire").click(function(event) {
        fire();
    });
    $("#surrender").click(function(event) {
        anotherEndCase("/surrender");
    });
    $("#escape").click(function(event) {
        anotherEndCase("/escape");
    });

    payoffAvailable = $("body").attr("payoffAvailable");
    console.log("payoffAvailable: " + payoffAvailable);
    if (payoffAvailable == "true") {
        enable("payoff");
        $("#payoff").click(function(event) {
            payoff();
        });
    }

    infoTabUpdate(true);
    battleEndTask();
    soundButton("#audio");
    scrollBars();
    
    $( "td:has(.button_pick), .icon" ).tooltip({
        hide: { effect: "fadeOut", duration: 500 },
        show: { effect: "fadeIn", duration: 1000, delay: 500 },
        content: function () {return $( this ).attr("title")},
        position: {
            my: "left bottom",
            at: "center top",
            collision: "flip"
        }
    });
});

function scrollBars() {
    $("#myScroll").mCustomScrollbar({
        axis:"y", // vertical scrollbar
        theme:"minimal-dark"
    });
}

function enable(id) {
    console.log("enable #" + id);
    var button = $("#" + id);
    if (!button.prop("disabled")) {
        return;
    }
    button.prop("disabled", false);
    var icon = button.find(".icon_" + id + "_disable");
    icon.removeClass("icon_" + id + "_disable");
    icon.addClass("icon_" + id);
    hoverInit(id);
}

function disable(id) {
    console.log("disable #" + id);
    var button = $("#" + id);
    if (button.prop("disabled")) {
        return;
    }
    button.prop("disabled", true);
    var icon = button.find(".icon_" + id);
    icon.removeClass("icon_" + id);
    icon.removeClass("icon_" + id + "_select");
    icon.addClass("icon_" + id + "_disable");
    button.unbind('hover');
}