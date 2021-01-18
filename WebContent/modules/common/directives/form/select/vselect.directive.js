/**
 * Created by my on 2016-08-14.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("vselect", ["$timeout", "http", "vformSvcs", "$compile", function ($timeout, http, vformSvcs, $compile) {

        return {
            restrict: "E",
            replace: true,
            scope: {
                formConfig: '=?',
                preCompile: '=?',
                postCompile: '=?'
            },
            transclude: true,
            template: '<select ng-transclude ></select>',
            compile: function (element, attrs) {
                return {
                    pre: function (scope, element, attrs) {

                        if (scope.preCompile && typeof(scope.preCompile) == "function") {
                            scope.preCompile({
                                scope: scope,
                                element: element,
                                attrs: attrs
                            });
                        }

                        if (scope.formConfig) {
                            if (scope.formConfig.vselect == undefined) {
                                scope.formConfig.vselect = {};
                            }
                            scope.formConfig.vselect[attrs['name']] = {
                                scope: scope,
                                element: element,
                                attrs: attrs,
                                valid: function () {
                                    if (scope.formConfig.formScope[scope.formConfig.name][attrs['name']].$valid && scope.formConfig.formScope[scope.formConfig.name][attrs['name']].$viewValue != '') {
                                        element[0].classList.remove("vselect-error");
                                        return true;
                                    }
                                    else {
                                        element[0].classList.add("vselect-error");
                                        return false;
                                    }
                                }
                            }
                        }
                    },
                    post: function (scope, element, attrs) {

                        if (scope.postCompile && typeof(scope.postCompile) == "function") {
                            scope.postCompile({
                                scope: scope,
                                element: element,
                                attrs: attrs
                            });
                        }

                        scope.$on("$destroy", function (e) {

                            if (scope.formConfig) {
                                if (scope.formConfig.vselect[attrs['name']]) {
                                    delete scope.formConfig.vselect[attrs['name']];
                                }
                            }
                        });

                    }
                }
            }

        };
    }]);
})(window, window.document, window.jQuery, window.angular);