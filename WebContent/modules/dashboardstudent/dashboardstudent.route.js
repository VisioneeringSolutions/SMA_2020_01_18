/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardStudentApp = angular.module("dashBoardStudentApp");

    dashBoardStudentApp.run(function (amMoment) {

    });

    dashBoardStudentApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('dashboardstudent', {
            url: "/dashboardstudent",
            data: {
                parentUrl: "/dashboardstudent"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs","dashBoardStudentSvcs","lookupStoreSvcs","localStorage", function ($q, registrationSvcs,dashBoardStudentSvcs, lookupStoreSvcs,localStorage) {
                    var deferred = $q.defer();
                    async.parallel({
                            BatchList: function (callback) {
                                var reqMap  = {};
                                registrationSvcs.getBatchList(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            },
                            RoomList: function (callback) {
                                var reqMap  = {};
                                registrationSvcs.getMusicRoomList(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            },
                            BatchListByPk: function (callback) {
                                var reqMap  = {
                                                userKey:localStorage.get("userKey")
                                                };
                                dashBoardStudentSvcs.getBatchByStudentPk(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            }
                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            abstract: false,
            templateUrl: 'modules/dashboardstudent/dashboardstudent.html',
            controller: 'dashboardStudentCtrl',
            controllerAs: 'dashboardStudent',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })

    }]);

})(window, window.document, window.jQuery, window.angular);