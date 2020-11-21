<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<link href="static/css/text.css" rel="stylesheet" media="screen">
<link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">
<link href="static/css/city.css" rel="stylesheet" media="screen">
<link href="static/css/general.css" rel="stylesheet" media="screen">
<link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />

<script src="static/js/jquery.min.js"></script>
<script src="static/js/jquery-ui.min.js"></script>
<script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>

<script type="text/javascript">
    $(document).ready(function() { 
        $("#B_travel").click(function() { 
            window.location.href = "/world?city="+$("#B_travel").val();
        }); 

        $("#myScroll").mCustomScrollbar({
            axis:"y", // vertical scrollbar
            theme:"minimal-dark",
            advanced:{ autoScrollOnFocus: false }
        });
    });
</script>
</head>

    
<body>
<div id = "myScroll" style="height: 100%">
    <div align="center">
    	<h1 class="titleText">${city}</h1>
    </div>
    
    <a href="/logout" class="logOutBottom">Logout</a>
    <c:import url= "/addHeader"/>
    <form method="get">
    <div align="center">
    	<table class="panel">
    	<tr align="center">
    			<td>
    			<button class="button"  style="vertical-align:middle" name="city" type="submit" value="${city}" formaction="/market">
    			<span>Market</span>
    			</button>
    			</td>
    		</tr>
    		<tr align="center">
    			<td>
    			<button class="button" style="vertical-align:middle" name="city" type="submit" value="${city}" formaction="/shipyard">
    			<span>Shipyard</span>
    			</button>
    			</td>
    		</tr>
    		<tr align="center">
    			<td>
    			<button class="button" style="vertical-align:middle" name="tavern" type="submit" value="Tavern ${city}" formaction="/tavern">
    			<span>Tavern</span>
    			</button>
    			</td>
    		</tr>
    		<tr align="center">
    			<td>
    			<button id="B_travel" class="button" style="vertical-align:middle" name="travel" type="button" value="${city}" >
    			<span>Travel</span>
    			</button>
    			</td>
    		</tr>
    		<c:if test="${level>=nextLevel}">
    		<tr align="center">
    			<td>
    				<button class="button" style="vertical-align:middle" name="diff" type="submit" formaction="/update">
    					<span>Update</span>
    				</button>
    			</td>
    		</tr>
    		</c:if>
    	</table>
    </div>
    </form>
    <button class = "helpButton" type = "button" onclick = "openHelp('#cityInfo')"></button>
    <%@include file="fragment/footer.jsp" %>
</div>
<jsp:include page="fragment/help.jsp" />
</body>


</html>