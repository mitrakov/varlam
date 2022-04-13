(function (varlam, $) {
    /**
     * Try now dialog
     * @constructor
     */
    varlam.TryNow = function () {

        // init the dialog
        $('#dlg_try_now').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            keyboardCloseKey : '',
            animationType: 'none', //performance issue
            autoOpen: false
        }).on('close', function() {
                $('#try_now_ok').click();
            });

        // on TryNow button click it opens the dialog
        $('#try_now').click(function () {
            $('#dlg_try_now').jqxWindow('open');
            new varlam.Request('/try', '', 'POST').done(function (data) {
                    var json = JSON.parse(data);
                    $.localStorage.set('username', json.username);
                    $.localStorage.set('password', json.password);
                    $.localStorage.set('token', json.token);
                });
        });

        // on OK button it opens main page
        $('#try_now_ok').click(function() {
            window.location = 'main.html';
        });
    };
})(window.varlam = window.varlam || {}, jQuery);
