/**
 * Created by dell on 17-06-19.
 */

(function (window, document, $, angular) {

    var messageApp = angular.module("messageApp");

    messageApp.run(["$rootScope", "amMoment", function ($rootScope, amMoment) {
        /*$rootScope.baseAPIUrl = window.sInstances.origin + "/SMA/rest/";
        $rootScope.baseUrl = window.sInstances.origin ;*/
    }]);

    messageApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('message', {
                url: "/message",
                resolve: {
                    messageData: ["$q", "messageSvcs","localStorage", function ($q, messageSvcs,localStorage) {
                        var deferred = $q.defer();
                        async.parallel({
                                AvailableContactList: function (callback) {
                                    var reqMap  = {
                                        userKey : localStorage.get("userKey")+"",
                                        role: localStorage.get("role")
                                    };
                                    messageSvcs.getAvailableContactList(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                },
                                ContactList: function(callback){
                                    var reqMap  = {
                                        userKey : localStorage.get("userKey"),
                                        role: localStorage.get("role")
                                    };
                                    messageSvcs.getContactListByUserKey(reqMap,function (res) {
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
                templateUrl: 'modules/message/message.html',
                controller: 'messageCtrl',
                controllerAs: 'message',
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