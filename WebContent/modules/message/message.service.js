/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    angular.module('messageApp').factory("messageSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var getMessageList = function (model, successCallback, errorCallback) {
            http.getAjaxMessageMessageList(model, successCallback, errorCallback);
        };
        var getAvailableContactList = function (model, successCallback, errorCallback) {
            http.getAjaxMessageAvailableContactList(model, successCallback, errorCallback);
        };
        var getContactListByUserKey = function (model, successCallback, errorCallback) {
            http.getAjaxMessageContactListByUserKey(model, successCallback, errorCallback);
        };
        var addContactList = function (model, successCallback, errorCallback) {
            http.addAjaxMessageContactList(model, successCallback, errorCallback);
        };
        var createUserMessage = function (model, successCallback, errorCallback) {
            http.createAjaxMessageUserMessage(model, successCallback, errorCallback);
        };
        var getMessageByUser = function (model, successCallback, errorCallback) {
            http.getAjaxMessageMessageByUser(model, successCallback, errorCallback);
        };

        return {
            getMessageList: getMessageList,
            getAvailableContactList: getAvailableContactList,
            getContactListByUserKey: getContactListByUserKey,
            addContactList: addContactList,
            createUserMessage: createUserMessage,
            getMessageByUser: getMessageByUser


        }

    }]);

})(window, window.document, window.jQuery, window.angular);
