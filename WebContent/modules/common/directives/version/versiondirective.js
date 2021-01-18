(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    //QA
    //commonApp.value('version', '1.45');

    //PROD
    commonApp.value('version', '1.09');

    commonApp.directive('disableCache', function(version) {
        return {
            restrict: 'A',
            replace: false,
            link: function(scope, elem, attr) {
                attr.$set("src", attr.src +  "?v=" + version);
            }
        };
    });
    commonApp.directive('disableCacheCss', function(version) {
        return {
            restrict: 'A',
            replace: false,
            link: function(scope, elem, attr) {
                attr.$set("href", attr.href +  "?v=" + version);
            }
        };
    });

})(window, window.document, window.jQuery, window.angular);
