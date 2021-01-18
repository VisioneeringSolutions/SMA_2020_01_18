/**
 * Created by nikita on 2019-06-14.
 */


(function (window, document, $, angular) {
    var trialStudentsApp = angular.module("queryFormApp");
    trialStudentsApp.run(function (amMoment) {
    });
    trialStudentsApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }
        $stateProvider
            .state('trialstudents', {
                url: "/trialstudents",
                data: {
                    parentUrl: "/trialstudents"
                },
                resolve: {
                    trialStudentsData: ["$q", "queryFormSvcs","trialStudentsSvcs","registrationSvcs", function ($q, queryFormSvcs,trialStudentsSvcs,registrationSvcs) {
                        var deferred = $q.defer();

                           // var deferred = $q.defer();
                            var reqMap = {
                                student: 'student'
                            };
                        trialStudentsSvcs.getTrialStudentUser(reqMap, function (res) {
                                deferred.resolve(res.data);
                            }, function (err) {
                                deferred.resolve(err);
                            });
                            return deferred.promise;
                        }]
                    },
                templateUrl: 'modules/trialstudents/trialstudents.html',
                controller: 'trialStudentsCtrl',
                controllerAs: 'trialStudents',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);