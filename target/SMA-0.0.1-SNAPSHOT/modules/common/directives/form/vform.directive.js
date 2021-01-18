/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("vform", ["$timeout", "http", "vformSvcs", "$compile", function ($timeout, http, vformSvcs, $compile) {
        return {
            restrict: "E",
            replace: true,
            transclude: true,
            scope: {
                config: "=?",
                preCompile: "=?",
                postCompile: "=?"
            },
            template: '<form ng-transclude ></form>',
            compile: function (element, attrs) {
                return {
                    pre: function (scope, element, attrs) {
                        function validateFormInputs() {
                            var validationResponse = {
                                form: true,
                                validInputs: [],
                                invalidInputs: []
                            };
                            for (var inputName in scope.config['vinputs']) {
                                if (!scope.config['vinputs'][inputName].valid()) {
                                    validationResponse.invalidInputs.push(inputName);
                                } else {
                                    validationResponse.validInputs.push(inputName);
                                }
                            }
                            if (scope.config['vselect']) {
                                for (var selectName in scope.config['vselect']) {
                                    if (!scope.config['vselect'][selectName].valid()) {
                                        validationResponse.invalidInputs.push(selectName);
                                    } else {
                                        validationResponse.validInputs.push(selectName);
                                    }
                                }
                            }

                            if (validationResponse.invalidInputs.length > 0) {
                                scope.config.formScope[scope.config.name].$valid = false;
                                scope.config.formScope[scope.config.name].$invalid = true;
                                validationResponse.form = false;
                            }

                            return validationResponse;
                        }

                        element.on("submit", function (e) {
                            e.preventDefault();
                            // scope.$apply(function(){
                            if (scope.config.submit && typeof(scope.config.submit) == "function")
                                scope.config.submit(e);
                            // });
                        });


                        // scope.onSubmit = function(e){
                        //     alert("yahoo2");
                        //     e.preventDefault();
                        //     if(scope.config.submit && typeof(scope.config.submit)=="function")
                        //         scope.config.submit(e,scope[attrs['name']]);
                        // };

                        if (scope.config) {
                            scope.config.name = attrs['name'];
                            scope.config.validateFormInputs = validateFormInputs;
                            if (scope.config.preCompile && typeof(scope.config.preCompile) == "function") {
                                scope.config.preCompile({scope: scope, element: element, attrs: attrs});
                            }
                        }

                    },
                    post: function (scope, element, attrs) {

                        if (scope.config) {
                            if (scope.config.postCompile && typeof(scope.config.postCompile) == "function") {
                                scope.config.postCompile({scope: scope, element: element, attrs: attrs});
                            }
                        }
                    }
                }
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);