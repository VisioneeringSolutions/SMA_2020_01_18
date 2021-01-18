/**
 * Created by VE on 31-Aug-16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("localStorage", [function () {

        return window.sInstances.localStorage;

    }]);

})(window, window.document, window.jQuery, window.angular);