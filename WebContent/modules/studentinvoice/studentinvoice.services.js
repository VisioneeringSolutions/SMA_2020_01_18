/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    angular.module('studentInvoiceApp').factory("studentInvoiceSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createInvoice = function (model, successCallback, errorCallback) {
            http.createAjaxInvoiceInvoice(model, successCallback, errorCallback);
        };
        var getStudentForInvoice = function (model, successCallback, errorCallback) {
            http.getAjaxInvoiceStudentForInvoice(model, successCallback, errorCallback);
        };
        var getInvoiceByStudentPk = function (model, successCallback, errorCallback) {
            http.getAjaxInvoiceInvoiceByStudentPk(model, successCallback, errorCallback);
        };
        var getMaxInvoiceNo = function (model, successCallback, errorCallback) {
            http.getAjaxInvoiceMaxInvoiceNo(model, successCallback, errorCallback);
        };
        var createFreeTextInvoiceByPk = function (model, successCallback, errorCallback) {
            http.createAjaxInvoiceFreeTextInvoiceByPk(model, successCallback, errorCallback);
        };
        var getFreeTextInvoiceByPk = function (model, successCallback, errorCallback) {
            http.getAjaxInvoiceFreeTextInvoiceByPk(model, successCallback, errorCallback);
        };
        var createInvoicePdf = function (model, successCallback, errorCallback) {
            http.createAjaxPdfInvoicePdf(model, successCallback, errorCallback);
        };
        var getInvoiceDateWiseDetails = function (model, successCallback, errorCallback) {
            http.getAjaxInvoiceInvoiceDateWiseDetails(model, successCallback, errorCallback);
        };
        var getStudentInvoicePdf = function (model, successCallback, errorCallback) {
            http.getAjaxPdfGetStudentInvoicePdf(model, successCallback, errorCallback);
        };


        return {
            createInvoice:createInvoice,
            getStudentForInvoice : getStudentForInvoice,
            getInvoiceByStudentPk : getInvoiceByStudentPk,
            getMaxInvoiceNo : getMaxInvoiceNo,
            createFreeTextInvoiceByPk : createFreeTextInvoiceByPk,
            getFreeTextInvoiceByPk : getFreeTextInvoiceByPk,
            createInvoicePdf : createInvoicePdf,
            getInvoiceDateWiseDetails : getInvoiceDateWiseDetails,
            getStudentInvoicePdf : getStudentInvoicePdf
        }

    }]);

})(window, window.document, window.jQuery, window.angular);