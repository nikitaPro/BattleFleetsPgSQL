<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<link href="static/css/text.css" rel="stylesheet" media="screen">
<link href="static/css/general.css" rel="stylesheet" media="screen">
<link href="static/css/update.css" rel="stylesheet" media="screen">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js"></script>
<script src="static/js/jquery.min.js"></script>
<script src="static/js/jquery-ui.min.js"></script>
<link href="static/bootstrap-3.3.7/css/bootstrap.css" rel="stylesheet">
<link href="static/bootstrap-3.3.7/css/bootstrap-theme.css" rel="stylesheet">
<script src="static/bootstrap-3.3.7/js/bootstrap.js"></script>
<html>
<c:import url="/addHeader"/>
<div align="center" id="congratulate">
    <h1 class="titleText">Select what you want to improve</h1>
</div>
<body>
<a href="/city" class="logOutBottom">Return to city</a>
<form method="get">
<div align="center">

<c:if test="${lvl<nextImprove}">
<table style="padding:1%" class="panel">
</c:if>
<c:if test="${lvl>=nextImprove}">
<table class="panel">
</c:if>
    <tr align="center">
        <td id="endOfUpdate">
            <c:if test="${lvl>=nextImprove}">
            <button id="btnShip" class="button"  style="vertical-align:middle" type="submit" onclick="shipUp()">
                <span style="font-size:21px">Ship +1</span>
            </button>
            </c:if>
            <c:if test="${lvl<nextImprove}">
            <p style="color:white; font-size:30px">Now, you don't have any improvements</p>
            </c:if>
        </td>
    </tr>
    <tr align="center">
        <td>
            <c:if test="${lvl>=nextImprove}">
            <button id="btnIncome" class="button"  style="vertical-align:middle" type="submit" onclick="incomeUp()">
                <span style="font-size:22px">Income +50</span>
            </button>
            </c:if>
        </td>
    </tr>
    <input type="hidden" id="lvl" value="${lvl}">
    <input type="hidden" id="imp" value="${nextImprove}">
    <input type="hidden" id="maxLvl" value="${maxLvl}">
</table>
</div>
</form>
</body>
<div id="exit"></div>
<script>
    function shipUp(){
        event.preventDefault();
        $.ajax({
            url:'/shipUp',
            method:"GET",
            success: function(data) {
                    var nxt = 5;
                    $('#congratulate').html("<h1 class='titleText'>"+data[0]+"</h1>");
                    $('#maxShips').html(data[1]);
                    $('#improve').html(data[2]);
                    $('#imp').val(parseInt($('#imp').val())+nxt);
                    if(parseInt($('#imp').val())>parseInt($('#lvl').val())){
                        $('#btnShip').attr('disabled',true);
                        $('#btnIncome').attr('disabled',true);
                        $('#btnShip').hide();
                        $('#btnIncome').hide();
                        $('.panel').css('padding','1%');
                        $('#endOfUpdate').html("<p style=\"color:white; font-size:30px\">"+"Now, you don't have any improvements"+"</p>");
                        $('#exit').html("<a href='/city' class='logOutBottom'>"+"Return to city"+"</a>");
                        if(parseInt($('#imp').val())>parseInt($('#maxLvl').val())) {
                            $('#improveWrapper').css('display', 'none');
                        }
                    }
            },
            error : function(e) {
                console.log("ERROR",e);
                window.location.href="/city";
            }
        } );
    }
    function incomeUp() {
        event.preventDefault();
        $.ajax({
            url:'/incomeUp',
            method:"GET",
            success: function(data) {
                    var nxt = 5;
                    $('#congratulate').html("<h1 class='titleText'>"+data[0]+"</h1>");
                    $('#income').html(data[1]);
                    $('#improve').html(data[2]);
                    $('#imp').val(parseInt($('#imp').val())+nxt);
                    if(parseInt($('#imp').val())>parseInt($('#lvl').val())){
                        $('#btnShip').attr('disabled',true);
                        $('#btnIncome').attr('disabled',true);
                        $('#btnShip').hide();
                        $('#btnIncome').hide();
                        $('.panel').css('padding','1%');
                        $('#endOfUpdate').html("<p style=\"color:white; font-size:30px\">"+"Now, you don't have any improvements"+"</p>");
                        $('#exit').html("<a href='/city' class='logOutBottom'>"+"Return to city"+"</a>");
                        if(parseInt($('#imp').val())>parseInt($('#maxLvl').val())) {
                            $('#improveWrapper').css('display', 'none');
                        }
                    }
            },
            error : function(e) {
                console.log("ERROR",e);
                window.location.href="/city";
            }
        } );
    }

</script>
<%@include file="fragment/footer.jsp"%>
</html>
