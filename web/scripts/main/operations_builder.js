(function (varlam, $) {
    /**
     * Object that builds a list of operations
     * @constructor
     */
    varlam.OperationsBuilder = function () {

        /**
         * Array to store operation IDs for infinite-scrolling
         * @private
         * @type {Array.<number>}
         */
        this.operationIDs = [];

        /**
         * Ref to AddOperation
         * @private
         * @type {varlam.AddOperation}
         */
        this.addOperation = null;

        // remember current instance inside a static context to make it possible to access OperationBuilder inside new
        // DOM elements (note: is this a good practice?)
        varlam.opBuilder = this;

        // create a listbox
        $('#operations').jqxListBox({width: '100%', height: '100%'});

        // extract a scrollbar from the listbox
        var vScrollBar = $('#verticalScrollBaroperations');

        // now we try to implement an infinite scrolling. Does anybody know how to do it in jqxWidgets?
        // jqxScrollBar has no events to catch the moment the user scrolls down to the end.
        // so we use the usual timer (crutch-oriented programming)
        setInterval((function () {
            if (this.operationIDs.length > 0) {
                var value = ~~vScrollBar.jqxScrollBar('value');
                var max = ~~vScrollBar.jqxScrollBar('max');
                if (value === max)
                    this.loadMore(this.operationIDs.shift())
            }
        }).bind(this), 400);
    };

    /**
     * Sets the current instance of AddOperation (it removed from constructor in the wake of cyclic references)
     * {AddOperation} addOperation instance of varlam.AddOperation
     */
    varlam.OperationsBuilder.prototype.setAddOperation = function(addOperation) {
        this.addOperation = addOperation;
    };

    /**
     * Opens the dialog to change an operation which ID is preserved inside '#operations' container
     * @param id
     */
    varlam.OperationsBuilder.prototype.changeOperation = function (id, category, item, summa, person, time) {
        /*var $operations = $('#operations');
        var elem = $operations.find('[data-id="' + id + '"]');
        var category = elem.find('.op_category').text();
        var item = elem.find('.op_item').text();
        var summa = ~~elem.find('.op_summa').text();
        var person = elem.find('.op_person').text();*/
        var dateStr = time.split('-');
        var date = new Date(dateStr[2], dateStr[1] - 1, dateStr[0]);
        this.addOperation.setParameters(id, category, item, summa, person, date);
        $('#add_operation').click();
    };

    /**
     * Removes the operation which ID is preserved inside '#operations' container
     * @param id
     */
    varlam.OperationsBuilder.prototype.removeOperation = function (id) {
        new varlam.ShowYesNoDialog('Предупреждение', 'Вы точно хотите удалить запись?', (function () {
            new varlam.Request('/operation/delete', JSON.stringify({id: id}), 'DELETE')
                .done((function () {
                    this.refreshOperations();
                }).bind(this));
        }).bind(this), 'document');
    };

    /**
     * Reloads the operations list
     */
    varlam.OperationsBuilder.prototype.refreshOperations = function () {
        var node = $('#categories_tree').jqxTree('getSelectedItem');
        var parameters = node.id.indexOf('jqxWidget') === 0 ? {item: node.label} : {category: node.label};
        new varlam.Request('/operation/list', parameters).done((function (data) {
            var operationIDs = JSON.parse(data).msg;
            $('#operations').jqxListBox('clear');
            this.operationIDs = operationIDs;
            // because the array is copied by ref we'll lose its original length inside a loop
            var size = operationIDs.length;
            for (var i = 0; i < Math.min(16, size); i++)
                this.loadMore(this.operationIDs.shift());
        }).bind(this));
    };

    /**
     * Appends new Operation Items via AJAX (used for infinite-scroll)
     * @private
     * @param {number} operationID
     */
    varlam.OperationsBuilder.prototype.loadMore = function (operationID) {
        // Dirty hack! As far as AJAX is asynchronous, we couldn't add new items inside a callback (otherwise they may
        // appear in different order). Therefore we first create new blank elements and as sson as AJAX is done their
        // content is filled up with real data
        var $operations = $('#operations');
        $operations.jqxListBox('addItem', {
            html: '<br><br><img src="images/loader.gif"><br><br>',
            value: operationID
        });
        new varlam.Request('/operation/get', {id: operationID}).done(function (opData) {
            var json = JSON.parse(opData).operation;
            var item =
                '<a href="#" class="list-group-item">' +
                    '<div class="op_time" style="margin: 10px; display: inline-block">' + json.time + '</div>' +
                    '<div class="op_summa" style="margin: 10px; display: inline-block">' + json.summa + '</div>' +
                    '<div class="" style="margin: 10px; display: inline-block">' +
                        '<div class="op_item">' + json.item + '</div>' +
                        '<div class="op_category">' + json.category + '</div>' +
                    '</div>' +
                    '<div class="op_person" style="margin: 10px; display: inline-block">' + json.person + '</div>' +
                    '<div style="margin: 10px; display: inline-block" onclick="varlam.opBuilder.changeOperation('+ json.id + ',\'' + json.category + '\',\'' + json.item + '\',' + json.summa + ',\'' + json.person + '\',\'' + json.time + '\')"><span class="glyphicon glyphicon-pencil btn-lg"></span></div>' +
                    '<div style="margin: 10px; display: inline-block" onclick="varlam.opBuilder.removeOperation('+ json.id + ')"><span class="glyphicon glyphicon-remove-circle btn-lg"></span></div>'
                '</a>';
            $operations.jqxListBox('updateItem', {html: item}, operationID);
        });
    }
})(window.varlam || {}, jQuery);
