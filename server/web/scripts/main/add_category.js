(function (varlam, $) {
    /**
     * Add new category dialog
     * @constructor
     * @param {TreeBuilder} treeBuilder - instance of varlam.TreeBuilder
     */
    varlam.AddCategory = function (treeBuilder) {
        // init the dialog
        $('#dlg_category').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 500,
            height: 350,
            animationType: 'none', //performance issue
            autoOpen: false
        }).on('close', function() {
                $('#txt_add_category_wrapper').hide();
                $('#txt_add_subcategory_wrapper').hide();
                $('#txt_change_category_wrapper').hide();
                $('#btn_add_category').text('Новая категория');
                $('#btn_add_subcategory').text('Новая подкатегория');
                $('#btn_change_category').text('Изменить');
                treeBuilder.refresh();
            });

        // load category list
        new varlam.Request('category/list').done(this.refresh.bind(this));

        // Add New Category button click
        $('#btn_add_category').click((function () {
            var $this = $('#btn_add_category');
            if ($this.text() === 'OK') {
                $('#txt_add_category_wrapper').hide();
                var name = $('#txt_add_category').val();
                new varlam.Request('/category/new', JSON.stringify({name: name, parent: ''}), 'POST')
                    .done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Новая категория');
            } else {
                $('#txt_add_category_wrapper').show();
                $('#txt_add_category').val('').focus();
                $this.text('OK');
            }
        }).bind(this));

        // Add Subcategory button click
        $('#btn_add_subcategory').click((function () {
            var selected = $('#list_category').jqxTree('getSelectedItem');
            if (!selected) return;
            var $this = $('#btn_add_subcategory');
            if ($this.text() === 'OK') {
                $('#txt_add_subcategory_wrapper').hide();
                var name = $('#txt_add_subcategory').val();
                new varlam.Request('category/new', JSON.stringify({name: name, parent: selected.label}), 'POST')
                    .done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Новая подкатегория');
            } else {
                $('#txt_add_subcategory_wrapper').show();
                $('#txt_add_subcategory').val('').focus();
                $this.text('OK');
            }
        }).bind(this));

        // Change Category button click
        $('#btn_change_category').click((function () {
            var selected = $('#list_category').jqxTree('getSelectedItem');
            if (!selected) return;
            var name = selected.label;
            var parent = selected.parentElement ? $('#' + selected.parentElement.id).find('div:first').text() : '';
            var $this = $('#btn_change_category');
            if ($this.text() === 'OK') {
                $('#txt_change_category_wrapper').hide();
                var newName = $('#txt_change_category').val();
                if (name !== newName)
                    new varlam.Request('category/change', JSON.stringify({
                        name: name, newName: newName, newParentName: parent
                    }), 'PUT').done((function () {
                            new varlam.Request('category/list').done(this.refresh.bind(this));
                        }).bind(this));
                $this.text('Изменить');
            } else {
                $('#txt_change_category_wrapper').show();
                $('#txt_change_category').val(name).select().focus();
                $this.text('OK');
            }
        }).bind(this));

        // Remove Category button click
        $('#btn_remove_category').click((function () {
            var selected = $('#list_category').jqxTree('getSelectedItem');
            if (!selected) return;
            new varlam.Request('/category/delete', {name: selected.label}).done((function (data) {
                var json = JSON.parse(data);
                switch (json.code) {
                    case 0:
                        this.removeCategory(selected.label);
                        break;
                    case 14:
                        var categoryStr = varlam.Utils.inflect(
                            json.categories, 'ой категории', 'ых категорий', 'ых категорий'
                        );
                        var msg1 = 'Удаление этой категории приведёт к удалению ' + json.categories + ' зависим' +
                            categoryStr + '. Продолжить?';
                        new varlam.ShowYesNoDialog(
                            'Внимание!', msg1, this.removeCategory.bind(this, selected.label), '#dlg_category'
                        );
                        break;
                    case 15:
                        var itemStr = varlam.Utils.inflect(json.items, 'ого товара', 'ых товаров', 'ых товаров');
                        var msg2 = 'Удаление этой категории приведёт к удалению ' + json.items + ' зависим' +
                            itemStr + '. Продолжить?';
                        new varlam.ShowYesNoDialog(
                            'Внимание!', msg2, this.removeCategory.bind(this, selected.label), '#dlg_category'
                        );
                        break;
                    case 16:
                        var categoryStr_ = varlam.Utils.inflect(
                            json.categories, 'ой категории', 'ых категорий', 'ых категорий'
                        );
                        var itemStr_ = varlam.Utils.inflect(json.items, 'ого товара', 'ых товаров', 'ых товаров');
                        var msg3 = 'Удаление этой категории приведёт к удалению ' + json.categories + ' зависим' +
                            categoryStr_ + ' и ' + json.items + ' зависим' + itemStr_ + '. Продолжить?';
                        new varlam.ShowYesNoDialog(
                            'Внимание!', msg3, this.removeCategory.bind(this, selected.label), '#dlg_category'
                        );
                        break;
                    }
                }).bind(this));
        }).bind(this));

        // add keyboard navigation
        $('#txt_add_category').keypress(function(e) {
            if (e.which === 13) $('#btn_add_category').click();
        });
        $('#txt_add_subcategory').keypress(function(e) {
            if (e.which === 13) $('#btn_add_subcategory').click();
        });
        $('#txt_change_category').keypress(function(e) {
            if (e.which === 13) $('#btn_change_category').click();
        });

        // on-click handler (shows the dialog)
        $('#add_category').click(function () {
            var $dlg = $('#dlg_category');
            $dlg.jqxWindow('open');
            $dlg.focus();
        });
    };

    /**
     * Load the list of categories
     */
    varlam.AddCategory.prototype.refresh = function (data) {
        var json = JSON.parse(data).msg;
        var $tree = $('#list_category');
        var items = $tree.jqxTree({
            source: json,
            width: '100%',
            height: '100%',
            allowDrag: true,
            dragEnd: (function (item, dropItem, args, dropPos) {
                var oldParent = $('#' + item.parentId).find('div:first').text();
                var newParent = dropPos === 'inside'
                    ? dropItem.label
                    : $('#' + dropItem.parentId).find('div:first').text();
                console.log('oldParent: ' + oldParent + '; newParent: ' + newParent);
                if (oldParent !== newParent)
                    new varlam.Request('category/change', JSON.stringify({
                        name: item.label, newName: item.label, newParentName: newParent
                    }), 'PUT').done((function () {
                            new varlam.Request('category/list').done(this.refresh.bind(this));
                        }).bind(this));
            }).bind(this)
        }).jqxTree('getItems');
        if (items.length > 0) $tree.jqxTree('selectItem', items[0].element);
    };

    /**
     * Sends a request to remove the category (with cascading)
     * @param {string} name category name
     */
    varlam.AddCategory.prototype.removeCategory = function (name) {
        new varlam.Request('category/delete', JSON.stringify({name: name}), 'DELETE').done((function () {
            new varlam.Request('category/list').done(this.refresh.bind(this));
        }).bind(this));
    }
})(window.varlam = window.varlam || {}, jQuery);
