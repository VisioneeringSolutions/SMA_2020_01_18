/**
 * Created by nikita on 2019-06-24.
 */


(function (window, document, $, angular) {
    var competitionApp = angular.module("competitionApp");
    competitionApp.run(function (amMoment) {
    });
    competitionApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('competition', {
                url: "/competition",
                data: {
                    parentUrl: "/competition"
                },
                resolve: {
                    competitionData: ["$q", "competitionSvcs", function ($q, competitionSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                               CompetitionList: function (callback) {
                                    var reqMap  = {
                                    };
                                   competitionSvcs.getCompetition(reqMap,function (res) {
                                        console.log("res.data:::::::", res.data);
                                        callback(null, res.data);
                                    });
                                }
                               /* PhoneNumberList: function (callback) {
                                    var reqMap  = {

                                    };
                                    queryFormSvcs.getPhoneNumberList(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                }*/
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
                templateUrl: 'modules/competition/competition.html',
                controller: 'competitionCtrl',
                controllerAs: 'competition',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);