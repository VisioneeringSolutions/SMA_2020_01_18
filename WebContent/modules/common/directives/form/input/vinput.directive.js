/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("validatePattern", ["$timeout", "vformSvcs", "$compile", function ($timeout, vformSvcs, $compile) {
        var isUndefined = function (val) {
            return val == undefined;
        };

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attr, ctrl) {
                if (!ctrl) return;
                ctrl.$validators.pattern = function (modelValue, viewValue) {
                    var inputCon = vformSvcs.inputPattern[attr['vtype']];
                    return ctrl.$isEmpty(viewValue) || isUndefined(inputCon.regx) || inputCon.regx.test(viewValue);
                };
            }
        };
    }]);

    commonApp.directive("charminlength", ["$timeout", "http", "vformSvcs", "$compile", function ($timeout, http, vformSvcs, $compile) {

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attr, ctrl) {
                if (!ctrl) return;

                var minlength = 0;
                attr.$observe('charminlength', function (value) {
                    minlength = parseInt(value) || 0;
                    ctrl.$validate();
                });
                ctrl.$validators.charminlength = function (modelValue, viewValue) {
                    return ctrl.$isEmpty(viewValue) || (viewValue + "").length >= minlength;
                };
            }
        }
    }]);

    commonApp.directive("customcon", ["$timeout", "http", "vformSvcs", "$compile", "$parse", function ($timeout, http, vformSvcs, $compile, $parse) {

        return {
            restrict: 'A',
            require: '?ngModel',
            compile: function ($element, attr) {
                // NOTE:
                // We expose the powerful `$event` object on the scope that provides access to the Window,
                // etc. This is OK, because expressions are not sandboxed any more (and the expression
                // sandbox was never meant to be a security feature anyway).

                var fn = $parse(attr['customcon']), isValid = true;
                return {
                    pre: function (scope, element, attr, ctrl) {
                        if (!ctrl) return;
                        element.on('keyup', function (event) {
                            var callback = function () {
                                fn(scope, {$event: event}).then(function (valid) {
                                    isValid = valid;
                                    ctrl.$validate();
                                });
                            };
                            scope.$apply(callback);
                        });
                        attr.$observe('customcon', function (value) {
                            ctrl.$validate();
                        });
                        ctrl.$validators.customcon = function (modelValue, viewValue) {
                            if (isValid) {
                                element[0].classList.remove("vinput-error");
                            } else {
                                element[0].classList.add("vinput-error");
                            }

                            return isValid;
                        };
                    }
                };
            }
        }
    }]);

    commonApp.directive("charmaxlength", ["$timeout", "http", "vformSvcs", "$compile", "$parse", function ($timeout, http, $parse, vformSvcs, $compile) {

        return {
            restrict: 'A',
            require: '?ngModel',
            link: function (scope, elm, attr, ctrl) {
                if (!ctrl) return;

                var maxlength = -1;
                attr.$observe('charmaxlength', function (value) {
                    var intVal = parseInt(value);
                    maxlength = isNaN(intVal) ? -1 : intVal;
                    ctrl.$validate();
                });
                ctrl.$validators.charmaxlength = function (modelValue, viewValue) {
                    return ctrl.$isEmpty(viewValue) || (viewValue + "").length <= maxlength;
                };
            }
        }
    }]);

    commonApp.directive('myValidator', ["$timeout", "http", "$parse", "vformSvcs", "$compile", function ($timeout, http, $parse, vformSvcs, $compile) {
        return {
            scope: {
                validValues: '=validValues',
                validValues1: '=validValues1'
            },
            link: function (scope, elm, attrs) {
                elm.bind('keypress', function (e) {
                    var char = String.fromCharCode(e.which || e.charCode || e.keyCode), matches = [];
                    angular.forEach(scope.validValues, function (value, key) {
                        if (char === value) matches.push(char);
                    }, matches);
                    angular.forEach(scope.validValues1, function (value, key) {
                        if (char === value) matches.push(char);
                    }, matches);
                    if (matches.length == 0) {
                        e.preventDefault();
                        return false;
                    }
                    if (e.keyCode === 46 && this.value.split('.').length === 2) {
                        return false;
                    }
                });
            }
        }
    }]);

    commonApp.directive('positiveNumbers', function () {
        return function (scope, element, attrs) {
            var keyCode = [8, 9, 17, 37, 39, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 110, 190];
            element.bind("keydown", function (event) {
                if ($.inArray(event.which, keyCode) == -1) {
                    scope.$apply(function () {
                        scope.$eval(attrs.positiveNumbers);
                        //   console.log("positive"+event)
                        event.preventDefault();
                    });
                    event.preventDefault();
                }
                if (event.keyCode === 46 && this.value.split('.').length === 2) {
                    // console.log("event.keyCode...",event.keyCode);
                    return false;
                }
            });
        };
    });

    commonApp.directive("maxLimit", [function () {
        return {
            restrict: "A",
            link: function (scope, elem, attrs) {
                var limit = parseInt(attrs.maxLimit);
                angular.element(elem).on("keypress", function (e) {
                    if (this.value.length == limit) e.preventDefault();
                });
            }
        }
    }]);

    commonApp.directive('onlyAlphabets', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^A-Za-z ]/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });


    commonApp.directive('onlyAlphabetNumeric', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^A-Za-z0-9]/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });

    commonApp.directive('onlyAlphabetNumericSpace', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^A-Za-z0-9_ ]/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });

    commonApp.directive('checkPanCard', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[A-Za-z]{5}\d{4}[A-Za-z]{1}/g);
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });

    commonApp.directive('onlyNumeric', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^0-9]/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });
