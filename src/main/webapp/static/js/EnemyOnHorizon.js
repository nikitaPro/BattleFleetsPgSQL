var $dialog_hint
var decisionId;
var acceptDecision = false;
var lookoutId;
function isDecisionAccept() {
    clearInterval(decisionId);
    var n = number++;
    console.log("id decision accept? request *" + n);
    $.get("/is_decision_accept")
    .done(function(response, status, xhr) {
        console.log("id decision accept? response done " + response + " *" + n);
        if (response == "true") {
            if (!acceptDecision) {
                arrivalTimerTask();
	        lookoutTask();
            }
            $dialog_hint.dialog('close');
        } else {
            isDecisionAcceptTask();
        }
    }).fail(function(xhr, status, error) {
        console.log("id decision accept? response fail " + status + " *" + n);
        if (xhr.status == 405) {
            console.log("reason: " + xhr.status + "player not found in travel");
            window.location.href = "/city";
        }
        
    });
};
        
function isDecisionAcceptTask() {
    clearInterval(decisionId);
    decisionId = setInterval(function() {isDecisionAccept();}, 1000);
};
        
function lookout() {
    var n = number++;
    console.log("request /is_enemy_on_horizon *" + n);
    clearInterval(lookoutId);
    $.get("/is_enemy_on_horizon")
    .done(function(response, status, xhr) {
        console.log("response /is_enemy_on_horizon");
        console.log(xhr);
        if (xhr.status == 204) {
            return;
        }
        if (response == "true") {
            clearInterval(timerId);
            clearInterval(lookoutId);
            isDecisionAcceptTask();
            $dialog_hint = $( "#warning_info" ).dialog({
                modal: true,
                title: "Captain! Fleet on the horizon",
                width: 670,
                height: 200,
                buttons: [{
                    id: "Accept",
                    text: "Attack, stupid ship rats!",
                    click: function () {
                        console.log("ACCEPT *" + n);
                        acceptDecision = true;
                        $.post("/attack_decision", {decision: "true"})
                        .done(function(response, status, xhr) {
                            /*clearInterval(decisionId);
                            clearInterval(battleStartId);*/
                            /*window.location.href = "/battle_preparing";*/
                        }).fail(function(xhr, status, error) {
                            if (xhr.status == 405) {
                                $( "#error_info" ).html(error + " " + xhr.responseText);
                                clearInterval(lookoutId);
                                window.location.href = "/city";
                            } else {
                                window.location.href = "/error";
                            }
                        });
                    }
                }, {
                    id: "Cancel",
                    text: "Shut up! I hope they are blind.",
                    click: function () {
                        console.log("REJECT *" + n);
                        $.post("/attack_decision", {decision: "false"});
                    }
                }]
            });
        } else if (response == "false"){
            lookoutTask();
        } else {
            window.location.href = "/login";
        }
    })
    .fail(function(xhr, status, error) {
        if (xhr.status == 405) {
            clearInterval(lookoutId);
            window.location.href = "/city";
        } else {
            lookoutTask();/*already arrived*/
        }
    });
}
        
function lookoutTask() {
    clearInterval(lookoutId);
    lookoutId = setInterval(function () {lookout();}, 1000);
};
        
$(document).ready(function() {
    lookoutTask();
});