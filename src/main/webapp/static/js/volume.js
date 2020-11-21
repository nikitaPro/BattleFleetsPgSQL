function setVolume(id, vol) {
    var audio = document.getElementById(id);
    audio.volume = vol;
};

var mute = false;
function soundButton(jq_id) {
    $(jq_id).click(function() {
        mute = !mute;
        $("audio").prop("muted", mute);
        if (mute) {
            $(this).addClass("icon_mute");
        } else {
            $(this).removeClass("icon_mute");
        }
    });
}