<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id = "shipTableId">
    <c:if test="${playerShips.size()==0}">
            <table class="panel">
             <tr align="center">
              <td >
                <p style="font-size:40px; font-family: tempus sans itc; color:white">You dont't have any ships</p>
              </td>
             </tr>
            </table>
        </c:if>
    <table class = "externalBorder">
    <tr>
    <c:forEach items="${playerShips}" var="shipTemplates" varStatus="status">
        <td>
            <table class ="tableClass">
            <tr>
                <td align="center">
                <button class="capacity_for_background button shipTemplateId" name="shipTemplateId" value="${shipTemplates.getShipId()}" onclick="chooseOfAction(this,'${action}',${shipTemplates.getCost()-shipCosts.get(status.index)*2}, ${shipTemplates.curCarryingLimit})">
                <span>${action} ${shipTemplates.getCurName()}</span>
                </button>
                </td>
                <c:if test = "${action == 'Sell'}">
                    <td class="price">SellingCost:  <b class="values">${shipCosts.get(status.index)}</b></td>
                </c:if>
                <c:if test = "${action == 'Repair'}">
                    <td class="price">RepairCost:  <b class="values"><c:out value = "${shipTemplates.getCost()-shipCosts.get(status.index)*2}"/></b></td>
                </c:if>
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
                <td>Type:  <b class="values">${shipTemplates.getTName()}</b></td>
            </tr>
            <tr>
                <td>Health:  <b class="values">${shipTemplates.getCurHealth()}/${shipTemplates.getMaxHealth()}</b></td>
            </tr>
            <tr>
                <c:if test = "${action == 'Sell'}">
                    <td>Crew:  <b class="values">${shipTemplates.getCurSailorsQuantity()}/${shipTemplates.getMaxSailorsQuantity()}</b></td>
                </c:if>
                <c:if test = "${action == 'Repair'}">
                    <td>Speed:  <b class="values">${shipsSpeed.get(status.index).curSpeed}/${shipsSpeed.get(status.index).maxSpeed}</b></td>
                </c:if>
            </tr>
            <tr>
                <td></td>
                <td>Carrying: <b class="values">${shipTemplates.curCarryingLimit}/${shipTemplates.maxCarryingLimit}</b></td>
            </tr>
            </table>
            </td>
        </c:forEach>
        </tr>
        </table>
</div>

<div id="setConfirmModal" >
    <p class="big_text">That ship have something in hold, captain. We will give it away.</p>
</div>

<script>
var modal = document.getElementById('myModal');
var text = document.getElementById('text');
var btn = document.getElementById("shipTemplateId");
var currentAction = '';
var needUpdate = false;

var setConfirmModal = $("#setConfirmModal");

setConfirmModal.dialog({
    autoOpen: false,
    resizable: false,
    height: 300,
    width: 550,
    modal: true,
});

$(document).ready(function () {
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
                refresh(currentAction);
              }
        }]
    });
});

function refresh(action) {
    if (!needUpdate)
        return;
    headerUpdate();
    if (action == 'Sell')
        showPlayerShips();
    else if (action == 'Repair')
        repairShips();
    else
        console.log('Unnkown action');
}

function chooseOfAction(elem, action, diffcost, carringLimit) {
    if (action == 'Sell') {
        sellConfirm(elem, carringLimit);
        currentAction = 'Sell';
    }
    else if (action == 'Repair') {
        repairShip(elem, diffcost);
        currentAction = 'Repair';
    }
    else {
        console.log('Unnkown action');
        window.location.href = "/error";
    }
}

function sellConfirm(elem, carringLimit) {
    if (carringLimit > 0) {
        setConfirmModal.dialog( "option", "buttons",
            [{
               text: "Ok",
               click: function() {
                   sellship(elem.value);
                   $(this).dialog('close');
                   needUpdate = true;
               }
            }, {
                text: "Cancel",
                click: function() {
                    $(this).dialog('close');
                    needUpdate = false;
                }
            }]
        );
        setConfirmModal.dialog( "open" );
    }
    else
        sellship(elem.value);
}

function sellship(elem) {
var shipId = elem;
    $(function(){
        $.ajax({
            url:'/sell',
            method:"GET",
            data: { 'shipId' : shipId },
            beforeSend: function() {
                $('.modal').show();
            },
            success: function(data) {
                console.log("SUCCESS: ");
                if (data == 'You sold your ship!')
                    needUpdate = true;
                else
                    needUpdate = false;
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
            } );
    });
}

function repairShip(elem, diffcost) {
var shipId = elem.value;
    $(function(){
        $.ajax({
            url:'/repair',
            method:"GET",
            data: { 'shipId' : shipId },
            beforeSend: function() {
                $('.modal').show();
            },
            success: function(data) {
                console.log("SUCCESS: ",data);
                var message = "";
                if (data)
                    if (diffcost == 0) {
                        message="Ship is already repaired";
                        needUpdate = false;
                    }
                else {
                    message="Ship repaired";
                    needUpdate = true;
                }
                else {
                    message="We need more money, captain!";
                    needUpdate = false;
                }
                $("#dialogInfoContent").text(message);
                $("#dialogInfo").dialog("open");
            },
            complete: function() {
                $('.modal').hide();
            },
            error : function(e) {
                console.log("ERROR: ", e);
                window.location.href = "/error";
            }
            } );
    });
}
</script>