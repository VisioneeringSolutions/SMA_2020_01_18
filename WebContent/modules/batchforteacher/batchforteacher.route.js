/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    var batchForTeacherApp = angular.module("batchForTeacherApp");

    batchForTeacherApp.run(function (amMoment) {

    });

    batchForTeacherApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('batchforteacher', {
                url: "/batchforteacher",

                data: {
                    parentUrl: "/batchforteacher"
                },
                resolve: {
                    typeData: ["$q", "batchForTeacherSvcs", "lookupStoreSvcs", function ($q, batchForTeacherSvcs, lookupStoreSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                              /*  HomeWorkList: function (callback) {
                                    var reqMap  = {

                                    };
                                    batchForTeacherSvcs.getHomeWrkList(reqMap,function (res) {
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

                abstract: false,
                templateUrl: 'modules/batchforteacher/batchforteacher.html',
                controller: 'batchForTeacherCtrl',
                controllerAs: 'batchForTeacher',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            })

    }]);

})(window, window.document, window.jQuery, window.angular);
