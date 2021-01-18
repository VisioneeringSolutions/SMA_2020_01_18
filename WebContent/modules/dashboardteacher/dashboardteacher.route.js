/**
 * Created by kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardTeacherApp = angular.module("dashBoardTeacherApp");

    dashBoardTeacherApp.run(function (amMoment) {

    });

    dashBoardTeacherApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

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
            .state('dashboardteacher', {
            url: "/dashboardteacher",
            data: {
                parentUrl: "/dashboardteacher"
            },
            resolve: {
                studentData: ["$q", "dashBoardAdminSvcs","localStorage", function ($q, dashBoardAdminSvcs,localStorage) {
                    var deferred = $q.defer();
                    var reqMap  = {
                        eoTeacherUser:localStorage.get('userKey')
                    };
                    dashBoardAdminSvcs.getStudentBatchByTecherPk(reqMap,function (res) {
                        deferred.resolve(res.data);
                    }, function (err) {
                        deferred.resolve(err);
                    });
                    return deferred.promise;
                }]
            },
            abstract: false,
            templateUrl: 'modules/dashboardteacher/dashboardteacher.html',
            controller: 'dashboardTeacherCtrl',
            controllerAs: 'dashboardTeacher',
            onEnter: ["$rootScope", "$state", onEnter],
            onExit: ["$rootScope", "$state", onExit]
        })

    }]);

})(window, window.document, window.jQuery, window.angular);