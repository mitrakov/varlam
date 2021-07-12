(function (varlam, $) {
    /**
     * TreeView Builder
     * @constructor
     * @param {OperationsBuilder} operationsBuilder instance of varlam.OperationsBuilder
     */
    varlam.TreeBuilder = function (operationsBuilder) {
        this.operationsBuilder = operationsBuilder;
        this.refresh();
    };

    /**
     * Refreshes the left-side treeView
     */
    varlam.TreeBuilder.prototype.refresh = function() {
        new varlam.Request('category/list/full').done((function (data) {
            var json = JSON.parse(data).msg;
            var $tree = $('#categories_tree');
            var items = $tree.jqxTree({
                source: json,
                width: '100%',
                height: '100%',
                allowDrag: false
            }).off('select').on('select', (function () {
                this.operationsBuilder.refreshOperations();
            }).bind(this)).jqxTree('getItems');
            if (items.length > 0) $tree.jqxTree('selectItem', items[0].element);
        }).bind(this));
    };
})(window.varlam = window.varlam || {}, jQuery);
