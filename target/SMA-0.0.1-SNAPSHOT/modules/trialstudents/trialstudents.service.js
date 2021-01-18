/**
 * Created by nikita on 2019-06-14.
 */

(function (window, document, $, angular) {

    angular.module('trialStudentsApp').factory("trialStudentsSvcs", ["http", "$rootScope", function (http, $rootScope) {


        var getTrialStudents = function (model, successCallback, errorCallback) {
            http.getAjaxTrialStudents(model, successCallback, errorCallback);
        };

        var getPhoneNumberList = function (model, successCallback, errorCallback) {
            http.getAjaxPhoneNumberList(model, successCallback, errorCallback);
        };
        var updateTrialStudents = function (model, successCallback, errorCallback) {
            http.updateAjaxTrialStudents(model, successCallback, errorCallback);
        };
        var getTrialStudentUser = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationTrialStudentUser(model, successCallback, errorCallback);
        };


        return {

            getTrialStudents:getTrialStudents,
            getPhoneNumberList:getPhoneNumberList,
            updateTrialStudents:updateTrialStudents,
            getTrialStudentUser:getTrialStudentUser
        }

    }]);

})(window, window.document, window.jQuery, window.angular);
