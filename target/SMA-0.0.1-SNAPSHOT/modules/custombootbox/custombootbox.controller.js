/**
 * Created by Ankit Sharma on 14-Jun-19.
 */
(function (window, document, $, angular) {

    var homeApp = angular.module('homeApp');

    homeApp.controller("customBootBoxCtrl", ["$scope", "$uibModalInstance", "metadata", "homeModel", "homeSvcs", "$state", "$q", "notifySvcs", "$compile", "cacheSvcs", function ($scope, $uibModalInstance, metadata, homeModel, homeSvcs, $state, $q, notifySvcs, $compile, cacheSvcs) {
        var mvm = this;
        mvm.modalInstance = $uibModalInstance;
        mvm.metadata = metadata.data;
        mvm.title = metadata.title;
        mvm.type = metadata.type;
        if(metadata.data != undefined){
            if(metadata.data.isTeacherPaid != null ){
                mvm.isTeacherPaid = metadata.data.isTeacherPaid;
            }
        }
        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'yyyy-MM-dd';
                    this.popup = popup || {
                            opened: false
                        };
                    this.open = open || function () {
                            this.popup.opened = true;
                        };
                    this.dateOptions = dateOptions || {
                            formatYear: 'yyyy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        mvm.createDateMap = function () {
            return dateMaps.getMap();
        };

        /* --------------end-------------------- */

        if(mvm.type == 'General' || mvm.type == 'Teacher' || mvm.type == 'Admin'){
            mvm.cancellationPercentage = metadata.data.cancellationPercentage;
        }
        if(mvm.type == 'Student'){
            mvm.cancellationReqDate = metadata.data.cancellationRequestDate;
        }

        mvm.setCancellationAmount = function(){
            if(parseFloat(mvm.cancellationPercentage) >100 || mvm.cancellationPercentage == undefined){
                mvm.cancellationPercentage = 0;
                mvm.cancellationAmount = 0;
            }else{
                mvm.cancellationAmount = Math.round(parseFloat(mvm.metadata.newFees) * ( (parseFloat(mvm.cancellationPercentage)) / 100));
            }
        };
        mvm.success = function () {
            metadata.events.success();
            $uibModalInstance.close();
        };
        mvm.cancelInvoice = function () {
            metadata.events.cancelInvoice();
            $uibModalInstance.close();
        };
        mvm.saveTeacher = function () {
            metadata.events.saveTeacher(mvm.metadata,'Cancelled');
            $uibModalInstance.close();
      };
        mvm.saveAdmin = function () {
            metadata.events.saveAdmin(mvm.metadata,'Cancelled');
            $uibModalInstance.close();
        };
        mvm.save = function () {
            if(mvm.cancellationPercentage == undefined){
                mvm.cancellationPercentage = 0;
                mvm.cancellationAmount = 0;
            }
            metadata.events.save(mvm.cancellationAmount,'Cancelled',mvm.isTeacherPaid, mvm.cancellationPercentage);
            $uibModalInstance.close();
        };
        mvm.saveDate = function () {

            metadata.events.studentCancellationReq(mvm.cancellationDate);
            $uibModalInstance.close();
        };

        mvm.cancel = function(){
            $uibModalInstance.close();
        }
    }
    ]);
})(window, window.document, window.jQuery, window.angular);