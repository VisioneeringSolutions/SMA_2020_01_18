/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardAdminApp = angular.module("dashBoardAdminApp");

    dashBoardAdminApp.run(function (amMoment) {

    });

    dashBoardAdminApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }

        function onExit($rootScope, $state) {

            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }

        $stateProvider
            .state('dashboardadmin', {
            url: "/dashboardadmin",
            data: {
                parentUrl: "/dashboardadmin"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs","dashBoardAdminSvcs", function ($q, registrationSvcs, lookupStoreSvcs,dashBoardAdminSvcs) {
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
                            StudentList: function (callback) {
                                var reqMap  = { };
                                registrationSvcs.getStudentUser(reqMap,function (res) {
                                    //console.log("route:",res.data);
                                    callback(null, res.data);
                                });
                            },
                            FollowUpDateList: function (callback) {
                                var reqMap  = {
                                };
                                dashBoardAdminSvcs.getFollowUpDate(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            },
                            AllFollowUpList: function (callback) {
                                var reqMap  = {
                                };
                                dashBoardAdminSvcs.getAllFollowUpDate(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            }
                         /*   FollowUpDateList :function (callback) {
                                 var reqMap  = {
                                    currentDate: moment.format("DD-MM-YYYY")
                                      };
                            console.log("currentDate:",currentDate);
                            dashBoardAdminSvcs.getFollowUpDate(reqMap,function (res) {
                            console.log("currentDate  res.data---:",res.data);
                            callback(null, res.data);
                        });
                      }*/
                        },
                        function (err, results) {
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            abstract: false,
            templateUrl: 'modules/dashboardadmin/dashboardadmin.html',
            controller: 'dashboardAdminCtrl',
            controllerAs: 'dashboardAdmin',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })

    }]);

})(window, window.document, window.jQuery, window.angular);