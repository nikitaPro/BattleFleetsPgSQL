var dialogHelp;
$(document).ready(function() {
    dialogHelp = $( "#dialogHelp" );
    dialogHelp.dialog({
        autoOpen: false,
        resizable: false,
        title: null,
        height: 500,
        width: 600,
        buttons: [{
            id: "btnDialog",
            text: "OK",
            click: function() {
                $( this ).dialog( "close" );
            }
        }],
        modal: true,
        create: function(){
            $("#btnDialog").hide();
        }
    });
});   

function openHelp(selector){
    console.log("open help click");
    dialogHelp.dialog({
        open: function(){
            $("#dialogHelp").mCustomScrollbar("scrollTo",$(selector).delay( 500 ),{
                scrollInertia: 1000,
                scrollEasing:"easeOut"
            });
            $('.ui-widget-overlay').bind('click',function(){
                $('#dialogHelp').dialog('close');
            });
        }
    });
    
    dialogHelp.dialog("open");       
}