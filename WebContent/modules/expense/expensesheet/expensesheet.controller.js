/**
 * Created by kundan
 */
(function (window, document, $, angular) {

    var expenseApp = angular.module('expenseApp');

    expenseApp.controller("expenseSheetCtrl", ["$scope", "$rootScope", "expenseSvcs", "expenseModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "expenseData", "registrationSvcs", function ($scope, $rootScope, expenseSvcs, expenseModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, expenseData, registrationSvcs) {

        var vm = this;
        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
        //console.log("vm.respData::",vm.respData);
        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
            var monthIndex = moment((Date.today()).addDays(0)).format("M");
            vm.month = vm.monthArray[monthIndex - 1];
        }();

        vm.resetData = function(){
            vm.incomeData = {};
            vm.salaryData = [];
            vm.expenseData = [];
            vm.incomeTotal = 0;
            vm.salaryTotal = 0;
            vm.showData = false;
            vm.showIncome = false;
        };

        vm.defaultData = function(){
            vm.resetData();
            var reqMap = {
                month: vm.month,
                year: vm.year
            };
            expenseSvcs.getDataForExpenseSheet(reqMap,function (res) {
                if(res.data != 'no data'){
                    vm.showData = true;
                    vm.incomeData = res.data.income;
                    if(Object.keys(vm.incomeData).length){
                        vm.showIncome = true;
                    }
                    vm.salaryData = res.data.salaryData;
                    vm.expenseData = res.data.expenseData;

                    for(var k in vm.incomeData){
                        if(vm.incomeData[k].grandTotal)vm.incomeTotal += parseFloat(vm.incomeData[k].grandTotal);
                    }
                    for(var k in vm.salaryData){
                        if(vm.salaryData[k].total_salary) vm.salaryTotal += parseFloat(vm.salaryData[k].total_salary);
                    }
                    vm.totalExpense();
                }else{
                    vm.showData = false;
                    notifySvcs.info({
                        content : "No data"
                    })
                }

            });
        };vm.defaultData();

        vm.getFontColor = function(color){
            return {"color":color.split('-')[0]}
        };
        vm.getExpensesSum = function(data){
            vm.expenseAmount = 0;
                vm.expenseAmount += parseFloat(data.amount);
                for(var m in data.subExpenseList){
                    vm.expenseAmount += parseFloat(data.subExpenseList[m].amount);
                }
            return vm.expenseAmount;
        };

        vm.totalExpense = function(){
            vm.totalExpenses = 0;
            var expenses = 0;
            for (var k in vm.expenseData){
                expenses += parseFloat(vm.expenseData[k].amount);
                for(var m in vm.expenseData[k].subExpenseList){
                    expenses += parseFloat(vm.expenseData[k].subExpenseList[m].amount);
                }
            }
            vm.totalExpenses = expenses+vm.salaryTotal;
        };

        vm.print = function(){

            //console.log("vm.incomeData:",JSON.stringify(vm.incomeData));
            //console.log("vm.salaryData:",JSON.stringify(vm.salaryData));
            //console.log("vm.expenseData:",JSON.stringify(vm.expenseData));
            var blankForm = {};
            blankForm["income"] = vm.incomeData;
            blankForm["salary"] = vm.salaryData;
            blankForm["expense"] = vm.expenseData;
            blankForm["month"] = vm.month;
            blankForm["year"] = vm.year;

            expenseSvcs.expenseSheetPdf(blankForm , function(res){
                if((res.data).split('.')[1] == "pdf"){
                    async.parallel({

                        },
                        function (err, results) {
                            modalInstance = modalSvcs.open({
                                windowClass: "fullHeight",
                                size: "lg",
                                lkInstances: results,
                                title: 'Expense Sheet',
                                data: res.data,
                                templateUrl: "modules/studentinvoice/invoicepdfviewer/invoicepdfviewer.html",
                                controller: "invoicePdfViewerCtrl",
                                controllerAs: "invoicePdfViewer"

                            });
                            modalInstance.rendered.then(function () {
                                //console.log("modal template rendered");
                            });
                            modalInstance.opened.then(function () {
                                //console.log("modal template opened");
                            });
                            modalInstance.closed.then(function () {
                                //console.log("modal template closed");
                                modalInstance = undefined;
                            });
                        });
                }else{
                    notifySvcs.info({
                        content : "Something went while generating pdf"
                    })
                }
            });
        }
    }]);

})(window, window.document, window.jQuery, window.angular);