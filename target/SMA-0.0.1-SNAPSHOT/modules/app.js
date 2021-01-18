(function (window, document, $, angular) {

    var music = angular.module('music', ['ui.router', 'ui.bootstrap', 'ngSanitize', 'ui.select', 'angularMoment', 'commonApp', 'homeApp','registrationApp',"salaryApp","batchForTeacherApp","newsApp","feedbackOfTeacherApp","studentInvoiceApp","reportApp","settingApp","dashBoardAdminApp","dashBoardStudentApp","dashBoardTeacherApp","queryFormApp","messageApp","competitionApp","competitionForStudentsApp","competitionForTeachersApp","newsForStudentsApp","newsForTeachersApp","expenseApp","trialStudentsApp"]);

    music.controller("bodyCtrl", ["$scope", "$rootScope", "$state", "localStorage", function ($scope, $rootScope, $state, localStorage) {
        if (!localStorage.get('visionKey')) {
            window.location.href = $rootScope.baseUrl + "logout";
        }
        $rootScope.$on('$stateChangeStart',
            function (event, toState, toParams, fromState, fromParams, options) {

                //console.log("$stateChangeStart", [event, toState, toParams, fromState, fromParams, options]);
            });
        $rootScope.$on('$stateNotFound',
            function (event, unfoundState, fromState, fromParams) {

                //console.log("$stateNotFound", [event, unfoundState, fromState, fromParams]);
            });
        $rootScope.$on('$stateChangeSuccess',
            function (event, toState, toParams, fromState, fromParams) {

                //console.log("$stateChangeSuccess", [event, toState, toParams, fromState, fromParams]);
            });
        $rootScope.$on('$stateChangeError',
            function (event, toState, toParams, fromState, fromParams, error) {

                //console.log("$stateChangeError", [event, toState, toParams, fromState, fromParams, error]);
            });

        $rootScope.$on('$viewContentLoading',
            function (event, viewConfig) {

                //console.log("$viewContentLoading", [event, viewConfig]);
            });

        $scope.$on('$viewContentLoaded',
            function (event) {

                //console.log("$viewContentLoaded", [event]);
            });



    }])

})(window, window.document, window.jQuery, window.angular);
