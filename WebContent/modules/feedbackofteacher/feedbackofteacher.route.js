/**
 * Created by nikita on 2019-05-29.
 */

/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    var feedbackOfTeacherApp = angular.module("feedbackOfTeacherApp");

    feedbackOfTeacherApp.run(function (amMoment) {

    });

    feedbackOfTeacherApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {
        }

        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }

       $stateProvider
            .state('feedbackofteacher', {
                url: "/feedbackofteacher",
                data: {
                    parentUrl: "/feedbackofteacher"
                },
                resolve: {
                    batchData: ["$q", "feedbackOfTeacherSvcs", "lookupStoreSvcs","localStorage", function ($q, feedbackOfTeacherSvcs, lookupStoreSvcs,localStorage) {
                        var deferred = $q.defer();
                        async.parallel({
                                BatchList: function (callback) {
                                    var reqMap  = {
                                        studPk : localStorage.get("userKey")
                                              };
                                    feedbackOfTeacherSvcs.getBatchListForStudent(reqMap,function (res) {
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
                templateUrl: 'modules/feedbackofteacher/feedbackofteacher.html',
                controller: 'feedbackOfTeacherCtrl',
                controllerAs: 'feedbackOfTeacher',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });

    }]);

})(window, window.document, window.jQuery, window.angular);