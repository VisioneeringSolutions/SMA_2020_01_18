/**
 * Created by my on 2016-08-14.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("vtextarea", ["$timeout", "http", "vformSvcs", "$compile", function ($timeout, http, vformSvcs, $compile) {

        return {
            restrict: "E",
            replace: true,
            scope: {
                formConfig: '=?',
                preCompile: '=?',
                postCompile: '=?'
            },
            template: '<textarea />',
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
                            if (scope.formConfig.vinputs == undefined) {
                                scope.formConfig.vinputs = {};
                            }
                            scope.formConfig.vinputs[attrs['name']] = {
                                scope: scope,
                                element: element,
                                attrs: attrs,
                                valid: function () {
                                    if (scope.formConfig.formScope[scope.formConfig.name][attrs['name']].$valid) {
                                        element[0].classList.remove("vtextarea-error");
                                        if (attrs['errorMsgElm']) {
                                            document.getElementById(attrs['errorMsgElm']).style.display = "none";
                                        }
                                        return true;
                                    }
                                    else {
                                        element[0].classList.add("vtextarea-error");
                                        if (attrs['errorMsgElm']) {
                                            document.getElementById(attrs['errorMsgElm']).setAttribute("title", attrs['title']);
                                            document.getElementById(attrs['errorMsgElm']).style.display = "block";
                                        }
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
                                if (scope.formConfig.vinputs[attrs['name']]) {
                                    delete scope.formConfig.vinputs[attrs['name']];
                                }
                            }
                        });
                    }
                }
            }

        };
    }]);
})(window, window.document, window.jQuery, window.angular);