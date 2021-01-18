/**
 * Created by nikitaaa on 2019-05-24.
 */


(function (window, document, $, angular) {

    var batchForTeacherApp = angular.module('batchForTeacherApp');

    batchForTeacherApp.controller("fileViewCtrl", ["$scope", "$uibModalInstance", "metadata", "batchForTeacherModel", "batchForTeacherSvcs", "lookupStoreSvcs", "$state", "$q", "notifySvcs", "$compile", "cacheSvcs", function ($scope, $uibModalInstance, metadata, batchForTeacherModel, batchForTeacherSvcs, lookupStoreSvcs, $state, $q, notifySvcs, $compile, cacheSvcs) {

        console.log(metadata);

        var mvmv = this;
        mvmv.fileInstances = metadata.fileInstances;
        mvmv.modalInstance = $uibModalInstance;

        mvmv.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        mvmv.metadata = metadata;


        mvmv.view = function (name, file) {
            if (file.primaryKey) {
                window.open(mvmv.baseImgUrl + file.name, '_blank', "width=500,height=500");
            } else {
                if (file.result.indexOf('data:image') > -1) {
                    var imageWindow = window.open("", '_blank', "width=500,height=500");
                    imageWindow.document.write("<img src='" + file.result + "'>");
                } else if (file.result.indexOf('data:application/pdf') > -1) {
                    var pdfWindow = window.open("", '_blank', "width=500,height=500");
                    pdfWindow.document.write("<embed src='" + file.result + "' width='100%' height='120%'>");
                } else {
                    window.open(file.result, '_blank', "width=500,height=500");
                }
            }
        };

        mvmv.download = function (name) {
            console.log(mvmv.fileInstances[name]);
        };
        mvmv.delete = function (key, filename, index, primaryKey) {
            console.log("PK", primaryKey);
            if(primaryKey == undefined){
                metadata.events.delete(key, filename, index);
            } else{
                if (confirm("Are you sure you want to delete " + filename + " ?")) {
                    metadata.events.delete(key, filename, index);
                    var reqMap = {
                        className: "EOImage",
                        primaryKey: primaryKey + ""
                    };
                    batchForTeacherSvcs.deleteObject(reqMap, function (res) {
                        if (res.data == 'success') {
                            notifySvcs.info({
                                content: "Image Deleted Successfully"
                            });
                        }

                    });

                }
            }

        };


    }]);
})(window, window.document, window.jQuery, window.angular);
