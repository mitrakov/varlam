(function (varlam, $) {
    /**
     * Registration dialog
     * @constructor
     */
    varlam.Registration = function () {
        
        /**
         * All user-specific functions of jqxValidator must return true or false. But name busy checking is performed
         * on the server via AJAX so that it's impossible to return boolean. Therefore we are to always return true and
         * allocate excessive variable to control 'OK' button click
         * @type {boolean}
         */
        var nameIsBusy = true;

        // init the dialog
        $('#dlg_sign_up').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            autoOpen: false,
            animationType: 'none', //performance issue
            width: 300
        })  .keypress(function (e) {
                if (e.which === 13) $('#btn_sign_up_ok').click();
            })
            .on('close', function () {
                $(this).jqxValidator('hide');
            })
            .jqxValidator(
                // external bug: action must be 'keyup, blur' but the behaviour is incorrect on Tab/Esc keys.
                // so 'keyup' is removed
                {rules: [
                    {
                        input: '#sign_up_login',
                        message: 'Введите имя',
                        action: 'blur',
                        rule: 'required'
                    },
                    {
                        input: '#sign_up_login',
                        message: 'Имя должно содержать от 4 до 30 символов',
                        action: 'blur',
                        rule: 'length=4,30'
                    },
                    {
                        input: '#sign_up_login',
                        message: 'Имя занято',
                        action: 'blur',
                        rule: function (elem, commit) {
                            if (elem.val())
                                new varlam.Request('is/busy', {username: elem.val()}).done(function (data) {
                                    nameIsBusy = JSON.parse(data).busy;
                                    commit(!nameIsBusy);
                                });
                            return true;
                        }
                    },
                    {input: '#sign_up_password',
                        message: 'Введите пароль',
                        action: 'blur',
                        rule: 'required'
                    },
                    {
                        input: '#sign_up_password',
                        message: 'Пароль должен содержать от 6 до 30 символов',
                        action: 'blur',
                        rule: 'length=6,30'
                    },
                    {
                        input: '#sign_up_password_confirm',
                        message: 'Введите пароль ещё разок',
                        action: 'blur',
                        rule: 'required'},
                    {
                        input: '#sign_up_password_confirm',
                        message: 'Пароли не совпадают',
                        action: 'blur',
                        rule: function (elem) {
                            return elem.val() === $('#sign_up_password').val();
                        }
                    }
                ]}
            )
            .on('validationSuccess', function () {
                // check if name is busy (see jsDoc)
                if (nameIsBusy) return;
                new varlam.Request('/sign/up', JSON.stringify({
                    username: $('#sign_up_login').val(),
                    password: CryptoJS.SHA256($('#sign_up_password').val()).toString(),
                    client: 'web'
                }), 'POST').done(function (data) {
                        var json = JSON.parse(data);
                        $.localStorage.remove('password');
                        $.localStorage.set('username', $('#sign_up_login').val());
                        $.localStorage.set('token', json.token);
                        $('#dlg_sign_up').jqxWindow('close');
                        window.location = 'main.html';
                    });
            });

        // on OK button click it performs the validation
        $('#btn_sign_up_ok').click(function () {
            $('#dlg_sign_up').jqxValidator('validate')
        });

        // on Cancel button click it closes the dialog
        $('#btn_sign_up_cancel').click(function () {
            $('#dlg_sign_up').jqxWindow('close')
        });

        // on Register button click it opens the dialog
        $('#register').click(function () {
            $('#dlg_sign_up').jqxWindow('open');
            $('#sign_up_login').focus();
        });
    };
})(window.varlam = window.varlam || {}, jQuery);
