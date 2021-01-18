/**
 * Created by vishuja
 */
(function (window, document, $, angular) {

    var expenseApp = angular.module('expenseApp');

    expenseApp.controller("registerExpensesCtrl", ["$scope", "$rootScope", "expenseSvcs", "expenseModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "queryFormData", "registrationSvcs", function ($scope, $rootScope, expenseSvcs, expenseModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, queryFormData, registrationSvcs) {

        var vm = this;
        vm.respData = queryFormData.ExpenseList;
        vm.mainList = vm.respData;
        vm.showTable = true;
        function getModel (){
            vm.formModel = {};
            vm.formModel = expenseModel.getInstance('addAccountType');
            vm.showTable = true;
            return  vm.formModel;
        }
        vm.addExpense = function(){
            vm.showTable = false;
            vm.mainList.push(getModel());
        };
        vm.addSubExpense = function(indexValue){
            vm.showTable = false;
            vm.mainList[indexValue-1].subAccountList.push(expenseModel.getInstance('addSubAccountType'));
            vm.showTable = true;
        };
        vm.indexChar = function (index, char) {
            return String.fromCharCode(97 + char);
        };

        vm.removeExpenseArr = [];
        vm.removeExpenseList = [];
        vm.getPreviousData = function (index,d) {
            if(d.isActive ===true) {
                d.isActive=false;
                var tempArr = [];
                vm.removeExpenseList = vm.removeExpenseArr.filter(function (temp) {
                    if (tempArr.indexOf(temp.primaryKey) === -1) {
                        tempArr.push(temp.primaryKey);
                        return true;
                    } else {
                        // Already present in array, don't add it
                        return false;
                    }
                });
            }
        };
        vm.removeSubExpenseList = [];
        vm.getRemoveData = function (index, d) {
            if(d.isActive === true) {
                d.isActive=false;
                var tempArr = [];
                vm.removeSubExpenseList = vm.removeExpenseArr.filter(function (temp) {
                    if (tempArr.indexOf(temp.primaryKey) === -1) {
                        tempArr.push(temp.primaryKey);
                        return true;
                    } else {
                        // Already present in array, don't add it
                        return false;
                    }
                });
            }
        };
        vm.removeSubAccount = function(index,d,data){
            if(data.primaryKey) vm.removeExpenseArr.push(data);
            d.subAccountList.splice(index,1);
            vm.getRemoveData(index,data);
        };
        vm.removeExpenses = function (index, data, d) {
            vm.mainList.splice(index, 1);
            vm.removeExpenseArr.push(data);
            vm.getPreviousData(index, data, d);
        };
        vm.accountType = function(data){
            if(data.accountType == ''){
                data.accountType = null;
            }
        };
        vm.accountName = function(data){
            if(data.accountName == ''){
                data.accountName = null;
            }
        };
        vm.descriptions = function(data){
            if(data.descriptions == ''){
                data.descriptions = null;
            }
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
                if (validationResponse.invalidInputs.length == 0) {
                    var blankInstanceForm = {};

                    blankInstanceForm['data']= vm.mainList;
                    blankInstanceForm['removeExpenseList']= vm.removeExpenseList;
                    blankInstanceForm['removeSubExpenseList']= vm.removeSubExpenseList;
                    console.log("blankInstanceForm:",blankInstanceForm);
                    //return null;
                    expenseSvcs.createExpenses(blankInstanceForm , function (res){

                        if(res.data == 'Success'){
                            notifySvcs.success({
                                content:"Submitted Successfully."
                            });
                            $state.reload();

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