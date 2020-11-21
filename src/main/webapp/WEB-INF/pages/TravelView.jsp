<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <title>Trip</title>
    <link href="static/css/text.css" rel="stylesheet" media="screen">
    <link href="static/css/travel.css" rel="stylesheet" media="screen">
    <link href="static/css/general.css" rel="stylesheet" media="screen">
    <link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script src="static/js/jquery.min.js"></script>
    <script src="static/js/jquery-ui.min.js"></script>
    <script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>
    <script src="static/js/IsBattleStartTask.js" type="text/javascript"></script>
    <script src="static/js/volume.js" type="text/javascript"></script>
    <script type="text/javascript">
        var number = 1;
        var city = '${city}';
        var seconds = '${time}';
        var timerId;
        function arrivalTimer() {
            seconds= seconds - 1;
            $("#timer").html("Arrival in " + city + ": " + seconds + " sec");
            if (seconds == 0) {
            	clearInterval(timerId);
            	window.location.href = "/city";
            }
        };
        
        function arrivalTimerTask() {
	        timerId = setInterval(function() {
	        	arrivalTimer();
	        }, 1000);
        };
        
        $(document).ready(function() {
	        $("#timer").html("Arrival in " + city + ": " + seconds + " sec");
            arrivalTimerTask();
            soundButton("#audio");
        });
    </script>
    <script src="static/js/EnemyOnHorizon.js" type="text/javascript"></script>
</head>
<body>
<button id="audio" class="icon_sound" type="submit" title="Mute" style="vertical-align:middle"></button>
<div id="ship" class="layer">
    <img alt="10" src="static/images/travel/ship.png">
</div>
<div>
    <p class="timer" id="timer"></p>
</div>
<audio autoplay id="back_audio" onloadeddata="setVolume('back_audio', 0.1);">
  <source src="static/audio/piraty-karibskogo-morya--original.mp3" type="audio/mp3">
</audio>
<div id="warning_info">

</div>

<div id="error_info">

</div>
<button class = "helpButton" type = "button" onclick = "openHelp('#tripInfo')"></button>
<jsp:include page="fragment/help.jsp" />
</body>
</html>