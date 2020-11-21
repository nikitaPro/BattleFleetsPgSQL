<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>

<link href="static/css/text.css" rel="stylesheet" media="screen">
<link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">
<link href="static/css/stock.css" rel="stylesheet" media="screen">
<link href="static/css/general.css" rel="stylesheet" media="screen">
<link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />


<script src="static/js/jquery.min.js"></script>
<script src="static/js/jquery-ui.min.js"></script>
<script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>


<script type="text/javascript">
    var pressedShipId;
    var maxCarryingLimit;
    var curCarryingLimit;
    var maxCannons;
    var curCannons;
    var maxMasts;
    var curMasts;

    $( function() {

        $( "#dialogInfo" ).dialog({
            autoOpen: false,
            resizable: false,
            height: "auto",
            width: "auto",
            modal: true,
            buttons: [{
                  text: "OK",
                  click: function() {
                    $( this ).dialog( "close" );
                  }
            }]
        });

        $( "#dialogGoods" ).dialog({
            autoOpen: false,
            resizable: false,
            height: 300,
            width: 550,
            modal: true
            });

        $( "#dialogInventory" ).dialog({
            autoOpen: false,
            resizable: false,
            height: "auto",
            width: 500,
            modal: true
            });
      } );

    function moveToHold(goodId, quantity, from){
     $.ajax({
        url:'/tohold',
        dataType: "json",
        method: "POST",
        data: { 'cargoId' : goodId, 'cargoQuantity' : quantity, 'shipId' : pressedShipId, 'source': from},
        success: function(data) {
            fillWithGoods(data.hold, "hold");
            if(from == "stock"){
                fillWithGoods(data.stock, "stock");
            } else {
                fillWithGoods(data.inventory, "inventory");
            }
            curCarryingLimit = data.curCarryingLimit;
            curCannons = data.curCannons;
            curMasts = data.curMasts;
            setHoldLimit();
            setInventoryLimit();
            $("#ships #"+pressedShipId+" b#shipDescription").html(curCarryingLimit + "/" + maxCarryingLimit);
            
            scrollBarsForContainers();
        },
        error: function(data){
            $("#dialogInfoContent").html("<b>"+ data.msg +"</b>");
            $("#dialogInfo").dialog( "open" );
        }
        });
    }

    function moveToStock(goodId, quantity, from){
    $.ajax({
        url:'/tostock',
        dataType: "json",
        method:"POST",
        data: { 'cargoId' : goodId, 'cargoQuantity' : quantity, 'shipId' : pressedShipId, 'source': from},
        success: function(data) {
            fillWithGoods(data.stock, "stock");
            if(from == "hold"){
                fillWithGoods(data.hold, "hold");
            } else {
                fillWithGoods(data.inventory, "inventory");
            }
            curCarryingLimit = data.curCarryingLimit;
            curCannons = data.curCannons;
            curMasts = data.curMasts;
            setHoldLimit();
            setInventoryLimit();
            $("#ships #"+pressedShipId+" b#shipDescription").html(curCarryingLimit + "/" + maxCarryingLimit);
            
            scrollBarsForContainers();
        },
        error: function(data){
            $("#dialogInfoContent").html("<b>"+ data.msg +"</b>");
            $("#dialogInfo").dialog( "open" );
        }
        });
    }

    function moveToInventory(goodId, from, type){
     $.ajax({
        url:'/toinventory',
        dataType: "json",
        method:"POST",
        data: { 'cargoId' : goodId, 'shipId' : pressedShipId, 'cargoType': type, 'source': from},
        success: function(data) {
            fillWithGoods(data.inventory, "inventory");
            if(from == "stock"){
                fillWithGoods(data.stock, "stock");
            } else {
                fillWithGoods(data.hold, "hold");
            }
            curCarryingLimit = data.curCarryingLimit;
            curCannons = data.curCannons;
            curMasts = data.curMasts;
            setHoldLimit();
            setInventoryLimit();
            $("#ships #"+pressedShipId+" b#shipDescription").html(curCarryingLimit + "/" + maxCarryingLimit);
            
            scrollBarsForContainers();
        },
        error: function(data){
            $("#dialogInfoContent").html("<b>"+ data.msg +"</b>");
            $("#dialogInfo").dialog( "open" );
        }
        });
    }