// notworkinf
    commonApp.directive('onlyPercentage', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/[^0-9.]/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });

    commonApp.directive('checkPercentage', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/(?=\s+|^)(?:-1|100(?:\.0+)?|\d{1,2}(?:\.\d{1,})?)(?=\s+)/gm, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });
    commonApp.directive('capitalize', ["$parse", function ($parse) {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                var capitalize = function (inputValue) {
                    if (inputValue === undefined) {
                        inputValue = '';
                    }
                    if (inputValue != null) {
                        var capitalized = inputValue.charAt(0).toUpperCase() +
                            inputValue.substring(1);
                        if (capitalized !== inputValue) {
                            modelCtrl.$setViewValue(capitalized);
                            modelCtrl.$render();
                        }
                    }


                    return capitalized;
                };
                modelCtrl.$parsers.push(capitalize);
                capitalize($parse(attrs.ngModel)(scope)); // capitalize initial value
            }
        };
    }]);

    commonApp.directive('upperCaseCapital', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                var capitalize = function (inputValue) {
                    if (inputValue == undefined) inputValue = '';
                    var capitalized = inputValue.toUpperCase();
                    if (capitalized !== inputValue) {
                        modelCtrl.$setViewValue(capitalized);
                        modelCtrl.$render();
                    }
                    return capitalized;
                };
                modelCtrl.$parsers.push(capitalize);
                capitalize(scope[attrs.ngModel]); // capitalize initial value
            }
        };
    });

    commonApp.directive('onlyNumbers', function () {
        return function (scope, element, attrs) {
            var keyCode = [8, 9, 17, 37, 39, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 110];
            element.bind("keydown", function (event) {
                if ($.inArray(event.which, keyCode) == -1) {
                    scope.$apply(function () {
                        scope.$eval(attrs.onlyNumbers);
                        event.preventDefault();
                    });
                    event.preventDefault();
                }

            });
        };
    });

    commonApp.directive("preventTypingGreater", function () {
        return {
            link: function (scope, element, attributes) {
                var oldVal = null;
                element.on("keydown keyup", function (e) {
                    if (Number(element.val()) > Number(attributes.max) &&
                        e.keyCode != 46 // delete
                        &&
                        e.keyCode != 8 // backspace
                    ) {
                        e.preventDefault();
                        element.val(oldVal);
                    } else {
                        oldVal = Number(element.val());
                    }
                });
            }
        };
    });

    commonApp.directive('decimalNumber', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModelCtrl) {
                function fromUser(text) {
                    var transformedInput = text.replace(/^\d*(\.\d{0,2})?$/g, '');
                    //console.log(transformedInput);
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }

                ngModelCtrl.$parsers.push(fromUser);
            }
        };
    });

    commonApp.directive("vinput", ["$timeout", "http", "vformSvcs", "$compile", function ($timeout, http, vformSvcs, $compile) {
        return {
            restrict: "E",
            replace: true,
            scope: {
                formConfig: '=?',
                preCompile: '=?',
                postCompile: '=?'
            },
            template: '<input />',
            compile: function (element, attrs) {
                return {
                    pre: function (scope, element, attrs) {
                        scope.inputCon = vformSvcs.inputPattern[attrs['vtype']];
                        if (attrs['vtype']) {
                            scope.inputCon = vformSvcs.inputPattern[attrs['vtype']];
                            if (scope.inputCon) {
                                element.attr({
                                    type: scope.inputCon.type,
                                    'pattern': scope.inputCon.pattern,
                                    title: (scope.inputCon.title && !attrs['title']) ? scope.inputCon.title : ""
                                });
                            } else {
                                element.attr({type: attrs['vtype']});
                            }
                        }

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
                                inputCon: scope.inputCon,

                                valid: function () {
                                    if (scope.formConfig.formScope[scope.formConfig.name][attrs['name']].$valid) {
                                        element[0].classList.remove("vinput-error");
                                        if (attrs['errorMsgElm']) {
                                            document.getElementById(attrs['errorMsgElm']).style.display = "none";
                                        }
                                        return true;
                                    }
                                    else {
                                        element[0].classList.add("vinput-error");
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
                        //$compile(element[0])(scope.$parent.$parent.$parent);
                    }
                }
            }

        };
    }]);



})(window, window.document, window.jQuery, window.angular);