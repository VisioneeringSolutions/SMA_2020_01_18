/**
 * Created by nikita on 2019-06-24.
 */


(function (window, document, $, angular) {
    var competitionForTeachersApp = angular.module("competitionForTeachersApp");
    competitionForTeachersApp.run(function (amMoment) {
    });
    competitionForTeachersApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('competitionforteachers', {
                url: "/competitionforteachers",
                data: {
                    parentUrl: "/competitionforteachers"
                },
                resolve: {
                    competitionData: ["$q", "competitionForTeachersSvcs","localStorage","notifySvcs", function ($q, competitionForTeachersSvcs,localStorage,notifySvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                               TeacherCompList: function (callback) {
                                    var reqMap  = {
                                        userPk : localStorage.get("userKey")
                                    };
                                   competitionForTeachersSvcs.getCompListForTeacher(reqMap,function (res) {
                                       if (res.data.length > 0) {

                                       } else {
                                           notifySvcs.info({
                                               content: "No Data"
                                           })

                                       }

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
                templateUrl: 'modules/competitionforteachers/competitionforteachers.html',
                controller: 'competitionForTeachersCtrl',
                controllerAs: 'competitionForTeachers',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);