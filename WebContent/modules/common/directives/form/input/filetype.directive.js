/**
 * Created by my on 2016-08-14.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("filetypeDrct", ["$timeout", "http", "$compile", function ($timeout, http, $compile) {

        return {
            restrict: "A",
            multiElement: true,
            link: function (scope, elm, attr) {

                scope.$fileChangeConfig = {
                    onError: undefined,
                    onLoad: undefined,
                    onProgress: undefined,
                    onAbort: undefined,
                    onLoadStart: undefined,
                    onLoadEnd: undefined,
                    change: undefined
                };
                //console.log("filetypeDrct Controller",scope);
                elm.on("change", function (e) {
                    if (scope.$fileChangeConfig.change && typeof(scope.$fileChangeConfig.change) == "function")
                        scope.$fileChangeConfig.change(e, scope, elm, attr);
                    //console.log("file type change event ", e);
                    angular.forEach(e.target.files, function (data, index) {
                        // init variables
                        var $reader = new FileReader(), result;

                        // set fallback image on error
                        $reader.onloaderror = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onError && typeof(scope.$fileChangeConfig.onError) == "function")
                                scope.$fileChangeConfig.onError(e, scope, elm, attr, index);
                        };

                        // set resulting image
                        $reader.onload = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onLoad && typeof(scope.$fileChangeConfig.onLoad) == "function")
                                scope.$fileChangeConfig.onLoad(e, scope, elm, attr, index);
                        };

                        $reader.onprogress = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onProgress && typeof(scope.$fileChangeConfig.onProgress) == "function")
                                scope.$fileChangeConfig.onProgress(e, scope, elm, attr, index);
                        };
                        $reader.onabort = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onAbort && typeof(scope.$fileChangeConfig.onAbort) == "function")
                                scope.$fileChangeConfig.onAbort(e, scope, elm, attr, index);
                        };
                        $reader.onloadstart = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onLoadStart && typeof(scope.$fileChangeConfig.onLoadStart) == "function")
                                scope.$fileChangeConfig.onLoadStart(e, scope, elm, attr, index);
                        };

                        $reader.onloadend = function (e) {
                            e.vfile = data;
                            e.vindex = index;
                            if (scope.$fileChangeConfig.onLoadEnd && typeof(scope.$fileChangeConfig.onLoadEnd) == "function")
                                scope.$fileChangeConfig.onLoadEnd(e, scope, elm, attr, index);
                        };
                        $reader.readAsDataURL(data);
                    });
                });
                scope.$watch(attr.filetypeDrct, function (value) {
                    scope.$fileChangeConfig = value;
                });
                scope.$on("$destroy", function () {
                    elm.off("change");
                });
            }

        };
    }]);
})(window, window.document, window.jQuery, window.angular);