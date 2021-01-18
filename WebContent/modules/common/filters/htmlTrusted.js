/**
 * Created by dell on 04-06-19.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');
    commonApp.filter('trusted', function($sce){
        return function(html){
            return $sce.trustAsHtml(html)
        }
    });

})(window, window.document, window.jQuery, window.angular);