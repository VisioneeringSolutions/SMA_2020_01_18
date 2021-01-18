
(function (window, document, $, angular) {

    var homeApp = angular.module("homeApp");

    homeApp.run(function (amMoment) {

    });

    homeApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {
            //console.log("onEnter");
            //console.log("$rootScope", $rootScope);
            //console.log("$state", $state);
        };
        function onExit($rootScope, $state) {
            //console.log("onExit");
            //console.log("$rootScope", $rootScope);
            //console.log("$state", $state);
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        };

        $stateProvider
            .state('home', {
                url: "/dashboard",
                abstract: true,
                templateUrl: 'modules/home/home.html',
                controller: 'homeCtrl',
                controllerAs: 'home',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);