/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    angular.module('competitionForStudentsApp').factory("competitionForStudentsSvcs", ["http", "$rootScope", function (http, $rootScope) {


        var getCompListForStud = function (model, successCallback, errorCallback) {
            http.getAjaxCompListForStud (model, successCallback, errorCallback);
        };
        var getCompListForStudByPk = function (model, successCallback, errorCallback) {
            http.getAjaxCompListForStudByPk (model, successCallback, errorCallback);
        };


        return {
            getCompListForStud:getCompListForStud,
            getCompListForStudByPk:getCompListForStudByPk
        }

    }]);

})(window, window.document, window.jQuery, window.angular);