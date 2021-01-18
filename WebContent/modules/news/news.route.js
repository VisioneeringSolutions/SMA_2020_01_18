/**
 * Created by nikitaa on 2019-05-25.
 */
/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    var newsApp = angular.module("newsApp");

    newsApp.run(function (amMoment) {

    });

    newsApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
        .state('news', {
            url: "/news",
            data: {
                parentUrl: "/news"
            },
            resolve: {
                newsData: ["$q", "newsSvcs", "lookupStoreSvcs", function ($q, newsSvcs, lookupStoreSvcs) {
                    var deferred = $q.defer();
                    async.parallel({
                          NewsList: function (callback) {
                                var reqMap  = {

                                };
                                newsSvcs.getNewsList(reqMap,function (res) {
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
            templateUrl: 'modules/news/news.html',
            controller: 'newsCtrl',
            controllerAs: 'news',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        });


            /*.state('news', {
                url: "/news",
                abstract: false,
                templateUrl: 'modules/news/news.html',
                controller: 'newsCtrl',
                controllerAs: 'news',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            })*/

    }]);

})(window, window.document, window.jQuery, window.angular);