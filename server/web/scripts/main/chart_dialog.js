(function (varlam, $) {
    /**
     * Chart Dialog
     * @param {Tutor} tutor
     * @constructor
     */
    varlam.ChartDialog = function (tutor) {
        /**
         * Today date
         * @type {Date}
         */
        var today = new Date();

        /**
         * Current mode of arbitrary queries ('day', 'month', 'piechart')
         * @type {string}
         */
        var arbitrary_mode = 'day';

        // init the dialog
        $('#chart_dlg').jqxWindow({
            resizable: false,
            isModal: true,
            modalOpacity: 0.5,
            width: 700,
            height: 350,
            animationType: 'none', //performance issue
            autoOpen: false
        })  .on('open', function() {
                // load categories
                new varlam.Request('category/list/linear').done(function (data) {
                    var json = JSON.parse(data).msg;
                    $('#chart_categories').jqxListBox({
                        width: '100%', height: '100%', source: json, multipleextended: true, selectedIndex: 0
                    });
                });
                // fire tutor
                tutor.fire('#chart_cur_month');
                tutor.fire('#piechart_cur_month_wrapper');
                setTimeout(function() {
                    tutor.fire('#chart_cur_year');
                }, 500);
                setTimeout(function() {
                    tutor.fire('#piechart_cur_month');
                }, 1000);
            })
            .on('close', function() {
                tutor.fire('#add_operation');
                tutor.fire('#user_info');
            });

        // init 'Arbitrary query' window
        $('#dlg_arbitrary_query').jqxWindow({
            resizable: false,
            isModal: true,
            width: 500,
            height: 300,
            animationType: 'none', //performance issue
            autoOpen: false
        }).on('close', function() {
                $('#chart_dlg').focus()
            });

        // init the result window (window with picture)
        $('#chart_window').jqxWindow({
            resizable: false,
            isModal: true,
            width: 660,
            height: 550,
            animationType: 'none', //performance issue
            autoOpen: false
        }).on('close', function() {
                $('#chart_dlg').focus();
                tutor.fire('#chart_categories');
            });

        // init 'from' calendar
        $('#calendar_from')
            .jqxCalendar({firstDayOfWeek: 1, width: '100%', height: '100%', max: today})
            .jqxCalendar('setDate', new Date(today.getFullYear(), 0, 1));

        // init 'to' calendar
        $('#calendar_to').jqxCalendar({firstDayOfWeek: 1, width: '100%', height: '100%', max: today})
            .on('change', function(event) {
                $('#calendar_from').jqxCalendar('setMaxDate', event.args.date);
            });

        // init months combobox
        $('#cmb_chart_month').jqxComboBox({
            source: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            selectedIndex: today.getMonth(),
            width: '100%',
            dropDownHeight: 100
        });

        // init years combobox
        $('#cmb_chart_year').jqxComboBox({
            source: [today.getFullYear()-1, today.getFullYear()],
            selectedIndex: 1,
            width: 70,
            dropDownHeight: 60
        });

        // on 'Show' click handler
        $('#btn_run_arbitrary').click((function() {
            $('#dlg_arbitrary_query').jqxWindow('close');
            arbitrary_mode === 'piechart'
                ? this.queryPieChart(
                    $('#cmb_chart_month').jqxComboBox('getSelectedIndex'),
                    ~~$('#cmb_chart_year').val()
                )
                : this.queryTimeChart(
                    arbitrary_mode,
                    $('#calendar_from').jqxCalendar('getDate'),
                    $('#calendar_to').jqxCalendar('getDate')
                );
        }).bind(this));

        // time chart on current month by days
        $('#chart_cur_month').click((function () {
            this.queryTimeChart('day', new Date(today.getFullYear(), today.getMonth(), 1))
        }).bind(this));

        // time chart on 2 months by days
        $('#chart_2_months').click((function () {
            // 'today.getMonth() - 1' works correctly even on January
            this.queryTimeChart('day', new Date(today.getFullYear(), today.getMonth() - 1, 1));
        }).bind(this));

        // time chart on 3 months by days
        $('#chart_3_months').click((function () {
            // 'today.getMonth() - 2' works correctly even on January/February
            this.queryTimeChart('day', new Date(today.getFullYear(), today.getMonth() - 2, 1));
        }).bind(this));

        // time chart on arbitrary interval by days
        $('#chart_arbitrary_days').click(function() {
            arbitrary_mode = 'day';
            $('#arbitrary_query_title').find('div:first').text('Произвольный диапазон по дням');
            $('#lbl_chart_1').text('Начало интервала');
            $('#lbl_chart_2').text('Конец интервала');
            $('#calendar_from, #calendar_to').show();
            $('#cmb_chart_month, #cmb_chart_year').hide();
            var dlg = $('#dlg_arbitrary_query');
            dlg.jqxWindow('open');
            dlg.focus();
        });

        // time chart on current year by months
        $('#chart_cur_year').click((function () {
            this.queryTimeChart('month', new Date(today.getFullYear(), 0, 1));
        }).bind(this));

        // time chart on 2 years by months
        $('#chart_2_years').click((function () {
            this.queryTimeChart('month', new Date(today.getFullYear() - 1, 0, 1));
        }).bind(this));

        // time chart on 3 years by months
        $('#chart_3_years').click((function () {
            this.queryTimeChart('month', new Date(today.getFullYear() - 2, 0, 1));
        }).bind(this));

        // time chart on arbitrary interval by months
        $('#chart_arbitrary_months').click(function() {
            arbitrary_mode = 'month';
            $('#arbitrary_query_title').find('div:first').text('Произвольный диапазон по месяцам');
            $('#lbl_chart_1').text('Начало интервала');
            $('#lbl_chart_2').text('Конец интервала');
            $('#calendar_from, #calendar_to').show();
            $('#cmb_chart_month, #cmb_chart_year').hide();
            var dlg = $('#dlg_arbitrary_query');
            dlg.jqxWindow('open');
            dlg.focus();
        });

        // pie chart on current month
        $('#piechart_cur_month').click((function () {
            this.queryPieChart(today.getMonth(), today.getFullYear());
        }).bind(this));

        // pie chart on previous month
        $('#piechart_previous_month').click((function () {
            // 'this.queryPieChart(today.getMonth()-1, today.getFullYear())' is incorrect!
            var date = new Date(today.getFullYear(), today.getMonth() - 1, 1);
            this.queryPieChart(date.getMonth(), date.getFullYear());
        }).bind(this));

        // pie chart on 2 months ago
        $('#piechart_2_months_ago').click((function () {
            // 'this.queryPieChart(today.getMonth()-2, today.getFullYear())' is incorrect!
            var date = new Date(today.getFullYear(), today.getMonth() - 2, 1);
            this.queryPieChart(date.getMonth(), date.getFullYear());
        }).bind(this));

        // pie chart on arbitrary month
        $('#piechart_arbitrary').click(function() {
            arbitrary_mode = 'piechart';
            $('#arbitrary_query_title').find('div:first').text('Произвольный месяц');
            $('#lbl_chart_1').text('Месяц');
            $('#lbl_chart_2').text('Год');
            $('#calendar_from, #calendar_to').hide();
            $('#cmb_chart_month, #cmb_chart_year').show();
            var dlg = $('#dlg_arbitrary_query');
            dlg.jqxWindow('open');
            dlg.focus();
        });

        // on-click handler (shows the dialog)
        $('#charts').click(function () {
            var $dlg = $('#chart_dlg');
            $dlg.jqxWindow('open');
            $dlg.focus();
        });
    };

    /**
     * Sends a request to build a new Time Chart
     * @param {string} step 'day' or 'month'
     * @param {Date} fromDate start date of interval
     * @param {Date=} toDate finish date of interval (if undefined, server accepts as 'now')
     */
    varlam.ChartDialog.prototype.queryTimeChart = function (step, fromDate, toDate) {
        new varlam.Request('chart/time', JSON.stringify({
            step: step,
            from: fromDate.format('dd-mm-yyyy'),
            to: toDate ? toDate.format('dd-mm-yyyy') : undefined,
            categories: $('#chart_categories').jqxListBox('getSelectedItems').map(function (x) {
                return x.label;
            })
        }), 'PUT').done((function (data) {
            this.drawChart(JSON.parse(data).url);
        }).bind(this));
    };

    /**
     * Sends a request to build a new Pie Chart
     * @param {number} month month (0-11)
     * @param {number} year year in full format (e.g. 2014)
     */
    varlam.ChartDialog.prototype.queryPieChart = function (month, year) {
        new varlam.Request('chart/pie', JSON.stringify({
            // on server months are numerated with 1
            month: month + 1,
            year: year
        }), 'PUT').done((function (data) {
            this.drawChart(JSON.parse(data).url);
        }).bind(this));
    };

    /**
     * Shows a chart in a modal window
     * @private
     * @param {string} url URL of chart
     */
    varlam.ChartDialog.prototype.drawChart = function(url) {
        $('#chart_window_img').attr('src', url);
        var $dlg = $('#chart_window');
        $dlg.jqxWindow('open');
        $dlg.focus();
    };
})(window.varlam = window.varlam || {}, jQuery);
