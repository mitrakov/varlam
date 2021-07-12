(function(varlam, $) {
    /**
     * @constructor
     */
    varlam.Common = function() {
        $('#dlg_about').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 700,
            animationType: 'none', //performance issue
            autoOpen: false,
            cancelButton: $('#btn_dlg_about_ok')
        });

        $('#about_project').click(function() {
            $('#dlg_about').jqxWindow('open');
        });
    };
})(window.varlam = window.varlam || {}, jQuery);