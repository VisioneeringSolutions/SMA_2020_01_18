(function (window, document, $, angular) {

    var musicAuth = angular.module('musicAuth');

    musicAuth.controller("changePasswordCtrl", ["$scope", "$rootScope", "$http", "authSvcs", "authModel", "localStorage", function ($scope, $rootScope, $http, authSvcs, authModel, localStorage) {

        var vm = this;

        if (localStorage.get('visionKey')) {
            //authSvcs.isLoggedIn();
            //$rootScope.pageLoader.stop();
        } else {
            //$rootScope.pageLoader.stop();
        }

        vm.submit = function (event, form) {
            event.preventDefault();
            if (form.password.$modelValue == form.cpassword.$modelValue) {
                var requestMap = {
                    usrName: localStorage.get('userID'),
                    password: form.password.$modelValue
                };
                authSvcs.changePassword(requestMap);
            } else {
                alert("form is not valid check details please");
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
