/**
 * Created by nikita on 2019-06-17.
 */

(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("teacherDetailsViewModelCtrl", ["$scope", "$rootScope","reportSvcs", "reportModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, reportSvcs, reportModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        console.log("INNNN");
        var mvm = this;

        mvm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";

        mvm.formModel = angular.copy(metadata.lkInstances.TeacherViewDetails);
        console.log("mvm.formModel:::",mvm.formModel);

        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);