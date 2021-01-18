/**
 * Created by nikita on 2019-06-24.
 */


(function (window, document, $, angular) {
    var newsForTeachersApp = angular.module("newsForTeachersApp");
    newsForTeachersApp.run(function (amMoment) {
    });
    newsForTeachersApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('newsforteachers', {
                url: "/newsforteachers",
                data: {
                    parentUrl: "/newsforteachers"
                },
                resolve: {
                    newsData: ["$q", "newsSvcs","notifySvcs", function ($q, newsSvcs,notifySvcs) {
                        console.log("route");
                        var deferred = $q.defer();
                        async.parallel({
                                TeachersNewsList: function (callback) {
                                    var reqMap  = {

                                    };
                                    console.log("reqMap::::",reqMap);
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
                templateUrl: 'modules/newsforteachers/newsforteachers.html',
                controller: 'newsForTeachersCtrl',
                controllerAs: 'newsForTeachers',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);