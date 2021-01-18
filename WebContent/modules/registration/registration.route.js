/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module("registrationApp");

    registrationApp.run(function (amMoment) {

    });

    registrationApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {
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
            .state('registration', {
                url: "/registration",
                abstract: true,
                templateUrl: 'modules/registration/registration.html',
                controller: 'registrationCtrl',
                controllerAs: 'registration',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('registration.studentregistration', {
            url: "/studentregistration",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                studentData: ["$q", "registrationSvcs", function ($q, registrationSvcs) {
                    var deferred = $q.defer();
                    var reqMap = {
                        student: 'student'
                    };
                    registrationSvcs.getStudentUser(reqMap, function (res) {
                        deferred.resolve(res.data);
                    }, function (err) {
                        deferred.resolve(err);
                    });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/registration/studentregistration/studentregistration.html',
            controller: 'studentRegistrationCtrl',
            controllerAs: 'studentRegistration',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.teacherregistration', {
            url: "/teacherregistration",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                teacherData: ["$q", "registrationSvcs", function ($q, registrationSvcs) {
                    var deferred = $q.defer();
                    var reqMap = {};


                    registrationSvcs.getTeacherUser(reqMap, function (res) {
                        deferred.resolve(res.data);
                    }, function (err) {
                        deferred.resolve(err);
                    });
                    return deferred.promise;
                }]
            },
            templateUrl: 'modules/registration/teacherregistration/teacherregistration.html',
            controller: 'teacherRegistrationCtrl',
            controllerAs: 'teacherRegistration',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.courseregistration', {
            url: "/courseregistration",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            MusicType: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getMusicTypeList(reqMap, function (res) {
                                    callback(null, res.data);
                                });
                            },
                            CategoryType: function (callback) {
                                /*lookupStoreSvcs.getCategoryType().then(function (res) {
                                 callback(null, res);
                                 });*/
                                var reqMap = {};
                                registrationSvcs.getMusicCategoryList(reqMap, function (res) {
                                    callback(null, res.data);
                                });
                            },
                            Duration: function (callback) {
                                lookupStoreSvcs.getClassDuration().then(function (res) {
                                    callback(null, res);
                                });
                            },
                            CourseList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getCourseList(reqMap, function (res) {
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
            templateUrl: 'modules/registration/courseregistration/courseregistration.html',
            controller: 'courseregistrationCtrl',
            controllerAs: 'courseregistration',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.musicroom', {
            url: "/musicroom",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            MusicRoomList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getMusicRoomList(reqMap, function (res) {
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
            templateUrl: 'modules/registration/musicroom/musicroom.html',
            controller: 'musicRoomCtrl',
            controllerAs: 'musicRoom',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.batchregistration', {
            url: "/batchregistration",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            BatchList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getBatchList(reqMap, function (res) {
                                    callback(null, res.data);
                                });
                            },
                            CourseList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getCourseList(reqMap, function (res) {
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
            templateUrl: 'modules/registration/batchregistration/batchregistration.html',
            controller: 'batchRegistrationCtrl',
            controllerAs: 'batchRegistration',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.studentbatchregistration', {
            url: "/studentbatchregistration",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            BatchList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getBatchList(reqMap, function (res) {
                                    callback(null, res.data);
                                });
                            },
                            MusicType: function (callback) {
                                lookupStoreSvcs.getMusicType().then(function (res) {
                                    callback(null, res);
                                });
                            },
                            StudentBatchList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getBatchesForStudent(reqMap, function (res) {
                                    callback(null, res.data);
                                });
                            },
                            StudentList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getStudentUser(reqMap, function (res) {
                                    //console.log("route:",res.data);
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
            templateUrl: 'modules/registration/studentbatchregistration/studentbatchregistration.html',
            controller: 'studentBatchRegistrationCtrl',
            controllerAs: 'studentBatchRegistration',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.musictype', {
            url: "/musictype",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            MusicTypeList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getMusicTypeList(reqMap, function (res) {
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
            templateUrl: 'modules/registration/musictype/musictype.html',
            controller: 'musicTypeCtrl',
            controllerAs: 'musicType',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        }).state('registration.musiccategory', {
            url: "/musiccategory",
            data: {
                parentUrl: "/registration"
            },
            resolve: {
                typeData: ["$q", "registrationSvcs", "lookupStoreSvcs", function ($q, registrationSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                            MusicCategoryList: function (callback) {
                                var reqMap = {};
                                registrationSvcs.getMusicCategoryList(reqMap, function (res) {
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
            templateUrl: 'modules/registration/musiccategory/musiccategory.html',
            controller: 'musicCategoryCtrl',
            controllerAs: 'musicCategory',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        });

    }])

})(window, window.document, window.jQuery, window.angular);