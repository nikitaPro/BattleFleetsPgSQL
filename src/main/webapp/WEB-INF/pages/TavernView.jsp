<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <link href="static/css/text.css" rel="stylesheet" media="screen">
    <link href="static/css/tavern.css" rel="stylesheet" media="screen">
    <link href="static/css/general.css" rel="stylesheet" media="screen">
    <link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />

    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/jquery-ui.min.js"></script>
    <script src="static/js/volume.js" type="text/javascript"></script>
    <script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>
</head>

<body>

    <audio autoplay id="piratesSong" onloadeddata="setVolume('piratesSong', 0.05);">
        <source src="static/audio/pirates_song.mp3" type="audio/mp3">
    </audio>
<div id="myScroll" style="height: 100%">   
    <div align="center" id="res">
        <h1 class="titleText">${city}</h1>
    </div>
    
    <c:import url="/addHeader"/>

    <form method="get">
    <table align="center"  id="oneShip" style="display: none; margin-top: 30px;" class="tableClass1"></table>
        <div align="center">
            <c:if test="${empty ships}">
                <table class="panelTavern">
                    <tr align="center">
                        <td >
                            <p style="font-size:40px;height:10px;margin-top:10px; font-family: tempus sans itc; color:white;">You don't have any ships</p>
                        </td>
                    </tr>
                </table>
            </c:if>
            <c:if test="${not empty ships}">
                <div id="cont" class="shipContainer">
                  <c:if test="${ships.size()==1}">
                    <table class="tableClass1">
                  </c:if>
                  <c:if test="${ships.size()==2}">
                    <table class="tableClass2">
                  </c:if>
                  <c:if test="${ships.size()>=3}">
                     <table class="tableClass3">
                  </c:if>
                         <tr>
                         <c:forEach items = "${ships}" var = "nextShip">
                             <c:if test="${nextShip.curSailorsQuantity==nextShip.maxSailorsQuantity ||(nextShip.curSailorsQuantity!=nextShip.maxSailorsQuantity && money<sailorCost)}">
                                 <td class="listOfShips" valign="top">
                             </c:if>
                             <c:if test="${nextShip.curSailorsQuantity!=nextShip.maxSailorsQuantity && money>=sailorCost}">
                                  <td class="listOfShips" valign="top" id="Id${nextShip.shipId}" value="${nextShip.shipId}" style="cursor: pointer; background: linear-gradient(to top, #520000 , #030009)" onclick="toggle(sailors,cont,buy,oneShip),show(Id${nextShip.shipId}), maxValue(${nextShip.shipId}),btnSetValue(${nextShip.shipId})">
                             </c:if>
                             <p align="center">${nextShip.curName}</p>
                             <c:choose>
                                 <c:when test = "${nextShip.templateId == 1}">
                                     <img src = "static/images/ships/Caravela.png">
                                 </c:when>
                                 <c:when test = "${nextShip.templateId == 2}">
                                     <img src = "static\images\ships\Caracca.png">
                                 </c:when>
                                 <c:when test = "${nextShip.templateId == 3}">
                                     <img src = "static/images/ships/Galion.png">
                                 </c:when>
                                 <c:when test = "${nextShip.templateId == 4}">
                                     <img src = "static/images/ships/Clipper.png">
                                 </c:when>
                                 <c:when test = "${nextShip.templateId == 5}">
                                     <img src = "static/images/ships/Fregata.png">
                                 </c:when>
                             </c:choose>
                             <p>Health: ${nextShip.curHealth}/${nextShip.maxHealth}</p>
                             <p>Crew: <span id="crew">${nextShip.curSailorsQuantity}</span>/${nextShip.maxSailorsQuantity}</p>
                             </td>
                                        
                         </c:forEach>
                         </tr>
                  </table>
                </div>
                <table class="panelTavern">
                    <tr align="center">
                        <td>
                            <c:if test="${completedShip!=ships.size() && money>=sailorCost}">
                                <span id="info">You can hire sailors on your ships</span>
                            </c:if>
                            <c:if test="${completedShip==ships.size()}">
                                <span id="info">All your ships are staffed with sailors</span>
                            </c:if>
                            <c:if test="${money<sailorCost && completedShip!=ships.size()}">
                                <span id="info">You need ${sailorCost-money} more money</span>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div align="center" id="buy" style="display: none;">
                                <button  class="button" style="vertical-align:middle; padding-right: 10%;" id="shipId" type="submit" onclick="hireSailors()">
                                    <span>Hire</span>
                                </button>
                                <input style="width:35px;" type="text" maxlength="3" class="sailorsNumber" min="1" max="" autocomplete="off" onkeyup="cost(${sailorCost})">
                                <span id="spend" style="font-family: tempus sans itc; color:white;"></span>
                            </div>
                        </td>
                    </tr>
                </table>
            </c:if>

        
        </div>
    
    </form>
    <div align="center" id="sailors"  style="display: none;">
        <button class="buttonBack" id="btnShow" name="showShips" value="showShips" type="submit" onclick="toggle(sailors,cont,buy,oneShip),back()">
            <span>Back</span>
        </button>
    </div>
    <div>
        <%@include file="fragment/footer.jsp"%>
    </div>
