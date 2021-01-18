/**
 * Created by nikita on 2019-06-17.
 */

(function (window, document, $, angular) {

    var competitionForStudentsApp = angular.module('competitionForStudentsApp');

    competitionForStudentsApp.controller("compForStudViewModalCtrl", ["$scope", "$rootScope","competitionForStudentsSvcs", "competitionForStudentsModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, competitionForStudentsSvcs, competitionForStudentsModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        var mvm = this;

        mvm.formModel = angular.copy(metadata.lkInstances.CompetitionDetails);
        //mvm.formModel = {};
        console.log("mvm.formModel:::",mvm.formModel);
        mvm.getDateFormat = function(date) {
            var newdate = date.split("-").reverse().join("-");

            return newdate;
        };

        //console.log("mvm.formModel.competition_date",mvm.competitionValue) ;

        /*mvm.showData = false;
        mvm.defaultFunction = function(){
            mvm.formModel.competitionDate =  mvm.competitionValue.competition_date;
            mvm.formModel.details =  mvm.competitionValue.details;
            mvm.formModel.name =  mvm.competitionValue.name;
            mvm.formModel.brochureUrl =  mvm.competitionValue.brochure_url;
            mvm.formModel.organizingAuthority =  mvm.competitionValue.organizing_authority;
            mvm.formModel.prerequisite =  mvm.competitionValue.prerequisite;
            mvm.formModel.venue =  mvm.competitionValue.venue;

            mvm.showData = true;

            console.log("formModel::",mvm.formModel);
        }();*/

        //for (var key in mvm.competitionValue){


        //}


        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);