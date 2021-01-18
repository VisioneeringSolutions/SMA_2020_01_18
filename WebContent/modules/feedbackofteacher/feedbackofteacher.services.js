/**
 * Created by nikita on 2019-05-29.
 */

(function (window, document, $, angular) {

    angular.module('feedbackOfTeacherApp').factory("feedbackOfTeacherSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var getBatchListForStudent = function (model, successCallback, errorCallback) {
            http.getAjaxFeedbackBatchListForStudent(model, successCallback, errorCallback);
        };
        var getSlotForStudent = function (model, successCallback, errorCallback) {
            http.getAjaxFeedbackSlotForStudent(model, successCallback, errorCallback);
        };
        var getTeacherList = function (model, successCallback, errorCallback) {
            http.getAjaxFeedbackTeacherList(model, successCallback, errorCallback);
        };
        var getAttrForRating = function (model, successCallback, errorCallback) {
            http.getAjaxFeedbackAttrForRating(model, successCallback, errorCallback);
        };
        var createRatingsForTeacher = function (model, successCallback, errorCallback) {
            http.createAjaxFeedbackRatingsForTeacher(model, successCallback, errorCallback);
        };

        return {
            getBatchListForStudent: getBatchListForStudent,
            getSlotForStudent: getSlotForStudent,
            getTeacherList:getTeacherList,
            getAttrForRating:getAttrForRating,
            createRatingsForTeacher:createRatingsForTeacher
        }

    }]);

})(window, window.document, window.jQuery, window.angular);