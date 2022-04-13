(function (varlam, $) {
    /**
     * Add new operation dialog
     * @constructor
     * @param {TreeBuilder} treeBuilder - instance of varlam.TreeBuilder
     * @param {Tutor} tutor
     */
    varlam.AddOperation = function (treeBuilder, tutor) {
        this.treeBuilder = treeBuilder;
        /**
         * Current operation ID. If >= 0 then the dialog is opened in a 'change' mode. Otherwise in an 'add' mode
         * @private
         * @type {number}
         */
        this.curID = -1;
        this.curCategory = null;
        this.curItem = null;
        this.curSumma = 0;
        this.curPerson = null;
        this.curDate = null;

        // create listbox
        $('#list_op_item').jqxListBox({width: '100%', height: '100%'});

        // init the dialog
        $('#dlg_operations').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 700,
            height: 450,
            animationType: 'none', //performance issue
            autoOpen: false
        })  .keypress(function(e) {
                if (e.which === 13) $('#btn_new_operation').click()
            })
            .on('close', (function() {
                $('#txt_opctxt_add_category_wrapper').hide();
                $('#txt_opctxt_add_item_wrapper').hide();
                $('#btn_opctxt_add_category').removeClass('disabled').text('+');
                $('#btn_opctxt_add_subcategory').removeClass('disabled').text('Sub');
                $('#btn_opctxt_add_item').text('+');
                this.curID = -1;
                this.curSumma = 0;
                this.curCategory = this.curItem = this.curPerson = this.curDate = null;
                tutor.fire('#charts_wrapper');
            }).bind(this))
            .on('open', (function() {
                // load categories/items
                new varlam.Request('category/list').done(this.refresh.bind(this));
                // load persons
                new varlam.Request('person/list').done(
                    (function (data) {
                        var json = JSON.parse(data).msg;
                        $('#txt_person').jqxInput({minLength: 1, source: json});
                        // on OK click handler
                        $('#btn_new_operation').off('click').click((function () {
                            var selected = $('#list_op_item').jqxListBox('getSelectedItem');
                            var summa = ~~$('#txt_summa').val();
                            var person = $('#txt_person').val();
                            var date = $('#calendar').jqxCalendar('value');
                            if (!selected || summa < 0) return;
                            // context appending of Person dictionary
                            if (person.length > 0 && json.indexOf(person) < 0) {
                                new varlam.Request('person/new', JSON.stringify({name: person}), 'POST').done((function () {
                                    this.newOperation(selected.label, summa, person, date);
                                }).bind(this));
                            } else this.newOperation(selected.label, summa, person, date);
                        }).bind(this));
                    }).bind(this)
                );
                // init other components
                $('#txt_summa').val(this.curSumma === 0 ? '' : this.curSumma);
                $("#txt_person").val(this.curPerson);
                $('#calendar').jqxCalendar(this.curDate != null ? 'setDate' : 'today', this.curDate);
                $('#btn_new_operation').text(this.curID >=0 ? 'Изменить' : 'Добавить');
                // init tutor
                tutor.fire('#txt_summa');
            }).bind(this));

        // Context-Add new Category button click
        $('#btn_opctxt_add_category').click((function() {
            var $this = $('#btn_opctxt_add_category');
            if ($this.text() === 'OK') {
                new varlam.Request('/category/new', JSON.stringify({
                        name: $('#txt_opctxt_add_category').val(),
                        parent: ''}
                ), 'POST').done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('+');
                $('#btn_opctxt_add_subcategory').toggleClass('disabled');
                $('#txt_opctxt_add_category_wrapper').fadeOut('fast');
            } else {
                $this.text('OK');
                $('#btn_opctxt_add_subcategory').toggleClass('disabled');
                $('#txt_opctxt_add_category_wrapper').fadeIn('fast');
                $('#txt_opctxt_add_category').attr('placeholder', 'Категория').val('').focus();
            }
        }).bind(this));

        // Context-Add new Subcategory button click
        $('#btn_opctxt_add_subcategory').click((function() {
            var $this = $('#btn_opctxt_add_subcategory');
            var selected = $('#list_op_item_category').jqxTree('getSelectedItem');
            if (!selected) return;
            if ($this.text() === 'OK') {
                new varlam.Request('category/new', JSON.stringify({
                    name: $('#txt_opctxt_add_category').val(),
                    parent: selected.label
                }), 'POST').done((function () {
                        new varlam.Request('category/list').done(this.refresh.bind(this));
                    }).bind(this));
                $this.text('Sub');
                $('#btn_opctxt_add_category').toggleClass('disabled');
                $('#txt_opctxt_add_category_wrapper').fadeOut('fast');
            } else {
                $this.text('OK');
                $('#btn_opctxt_add_category').toggleClass('disabled');
                $('#txt_opctxt_add_category_wrapper').fadeIn('fast');
                $('#txt_opctxt_add_category').attr('placeholder', 'Подкатегория').val('').focus();
            }
        }).bind(this));

        // Context-Add new Item button click
        $('#btn_opctxt_add_item').click((function() {
            var $this = $('#btn_opctxt_add_item');
            var selected = $('#list_op_item_category').jqxTree('getSelectedItem');
            if (!selected) return;
            if ($this.text() === 'OK') {
                new varlam.Request('item/new', JSON.stringify({
                    name: $('#txt_opctxt_add_item').val(),
                    category: selected.label
                }), 'POST').done((function () {
                        new varlam.Request('/item/list', {category: selected.label}).done((function (d) {
                            $('#list_op_item').jqxListBox({
                                source: JSON.parse(d).msg, width: '100%', height: '100%', selectedIndex: 0
                            });
                        }));
                    }).bind(this));
                $this.text('+');
                $('#txt_opctxt_add_item_wrapper').fadeOut('fast');
            } else {
                $this.text('OK');
                $('#txt_opctxt_add_item_wrapper').fadeIn('fast');
                $('#txt_opctxt_add_item').val('').focus();
            }
        }).bind(this));

        // create spinner for summa
        $('#txt_summa').keypress(function(event) {
            return (48 <= event.which && event.which <= 57) || event.which === 13;
        });

        // create a calendar
        $('#calendar').jqxCalendar({firstDayOfWeek: 1, height: 180});

        // open the dialog
        $('#add_operation').click(function() {
            var $dlg = $('#dlg_operations');
            $dlg.jqxWindow('open');
            $dlg.focus();
        });
    };

    /**
     * Sets the parameters for changing an Operation
     * @param id
     * @param category
     * @param item
     * @param summa
     * @param person
     * @param date
     */
    varlam.AddOperation.prototype.setParameters = function(id, category, item, summa, person, date) {
        this.curID = id;
        this.curCategory = category;
        this.curItem = item;
        this.curSumma = summa;
        this.curPerson = person;
        this.curDate = date;
    };

    /**
     * Adds a new Operation (by POST-request) or changes the current Operation (by PUT-request)
     * @param {string} item
     * @param {number} summa
     * @param {string=} person
     * @param {Date=} date
     */
    varlam.AddOperation.prototype.newOperation = function (item, summa, person, date) {
        new varlam.Request('/operation/' + (this.curID >= 0 ? 'change' : 'new'), JSON.stringify({
            id: this.curID >=0 ? this.curID : undefined,
            itemName: item,
            personName: person || '',
            summa: summa,
            date: date ? date.format('dd-mm-yyyy') : undefined
        }), this.curID >= 0 ? 'PUT' : 'POST').done((function () {
                $('#dlg_operations').jqxWindow('close');
                $('#operation_saved').delay(1000).fadeIn('slow').delay(1000).fadeOut('slow');
                this.treeBuilder.refresh();
            }).bind(this));
    };

    varlam.AddOperation.prototype.refresh = function(data) {
        var json = JSON.parse(data).msg;
        var $tree = $('#list_op_item_category');
        $tree.jqxTree({
            source: json,
            width: '100%',
            height: '100%',
            allowDrag: false
        }).off('select').on('select', (function (event) {
                var node = $tree.jqxTree('getItem', event.args.element);
                if (node == null) return;
                new varlam.Request('/item/list', {category: node.label}).done((function (d) {
                    var $listbox = $('#list_op_item');
                    $listbox.jqxListBox({source: JSON.parse(d).msg, width: '100%', height: '100%', selectedIndex: 0});
                    // select an item needed
                    if (this.curItem) {
                        $listbox.jqxListBox('selectItem', this.curItem);
                        this.curItem = null;
                    }
                }).bind(this));
            }).bind(this)).jqxTree('expandAll');
        // select a node needed (listBox will be updated automatically)
        var items = $tree.jqxTree('getItems');
        $tree.jqxTree('selectItem', this.curCategory
            ? $.grep(items, (function(x) {return x.label === this.curCategory;}).bind(this))[0].element
            : items.length > 0 ? items[0].element : null
        );
    };
})(window.varlam = window.varlam || {}, jQuery);
