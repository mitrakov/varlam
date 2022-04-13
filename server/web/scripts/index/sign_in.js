(function (varlam, $) {
    /**
     * Authentication dialog
     * @constructor
     */
    varlam.SignIn = function () {

        // init the dialog
        $('#dlg_sign_in').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            autoOpen: false,
            animationType: 'none', //performance issue
            width: 300
        })  .keypress(function (e) {
                if (e.which === 13) $('#btn_sign_in_ok').click();
            })
            .on('close',function () {
                $(this).jqxValidator('hide');
            })
            .jqxValidator({ rules: [
                // external bug: action must be 'keyup, blur' but the behaviour is incorrect on Tab/Esc keys.
                // so 'keyup' is removed
                {input: '#sign_in_login', message: 'Введите имя', action: 'blur', rule: 'required'},
                {input: '#sign_in_password', message: 'Введите пароль', action: 'blur', rule: 'required'}
            ]})
            .on('validationSuccess', function () {
                new varlam.Request('sign/in', JSON.stringify({
                    username: $('#sign_in_login').val(),
                    password: CryptoJS.SHA256($('#sign_in_password').val()).toString(),
                    client: 'web'
                }), 'PUT').done(function (data) {
                        var json = JSON.parse(data);
                        // first remove temp login & password
                        $.localStorage.set('username', $('#sign_in_login').val());
                        $.localStorage.set('token', json.token);
                        window.location = 'main.html';
                    });
            });

        // on OK button it performs validation
        $('#btn_sign_in_ok').click(function () {
            $('#dlg_sign_in').jqxValidator('validate');
        });

        // on Cancel button it performs validation
        $('#btn_sign_in_cancel').click(function () {
            $('#dlg_sign_in').jqxWindow('close');
        });

        // on Sign_In button click it opens the dialog
        $('#sign_in').click(function () {
            $('#dlg_sign_in').jqxWindow('open');
            $('#sign_in_login').focus();
        });
    };
})(window.varlam = window.varlam || {}, jQuery);
