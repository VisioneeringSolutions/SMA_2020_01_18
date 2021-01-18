/**
 * Created by nikita on 2019-06-24.
 */


(function (window, document, $, angular) {
    var competitionForStudentsApp = angular.module("competitionForStudentsApp");
    competitionForStudentsApp.run(function (amMoment) {
    });
    competitionForStudentsApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('competitionforstudents', {
                url: "/competitionforstudents",
                data: {
                    parentUrl: "/competitionforstudents"
                },
                resolve: {
                    competitionData: ["$q", "competitionForStudentsSvcs","localStorage","notifySvcs", function ($q, competitionForStudentsSvcs,localStorage,notifySvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                StudCompList: function (callback) {
                                    var reqMap  = {
                                        userPk : localStorage.get("userKey")
                                    };
                                    competitionForStudentsSvcs.getCompListForStud(reqMap,function (res) {
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
                                //console.log(results);
                                deferred.resolve(results);
                            });
                        /*console.log("routeeee form");
                         var reqMap  = {
                         };
                         queryFormSvcs.getQueryForm(reqMap,function (res) {
                         console.log("res.data:::::::",res.data);
                         deferred.resolve(res.data);
                         },
                         function (err, results) {
                         //console.log(results);
                         deferred.resolve(results);
                         });*/
                        return deferred.promise;
                    }]
                },
                templateUrl: 'modules/competitionforstudents/competitionforstudents.html',
                controller: 'competitionForStudentsCtrl',
                controllerAs: 'competitionForStudents',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);