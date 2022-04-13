(function (varlam, $) {
    /**
     * Add new person dialog
     * @constructor
     * @param {TreeBuilder} treeBuilder - instance of varlam.TreeBuilder
     */
    varlam.AddPerson = function (treeBuilder) {
        // init the dialog
        $('#dlg_person').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 500,
            height: 350,
            animationType: 'none', //performance issue
            autoOpen: false
        }).on('close', function() {
                $('#txt_add_person_wrapper').hide();
                $('#txt_change_person_wrapper').hide();
                $('#btn_add_person').text('Добавить');
                $('#btn_change_person').text('Изменить');
                treeBuilder.refresh();
            });

        // load person list
        new varlam.Request('person/list').done(this.refresh.bind(this));

        // Add New Person button click
        $('#btn_add_person').click((function () {
            var $this = $('#btn_add_person');
            if ($this.text() === 'OK') {
                $('#txt_add_person_wrapper').hide();
                var name = $('#txt_add_person').val();
                new varlam.Request('person/new', JSON.stringify({name: name}), 'POST').done((function () {
                    new varlam.Request('person/list').done(this.refresh.bind(this));
                }).bind(this));
                $this.text('Добавить');
            } else {
                $('#txt_add_person_wrapper').show();
                $('#txt_add_person').val('').focus();
                $this.text('OK');
            }
        }).bind(this));

        // Change Person button click
        $('#btn_change_person').click((function () {
            var $this = $('#btn_change_person');
            var selected = $('#list_person').jqxListBox('getSelectedItem');
            if (!selected) return;
            var name = selected.label;
            if ($this.text() === 'OK') {
                $('#txt_change_person_wrapper').hide();
                var newName = $('#txt_change_person').val();
                if (name !== newName)
                    new varlam.Request('person/change', JSON.stringify({name: name, newName: newName}), 'PUT')
                        .done((function () {
                            new varlam.Request('person/list').done(this.refresh.bind(this));
                        }).bind(this));
                $this.text('Изменить');
            } else {
                $('#txt_change_person_wrapper').show();
                $('#txt_change_person').val(name).select().focus();
                $this.text('OK');
            }
        }).bind(this));

        // Remove Person button click
        $('#btn_remove_person').click((function () {
            var selected = $('#list_person').jqxListBox('getSelectedItem');
            if (!selected) return;
            new varlam.Request('/person/delete', {name: selected.label}).done((function (data) {
                var json = JSON.parse(data);
                switch (json.code) {
                    case 0:
                        this.removePerson(selected.label);
                        break;
                    case 44:
                        var operStr = varlam.Utils.inflect(json.operations, ' операции', ' операций', ' операций');
                        var msg = 'Удаление этого персонажа приведёт к удалению ' + json.operations +
                            operStr + '. Продолжить?';
                        new varlam.ShowYesNoDialog(
                            'Внимание!', msg, this.removePerson.bind(this, selected.label), '#dlg_person'
                        );
                        break;
                }
            }).bind(this));
        }).bind(this));

        // add keyboard navigation
        $('#txt_add_person').keypress(function(e) {
            if (e.which === 13) $('#btn_add_person').click();
        });
        $('#txt_change_person').keypress(function(e) {
            if (e.which === 13) $('#btn_change_person').click();
        });

        // on-click handler (shows the dialog)
        $('#add_person').click(function () {
            var $dlg = $('#dlg_person');
            $dlg.jqxWindow('open');
            $dlg.focus();
        });
    };

    /**
     * Loads the person list
     */
    varlam.AddPerson.prototype.refresh = function (data) {
        var json = JSON.parse(data).msg;
        $('#list_person').jqxListBox({source: json, width: '100%', height: '100%', selectedIndex: 0});
    };

    /**
     * Sends a request to remove a person (with cascading)
     * @param {string} name person name
     */
    varlam.AddPerson.prototype.removePerson = function (name) {
        new varlam.Request('/person/delete', JSON.stringify({name: name}), 'DELETE').done((function () {
            new varlam.Request('person/list').done(this.refresh.bind(this));
        }).bind(this));
    }
})(window.varlam = window.varlam || {}, jQuery);
