(function (varlam, $) {
    /**
     * Authorized Ajax-Request to the Server
     * @param {string} path URL without protocol://host/varlam
     * @param {string | object=} data data (body or queryString, depends on a method)
     * @param {string=} method GET (default), PUT, POST, etc.
     * @returns {Deferred}
     * @constructor
     */
    varlam.Request = function(path, data, method) {
        var url = 'https://guap.mitrakoff.com/varlam' + (path.charAt(0) === '/' ? '' : '/') + path;
        return $.ajax(url, {
            type: method || undefined,
            headers: {
                'username': $.localStorage.get('username'),
                'token': $.localStorage.get('token')
            },
            data: data || undefined
        }).fail(function(e) {
                switch (JSON.parse(e.responseText).code) {
                    case 4:
                        console.log('Unauthorized');
                        window.location = 'index.html';
                        break;
                    default:
                        console.log(e);
                }
            })
    };
})(window.varlam = window.varlam || {}, jQuery);
