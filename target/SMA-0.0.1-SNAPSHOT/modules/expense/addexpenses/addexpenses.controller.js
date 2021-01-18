/**
 * Created by kundan
 */
(function (window, document, $, angular) {

    var expenseApp = angular.module('expenseApp');

    expenseApp.controller("addExpensesCtrl", ["$scope", "$rootScope", "expenseSvcs", "expenseModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "expenseData", "registrationSvcs", function ($scope, $rootScope, expenseSvcs, expenseModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, expenseData, registrationSvcs) {

        var vm = this;

        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];


        //console.log("vm.respData::",vm.respData);
        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
            var monthIndex = moment((Date.today()).addDays(0)).format("M");
            vm.month = vm.monthArray[monthIndex - 1];
        }();

        vm.update = false;
        var defaultData = function(){
            var reqMap = {
                month: vm.month,
                year: vm.year
            };
            expenseSvcs.getExpensesByMonth(reqMap,function (res) {
                console.log("res.data:",res.data);
                if(res.data){
                    if(res.data.expenseData.length > 0){
                        vm.respData = res.data.expenseData;
                        vm.update = true;
                        for(var k in vm.respData){
                            vm.respData[k].date = new Date(vm.respData[k].expenseDate);

                            for(var m in vm.respData[k].subAccountList){
                                vm.respData[k].subAccountList[m].date = new Date( vm.respData[k].subAccountList[m].expenseDate);
                            }
                        }
                        console.log("respData:",vm.respData);
                    }else{
                        vm.update = false;
                        vm.respData = res.data.defaultData;
                    }

                }

            });
        };
        defaultData();
        vm.getSelectedDateRange = function(){
            defaultData();
        };

        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'yyyy-MM-dd';
                    this.popup = popup || {
                            opened: false
                        };
                    this.open = open || function () {
                            this.popup.opened = true;
                        };
                    this.dateOptions = dateOptions || {
                            formatYear: 'yyyy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        vm.createDateMap = function () {
            return dateMaps.getMap();
        };

        /* --------------end-------------------- */
        var count = 0;
        vm.getSerialNumber = function(){
            console.log("count:",count);
            count = count+1;
            return count;
        };
        vm.formSubmit = function () {
            vm.formConfig.formElement.trigger('submit');
        };
        vm.formConfig = {
            preCompile: function (e) {
            },
            postCompile: function (e) {
                vm.formConfig.formScope = e.scope;
                vm.formConfig.formElement = e.element;
            },

            submit: function (e) {
                e.preventDefault();
                var validationResponse = vm.formConfig.validateFormInputs();
                var form = vm.formConfig.formScope.addExpenses;
                if (validationResponse.invalidInputs.length == 0) {
                    var blankInstanceForm = {};
                    //console.log("vm.respData:",vm.respData);
                    for(var k in vm.respData){
                        if(vm.respData[k].date == null || vm.respData[k].date == '' || vm.respData[k].date == undefined){
                            notifySvcs.info({
                                content : "Please select all the date(s)"
                            });
                            return
                        }
                        for(var m in vm.respData[k].subAccountList){
                            if(vm.respData[k].subAccountList[m].date == null
                                || vm.respData[k].subAccountList[m].date == ''
                                || vm.respData[k].subAccountList[m].date == undefined){
                                notifySvcs.info({
                                    content : "Please select all the date(s)"
                                });
                                return
                            }
                        }
                    }

                    for(var k in vm.respData){
                        vm.respData[k].month = vm.month;
                        vm.respData[k].year = vm.year;
                        if(vm.respData[k].date != null || vm.respData[k].date != '' || vm.respData[k].date != undefined){
                            vm.respData[k].expenseDate = moment(vm.respData[k].date).format("YYYY-MM-DD");
                            delete vm.respData[k].date;
                        }
                        for(var m in vm.respData[k].subAccountList){
                            //vm.respData[k].subAccountList[m].month = vm.month;
                            //vm.respData[k].subAccountList[m].year = vm.year;
                            if(vm.respData[k].subAccountList[m].date != null
                                || vm.respData[k].subAccountList[m].date != ''
                                || vm.respData[k].subAccountList[m].date != undefined){
                                vm.respData[k].subAccountList[m].expenseDate = moment(vm.respData[k].subAccountList[m].date).format("YYYY-MM-DD");
                                delete vm.respData[k].subAccountList[m].date;
                            }
                        }
                    }
                    //console.log("vm.respData:",vm.respData);
                    blankInstanceForm["data"] = angular.copy(vm.respData);
                    //
                    console.log("blankInstanceForm:",blankInstanceForm);
                    //return;
                    expenseSvcs.createExpensesByMonth(blankInstanceForm , function (res){

                        if(res.data == 'Success'){
                            notifySvcs.success({
                                content:"Submitted Successfully."
                            });
                            //$state.reload();
                            defaultData();

                        }else{
                            notifySvcs.error({
                                content:"Action not performed"
                            })
                        }
                    });

                } else {
                    notifySvcs.info({
                        title: "Expenses",
                        content: "Please fill details."
                    });
                }
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);