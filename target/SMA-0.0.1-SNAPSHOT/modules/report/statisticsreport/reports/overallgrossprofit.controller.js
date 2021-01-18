/**
 * Created by dell on 08-06-19.
 */

(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("overallGrossProfitCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;

        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();


        /**************Gross Profit Start********************************/

        var grossProfitChart = function () {
            var ctx = document.getElementById("grossProfitCanvas");

            vm.grossProfitDataMap = {
                labels: vm.xAxisLabel,
                datasets: [
                    {
                        label: 'Income (JPY)',
                        data: vm.yAxisLabelIncome,
                        backgroundColor: '#41cf88' // green
                    },
                    {
                        label: 'Expense (JPY)',
                        data: vm.yAxisLabelExpense,
                        backgroundColor: '#1e1bd8' // blue
                    },
                    {
                        label: 'Gross Profit (JPY)',
                        data: vm.yAxisLabelProfit,
                        backgroundColor: '#f9687f' // pink
                    }
                ]
            };

            if (ctx != null) {
                if (vm.barChart2) {
                    vm.barChart2.destroy();
                }
                vm.barChart2 = new Chart(ctx, {
                    type: 'bar',
                    data: vm.grossProfitDataMap,
                    options: {
                        responsive: true,
                    }
                });
            }

        };

        vm.grossProfit = function (grossProfitData) {

            var xAxis = [];
            for (var key in grossProfitData) {
                xAxis.push(grossProfitData[key].month);
            }
            vm.xAxisLabel = xAxis;

            var yAxisIncome = [];
            var yAxisExpense = [];
            var yAxisProfit = [];


            for (var key in grossProfitData) {
                yAxisIncome.push(parseFloat(grossProfitData[key].income).toFixed(2));
                yAxisExpense.push(parseFloat(grossProfitData[key].expense).toFixed(2));
                yAxisProfit.push(parseFloat(grossProfitData[key].profit).toFixed(2));
            }

            vm.yAxisLabelIncome = yAxisIncome;
            vm.yAxisLabelExpense = yAxisExpense;
            vm.yAxisLabelProfit = yAxisProfit;
            grossProfitChart();

        };

        vm.getGrossProfit = function () {
            vm.overallTurnOverArr = [];
            var reqMap = {
                "year": vm.year
            };
            reportSvcs.getOverallGrossProfit(reqMap, function (res) {

                vm.grossProfitData = res.data;
                console.log("vm.grossProfitData:",vm.grossProfitData);
                vm.grossProfit(vm.grossProfitData);


            }, function (err) {

            });
        };



    }]);

})(window, window.document, window.jQuery, window.angular);