/**
 * Created by Kundan on 25-Apr-19.
 */


(function (window, document, $, angular) {

    angular.module('dashBoardTeacherApp').factory("dashBoardTeacherSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createTimeSlot = function (model, successCallback, errorCallback) {
            http.createAjaxTimeSlotTimeSlot(model, successCallback, errorCallback);
        };var getTimeSlotForAdmin = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotTimeSlotForAdmin(model, successCallback, errorCallback);
        };var getTimeSlotForTeacher = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotTimeSlotForTeacher(model, successCallback, errorCallback);
        };var createTimeSlotForTeacher = function (model, successCallback, errorCallback) {
            http.createAjaxTimeSlotTimeSlotForTeacher(model, successCallback, errorCallback);
        };var getTimeSlotForStudent = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotTimeSlotForStudent(model, successCallback, errorCallback);
        };var getTimeSlotCount = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotTimeSlotCount(model, successCallback, errorCallback);
        };var getBatchByStudentPk = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotBatchByStudentPk(model, successCallback, errorCallback);
        };var getStudentBatchByTecherPk = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotStudentBatchByTecherPk(model, successCallback, errorCallback);
        };var getTimeSlotDetailsForMobileTech = function (model, successCallback, errorCallback) {
            http.getAjaxTimeSlotTimeSlotDetailsForMobileTech(model, successCallback, errorCallback);
        };

        return {
            createTimeSlot : createTimeSlot,
            getTimeSlotForAdmin : getTimeSlotForAdmin,
            getTimeSlotForTeacher : getTimeSlotForTeacher,
            createTimeSlotForTeacher : createTimeSlotForTeacher,
            getTimeSlotForStudent : getTimeSlotForStudent,
            getTimeSlotCount : getTimeSlotCount,
            getBatchByStudentPk : getBatchByStudentPk,
            getStudentBatchByTecherPk : getStudentBatchByTecherPk,
            getTimeSlotDetailsForMobileTech : getTimeSlotDetailsForMobileTech
        }

    }]);

})(window, window.document, window.jQuery, window.angular);