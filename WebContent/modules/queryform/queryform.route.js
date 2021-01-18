/**
 * Created by nikita on 2019-06-14.
 */


(function (window, document, $, angular) {
    var queryFormApp = angular.module("queryFormApp");
    queryFormApp.run(function (amMoment) {
    });
    queryFormApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('queryform', {
                url: "/queryform",
                data: {
                    parentUrl: "/queryform"
                },
                resolve: {
                    queryFormData: ["$q", "queryFormSvcs","registrationSvcs", function ($q, queryFormSvcs,registrationSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                QueryFormList: function (callback) {
                                    var reqMap  = {
                                    };
                                    queryFormSvcs.getQueryForm(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                },
                                PhoneNumberList: function (callback) {
                                    var reqMap  = {

                                    };
                                    queryFormSvcs.getPhoneNumberList(reqMap,function (res) {
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
                templateUrl: 'modules/queryform/queryform.html',
                controller: 'queryFormCtrl',
                controllerAs: 'queryForm',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);