/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("salarySlipCtrl", ["$scope", "$rootScope","reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs,$state, notifySvcs, localStorage, $timeout) {
        $rootScope.pageHeader = "";
        $rootScope.pageDescription = "";

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        console.log("vm.yearArray :: ",vm.yearArray);

    }]);

})(window, window.document, window.jQuery, window.angular);