<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <link href="<c:url value="/static/css/text.css" />" rel="stylesheet" />
    <link href="<c:url value="/static/css/general.css" />" rel="stylesheet" />
    <link href="<c:url value="/static/css/market.css" />" rel="stylesheet" />
    <link href="<c:url value="/static/css/goods-images.css" />" rel="stylesheet" />
    <link href="<c:url value="/static/bootstrap-3.3.7/css/bootstrap.css" />" rel="stylesheet" />
    <link href="<c:url value="/static/bootstrap-3.3.7/css/bootstrap-theme.css" />" rel="stylesheet" />
   
    <link rel="stylesheet" href="static/css/jquery.mCustomScrollbar.min.css" />
    <link href="static/css/jquery-ui.css" rel="stylesheet" media="screen">

    <script type="text/javascript"  src="<c:url value="/static/js/jquery.min.js" />"></script>
    <script src="static/js/jquery-ui.min.js"></script>
    <script type="text/javascript"  src="<c:url value="/static/js/market.js" />"></script>
    <script src="static/js/jquery.mCustomScrollbar.concat.min.js"></script>
    <!--<script src="static/js/volume.js"></script>-->
    <script type="text/javascript"  src="<c:url value="/static/bootstrap-3.3.7/js/bootstrap.js" />"></script>
    <audio autoplay id="audio-market" onloadeddata="setHalfVolume()">
        <source src="<c:url value="/static/audio/market.mp3" />" type="audio/mp3">
    </audio>
</head>
<body>
<div align="center">
    <h1 class="titleText">Market ${city}</h1>
</div>
<header>
    <p>Money <span id="money"></span></p>
</header>
<a href="/city" class="logOutBottom">Return to city</a>

<!--Modal window for buy-->
<div class="modal fade" id="buyModal" role="dialog" tabindex="-1">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&#x274E;</button>
                <h4 class="modal-title modalText"></h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <p class="col-6 col-md-4 modalText">Price per unit:</p>
                    <p id="oneCount" class="col-6 col-md-4 modalText"></p>
                    <div class="col-6 col-md-4"></div>
                </div>
                <div class="row">
                    <p class="col-6 col-md-4 modalText quantityLimit"></p>
                    <div class="col-6 col-md-4"><input type="number" id="modalQuantity" min="0" max=""></div>
                    <div class="col-6 col-md-4"></div>
                </div>
                <div class="row">
                    <p class="col-6 col-md-4 modalText">Total amount:</p>
                    <p id="allCount" class="col-6 col-md-4 modalText"></p>
                    <div class="col-6 col-md-4"></div>
                </div>
            </div>
            <div class="modal-footer">
                <p id="messageBuy" class="pull-left"></p>
                <button type="button" class="btn buyButton pull-right">Buy</button>
            </div>
        </div>
    </div>
</div>

<!--Modal window for sale-->
<div class="modal fade" id="saleModal" role="dialog" tabindex="-1">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&#x274E;</button>
                <h4 class="modal-title modalText"></h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <p class="col-6 col-md-4 modalText">Price per unit:</p>
                    <p id="oneSaleCount" class="col-6 col-md-4 modalText"></p>
                    <div class="col-6 col-md-4"></div>
                </div>
                <div class="row">
                    <p class="col-6 col-md-4 modalText quantityLimit"></p>
                    <div class="col-6 col-md-4"><input type="number" id="modalSaleQuantity" min="0" max=""></div>
                    <div class="col-6 col-md-4"></div>
                </div>
                <div class="row">
                    <p class="col-6 col-md-4 modalText">Total amount:</p>
                    <p id="allSaleCount" class="col-6 col-md-4 modalText"></p>
                    <div class="col-6 col-md-4"></div>
                </div>
            </div>
            <div class="modal-footer">
                <p id="messageSale" class="pull-left"></p>
                <button type="button" class="btn saleButton pull-right">Sell</button>
            </div>
        </div>
    </div>
</div>

<div class="col-sm-5 panels">
    <h1 class="messageText"><a href="/stock?page=market&city=${city}"  id="toStock">Stock</a></h1>
    <!--<button id="audio" class="icon_sound" type="submit" title="Mute" style="vertical-align:middle"></button>-->
    <div class="table">
        <ul class="nav nav-tabs center-block">
            <li><a href="#" class="saleJson" id="goodSaleJson">Goods</a></li>
            <li><a href="#" class="saleJson" id="ammoSaleJson">Ammo</a></li>
            <li><a href="#" class="saleJson" id="cannonSaleJson">Cannon</a></li>
            <li><a href="#" class="saleJson" id="mastSaleJson">Mast</a></li>
        </ul>
    </div>
    <table class="table table-hover">
        <thead>
        <tr>
            <th></th>
            <th>Product</th>
            <th>Cost</th>
            <th>Quantity</th>
        </tr>
        </thead>
        <tbody id="saleTable"></tbody>
    </table>
</div>
<div class="col-sm-2"></div>

<div class="col-sm-5 panels">
    <h1 class="messageText">Market</h1>
    <div class="table">
        <ul class="nav nav-tabs">
            <li><a href="#" class="buyJson" id="goodBuyJson">Goods</a></li>
            <li><a href="#" class="buyJson" id="ammoBuyJson">Ammo</a></li>
            <li><a href="#" class="buyJson" id="cannonBuyJson">Cannon</a></li>
            <li><a href="#" class="buyJson" id="mastBuyJson">Mast</a></li>
        </ul>
    </div>
    <table class="table table-hover">
        <thead>
        <tr>
            <th></th>
            <th>Product</th>
            <th>Cost</th>
            <th>Quantity</th>
        </tr>
        </thead>
        <tbody id="buyTable"></tbody>
    </table>
</div>
<div id="footer" align="center">
    <div align="center" style="position: relative;">
        <%@include file="fragment/footer.jsp" %>
    </div>
</div>
<button class = "helpButton" type = "button" onclick = "openHelp('#marketInfo')"></button>
<jsp:include page="fragment/help.jsp" />
</body>
</html>