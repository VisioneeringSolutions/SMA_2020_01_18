/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    angular.module('newsApp').factory("newsSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createNews = function (model, successCallback, errorCallback) {
            http.createAjaxNews(model, successCallback, errorCallback);
        };
        var getNewsList = function (model, successCallback, errorCallback) {
            http.getAjaxNewsList(model, successCallback, errorCallback);
        };
        var getNewsByPk = function (model, successCallback, errorCallback) {
            http.getAjaxNewsByPk(model, successCallback, errorCallback);
        };
        var deleteImageForNews = function (model, successCallback, errorCallback) {
            http.deleteAjaxImageForNews(model, successCallback, errorCallback);
        };
        var deleteNewsByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxNewsByPk(model, successCallback, errorCallback);
        };
        var createNewsForAdmin = function (model, successCallback, errorCallback) {
            http.createAjaxNewsForAdmin(model, successCallback, errorCallback);
        };

        return {
            createNews:createNews,
            getNewsList:getNewsList,
            getNewsByPk:getNewsByPk,
            deleteImageForNews:deleteImageForNews,
            deleteNewsByPk:deleteNewsByPk,
            createNewsForAdmin:createNewsForAdmin
               }

    }]);

})(window, window.document, window.jQuery, window.angular);
