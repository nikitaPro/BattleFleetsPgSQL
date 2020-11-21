var seconds;
var timerId;
var enemyReadyId;
function shipChooseTimer() {
    pick_time = pick_time - 1;
    $("#timer").html("Auto pick: " + pick_time + " sec");
    if (pick_time == 0) {
        timeout();
    }
};

function timeout() {
    console.log("Timer choose stop");
    clearInterval(timerId);
    disablePickButtons();
    $("#timer").html("Timeout!");
    $( "#warning_info" ).html("Wait for enemy pick...");/*wait msg*/
    waitEnemyReady();
}
    
function waitEnemyReady() {
    console.log("Wait enemy request");
    $.get("/wait_for_enemy")
    .done(function(response, status, xhr){
        console.log("Wait enemy response " + response);
        if (response == "true") {
            window.location.href = "/battle";
        } else if (response == "false") {
            waitEnemyReady();
            // window.location.href = "/error";
        } else {
            window.location.href = "/login";
        }
    }).fail(function(xhr, status, error) {
        console.log("Wait enemy response fail " + xhr.status);
        if (xhr.status == 405) {
            battleExit();
        } else {
            // window.location.href = "/error";
        }
    });
};
    
function pickTimerTask() {
    console.log("Pick timer start ");
    timerId = setInterval(function() {
        shipChooseTimer();
    }, 1000);
};
    
function disablePickButtons() {
    $(".button_pick").off( "mouseenter mouseleave" );
    $(".button_pick").unbind( "click" );
};

var isEnemyLeaveTaskId;

function isEnemyLeave() {
    clearInterval(isEnemyLeaveTaskId);
    console.log("check is Enemy Leave request");
    
    $.get("/is_enemy_leave_battlefield")
    .done(function(response, status, xhr) {
        if (response == "true") {
            battleExit();
        } else if (response == "false") {
            enemyLeaveCheckTask();
        } else {
            window.location.href = "/login";
        }
    }).fail(function(xhr, status, error) {
        	
    }).always(function(response, status, xhr) {
        console.log(" check is Enemy Leave response: " + response);
    });
}
    
function enemyLeaveCheckTask() {
    isEnemyLeaveTaskId = setInterval(function() {isEnemyLeave();}, 2000);
}

function getRealPickTime() {
    console.log("Request /get_auto_pick_time");
    var time = performance.now();
    $.get("/get_auto_pick_time")
    .done(function(response, status, xhr) {
        console.log(response);
        time = (performance.now() - time) / 2.0;
        console.log("Request time = " + time);
        pick_time = pick_time - Math.round(time / 1000.0);
        if (pick_time <= 0) {
            timeout();
        }
    }).fail(function(xhr, status, error) {
        if (xhr.status == 423) {
            timeout();
        }
        console.log("get_auto_pick_time FAIL " + xhr.status);
    });
}
    
$(document).ready(function() {
    pick_time = $("body").attr("timer");
    $("#timer").html("Auto pick: " + pick_time + " sec");
    pickTimerTask();
    $(".button_pick").click(function() { 
        $( this ).find(".icon_pick").addClass( "icon_pick_select" );
        $( this ).find(".icon_pick").removeClass( "icon_pick_hover" );
        $( this ).find(".icon_pick").removeClass( "icon_pick" );
        	
        var ship_id = $(this).attr("value");
        var name = $(this).attr("name");
        var ship = new Object();
        ship[name] = ship_id;
        console.log("Pick Timer stop ");
        clearInterval(timerId);
        console.log("Pick ship - requerst ");
        $.post("/pick_ship", ship)
        .done(function(response, status, xhr){
            console.log("Pick ship - response " + response);
            
            var n = response.search(/<html>/i);
            if (n != -1) window.location.href = "/login";
            
            $( "#warning_info" ).html(response);
            $('html, body').animate({
                scrollTop: $("#warning_info").offset().top
            }, 1000);
            waitEnemyReady();
        })
        .fail(function(xhr, status, error) {
            console.log("Pick User response fail" + status);
            if (xhr.status ==405) {
                battleExit();
            }
            // window.location.href = "/error";
        });
        disablePickButtons();
    });
    getRealPickTime(); 
    soundButton("#audio");
    scrollBars();
});

function scrollBars() {
    $("#myScroll").mCustomScrollbar({
        axis:"y", // vertical scrollbar
        theme:"minimal-dark"
    });
}

function battleExit() {
    clearInterval(timerId);
    console.log("request for battlefield exit");
    $.get("/battlefield_exit")
    .done(function(response, status, xhr){
        if (response == "true") {
            console.log("response for battlefield exit: " + response);
            window.location.href = "/trip";
        } else if (response == "false") {
            alert("No! You cannot leave!");
        } else {
            window.location.href = "/login";
        }
    }).fail(function(xhr, status, error) {
        console.log("response for battlefield exit FAIL " + xhr.status);
        if (xhr.status == 405) {
            window.location.href = "/trip";
        } else {
            window.location.href = "/error";
        }
    });
};
	
function isExitAvailable() {
    $.get("/is_exit_available")
    .done(function(response, status, xhr){
        console.log("response for exit available: " + response);
        if (response == "true") {
            var exit = $("#exit");
            exit.removeAttr("disabled");
            var exit_icon = exit.find(".icon_exit_disable");
            exit_icon.removeClass("icon_exit_disable");
            exit_icon.addClass("icon_exit");
            hoverInit("exit");
            $(".button_exit").click(function() { 
                battleExit();
            });
        } else if (response == "false") {
            
        } else {
            window.location.href = "/login";
        }
    }).fail(function(xhr, status, error) {
        console.log("response for exit available FAIL " + xhr.status);
        if (xhr.status == 405) {
            battleExit();
        }
    });
};
	
$(document).ready(function(){
    var block = $(".ship_accordion");
    block.accordion({heightStyle: "content"});
    block.accordion({ collapsible: true});
    block.accordion({ active: false });
    $(".button_pick").hover(function() {
        $( this ).find(".icon_pick").addClass( "icon_pick_hover" );
    }, function() {
        $( this ).find(".icon_pick").removeClass( "icon_pick_hover" );
    });
    console.log("request for exit available");
    isExitAvailable();
    
    $( "td[title]" ).tooltip({
        content: function () {return $( this ).attr("title")},
        position: {
            my: "center bottom",
            at: "center top-10",
            collision: "flip"
        }
    });
    
    enemyLeaveCheckTask();
});