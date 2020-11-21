    function hoverButton(b_id, rej, hov) {
        $("#" + b_id).hover(function() {
            $( this ).find("." + rej).addClass( hov );
        }, function() {
            $( this ).find("." + rej).removeClass( hov );
        });
    }
    
    function hoverInit(act_name) {
        var icon = "icon_";
        var select = "_select";
        hoverButton(act_name, icon + act_name, icon + act_name + select);
    }