function showShipResources(event){
    if(pressedShipId == event.data.shipId) return;
    pressedShipId = event.data.shipId;
    maxCarryingLimit = event.data.maxCarryingLimit;
    curCarryingLimit = event.data.curCarryingLimit;
    maxCannons = event.data.maxCannons;
    maxMasts = event.data.maxMasts;
    $.ajax({
     url:'/shipresources',
     dataType: "json",
     method:"POST",
     data: { 'shipId' : event.data.shipId },
            success: function(data) {
                fillWithGoods(data.hold, "hold");
                fillWithGoods(data.inventory, "inventory");
                curCarryingLimit = data.curCarryingLimit;
                setHoldLimit();
                curCannons = data.curCannons;
                curMasts = data.curMasts;
                setInventoryLimit();
                $("#ships td > div.selectedShipElement").removeClass("selectedShipElement").addClass("element");
                $("#ships td#"+pressedShipId+" > div").removeClass("element").addClass("selectedShipElement");
                
                scrollBarsForContainers();
            }
    });
}


    function getShipHtml(shipId, name, curCarryingLimit, maxCarryingLimit, image){
        var returnValue = "<td align=\"center\"><div id = \"" + shipId +"\" class=\"element\"><p><b class=\"values\">"+
                                  name + "</b> </p><img src = \"" + image + "\"><p>Carrying: <b id=\"shipDescription\">" +
                                  curCarryingLimit + "/" + maxCarryingLimit + "</b> </p></div></td>";
        return returnValue;
        }


    function getGoodsHtml(goodsId, name, description, quantity, type, image){
        var returnValue = "<td align=\"center\"> <div id=\"" + goodsId +"\" class=\"element\"> <p><b class=\"values\">" + name + "</b> </p> <img src = \"" +
                                image + "\"><p><b>" + description + "</b> </p><p>Quantity: <b>" +
                                quantity + " </b></p></div></td>";
        return returnValue;
    }

    function setHoldLimit(){
        $("#holdLimit").empty();
        $("#holdLimit").append(curCarryingLimit + "/" + maxCarryingLimit);
    }

    function setInventoryLimit(){
            $("#inventoryLimit").empty();
            $("#inventoryLimit").append ("Cannons: " + curCannons + "/" + maxCannons +
                                         " Masts: "+ curMasts + "/" + maxMasts);
    }

function checkQuantity(goodsQuantity){
    var returnValue = "true";
    var movingQuantity = $("#moveQuantity").val();
    if (movingQuantity.match(/\D/g) != null) {
        $("#msgGoods").html("Please, enter natural number!");
        returnValue = "false";
    }
    if(movingQuantity < 1) {
        $("#msgGoods").html("You can move not less than 1 unit of goods!");
        returnValue = "false";
    }
    if(movingQuantity > goodsQuantity){
        $("#msgGoods").html("Oops. You have only " + goodsQuantity +"!");
        returnValue = "false";
    }
    return returnValue;
}

function checkQuantityForHold(){
    var returnValue = "true";
    var movingQuantity = $("#moveQuantity").val();
    if(movingQuantity > (maxCarryingLimit-curCarryingLimit)){
        $("#msgGoods").html("Ship cannot hold so many goods! <p/>Only " + (maxCarryingLimit-curCarryingLimit));
        returnValue = "false";
    }
    return returnValue;
}

