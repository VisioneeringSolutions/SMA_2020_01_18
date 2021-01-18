/**
 * Created by Tanuj on 16-05-2019.
 */
/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var settingApp = angular.module("settingApp");

    settingApp.run(function (amMoment) {

    });

    settingApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('setting', {
                url: "/setting",
                abstract: true,
                templateUrl: 'modules/setting/setting.html',
                controller: 'settingCtrl',
                controllerAs: 'setting',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('setting.profile', {
            url: "/profile",
            data: {
                parentUrl: "/setting"
            },
          resolve:{
                studentData: ["$q", "settingSvcs","localStorage", function ($q, settingSvcs,localStorage) {
                    var deferred = $q.defer();
                    async.parallel({
                        managementList: function (callback) {
                            var reqMap  = {
                                usrName: localStorage.get('userID')
                            };
                            settingSvcs.updateLoginUserData(reqMap,function (res) {
                                callback(null, res.data);
                            });
                        },
                        dataList: function (callback) {
                                var reqMap  = {
                                };
                               settingSvcs.getManagementUser(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            }
                        },
                        function (err, results) {
                            //console.log(results);
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
          },
            templateUrl: 'modules/setting/profile/updateprofile.html',
            controller: 'profileCtrl',
            controllerAs: 'profile',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('setting.studentprofile', {
            url: "/studentprofile",
            data: {
                parentUrl: "/setting"
            },
            resolve:{
                studData: ["$q", "settingSvcs","localStorage", function ($q, settingSvcs,localStorage) {
                    var deferred = $q.defer();
                    async.parallel({
                           studentList: function (callback) {
                                var reqMap  = {
                                    usrName: localStorage.get('userID'),
                                    userKey: localStorage.get('userKey')
                                };
                                settingSvcs.updateLoginUserData(reqMap,function (res) {
                                    console.log("reqMap: studentList:",reqMap);
                                    callback(null, res.data);
                                });
                            }
                           /* studDataList: function (callback) {
                                var reqMap  = {
                                    usrName: localStorage.get('userID')
                                };
                                settingSvcs.getStudentUserData(reqMap,function (res) {
                                    console.log("reqMap: studDataList:",reqMap);
                                    callback(null, res.data);
                                });
                            }*/
                        },
                        function (err, results) {
                            //console.log(results);
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/setting/studentprofile/studentprofile.html',
            controller: 'studentCtrl',
            controllerAs: 'studentProfile',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('setting.teacherprofile', {
            url: "/teacherprofile",
            data: {
                parentUrl: "/setting"
            },
            resolve:{
                teacherData: ["$q", "settingSvcs","localStorage", function ($q, settingSvcs,localStorage) {
                    var deferred = $q.defer();
                    async.parallel({
                            teacherList: function (callback) {
                                var reqMap  = {
                                    usrName: localStorage.get('userID'),
                                    userKey: localStorage.get('userKey')
                                };
                                settingSvcs.updateLoginUserData(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            }/*,
                            teacherDataList: function (callback) {
                                var reqMap  = {
                                };
                                settingSvcs.getTeacherUserData(reqMap,function (res) {
                                    callback(null, res.data);
                                });
                            }*/
                        },
                        function (err, results) {
                            //console.log(results);
                            deferred.resolve(results);
                        });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/setting/teacherprofile/teacherprofile.html',
            controller: 'teacherProfileCtrl',
            controllerAs: 'teacherProfile',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        });

    }]);

})(window, window.document, window.jQuery, window.angular);