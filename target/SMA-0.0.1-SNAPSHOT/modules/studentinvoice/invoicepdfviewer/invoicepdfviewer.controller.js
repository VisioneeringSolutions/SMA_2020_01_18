/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("invoicePdfViewerCtrl", ["$scope", "studentInvoiceModel", "studentInvoiceSvcs", "notifySvcs", "$state", "localStorage", "modalSvcs", "$uibModalInstance", "metadata", function ($scope, studentInvoiceModel, studentInvoiceSvcs, notifySvcs, $state, localStorage, modalSvcs,$uibModalInstance,metadata) {

        var mvm = this;

        mvm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";

        mvm.pdfData = metadata;
        console.log("mvm.pdfData::",mvm.pdfData);
        mvm.title = metadata.title;

        mvm.controller = metadata.controller;
        mvm.pdfUrl = mvm.baseImgUrl + metadata.data;
        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);