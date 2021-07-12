(function (varlam, $) {
    /**
     * Add new item dialog
     * @constructor
     * @param {TreeBuilder} treeBuilder - instance of varlam.TreeBuilder
     */
    varlam.AddItem = function (treeBuilder) {

        //init the dialog
        $('#dlg_item').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 650,
            height: 350,
            animationType: 'none', //performance issue
            autoOpen: false
        })  .on('open', (function() {
                new varlam.Request('category/list').done(this.refresh.bind(this));
            }).bind(this))
            .on('close', function() {
                $('#txt_add_item_wrapper').hide();
                $('#txt_change_item_wrapper').hide();
                $('#txt_ctxt_add_category_wrapper').hide();
                $('#btn_add_item').text('Добавить');
                $('#btn_change_item').text('Изменить');
                $('#btn_ctxt_add_category').removeClass('disabled').text('+');
                $('#btn_ctxt_add_subcategory').removeClass('disabled').text('Sub');
                treeBuilder.refresh();
            });

        // Add New Item button click
        $('#btn_add_item').click((function () {
            var selected = $('#list_item_category').jqxTree('getSelectedItem');
            if (!selected) return;
            var $this = $('#btn_add_item');
            if ($this.text() === 'OK') {
                $('#txt_add_item_wrapper').hide();
                var name = $('#txt_add_item').val();
                new varlam.Request('/item/new', JSON.stringify({name: name, category: selected.label}), 'POST')
                    .done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Добавить');
            } else {
                $('#txt_add_item_wrapper').show();
                $('#txt_add_item').val('').focus();
                $this.text('OK');
            }
        }).bind(this));

        // Change Item button click
        $('#btn_change_item').click((function () {
            var selectedCategory = $('#list_item_category').jqxTree('getSelectedItem');
            var selectedItem = $('#list_item').jqxTree('getSelectedItem');
            if (!selectedCategory || !selectedItem) return;
            var name = selectedItem.label;
            var $this = $('#btn_change_item');
            if ($this.text() === 'OK') {
                $('#txt_change_item_wrapper').hide();
                var newName = $('#txt_change_item').val();
                if (name !== newName)
                    new varlam.Request('item/change', JSON.stringify({
                        name: name, newName: newName, newCategoryName: selectedCategory.label
                    }), 'PUT').done((function () {
                            new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Изменить');
            } else {
                $('#txt_change_item_wrapper').show();
                $('#txt_change_item').val(name).select().focus();
                $this.text('OK');
            }
        }).bind(this));

        // Remove Item button click
        $('#btn_remove_item').click((function () {
            var selected = $('#list_item').jqxTree('getSelectedItem');
            if (!selected) return;
            new varlam.Request('item/delete', {name: selected.label}).done((function (data) {
                var json = JSON.parse(data);
                switch (json.code) {
                    case 0:
                        this.removeItem(selected.label);
                        break;
                    case 24:
                        var operStr = varlam.Utils.inflect(json.operations, ' операции', ' операций', ' операций');
                        var msg = 'Удаление этого товара приведёт к удалению ' + json.operations + operStr +
                            '. Продолжить?';
                        new varlam.ShowYesNoDialog(
                            'Внимание!', msg, this.removeItem.bind(this, selected.label), '#dlg_item'
                        );
                        break;
                }
            }).bind(this));
        }).bind(this));

        // Context-Add new Category button click
        $('#btn_ctxt_add_category').click((function() {
            var $this = $('#btn_ctxt_add_category');
            if ($this.text() === 'OK') {
                new varlam.Request('/category/new', JSON.stringify({
                        name: $('#txt_ctxt_add_category').val(),
                        parent: ''}
                ), 'POST').done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('+');
                $('#btn_ctxt_add_subcategory').toggleClass('disabled');
                $('#txt_ctxt_add_category_wrapper').fadeOut('fast');
            } else {
                $this.text('OK');
                $('#btn_ctxt_add_subcategory').toggleClass('disabled');
                $('#txt_ctxt_add_category_wrapper').fadeIn('fast');
                $('#txt_ctxt_add_category').attr('placeholder', 'Категория').val('').focus();
            }
        }).bind(this));

        // Context-Add new Subcategory button click
        $('#btn_ctxt_add_subcategory').click((function() {
            var $this = $('#btn_ctxt_add_subcategory');
            var selected = $('#list_item_category').jqxTree('getSelectedItem');
            if (!selected) return;
            if ($this.text() === 'OK') {
                new varlam.Request('category/new', JSON.stringify({
                    name: $('#txt_ctxt_add_category').val(),
                    parent: selected.label
                }), 'POST').done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Sub');
                $('#btn_ctxt_add_category').toggleClass('disabled');
                $('#txt_ctxt_add_category_wrapper').fadeOut('fast');
            } else {
                $this.text('OK');
                $('#btn_ctxt_add_category').toggleClass('disabled');
                $('#txt_ctxt_add_category_wrapper').fadeIn('fast');
                $('#txt_ctxt_add_category').attr('placeholder', 'Подкатегория').val('').focus();
            }
        }).bind(this));

        // add keyboard navigation
        $('#txt_add_item').keypress(function(e) {
            if (e.which === 13) $('#btn_add_item').click();
        });
        $('#txt_change_item').keypress(function(e) {
            if (e.which === 13) $('#btn_change_item').click();
        });

        // on-click handler (shows the dialog)
        $('#add_item').click(function () {
            var $dlg = $('#dlg_item');
            $dlg.jqxWindow('open');
            $dlg.focus();
        });
    };

    /**
     * Load the list of categories and items
     */
    varlam.AddItem.prototype.refresh = function (data) {
        var json = JSON.parse(data).msg;
        var $tree1 = $('#list_item_category');
        var $tree2 = $('#list_item');
        $tree2.jqxTree({width: '100%', height: '100%'}).jqxTree('clear');
        var items = $tree1.jqxTree({
            source: json,
            width: '100%',
            height: '100%',
            allowDrop: true,
            dragStart: function () {
                return false;
            }
        }).off('select').on('select', (function (event) {
                var node = $tree1.jqxTree('getItem', event.args.element);
                new varlam.Request('item/list', {category: node.label}).done((function (d) {
                    var itemz = $tree2.jqxTree({
                        source: JSON.parse(d).msg,
                        width: '100%',
                        height: '100%',
                        allowDrag: true,
                        dragEnd: (function (item, dropItem, args, dropPos, tree) {
                            // move an item to the other category if a user drops it
                            // inside left tree node (and item != self)
                            if (tree.attr('id') === 'list_item_category'
                                && dropPos === 'inside' && node !== dropItem) {
                                console.log(item.label + ' moved to ' + dropItem.label);
                                new varlam.Request('/item/change', JSON.stringify({
                                    name: item.label,
                                    newName: item.label,
                                    newCategoryName: dropItem.label
                                }), 'PUT').done((function () {
                                        new varlam.Request('category/list').done(this.refresh.bind(this));
                                    }).bind(this));
                            }
                            return false;
                        }).bind(this)
                    }).jqxTree('getItems');
                    if (itemz.length > 0) $tree2.jqxTree('selectItem', itemz[0].element);
                }).bind(this));
            }).bind(this)).jqxTree('getItems');
        if (items.length > 0) $tree1.jqxTree('selectItem', items[0].element);
    };

    /**
     * Sends a request to remove an item (with cascading)
     * @param {string} name item name
     */
    varlam.AddItem.prototype.removeItem = function (name) {
        new varlam.Request('item/delete', JSON.stringify({name: name}), 'DELETE').done((function () {
            new varlam.Request('category/list').done(this.refresh.bind(this));
        }).bind(this));
    }
})(window.varlam = window.varlam || {}, jQuery);
