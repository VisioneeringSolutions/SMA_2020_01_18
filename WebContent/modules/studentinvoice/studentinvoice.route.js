/**
 * Created by nikitaa on 2019-05-25.
 */
/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    var studentInvoiceApp = angular.module("studentInvoiceApp");

    studentInvoiceApp.run(function (amMoment) {

    });

    studentInvoiceApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('studentinvoice', {
                url: "/studentinvoice",
                abstract: true,
                templateUrl: 'modules/studentinvoice/studentinvoice.html',
                controller: 'studentInvoiceCtrl',
                controllerAs: 'studentInvoice',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('studentinvoice.generatestudentinvoice', {
            url: "/generatestudentinvoice",
            data: {
                parentUrl: "/studentinvoice"
            },
            resolve: {
                typeData: ["$q", "studentInvoiceSvcs", "lookupStoreSvcs", function ($q, studentInvoiceSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({

                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/studentinvoice/generatestudentinvoice/generatestudentinvoice.html',
            controller: 'generateStudentInvoiceCtrl',
            controllerAs: 'generateStudentInvoice',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('studentinvoice.studentinvoiceview', {
            url: "/studentinvoiceview",
            data: {
                parentUrl: "/studentinvoice"
            },
            resolve: {
                typeData: ["$q", "studentInvoiceSvcs", "localStorage", function ($q, studentInvoiceSvcs, localStorage) {
                    var deferred = $q.defer();
                    async.parallel({

                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/studentinvoice/studentinvoiceview/studentinvoiceview.html',
            controller: 'studentInvoiceViewCtrl',
            controllerAs: 'studentInvoiceView',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        });
    }]);

})(window, window.document, window.jQuery, window.angular);