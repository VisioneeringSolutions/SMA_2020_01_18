/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    angular.module('competitionApp').factory("competitionSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createCompetition = function (model, successCallback, errorCallback) {
            http.createAjaxCompetition (model, successCallback, errorCallback);
        };
        var getCompetition = function (model, successCallback, errorCallback) {
            http.getAjaxCompetition (model, successCallback, errorCallback);
        };
        var getCompetitionByPk = function (model, successCallback, errorCallback) {
            http.getAjaxCompetitionByPk (model, successCallback, errorCallback);
        };

        return {
            createCompetition:createCompetition,
            getCompetition:getCompetition,
            getCompetitionByPk:getCompetitionByPk
        }

    }]);

})(window, window.document, window.jQuery, window.angular);