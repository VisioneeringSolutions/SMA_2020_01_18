/**
 * Created by Kundan on 13/03/19.
 */
(function (window, document, $, angular) {

    var music = angular.module("music");

    music.run(function (amMoment) {

    });

    music.config(['$httpProvider', '$provide', '$stateProvider', '$urlRouterProvider', function ($httpProvider, $provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {
            //console.log("onEnter");
            //console.log("$rootScope", $rootScope);
            //console.log("$state", $state);
        }
        function onExit($rootScope, $state) {
            //console.log("onExit");
            //console.log("$rootScope", $rootScope);
            //console.log("$state", $state);
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }

        $stateProvider
            .state('music', {
                url: "/",
                templateUrl: 'modules/home/home.html',
                controller: 'homeCtrl',
                controllerAs: 'home',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('academy', {
            url: "/dashboardadmin",
            templateUrl: 'modules/dashboardadmin/dashboardadmin.html',
            controller: 'dashboardAdminCtrl',
            controllerAs: 'dashboardAdmin',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })
    }]);

})(window, window.document, window.jQuery, window.angular);