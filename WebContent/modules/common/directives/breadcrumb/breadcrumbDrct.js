/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("breadcrumbDrct", ["$timeout", function ($timeout) {
        return {
            replace: true,
            scope: {},
            templateUrl: 'modules/common/directives/breadcrumb/breadcrumbDrct.html',
            link: function (scope, element, attrs) {

            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
