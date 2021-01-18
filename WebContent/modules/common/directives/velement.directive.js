/**
 * Created by abc on 5/30/2017.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("velement", ["$compile", function ($compile) {
        return {
            restrict: "A",
            scope: {
                velement: '=?'
            },
            link: function (scope, element, attrs) {
                scope.velement[(attrs['name'] || ['velement'])] = {element: element, attrs: attrs};

            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);