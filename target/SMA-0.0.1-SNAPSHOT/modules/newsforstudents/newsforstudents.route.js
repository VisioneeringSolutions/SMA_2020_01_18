/**
 * Created by nikita on 2019-06-24.
 */


(function (window, document, $, angular) {
    var newsForStudentsApp = angular.module("newsForStudentsApp");
    newsForStudentsApp.run(function (amMoment) {
    });
    newsForStudentsApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('newsforstudents', {
                url: "/newsforstudents",
                data: {
                    parentUrl: "/newsforstudents"
                },
                resolve: {
                    newsData: ["$q", "newsSvcs","notifySvcs", function ($q, newsSvcs,notifySvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                StudNewsList: function (callback) {
                                    var reqMap  = {

                                    };
                                    newsSvcs.getNewsList(reqMap,function (res) {
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
                templateUrl: 'modules/newsforstudents/newsforstudents.html',
                controller: 'newsForStudentsCtrl',
                controllerAs: 'newsForStudents',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);