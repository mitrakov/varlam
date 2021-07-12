(function (varlam, $) {

    $(function() {
        $('#dlg_yes_no').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 300,
            height: 150,
            animationType: 'none', //performance issue
            autoOpen: false,
            cancelButton: $('#dlg_yes_no_no')
        });
    });

    /**
     * Shows the dialog with Yes/No buttons
     * @param {string} title dialog caption
     * @param {string} msg dialog main text
     * @param {function} onYesFunction function with no arguments to execute when 'YES' clicked
     * @param {string} parent parent window selector
     * @constructor
     */
    varlam.ShowYesNoDialog = function (title, msg, onYesFunction, parent) {
        $('#dlg_yes_no_title').text(title);
        $('#dlg_yes_no_msg').text(msg);
        $('#dlg_yes_no_yes').click(function() {
            onYesFunction();
            $dlg.jqxWindow('close');
        });
        $(parent).fadeOut('fast');
        var $dlg = $('#dlg_yes_no').on('close', function () {
            $('#dlg_yes_no_yes').off('click');
            $('#dlg_yes_no').off('close');
            $(parent).fadeIn('fast').focus();
        });
        $dlg.jqxWindow('open');
        $dlg.focus();
    }
})(window.varlam = window.varlam || {}, jQuery);
