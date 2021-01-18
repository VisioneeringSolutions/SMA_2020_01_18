/**
 * Created by Tanuj on 16-05-2019.
 */

(function (window, document, $, angular) {


    angular.module('settingApp').factory("settingSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var updateLoginUserData = function (model, successCallback, errorCallback) {
            http.updateAjaxRegistrationLoginUserData(model, successCallback, errorCallback);
        };
        var updateProfileObject = function (model, successCallback, errorCallback) {
            http.updateAjaxRegistrationProfileObject(model, successCallback, errorCallback);
        };
        var getManagementUser = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationManagementUser(model, successCallback, errorCallback);
        };
        var resetPassword = function (model, successCallback, errorCallback) {
            http.resetAuthPassword(model, successCallback, errorCallback);
        };

        /*student----------*/


        var getStudentUserData = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationStudentUserData(model, successCallback, errorCallback);
        };
        var getTeacherUserData = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationTeacherUserData(model, successCallback, errorCallback);
        };
        var getProfileImage = function (model, successCallback, errorCallback) {
            http.getAjaxProfileImage(model, successCallback, errorCallback);
        };






        return {
            updateLoginUserData : updateLoginUserData,
            updateProfileObject:  updateProfileObject,
            getManagementUser:getManagementUser,
            getTeacherUserData:getTeacherUserData,
            getStudentUserData:getStudentUserData,
            resetPassword : resetPassword,
            getProfileImage : getProfileImage
        }

    }]);
})(window, window.document, window.jQuery, window.angular);