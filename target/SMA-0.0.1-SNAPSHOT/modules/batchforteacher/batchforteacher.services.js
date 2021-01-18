/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    angular.module('batchForTeacherApp').factory("batchForTeacherSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var getBatchListForTeacher = function (model, successCallback, errorCallback) {
            http.getAjaxBatchListForTeacher(model, successCallback, errorCallback);
        };
        var getStudentListForTeacher = function (model, successCallback, errorCallback) {
            http.getAjaxStudentListForTeacher(model, successCallback, errorCallback);
        };
        var createFeedbackByTeacher = function (model, successCallback, errorCallback) {
            http.createAjaxFeedbackByTeacher(model, successCallback, errorCallback);
        };
        var createRatings = function (model, successCallback, errorCallback) {
            http.createAjaxRatings(model, successCallback, errorCallback);
        };
         var getAttributeForRating = function (model, successCallback, errorCallback) {
            http.getAjaxAttributeForRating(model, successCallback, errorCallback);
        };
        var getDateAndTimeForBatch = function (model, successCallback, errorCallback) {
            http.getAjaxDateAndTimeForBatch(model, successCallback, errorCallback);
        };

        var getStudentRating = function (model, successCallback, errorCallback) {
            http.getAjaxStudentRating(model, successCallback, errorCallback);
        };
        var deleteObject = function (model, successCallback, errorCallback) {
            http.deleteAjaxObject(model, successCallback, errorCallback);
        };
        var getHomeWorkData = function (model, successCallback, errorCallback) {
            http.getAjaxHomeWorkData(model, successCallback, errorCallback);
        };
        var getHomeWrkList = function (model, successCallback, errorCallback) {
            http.getAjaxHomeWrkList(model, successCallback, errorCallback);
        };

        return {
            getBatchListForTeacher : getBatchListForTeacher,
            getStudentListForTeacher:getStudentListForTeacher,
            createFeedbackByTeacher:createFeedbackByTeacher,
            createRatings:createRatings,
            getAttributeForRating:getAttributeForRating,
            getDateAndTimeForBatch:getDateAndTimeForBatch,
            getStudentRating:getStudentRating,
            deleteObject:deleteObject,
            getHomeWorkData:getHomeWorkData,
            getHomeWrkList:getHomeWrkList
        }

    }]);

})(window, window.document, window.jQuery, window.angular);
