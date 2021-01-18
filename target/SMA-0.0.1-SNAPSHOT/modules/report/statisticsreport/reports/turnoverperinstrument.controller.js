/**
 * Created by dell on 08-06-19.
 */

(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("turnoverPerInstrumentCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;

        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();

        /**************Turn over by instrument Start********************************/

        var turnOverChart = function () {
            var ctx = document.getElementById("turnOverCanvas");
            vm.turnOverData = {
                labels: vm.xAxisLabel,
                datasets: [{
                    label: 'Turnover (JPY)',
                    data: vm.yAxisLabelTurnOver,
                    backgroundColor: '#41cf88' // green

                }]
            };

            if (ctx != null) {
                if (vm.barChart1) {
                    vm.barChart1.destroy();
                }
                vm.barChart1 = new Chart(ctx, {
                    type: 'bar',
                    data: vm.turnOverData,
                    options: {
                        responsive: true,
                        scales: {
                            xAxes: [{stacked: true}],
                            yAxes: [{stacked: true}]
                        }
                    }
                });
            }

        };

        vm.turnOver = function (turnOverData) {

            var xAxis = [];
            for (var key in turnOverData) {
                xAxis.push(turnOverData[key].month);
            }
            vm.xAxisLabel = xAxis;

            var yAxisTurnOver = [];


            for (var key in turnOverData) {
                yAxisTurnOver.push(parseFloat(turnOverData[key].monthlyTurnOver).toFixed(2));
            }

            vm.yAxisLabelTurnOver = yAxisTurnOver;

            turnOverChart();


        };

        vm.getTurnOver = function (instrumentData) {
            vm.instrument = JSON.parse(instrumentData);
            vm.turnOverByInstrumentArr = [];
            var reqMap = {
                "year": vm.year,
                "instrumentPk": vm.instrument['primaryKey'] + ""
            };
            reportSvcs.getTurnOverByInstrument(reqMap, function (res) {

                vm.respData = res.data;
                console.log("vm.respData:",vm.respData);

                vm.turnOverByInstrumentArr = vm.respData;
                vm.turnOver(vm.turnOverByInstrumentArr);


                if (vm.respData.length == 0) {
                    notifySvcs.info({
                        content: "No Data found."
                    });
                }

            }, function (err) {

            });
        };



    }]);

})(window, window.document, window.jQuery, window.angular);