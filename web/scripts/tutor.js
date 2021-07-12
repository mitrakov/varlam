(function(varlam, $) {
    /**
     * Tutorial for newcomers
     * @param {Array.<string>} stages
     * @constructor
     */
    varlam.Tutor = function(stages) {
        this.deferredArray = [];
        stages.forEach((function(item) {
            this.add(item);
        }).bind(this));
    };

    /**
     * Appends an internal array of stages with a new stage
     * @param {string} selector jQuery selector (e.g. '#example')
     */
    varlam.Tutor.prototype.add = function(selector) {
        var d = $.Deferred();
        d.promise().done(function() {
            var curTutors = $.localStorage.get('tutor') || '';
            if ($.inArray(selector, curTutors.split('@')) < 0) {
                setTimeout(function() {
                    $(selector).popover('show');
                }, 3000);
                setTimeout(function() {
                    $(selector).popover('hide');
                }, 10000);
                $.localStorage.set('tutor', curTutors + '@' + selector);
            }
        });
        d.selector = selector;
        this.deferredArray.push(d);
    };

    /**
     * Triggers a current stage to be executed
     * @param {string=} selector if specified, it triggers the tutor stage only if current stage = selector
     *                 (otherwise it triggers anyway)
     * @returns {varlam.Tutor}
     */
    varlam.Tutor.prototype.fire = function(selector) {
        if (!this.deferredArray[0]) return this;
        if (selector && selector !== this.deferredArray[0].selector) return this;
        this.deferredArray.shift().resolve();
        return this;
    };
})(window.varlam = window.varlam || {}, jQuery);
