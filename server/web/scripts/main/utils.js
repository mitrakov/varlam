(function(varlam, $) {
    /**
     * Static Utils object
     */
    varlam.Utils = varlam.Utils || {};

    /**
     * Performs case inflection for Russian Language
     * @param {number} n number
     * @param {string} s1 string for 1, 21, 31, etc.
     * @param {string} s234 string for 2-4, 22-24, 32-34, etc.
     * @param {string} s567890 string for 0, 5-20, 25-30, 35-40, etc.
     * @returns {string}
     */
    varlam.Utils.inflect = function(n, s1, s234, s567890) {
        var str = n.toString();
        if (str.charAt(str.length - 1) === '1' && str.charAt(str.length - 2) !== '1') return s1;
        if (str.charAt(str.length - 1) === '2' && str.charAt(str.length - 2) !== '1') return s234;
        if (str.charAt(str.length - 1) === '3' && str.charAt(str.length - 2) !== '1') return s234;
        if (str.charAt(str.length - 1) === '4' && str.charAt(str.length - 2) !== '1') return s234;
        return s567890;
    };
}(window.varlam = window.varlam || {}, jQuery));