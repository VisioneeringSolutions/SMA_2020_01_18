/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var salaryApp = angular.module("salaryApp");

    salaryApp.run(function (amMoment) {

    });

    salaryApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('salary', {
                url: "/salary",
                abstract: true,
                templateUrl: 'modules/salary/salary.html',
                controller: 'salaryCtrl',
                controllerAs: 'salary',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('salary.createsalary', {
            url: "/createsalary",
            data: {
                parentUrl: "/salary"
            },
            resolve: {
                typeData: ["$q", "salarySvcs", "lookupStoreSvcs", function ($q, salarySvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            Duration: function (callback) {
                                lookupStoreSvcs.getClassDuration().then(function (res) {
                                    callback(null, res);
                                });
                            }
                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/salary/createsalary/createsalary.html',
            controller: 'createSalaryCtrl',
            controllerAs: 'createSalary',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('salary.generateslip', {
            url: "/generateslip",
            data: {
                parentUrl: "/salary"
            },
            resolve: {
                typeData: ["$q", "salarySvcs", "lookupStoreSvcs", function ($q, salarySvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            Duration: function (callback) {
                                lookupStoreSvcs.getClassDuration().then(function (res) {
                                    callback(null, res);
                                });
                            }

                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/salary/generateslip/generateslip.html',
            controller: 'generateSlipCtrl',
            controllerAs: 'generateSlip',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('salary.teachersalaryview', {
            url: "/teachersalaryview",
            data: {
                parentUrl: "/salary"
            },
            resolve: {
                typeData: ["$q", "salarySvcs", "lookupStoreSvcs", function ($q, salarySvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            Duration: function (callback) {
                                lookupStoreSvcs.getClassDuration().then(function (res) {
                                    callback(null, res);
                                });
                            }

                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/salary/teachersalaryview/teachersalaryview.html',
            controller: 'teacherSalaryViewCtrl',
            controllerAs: 'teacherSalaryView',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })

    }]);

})(window, window.document, window.jQuery, window.angular);