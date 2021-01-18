/**
 * Created by vishuja
 */

(function (window, document, $, angular) {

    angular.module('expenseApp').factory("expenseSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createExpenses = function (model, successCallback, errorCallback) {
            http.createAjaxExpensesExpenses(model, successCallback, errorCallback);
        };
        var getRegisterExpenses = function (model, successCallback, errorCallback) {
            http.getAjaxExpensesRegisterExpenses(model, successCallback, errorCallback);
        };
        var getExpensesByMonth = function (model, successCallback, errorCallback) {
            http.getAjaxExpensesExpensesByMonth(model, successCallback, errorCallback);
        };
        var createExpensesByMonth = function (model, successCallback, errorCallback) {
            http.createAjaxExpensesExpensesByMonth(model, successCallback, errorCallback);
        };
        var getDataForExpenseSheet = function (model, successCallback, errorCallback) {
            http.getAjaxExpensesDataForExpenseSheet(model, successCallback, errorCallback);
        };
        var expenseSheetPdf = function (model, successCallback, errorCallback) {
            http.expenseAjaxPdfSheetPdf(model, successCallback, errorCallback);
        };


        return {
            createExpenses:createExpenses,
            getRegisterExpenses:getRegisterExpenses,
            getExpensesByMonth : getExpensesByMonth,
            createExpensesByMonth : createExpensesByMonth,
            getDataForExpenseSheet : getDataForExpenseSheet,
            expenseSheetPdf : expenseSheetPdf
        }

    }]);

})(window, window.document, window.jQuery, window.angular);
