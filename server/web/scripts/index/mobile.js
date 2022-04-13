(function(varlam, $) {
    /**
     * @constructor
     */
    varlam.Mobile = function() {
        $('#dlg_not_implemented').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 300,
            animationType: 'none', //performance issue
            autoOpen: false,
            cancelButton: $('#btn_not_implemented_ok')
        });

        $('#btn_mobile, #lang_en').click(function() {
            $('#dlg_not_implemented').jqxWindow('open');
        });
    };
})(window.varlam = window.varlam || {}, jQuery);
