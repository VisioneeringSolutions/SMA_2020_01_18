(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');
    commonApp.filter('startFrom', function () {
        return function (input, start) {
            if (input) {
                start = +start;
                return input.slice(start);
            }
            return [];
        };
    });

})(window, window.document, window.jQuery, window.angular);


