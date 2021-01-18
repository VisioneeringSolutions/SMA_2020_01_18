
(function (window, document, $, angular) {

    var musicAuth = angular.module('musicAuth');

    musicAuth.controller("logoutCtrl", ["$scope", "$http", "$rootScope", "authSvcs", "authModel", "localStorage", function ($scope, $http, $rootScope, authSvcs, authModel, localStorage) {

        var vm = this;

        if (localStorage.get('visionKey')) {
            authSvcs.logout();
        } else {
            $rootScope.pageLoader.stop();
            authSvcs.redirect("login");
        }

        vm.submit = function () {
            console.log("logout function hit");
            authSvcs.logout();
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
