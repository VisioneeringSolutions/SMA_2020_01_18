/**
 * Created by nikita on 2019-06-14.
 */

(function (window, document, $, angular) {

    angular.module('queryFormApp').factory("queryFormSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createQueryForm = function (model, successCallback, errorCallback) {
            http.createAjaxQueryForm(model, successCallback, errorCallback);
        };
        var getQueryForm = function (model, successCallback, errorCallback) {
            http.getAjaxQueryForm(model, successCallback, errorCallback);
        };
        var moveQryFormFn = function (model, successCallback, errorCallback) {
            http.moveAjaxQryFormFn(model, successCallback, errorCallback);
        };
        var getQueryStudentsByPk = function (model, successCallback, errorCallback) {
            http.getAjaxQueryStudentsByPk(model, successCallback, errorCallback);
        };
        var notConvertedStudFn = function (model, successCallback, errorCallback) {
            http.notAjaxConvertedStudFn(model, successCallback, errorCallback);
        };
        var getPhoneNumberList = function (model, successCallback, errorCallback) {
            http.getAjaxPhoneNumberList(model, successCallback, errorCallback);
        };


        return {
            createQueryForm:createQueryForm,
            getQueryForm:getQueryForm,
            moveQryFormFn:moveQryFormFn,
            getQueryStudentsByPk:getQueryStudentsByPk,
            notConvertedStudFn:notConvertedStudFn,
            getPhoneNumberList:getPhoneNumberList
        }

    }]);

})(window, window.document, window.jQuery, window.angular);
