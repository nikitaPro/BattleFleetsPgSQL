<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id = "shipTableId">
    <table class="externalBorder">
    <tr>
        <c:forEach items="${shipTemplates}" var="shipTemplates" varStatus="status">
        <td>
                <table class ="tableClass">
                <tr>
                    <td class="center">
                    <button class="button capacity_for_background shipTemplateId" name="shipTemplateId" value="${shipTemplates.getTemplateId()}" onclick="setShipName(this,'${shipTemplates.getTName()}')">
                    <span>Buy ${shipTemplates.getTName()}</span>
                    </button>
                    </td>
                    <td>Cost:  <b class="values">${shipTemplates.getCost()}</b></td>
                    <td>CarryingLimit: <b class="values">${shipTemplates.getMaxCarryingLimit()}</b></td>
                </tr>
                <tr>
                    <td rowspan="3" id = "shipimg">
                    <c:choose>
                         <c:when test = "${shipTemplates.getTemplateId() == 1}">
                            <img src = "static/images/ships/Caravela.png">
                         </c:when>
                         <c:when test = "${shipTemplates.getTemplateId() == 2}">
                             <img src = "static\images\ships\Caracca.png">
                         </c:when>
                         <c:when test = "${shipTemplates.getTemplateId() == 3}">
                            <img src = "static/images/ships/Galion.png">
                         </c:when>
                         <c:when test = "${shipTemplates.getTemplateId() == 4}">
                             <img src = "static/images/ships/Clipper.png">
                         </c:when>
                         <c:when test = "${shipTemplates.getTemplateId() == 5}">
                             <img src = "static/images/ships/Fregata.png">
                         </c:when>
                        <c:otherwise>
                            Ooh.Something go wrong.This Template have not an image:
                        </c:otherwise>
                    </c:choose>
                    </td>
                    <td>Masts:  <b class="values">${shipEquipments.get(status.index).getStartNumMast()}/${shipTemplates.getMaxMastsQuantity()}</b></td>
                    <td>Cannon:  <b class="values">${shipEquipments.get(status.index).getStartNumCannon()}/${shipTemplates.getMaxCannonQuantity()}</b></td>
                </tr>
                <tr>
                    <td>Masts type:  <br><b class="values">${startTypeOfShipEquips.get(status.index).getTypeMastName()}</b></td>
                    <td>Cannons type:  <b class="values">${startTypeOfShipEquips.get(status.index).getTypeCannonName()}</b></td>
                </tr>
                <tr>
                    <td>Crew:  <b class="values">${shipTemplates.getMaxSailorsQuantity()}</b></td>
                    <td>Health:  <b class="values">${shipTemplates.getMaxHealth()}</b></td>
                </tr>
                </table>
        </td>
        </c:forEach>
    </tr>
    </table>
</div>

<div id="setNameModal">
	<p class="big_text">Max length 20. You can use English letters, numbers, space and underscore.</p>
    <input class = "capacity_for_background values" id="setNameText" autofocus="autofocus" name="inputShipName">
</div>

<div id="message">
</div>



<script>

var btn = document.getElementById("shipTemplateId");

var setShipButton = document.getElementById("setShipButton");
var setNewNameButton = document.getElementById("setShipButton");

var small_text = $("p.big_text");

var currentElem = 0;
var currentDefaultName = "";

var opt = {
    autoOpen: false,
    resizable: false,
    height: 300,
    width: 550,
    modal: true,
    title: "How we can call our ship, captain?"
};

$(document).ready(function () {
    document.getElementById('setNameModal').style.display="none";

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
});

function inizializeDialog() {
    $("#setNameModal").dialog(opt).dialog( "option", "buttons",
        [{
           text: "Ok",
           click: function() {
               var shipName = $('#setNameText').val();
               if (confirmNewName(shipName)) {
                   buyShip(currentElem, shipName, currentDefaultName);
                   $("#setNameText").val("");
                   $(this).dialog('destroy');
               }
           }
        }, {
            text: "Cancel",
            click: function() {
                $("#setNameText").val("");
                $(this).dialog('destroy');
            }
        }]
    );
}

function confirmNewName(shipName) {
    if (shipName.search(/[^A-z,0-9,\s,_]/g) > -1 || shipName.length > 20 || shipName.length == 0) {
        small_text.effect( "bounce", "slow" );
        return false;
    }
    return true;
}

function setShipName(elem, defaultName) {
    currentElem = elem.value;
    currentDefaultName = defaultName;
    inizializeDialog();
    $("#setNameModal").dialog(opt).dialog( "open" );
}

function buyShip(elem, shipName, defaultName) {
    var shipTemplateId = elem;
    $(function(){
        $.ajax({
            url:'/buy',
            method:"GET",
            data: { 'shipTemplateId' : shipTemplateId, 'shipName' : shipName , 'defaultName' : defaultName },
            beforeSend: function() {
                $('.modal').show();
            },
            success: function(data) {
                console.log("SUCCESS: ",data);
                $("#dialogInfoContent").text(data);
                $("#dialogInfo").dialog("open");
            },
            complete: function() {
                $('.modal').hide();
            },
             error : function(e) {
                 console.log("ERROR: ", e);
                 window.location.href = "/error";
             }
                         })
             .done(function() {
                headerUpdate();
            } );
    });
}
</script>