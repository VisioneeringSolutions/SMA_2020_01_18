/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    angular.module('competitionForTeachersApp').factory("competitionForTeachersSvcs", ["http", "$rootScope", function (http, $rootScope) {


         var getCompListForTeacher = function (model, successCallback, errorCallback) {
         http.getAjaxCompListForTeacher (model, successCallback, errorCallback);
         };
        var getCompListForTeacherPk = function (model, successCallback, errorCallback) {
         http.getAjaxCompListForTeacherPk (model, successCallback, errorCallback);
         };


        return {
            getCompListForTeacher:getCompListForTeacher,
            getCompListForTeacherPk:getCompListForTeacherPk
        }

    }]);

})(window, window.document, window.jQuery, window.angular);