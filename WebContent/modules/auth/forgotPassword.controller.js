
(function (window, document, $, angular) {

    var musicAuth = angular.module('musicAuth');

    musicAuth.controller("forgotPasswordCtrl", ["$scope", "$http", "$rootScope", "authSvcs", "authModel", "localStorage", function ($scope, $http, $rootScope, authSvcs, authModel, localStorage) {

        var vm = this;

        vm.baseUrl = window.sInstances.origin + "/SMA/";
        vm.description="Please use the following as ";

       /* vm.submitValue = function (userId,description) {
            authSvcs.forgotPassword({
                usrName: userId,
                description:description

            });
        };*/
    }]);

})(window, window.document, window.jQuery, window.angular);