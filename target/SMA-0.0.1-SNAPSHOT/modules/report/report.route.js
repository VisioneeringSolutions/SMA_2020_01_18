/**
 * Created by dell on 29-05-19.
 */
/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module("reportApp");

    reportApp.run(function (amMoment) {

    });

    reportApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('report', {
                url: "/report",
                abstract: true,
                templateUrl: 'modules/report/report.html',
                controller: 'reportCtrl',
                controllerAs: 'report',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('report.statistics', {
            url: "/statistics",
            data: {
                parentUrl: "/report"
            },
            resolve: {
                /*studentData: ["$q", "registrationSvcs", function ($q, registrationSvcs) {
                    var deferred = $q.defer();
                    var reqMap  = {
                        student:'student'
                    };
                    registrationSvcs.getStudentUser(reqMap,function (res) {
                        deferred.resolve(res.data);
                    }, function (err) {
                        deferred.resolve(err);
                    });
                    return deferred.promise;
                }]*/
            },
            templateUrl: 'modules/report/statisticsreport/statistics.html',
            controller: 'statisticsReportCtrl',
            controllerAs: 'statisticsReport',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('report.general', {
            url: "/general",
            data: {
                parentUrl: "/report"
            },
            resolve: {
                /*studentData: ["$q", "registrationSvcs", function ($q, registrationSvcs) {
                    var deferred = $q.defer();
                    var reqMap  = {
                        student:'student'
                    };
                    registrationSvcs.getStudentUser(reqMap,function (res) {
                        deferred.resolve(res.data);
                    }, function (err) {
                        deferred.resolve(err);
                    });
                    return deferred.promise;
                }]*/
            },
            templateUrl: 'modules/report/generalreport/general.html',
            controller: 'generalReportCtrl',
            controllerAs: 'generalReport',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })

    }]);

})(window, window.document, window.jQuery, window.angular);