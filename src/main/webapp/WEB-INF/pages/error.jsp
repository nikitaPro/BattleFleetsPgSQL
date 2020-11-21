<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <link href="static/css/text.css" rel="stylesheet" media="screen">
    <link href="static/css/general.css" rel="stylesheet" media="screen">
	<style type="text/css">
	html, body {
	    background-image: url('static/images/general/error_background.jpg');
        background-position: bottom; 
        background-repeat: no-repeat; 
        background-size: cover; 
        background-attachment: fixed;
        height: 100%;
	}
	</style>
</head>

<body>
    <c:choose>
        <c:when test="${empty reason or reason.length() == 0}">
            <c:set var = "errorMes" scope = "session" value = "Sorry, application error."/>
        </c:when>
        <c:otherwise>
            <c:set var = "errorMes" scope = "session" value = "Sorry, application error."/>
            <c:set var = "errTitle" scope = "session" value = "${reason}"/>
        </c:otherwise>
    </c:choose>
    
    <jsp:include page="fragment/message.jsp"/>
</body>
</html>