<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <link href="static/css/text.css" rel="stylesheet" media="screen">
    <link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">
    <link href="static/css/general.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />
    <link href="static/css/battle.css" rel="stylesheet" media="screen">
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script src="static/js/HoverButton.js"></script>
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/jquery-ui.min.js"></script>
	<script src="static/js/volume.js" type="text/javascript"></script>
	<script src="static/js/BattlePreparing.js" type="text/javascript"></script>
    <script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>
    <title>Battle Preparing</title>
</head>

<body timer="${timer}">
    <button id="audio" class="icon_sound" type="submit" title="Mute" style="vertical-align:middle"></button>
    <audio autoplay id="back_audio" onloadeddata="setVolume('back_audio', 0.1)">
        <source src="static/audio/battle_preparing.mp3" type="audio/mp3">
    </audio>
    <div id="myScroll" style="height: 100%">
        <div align="right" class="exit_button_block">
            <button id="exit" disabled="disabled" class="button_exit" style="/* vertical-align:middle; */width: 100%;height: 100%;" name="exit" type="submit">
                <span class="icon_exit_disable"></span><span>Exit</span>
            </button>
        </div>
    	<div align="center" >
            <div id="warning_info" class="titleText" >
                <p class="timer" id="timer" style="color: white;"></p>
            </div>
        </div>
        <div>
        <div class="player_side">
        <h1 class="up_title player_style">Your fleet</h1>
        <div class="ship_accordion" style="float: right">
    		<c:forEach var="shipInfo" items="${fleet}" varStatus="status">
    		    <c:set var = "ship" scope = "page" value = "${shipInfo.ship}"/>
    			<h3 class="player_style ship_accordion_header" style="float: right;">
    				<c:out value="${status.index + 1}."/>
    				<c:out value="${ship.getTName()} : ${ship.getCurName()}"/>
    			</h3>
    			
                <div class="player_style ship_accordion_content" style="float: right;">
    	            <div>
    	                <div class="ship_img">
    						<c:choose>
    							<c:when test="${ship.getTemplateId().intValueExact() == 1}">
    						        <img alt="3" src="static/images/ships/Caravela.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 2}">
    						        <img alt="3" src="static/images/ships/Caracca.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 3}">
    						        <img alt="3" src="static/images/ships/Galion.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 4}">
    						        <img alt="3" src="static/images/ships/Clipper.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 5}">
    						        <img alt="3" src="static/images/ships/Fregata.png" width="100%" height="100%">
    					        </c:when>
    							<c:otherwise>
    						        <img alt="3" src="static/images/ships/Caravela.png" width="100%" height="100%">
    							</c:otherwise>
    						</c:choose>
    					</div>
    					
    					<div class="ship_info">
    					<table style="width: 100%">
    						<tr><td>Health:</td><td>${ship.curHealth}/${ship.maxHealth}</td></tr>
    		                <tr><td>Crew:</td><td>${ship.curSailorsQuantity}/${ship.maxSailorsQuantity}</td></tr>
                            <c:set var = "cannonsPopup" scope = "page" value = "Cannons amount:"/>
    		                <c:forEach var="cannon" items="${shipInfo.cannons}">
                                <c:set var = "cannonsPopup" scope = "page">
                                    ${cannonsPopup}<br>${cannon.key}: ${cannon.value}
                                </c:set>
                            </c:forEach>
    		                <tr><td class="with_tooltip" title="${cannonsPopup}">Damage:</td><td>${ship.curDamage}</td></tr>
                            <c:set var = "mastPopup" scope = "page" value = "Mast speed:"/>
    		                <c:forEach var="mast" items="${shipInfo.masts}">
                                <c:set var = "mastPopup" scope = "page">
                                    ${mastPopup}<br>${mast.templateName}: ${mast.curSpeed}/${mast.maxSpeed}
                                </c:set>
                            </c:forEach>
    		                <tr><td class="with_tooltip" title="${mastPopup}">Speed:</td><td>${ship.curSpeed}</td></tr>
    		                <tr><td>Max dist:</td><td>${shipInfo.maxShotDistance}</td></tr>
                            <c:set var = "ammoPopup" scope = "page" value = "Ammo amount:"/>
                            <c:forEach var="ammo" items="${shipInfo.ammo}">
                                <c:set var = "ammoPopup" scope = "page">
                                    ${ammoPopup}<br>${ammo.name}: ${ammo.quantity}
                                </c:set>
                            </c:forEach>
    		                <tr><td class="with_tooltip" title="${ammoPopup}">Carrying:</td><td>${ship.curCarryingLimit}/${ship.maxCarryingLimit}</td></tr>
    		                <tr><td>Cost:</td><td>${ship.cost}</td></tr>
    		            </table>
    	                </div>
    	                <div style="clear: left"></div>
    	                <div align="center" style="margin-top: 3%;">
                            <button  class="button_pick" style="vertical-align:middle" <c:if test="${ship.shipId == null}">disabled="disabled"</c:if> name="ship_id" type="submit" value="${ship.shipId}">
                                <span class="icon_pick"></span><span class="player_style ship_info" style="float: none">Pick</span>
                            </button>
    			        </div>
                    </div>
                </div>
    		</c:forEach>
    	</div>
    	</div>
    	
    	<div class="enemy_side">
    	<h1 class="up_title enemy_style">${enemy.login}'s fleet</h1>
        <div class="ship_accordion" style="float: left">
    		<c:forEach var="shipInfo" items="${enemy_fleet}" varStatus="status">
    		    <c:set var = "ship" scope = "page" value = "${shipInfo}"/>
    			<h3 class="enemy_style ship_accordion_header" style="float: left;">
    				<c:out value="${status.index + 1}."/>
    				<c:out value="${ship.getTName()} : ${ship.getCurName()}"/>
    			</h3>
    			
                <div class="enemy_style ship_accordion_content" style="float: left">
    	            <div>
    	                <div class="ship_img">
    						<c:choose>
    							<c:when test="${ship.getTemplateId().intValueExact() == 1}">
    						        <img alt="3" src="static/images/ships/Caravela.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 2}">
    						        <img alt="3" src="static/images/ships/Caracca.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 3}">
    						        <img alt="3" src="static/images/ships/Galion.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 4}">
    						        <img alt="3" src="static/images/ships/Clipper.png" width="100%" height="100%">
    					        </c:when>
    					        <c:when test="${ship.getTemplateId().intValueExact() == 5}">
    						        <img alt="3" src="static/images/ships/Fregata.png" width="100%" height="100%">
    					        </c:when>
    							<c:otherwise>
    						        <img alt="3" src="static/images/ships/Caravela.png" width="100%" height="100%">
    							</c:otherwise>
    						</c:choose>
    					</div>
    					<div class="ship_info" style="float:left;">
                        <table style="width: 100%">
                            <tr><td>Health:</td><td>${ship.curHealth}/${ship.maxHealth}</td></tr>
                            <tr><td>Crew:</td><td>${ship.curSailorsQuantity}/${ship.maxSailorsQuantity}</td></tr>
                            <tr><td>Damage:</td><td>${ship.curDamage}</td></tr>
                            <tr><td>Speed:</td><td>${ship.curSpeed}</td></tr>
                            <tr><td>Carrying:</td><td>${ship.curCarryingLimit}/${ship.maxCarryingLimit}</td></tr>
                        </table>
    	                </div>
                    </div>
                </div>
    		</c:forEach>
    	</div>
    	</div>
    	</div>
    	<div style="clear: both;"></div>

    </div>
<button class = "helpButton" type = "button" onclick = "openHelp('#battleInfo')"></button>
<jsp:include page="fragment/help.jsp" />
    
</body>
</html>