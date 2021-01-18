/**
 * Created by Kundan on 25-Apr-19.
 */


(function (window, document, $, angular) {

    angular.module('salaryApp').factory("salarySvcs", ["http", "$rootScope", function (http, $rootScope) {

        var getTeacherForSalary = function (model, successCallback, errorCallback) {
            http.getAjaxSalaryTeacherForSalary(model, successCallback, errorCallback);
        };var createSalary = function (model, successCallback, errorCallback) {
            http.createAjaxSalarySalary(model, successCallback, errorCallback);
        };var getTeacherForSalaryByMonth = function (model, successCallback, errorCallback) {
            http.getAjaxSalaryTeacherForSalaryByMonth(model, successCallback, errorCallback);
        };var createSalaryByMonthForSlip = function (model, successCallback, errorCallback) {
            http.createAjaxSalarySalaryByMonthForSlip(model, successCallback, errorCallback);
        };var getSalaryByTeacherPk = function (model, successCallback, errorCallback) {
            http.getAjaxSalarySalaryByTeacherPk(model, successCallback, errorCallback);
        };var createSalaryPdf = function (model, successCallback, errorCallback) {
            http.createAjaxPdfSalaryPdf(model, successCallback, errorCallback);
        };


        return {
            getTeacherForSalary : getTeacherForSalary,
            createSalary : createSalary,
            getTeacherForSalaryByMonth : getTeacherForSalaryByMonth,
            createSalaryByMonthForSlip : createSalaryByMonthForSlip,
            getSalaryByTeacherPk : getSalaryByTeacherPk,
            createSalaryPdf : createSalaryPdf

        }

    }]);

})(window, window.document, window.jQuery, window.angular);