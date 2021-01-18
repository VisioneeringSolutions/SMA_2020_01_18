/**
 * Created by nikita on 2019-06-17.
 */

(function (window, document, $, angular) {

    var competitionForTeachersApp = angular.module('competitionForTeachersApp');

    competitionForTeachersApp.controller("compTeacherViewModalCtrl", ["$scope", "$rootScope","competitionForTeachersSvcs", "competitionForTeachersModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, competitionForTeachersSvcs, competitionForTeachersModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        var mvm = this;

        mvm.formModel = angular.copy(metadata.lkInstances.CompetitionDetails);
        console.log("mvm.formModel:::",mvm.formModel);
        mvm.getDateFormat = function(date) {
            var newdate = date.split("-").reverse().join("-");

            return newdate;
        };
        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);