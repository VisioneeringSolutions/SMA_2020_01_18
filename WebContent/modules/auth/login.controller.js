(function (window, document, $, angular) {

    var musicAuth = angular.module('musicAuth');

    musicAuth.controller("loginCtrl", ["$scope", "$http", "$rootScope", "authSvcs", "authModel", "localStorage", function ($scope, $http, $rootScope, authSvcs, authModel, localStorage) {

        var vm = this;

        vm.baseUrl = window.sInstances.origin + "/SMA/";




        vm.changeClassSignUp = function () {
            document.getElementById('login-container').style.display='none';
            document.getElementById('login-form-container').style.display='block';
        };

        vm.signIn = function(usrName, password){
            authSvcs.login({
                usrName: usrName,
                password: password
            });
        };




        $rootScope.pageLoader.stop();

    }]);

})(window, window.document, window.jQuery, window.angular);