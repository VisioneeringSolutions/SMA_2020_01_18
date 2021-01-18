/**
 * Created by dell on 29-05-19.
 */

(function (window, document, $, angular) {

    angular.module('reportApp').factory("reportSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var getStudentUser = function (model, successCallback, errorCallback) {
            http.getAjaxReportStudentUser(model, successCallback, errorCallback);
        };
        var getTeacherUser = function (model, successCallback, errorCallback) {
            http.getAjaxReportTeacherUser(model, successCallback, errorCallback);
        };
        var getMusicType = function (model, successCallback, errorCallback) {
            http.getAjaxMusicType(model, successCallback, errorCallback);
        };
        var getStudentRatingByMusicTypeAndPk = function (model, successCallback, errorCallback) {
            http.getAjaxReportStudentRatingByMusicTypeAndPk(model, successCallback, errorCallback);
        };
        var getTeacherRatingByMusicTypeAndPk = function (model, successCallback, errorCallback) {
            http.getAjaxReportTeacherRatingByMusicTypeAndPk(model, successCallback, errorCallback);
        };
        var getSalaryDetails = function (model, successCallback, errorCallback) {
            http.getAjaxReportSalaryDetails(model, successCallback, errorCallback);
        };
        var getMusicSessionDetails = function (model, successCallback, errorCallback) {
            http.getAjaxReportMusicSessionDetails(model, successCallback, errorCallback);
        };
        var getPaymentDetails = function (model, successCallback, errorCallback) {
            http.getAjaxReportPaymentDetails(model, successCallback, errorCallback);
        };
        var getOverallTurnOver = function (model, successCallback, errorCallback) {
            http.getAjaxReportOverallTurnOver(model, successCallback, errorCallback);
        };
        var getOverallGrossProfit = function (model, successCallback, errorCallback) {
            http.getAjaxReportOverallGrossProfit(model, successCallback, errorCallback);
        };
        var getTurnOverByInstrument = function (model, successCallback, errorCallback) {
            http.getAjaxReportTurnOverByInstrument(model, successCallback, errorCallback);
        };
        var getTurnOverByStudent = function (model, successCallback, errorCallback) {
            http.getAjaxReportTurnOverByStudent(model, successCallback, errorCallback);
        };
        var getOverallRating = function (model, successCallback, errorCallback) {
            http.getAjaxReportOverallRating(model, successCallback, errorCallback);
        };
        var getStudentRating = function (model, successCallback, errorCallback) {
            http.getAjaxDashboardStudentRating(model, successCallback, errorCallback);
        };
        var getStudentRatingByPk = function (model, successCallback, errorCallback) {
            http.getAjaxDashboardStudentRatingByPk(model, successCallback, errorCallback);
        };
        var getTeacherRating = function (model, successCallback, errorCallback) {
            http.getAjaxDashboardTeacherRating(model, successCallback, errorCallback);
        };
        var getTeacherRatingByPk = function (model, successCallback, errorCallback) {
            http.getAjaxDashboardTeacherRatingByPk(model, successCallback, errorCallback);
        };

        var getnotconvertedlist = function (model, successCallback, errorCallback) {
            http.getAjaxReportNotConvertStudent(model, successCallback, errorCallback);
        };
        var getStudentDetailsForInvoice = function (model, successCallback, errorCallback) {
            http.getAjaxReportStudentDetailsForInvoice(model, successCallback, errorCallback);
        };

        return {
            getStudentUser: getStudentUser,
            getTeacherUser: getTeacherUser,
            getMusicType: getMusicType,
            getStudentRatingByMusicTypeAndPk: getStudentRatingByMusicTypeAndPk,
            getTeacherRatingByMusicTypeAndPk: getTeacherRatingByMusicTypeAndPk,
            getSalaryDetails: getSalaryDetails,
            getMusicSessionDetails: getMusicSessionDetails,
            getPaymentDetails: getPaymentDetails,
            getOverallTurnOver: getOverallTurnOver,
            getOverallGrossProfit: getOverallGrossProfit,
            getTurnOverByInstrument: getTurnOverByInstrument,
            getTurnOverByStudent: getTurnOverByStudent,
            getOverallRating: getOverallRating,
            getStudentRating: getStudentRating,
            getStudentRatingByPk: getStudentRatingByPk,
            getTeacherRating: getTeacherRating,
            getTeacherRatingByPk: getTeacherRatingByPk,
            getnotconvertedlist: getnotconvertedlist,
            getStudentDetailsForInvoice: getStudentDetailsForInvoice

        }

    }]);

})(window, window.document, window.jQuery, window.angular);