</div> 

    <button id="audio" class="icon_sound" type="submit" title="Mute" style="vertical-align:middle"></button>
    <a href="/city" class="logOutBottom">Return to city</a>

    <button class = "helpButton" type = "button" onclick = "openHelp('#tavernInfo')"></button>
    <jsp:include page="fragment/help.jsp" />
</body>


<script>
    var animDuration = 1500;

    function toggle(el1,el2,el3,el4) {
        el1.style.display = (el1.style.display == 'none') ? '' : 'none';
        el2.style.display = (el2.style.display == 'none') ? '' : 'none';
        el3.style.display = (el3.style.display == 'none') ? '' : 'none';
        el4.style.display = (el4.style.display == 'none') ? '' : 'none';
    }
    function show(id) {
        bodyScroll.mCustomScrollbar("scrollTo", "input.sailorsNumber", {
            scrollInertia: animDuration,
            scrollEasing:"easeOut"
        });
        $('#info').html("You can hire sailors on this ship");
        $('#shipId').attr('disabled',false);
        $('#shipId').show();
        $("input.sailorsNumber").show();
        $("#spend").show();
        var $shipFromList = $(id).clone();
        var idf = $(id).attr('id');
        $shipFromList.css('cursor','default');
        $shipFromList.removeAttr('onclick');
        $shipFromList.attr('id','choiceShipCopy');
        $(id).attr('id','choiceShipOrig');
        $('#oneShip').append($shipFromList);
        $(document).ready(function () {
            $('#btnShow').click(function () {
                $('#oneShip').empty();
                $('td#choiceShipOrig.listOfShips p span').attr('id','Crew');
                $(id).attr('id',idf);
            });
        });
    }
    function maxValue(id) {
        $.ajax({
            url:'/maxValue',
            method:"GET",
            data:{'shipId':id},
            success: function (data) {
                $("input.sailorsNumber").attr('max', data[0]).val(data[0]);
                $('#spend').html(data[1]);
            }
        })

    }
    function cost(cost) {
       var val = $("input.sailorsNumber").val();
       var id = $('#shipId').val();
        $.ajax({
               url: '/cost',
               method: "GET",
               data: {'val': val, 'shipId': id},
               success: function (data) {
                   $("input.sailorsNumber").val(data);
                   $("#spend").html(parseInt(data) * cost);
               }
           })
    }
    function hireSailors() {
        event.preventDefault();
        var sailors = $("input.sailorsNumber").val();
        var id = $('#shipId').val();
        if(sailors.match("[0-9]+")){
            $.ajax({
                url:'/hireSailors',
                method:"GET",
                data:{'shipId':id, 'num':sailors},
                success: function(data) {
                    $('#money').html(data[0]);
                    $('span#choiceCrew').html(data[1]);
                    maxValue(id);
                    if (data[2]=='true') {
                        $('#info').html("This ship is stuffed with sailors");
                        $('#choiceShipCopy').css('background','transparent');
                        $('#choiceShipOrig').css('background','transparent');
                        $('#choiceShipOrig').css('cursor','default');
                        $('#choiceShipOrig').removeAttr('onclick');
                        $('#shipId').attr('disabled',true);
                        $('#shipId').hide();
                        $("input.sailorsNumber").hide();
                        $("#spend").hide();
                    } else if(data[3]=='false') {
                         $('#info').html("You need "+parseInt("${sailorCost}"-data[0])+ " more money");
                         $('#choiceShipCopy').css('background','transparent');
                         $('.listOfShips').css('background','transparent');
                         $('.listOfShips').css('cursor','default');
                         $('.listOfShips').removeAttr('onclick');
                         $('#shipId').attr('disabled',true);
                         $('#shipId').hide();
                         $("input.sailorsNumber").hide();
                         $("#spend").hide();
                     }
                },
                error: function (e) {
                    console.log("ERROR",e);
                    maxValue(id);
                }
            } );
        }
        else{
          maxValue(id);
        }
    }
    function back() {
        event.preventDefault();
        $.ajax({
            url: '/allStuffed',
            method: "GET",
            data:{'msg':$('#info').html()},
            success: function (data) {
               $('#info').html(data);
            }
        })
    }
    function btnSetValue(val) {
        $('#shipId').attr('value',val);
        $('td#choiceShipOrig.listOfShips p span').attr('id','choiceCrew');
        $('td#choiceShipCopy.listOfShips p span').attr('id','choiceCrew');
    }

    var bodyScroll;
    function scrollBars() {
        bodyScroll = $("#myScroll").mCustomScrollbar({
            axis:"y", // vertical scrollbar
            theme:"minimal-dark",
            advanced:{ autoScrollOnFocus: false }
        });
        $(".shipContainer").mCustomScrollbar({
            axis:"x", // horizontal scrollbar
            theme:"minimal-dark"
        });
    }

    $(document).ready(function() {
        scrollBars();
        soundButton("#audio");
    });
</script>
</html>