/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("storeSvcs", [function () {

        var storeEventManager = new window.sInstances.EventManager();

        return storeEventManager;

    }]);

})(window, window.document, window.jQuery, window.angular);