
function myDialog(title_, mes, buttons_) {
    var $wDialog =$( "#dialog_info" ).html(mes);
    $wDialog.dialog({
        modal: true,
        width: 500,
        height: 210,
        title: title_,
        buttons: buttons_
    });
}

function animateCityByClick() {
    $( ".city" ).click(function() {
        var img = $( this ).find("img");
        img.animate({width: "50%", height: "50%"}, 50);
        img.animate({width: "100%", height: "100%"}, 50);
    });
}

function error423(response) {
    var errMessage = $(response).find('.titleText').text();
    var errTitle = $(response).find('.titleText').attr('title');
    
    buttons = [ {
        text: "World Map",
        click: function() {
            $(this).dialog('close');
        }
    }, {
        text: "City",
        click: function() {
            window.location.href = "/city";
            $(this).dialog('close');
        }
    }];
    
    myDialog(errTitle, errMessage, buttons);
}
    
function tripAvailable(cityId) {
    $("#dialog_info").load("travel", function(response, status, xhr){
        if (xhr.status == 200){
            
            var n = response.search(/<html>/i);
            if (n != -1) window.location.href = "/login";
            
            setUpJourney(cityId);
        } else if (xhr.status == 423){
            error423(response);
        } else if (xhr.status == 202) {
            var errMessage = $(response).find('.titleText').text();
            var errTitle = $(response).find('.titleText').attr('title');
            
            buttons = [ {
                id: "Delete",
                text: "It's rubbish! ... Raise the sails!",
                click: function () {
                    setUpJourney(cityId);
                    $(this).dialog('close');
                }
            }, {
                id: "Cancel",
                text: "Back",
                click: function () {
                    window.location.href = "/stock?page=world&city=" + curCityName;
                    $(this).dialog('close');
                }
            }];
            
            myDialog(errTitle, errMessage, buttons);
        }
        return;
    });
}
    
function setUpJourney(cityId) {
    $.post("/relocate", {city_id: cityId})
    .done(function(response, status, xhr){
        var n = response.search(/<html>/i);
        if (n != -1) window.location.href = "/login";
        
        window.location.href = "/trip";
    })
    .fail(function(xhr, status, error) {
        if (xhr.status == 423) {
            error423(xhr.responseText);
        }
    });
}
    
function journeySetUpByClick() {
    $(".city").click(function() { 
        var cityId = $(this).find("input").attr("value");
        tripAvailable(cityId);
    }); 
}
    
function scrollToWorning() {
    $('html, body').animate({
        scrollTop: $("#warning_info").offset().top
    }, 1000);
}
    
function animateCurCity() {
    curCity.animate({opacity: '0.1'}, 1500);
    curCity.animate({opacity: '1.0'}, 1500);
}
    
function animateTask() {
    curCityAnimId = setInterval(function() {
        animateCurCity();
    }, 3000);
}

var curCityAnimId;
var curCity;
var curCityName;
$(document).ready(function() {
    curCityName = $("body").attr("curCityName");
    curCity = $(".city:contains('" + curCityName + "')");
    animateTask();
    animateCityByClick();
    journeySetUpByClick();
    
    var city = $( ".city > img, .city > p" );
    city.animate({width: "110%", height: "110%", opacity: "1"}, 1100);
    city.animate({width: "100%", height: "100%"}, 150);
    
    $( ".city").hover(function() {
        $( this ).find("img").animate({width: "110%", height: "110%"}, 100);
    }, function() {
        $( this ).find("img").animate({width: "100%", height: "100%"}, 100);
    });
    
    $("body").mCustomScrollbar({
        axis:"yx", 
        theme:"minimal-dark"
    });
    
    animateCurCity();
});