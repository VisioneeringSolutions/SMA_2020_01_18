/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("modalSvcs", ["http", "storeSvcs", "$uibModal", function (http, storeSvcs, $uibModal) {

        var open = function (metadata) {
            var defaultMetadata = {
                controller: metadata.controller,
                controllerAs: metadata.controllerAs || "modalController",
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    metadata: function () {
                        return metadata;
                    }
                }
            };

            var metadataAttrs = ["animation", "appendTo", "backdrop", "backdropClass", "bindToController", "controllerAs", "controller", "keyboard", "openedClass", "scope", "size", "template", "templateUrl", "windowClass", "windowTemplateUrl", "windowTopClass"]

            angular.forEach(metadata, function (val, key) {

                if (metadataAttrs[key] != -1) {
                    defaultMetadata[key] = val;
                }

            });

            try {
                return $uibModal.open(defaultMetadata);
                // mdl.result.then(function(res){
                //  defaultMetadata.resultClose(res);
                // },function(err){
                //  defaultMetadata.resultDismiss(err);
                // });
                // mdl.opened.then(function(){
                //  defaultMetadata.opened();
                // });
                // mdl.closed.then(function () {
                //  defaultMetadata.opened();
                // });
                // mdl.rendered.then(function () {
                //  defaultMetadata.opened();
                // });
            } catch (err) {
                return false;
            }
        };

        return {
            open: open
        }

    }]);

})(window, window.document, window.jQuery, window.angular);