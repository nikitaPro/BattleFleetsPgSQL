
<script src="static/js/help.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $("#dialogHelp").mCustomScrollbar({
            axis:"y", // vertical scrollbar
            theme:"minimal-dark",
            advanced:{ autoScrollOnFocus: false }
        });
    });
</script>


 <div id = "dialogHelp" class="helpContent" style="z-index: -5" hidden="hidden">
    <div>
      <div > <a id ="cityInfo" name="cityInfo"></a>
        <h1>
        City
        </h1>
        <div class = "helpText">
            This is a peaceful pier of every captain. In the city you can conduct trade transactions, sell booty, hire new sailors into the team and, of course, buy and equip new ships and repair those that have suffered in battle.<p/>
            By clicking on the "Market" menu you will go to the page of the city market.<p/>
            By clicking on the "Shipyard" menu, you will go to the page of the shipyard, where you can manage the status of your fleet.<p/>
            By clicking on the "Tavern" menu, you can hire new sailors if there are not enough people on your ships.<p/>
            By clicking on the "Trip" menu, you will go to the world map page, where you can select the city to which you want to send your sails.<p/>
            Also, every 5 levels, when the captain gets enough experience, he can use the "Update" menu to get a valuable reward.<p/>
        </div>
      </div>

      <div> <a id ="marketInfo" name="marketInfo"></a>
        <h1>
        Market
        </h1>
        <div class = "helpText">
            The main income the captain receives from trade transactions that are conducted on the market. There are 2 types of goods: general consumer goods and goods for building the combat power of the fleet (cannons, masts, ammo). <p/>
            The prices for the combat equipment of ships are surprisingly stable, therefore it is better to earn on general goods such as grain, tea or rum.<p/>
            Each city has its own independent economy, and therefore the proposals in the markets of different cities for the same commodity of general consumption can be completely different. To earn money, captains buy goods in one city, and then in other cities they resell goods at the best price. The quantity of all goods, except for ammo, is limited, which means that the product can end if it is in great demand. If the goods are sold by the captains themselves, its quantity on the market grows.<p/>
            The market page provides 2 areas - first, a stock area in which all the goods that are in stock at the captain's disposal and which he can sell, and second - a counter on which goods that can be bought are located.<p/>
            In order to buy or sell goods, you need to put the cursor on it and click, then the purchase dialog box appears. All goods you bought will appear in stock.<p/>
            Attention, damaged masts can not be sold! You need to fix it before you sell it.<p/>
        </div>
      </div>

      <div> <a id ="shipyardInfo" name="shipyardInfo"></a>
        <h1>
        Shipyard
        </h1>
        <div class = "helpText">
            In the shipyard the captain manages his fleet. He can buy ships if there is a necessary amount of money and a place in the fleet. Can sell ships. Or repair the ship if it was damaged during the battle.<p/>
            Also from the shipyard you can get to the captain's stock in the current city.<p/>
        </div>
      </div>

      <div> <a id ="tavernInfo" name="tavernInfo"></a>
        <h1>
        Tavern
        </h1>
        <div class = "helpText">
            There is a team of sailors who control the ship and take part in boarding on every ship. Unfortunately, the sailors are killed while boarding so the ships need replenishment of the team. You can hire sailors in a tavern.<p/>
            Entering the tavern you can see the list of your ships and those that lack the crew are highlighted. You can click on the highlighted ship, select the number of sailors and hire them.<p/>
        </div>
      </div>

      <div> <a id ="stockInfo"  name="stockInfo"></a>
        <h1>
        Stock
        </h1>
        <div class = "helpText">
            A warehouse is a place where the captain can store his goods while he is in the current city. The warehouse is so huge that it is unlimited in its capacity. The warehouse can be used for any type of goods.<p/>
            After the purchase all the goods fall into the warehouse and are subject to further distribution on ships. The ship has a hold in which it is possible to transport any goods, but the cargo capacity of the ship is limited and depends on the type of the ship. Also, the ship is equipped with a certain number of guns and masts.<p/>
            To distribute the goods you need to go to the warehouse page, select the ship on or from which the cargo will move and click on the product of interest. After that the dialog box for moving will appear.<p/>
        </div>
      </div>

      <div> <a id ="tripInfo" name="tripInfo"></a>
        <h1>
        Trip
        </h1>
        <div class = "helpText">
            To make money for speculation, the captain needs to move between cities in search of the best price. You can do this through the "World Map" page, which displays a map of the world and cities in which the player can get to. The city where the captain is now is flashing on the map.<p/>
            When traveling from one city to another, everything that was in the stock disappears and is not transferred to the stock of another city. If the captain has something left in the stock, and he will try to go to another city, he will receive a warning and the opportunity either to stay in the city with the preservation of goods, or still sail to another city and lose forever what is left in the stock.<p/>
            After selecting the city, the captain enters the travel page, which has a travel end timer. During a trip an enemy fleet may appear on the horizon and a battle may ensue between them.<p/>
            At the end of the countdown, the captain enters the city that was sailing.<p/>
            While traveling from one city to another two captains can notice each other on the horizon and a fight between them can begin. The battle will begin if at least one of the captains has expressed a desire to fight, and if both decide to swim further their journey will continue.<p/>
        </div>
      </div>

      <div> <a id ="battleInfo" name="battleInfo"></a>
      <h1>
       Battle
       </h1>
       <div class = "helpText"> 
            After confirming the entry into battle, the captains will be shown the battle preparation page, which displays the ships of their fleet with a full description of combat power and other characteristics, as well as ships of the enemy fleet with a partial description. The captain must choose one of his ships, which will now fight. After a lapse of a minute, if no choice has been made, the vehicle will automatically be selected.<p/>
            Then the captain is redirected to the battle page. There are 3 areas here.<p/>
            The upper one shows our characteristics and characteristics of the enemy. And also the distance between our ships. The distance affects whether the gun can fire, as it can shoot at a certain distance. Similarly, boarding is possible only if the distance between the ships is zero. There is also a proximity button, clicking on which you turn the approach of your ship to the enemy ship. The speed of approach is influenced by the speed of your ship.<p/>
            The middle panel is the control of which cannon which shells will fire.<p/>
            The bottom panel is the control panel, which has buttons on it to fire, to escape, boarding, buying and surrendering. Just here the countdown to automatic surrender is in progress - in case the captain did not make a move on time.<p/>
            When the battle between these ships is completed, the combat preparations page opens again where you can exit the battlefield or select a new ship.<p/>
       </div>
       </div>

    </div>
</div>