function moveDialog(event){
console.log("move dialog initiated type:" + event.data.type + " name:" + event.data.name + " from:" + event.data.from);
    if (typeof pressedShipId === 'undefined'){
        $( "#dialogInfo" ).dialog( "open" );
        return;
    }
    if(event.data.type == "GOODS" || event.data.type == "AMMO"){
console.log("move dialog for goods and ammos");
        var dialogGoods = $( "#dialogGoods");
        $("#moveQuantity").prop('max',event.data.quantity);
        $("#moveQuantity").prop('value',event.data.quantity);

        dialogGoods.dialog( "option", "title", "Move "+ event.data.name +"!" );
        $("#totalQuantity").html("" + event.data.quantity);
        if(event.data.from == "stock"){
        console.log("move goods and ammos from stock");
        dialogGoods.dialog( "option", "buttons",
                [{
                    text: "To Hold!" +" (free space: "+(maxCarryingLimit-curCarryingLimit)+")",
                    click: function() {
                    if((checkQuantity(event.data.quantity) == "false") || (checkQuantityForHold() == "false")){ return;}
                    moveToHold(event.data.goodsId, $("#moveQuantity").val(), event.data.from);
                    $("#moveQuantity").val('');
                    $("#msgGoods").empty();
                    dialogGoods.dialog( "close" );
                   }
                },{
                    text: "Cancel",
                    click: function() {
                    $("#moveQuantity").val('');
                    $("#msgGoods").empty();
                    dialogGoods.dialog( "close" );
                    }
                }]
                );

        } else {
        console.log("move goods and ammos from hold");
        dialogGoods.dialog( "option", "buttons",
            [{
                text: "To Stock!",
                click: function() {
                if(checkQuantity(event.data.quantity) == "false") { return; }
                moveToStock(event.data.goodsId, $("#moveQuantity").val(), event.data.from);
                $("#moveQuantity").val('');
                $("#msgGoods").empty();
                dialogGoods.dialog( "close" );
                }
            },{
                text: "Cancel",
                click: function() {
                $("#moveQuantity").val('');
                $("#msgGoods").empty();
                dialogGoods.dialog( "close" );
                }
            }]
            );
        }
        dialogGoods.dialog( "open" );
        return;

    }
    if(event.data.type == "CANNON" || event.data.type == "MAST"){
    console.log("move dialog for cannons and masts");
        var dialogInventory = $( "#dialogInventory");
        dialogInventory.dialog( "option", "title", "Move "+ event.data.name +"!" );

        var equipText = "Equip!"
        var equipLimit;
        if(event.data.type == "CANNON"){
        console.log("move cannon");
            equipText = equipText + " (cannons " + curCannons + "/" + maxCannons + ")";
            equipLimit = maxCannons - curCannons;
        } else {
        console.log("move mast");
            equipText = equipText + " (masts " + curMasts + "/" + maxMasts + ")"
            equipLimit = maxMasts - curMasts;
        }
        var equipLimit

         if(event.data.from == "stock"){
         console.log("move cannons and mast from stock");
                dialogInventory.dialog( "option", "buttons",
                        [{
                            text: "To Hold! " +"(free space: "+(maxCarryingLimit-curCarryingLimit)+")",
                            click: function() {
                            if ((maxCarryingLimit-curCarryingLimit) < 1){
                                $("#msgInventory").html("Ship hold is already full!");
                                return;
                            }
                            moveToHold(event.data.goodsId, 1, event.data.from);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                           }
                        },
                        {
                            text: equipText,
                            click: function() {
                            if(equipLimit<1){
                                $("#msgInventory").html("The ship is already fully equipped!");
                                return;
                            }
                            moveToInventory(event.data.goodsId, event.data.from, event.data.type);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        },{
                            text: "Cancel",
                            click: function() {
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        }]
                        );
                dialogInventory.dialog( "open" );
                return;
         }
         if(event.data.from == "hold"){
         console.log("move cannons and mast from hold");
                dialogInventory.dialog( "option", "buttons",
                        [{
                            text: "To Stock!",
                            click: function() {
                            moveToStock(event.data.goodsId, 1, event.data.from);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                           }
                        },
                        {
                            text: equipText,
                            click: function() {
                            if(equipLimit<1){
                                $("#msgInventory").html("The ship is already fully equipped!");
                                return;
                            }
                            moveToInventory(event.data.goodsId, event.data.from, event.data.type);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        },
                        {
                            text: "Cancel",
                            click: function() {
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        }]
                        );
                dialogInventory.dialog( "open" );
                return;
         }
         if(event.data.from == "inventory"){
         console.log("move cannons and mast from inventory");
                dialogInventory.dialog( "option", "buttons",
                        [{
                            text: "To Hold!" +"(free space:"+(maxCarryingLimit-curCarryingLimit)+")",
                            click: function() {
                            if ((maxCarryingLimit-curCarryingLimit) < 1){
                                $("#msgInventory").html("Ship hold is already full!");
                                return;
                            }
                            moveToHold(event.data.goodsId, 1, event.data.from);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                           }
                        },
                        {
                            text: "To Stock!",
                            click: function() {
                            moveToStock(event.data.goodsId, 1, event.data.from);
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        },
                        {
                            text: "Cancel",
                            click: function() {
                            $("#msgInventory").empty();
                            $(this).dialog( "close" );
                            }
                        }]
                        );
                dialogInventory.dialog( "open" );
                return;
         }
    }
}

function getShipImage(shipTemplateId){
    var returnValue = "";
    switch(shipTemplateId){
    case 1: returnValue = "static/images/ships/Caravela.png"; break;
    case 2: returnValue = "static/images/ships/Caracca.png"; break;
    case 3: returnValue = "static/images/ships/Galion.png"; break;
    case 4: returnValue = "static/images/ships/Clipper.png"; break;
    case 5: returnValue = "static/images/ships/Fregata.png"; break;
    }
    return returnValue;
}

function getGoodsImage(goodsType, goodsTemplateId){
    var returnValue = "";
    switch(goodsType){
        case "GOODS": {
            switch(goodsTemplateId){
                case 17: returnValue = "static/images/market/wood.png"; break;
                case 18: returnValue = "static/images/market/grain.png"; break;
                case 19: returnValue = "static/images/market/tea.png"; break;
                case 20: returnValue = "static/images/market/coffee.png"; break;
                case 21: returnValue = "static/images/market/sugarcane.png"; break;
                case 22: returnValue = "static/images/market/spices.png"; break;
                case 23: returnValue = "static/images/market/tobacco.png"; break;
                case 24: returnValue = "static/images/market/silk.png"; break;
                case 25: returnValue = "static/images/market/rum.png"; break;
                case 26: returnValue = "static/images/market/gems.png"; break;
            }
            break;
        }

        case "AMMO": {
            switch(goodsTemplateId){
                case 14: returnValue = "/static/images/market/cannonball.png"; break;
                case 15: returnValue = "/static/images/market/chain.png"; break;
                case 16: returnValue = "/static/images/market/buckshot.png"; break;
            }
            break;
        }

        case "CANNON": {
            switch(goodsTemplateId){
                case 11: returnValue = "/static/images/market/mortar.png"; break;
                case 12: returnValue = "/static/images/market/bombard.png"; break;
                case 13: returnValue = "/static/images/market/kulevrin.png"; break;
            }
            break;
        }

        case "MAST": {
            switch(goodsTemplateId){
                case 6: returnValue = "/static/images/market/mast_1.png"; break;
                case 7: returnValue = "/static/images/market/mast_2.png"; break;
                case 8: returnValue = "/static/images/market/mast_3.png"; break;
                case 9: returnValue = "/static/images/market/mast_4.png"; break;
                case 10: returnValue = "/static/images/market/mast_5.png"; break;
            }
            break;
        }

    }
    return returnValue;
}

function fillShips(playerShips){
    if(playerShips.length == 0) return;
    $("#ships").empty();

    $.each(JSON.parse(playerShips), function(index, element){
    var image = getShipImage(element.templateId);
    var ship = getShipHtml(element.shipId,
                           element.curName,
                           element.curCarryingLimit,
                           element.maxCarryingLimit,
                           image);
     $("#ships").append(ship);
     $("#"+element.shipId).on("click",{"shipId": element.shipId,
                                       "maxCarryingLimit" : element.maxCarryingLimit,
                                       "curCarryingLimit" : element.curCarryingLimit,
                                       "maxCannons" : element.maxCannonQuantity,
                                       "maxMasts" : element.maxMastsQuantity}, showShipResources);
    });
}


function fillWithGoods(playerGoods, whatToFillId){
    if(playerGoods.length == 0){
        if (typeof pressedShipId === 'undefined'){ return; }
            else {
                $("#"+whatToFillId).children().remove();
                $("#"+whatToFillId).append("<td class=\"noElements\">Thousand devils! It's empty here!</td>");
                return;
            }
    }
    $("#"+whatToFillId).children().remove();
    $.each(playerGoods, function( index, element ) {

    var image = getGoodsImage(element.type, element.goodsTemplateId);

    var good = getGoodsHtml(element.goodsId,
                                 element.name,
                                 element.description,
                                 element.quantity,
                                 element.type,
                                 image);

    $("#"+whatToFillId).append(good);
    });
    $.each(playerGoods, function( index, element ) {
    $("#" + whatToFillId +" #"+element.goodsId).on("click",{"goodsId" : element.goodsId,
                                        "name" : element.name,
                                        "description" : element.description,
                                        "quantity" : element.quantity,
                                        "type" : element.type,
                                        "from" : whatToFillId}, moveDialog);
    });

}
function scrollBars() {
    $("body").mCustomScrollbar({
        axis:"y", // vertical scrollbar
        theme:"minimal-dark"
    });
    scrollBarsForContainers();
}

function scrollBarsForContainers() {
    var scrollContainer = $(".container");
    scrollContainer.mCustomScrollbar("destroy");
    scrollContainer.mCustomScrollbar({
        axis:"x", // horizontal scrollbar
        theme:"minimal-dark"
    });
}

var playerStock = '${playerStock}';
var playerShips = '${playerShips}';

$(document).ready(function () {

    scrollBars();
    fillWithGoods(JSON.parse(playerStock), "stock");
    fillShips(playerShips);

});
</script>
</head>


<body>
<div align="center" >
	<h1 class="titleText">Stock</h1>
</div>
<c:import url="/addHeader"/>
<a id="returnLink" href="/${page}?city=${city}" class="logOutBottom">Return to ${page}</a>

<table style=" table-layout: fixed; width: 70%; margin-left: auto; margin-right: auto;" cellspacing="10" cellpadding="5">
    <tbody>
    <tr>
    <td colspan="2"><div class = "container">
        <table class="tableClass" cellspacing="10" cellpadding="5">
        <tbody>
        <tr id = "stock" align="center">
            <td class="noElements">Your stock is empty!</td>
        </tr>
        </tbody>
        </table></div>
    </td>
    </tr>
    <tr>
    <td >
        <h3 class="smallTitle">Hold<br><b style="font-size: 16px; font-weight: 300;" id = "holdLimit"></b></h3><div class = "container">
        <table class="tableClass" cellspacing="10" cellpadding="5">
        <tbody >
        <tr id = "hold" align="center">
            <td class="noElements">Choose a ship to see its treasures!</td>
        </tr>
        </tbody>
        </table></div>
    </td>
    <td >
        <h3 class="smallTitle">Equipment<br><b style="font-size: 16px; font-weight: 300;" id = "inventoryLimit"></b></h3><div class = "container">
        <table class="tableClass" cellspacing="10" cellpadding="5">
        <tbody>
        <tr id = "inventory" align="center">
            <td class="noElements">Choose a ship to see its equipment!</td>
        </tr>
        </tbody>
        </table></div>
    </td>
    </tr>
    <tr>
    <td colspan="2">
         <h3 class="smallTitle">Ships</h3><div class = "container">
        <table class="tableClass" cellspacing="10" cellpadding="5">
        <tbody>
        <tr id = "ships" align="center">
            <td class="noElements">"You don't have ships yet!"</td>
        </tr>
        </tbody>
        </table></div>
    </td>
    </tr>
    </tbody>
</table>

<div id="dialogGoods">
  <div id = "dialogGoodsContent" align="center">
  <p><b>You have <u id = "totalQuantity"></u> units! <p/> How many you want to move?</b></p>
  <input type="number" name="moveQuantity" id = "moveQuantity" min=1 size="4" value="1"/>
  <p id="msgGoods" style = "color: aqua; font-size: 1em;"></p>
  </div>
</div>

<div id="dialogInventory">
  <div id = "dialogInventoryContent"><p id="msgInventory" style = "color: aqua; font-size: 1em;"></p></div>
</div>

<div id="dialogInfo">
  <div id = "dialogInfoContent"><b>Choose a ship first!</b></div>
</div>

<button class = "helpButton" type = "button" onclick = "openHelp('#stockInfo')"></button>
<%@include file="fragment/footer.jsp" %>
<jsp:include page="fragment/help.jsp" />
</body>
</html>