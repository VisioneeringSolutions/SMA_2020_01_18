/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.factory("stabSvcs", [function () {

        return new window.sInstances.EventManager();

    }]);

    commonApp.directive("stabIndexDrct", ["$timeout", "http", "$compile", "stabSvcs", function ($timeout, http, $compile, stabSvcs) {
        return {
            restrict: 'A',
            replace: true,
            transclude: true,
            scope: {
                config: '='
            },
            template: "<ul ng-transclude></ul>",
            link: {
                pre: function (scope, element, attrs) {

                },
                post: function (scope, element, attrs) {
                    scope.config.defaultActive = (scope.config.defaultActive) ? scope.config.defaultActive : 0;
                    function setStates(isInit, i, cI) {
                        angular.forEach(scope.childrens, function (child, index) {

                            if (i != index)
                                $(child).removeClass("active");
                            else
                                $(child).addClass("active");
                            if (isInit) {
                                $(child).attr({sindex: index});
                                $(child).on("click", function (e) {
                                    scope.$apply(function () {
                                        var indx = $(e.currentTarget).attr('sindex');
                                        if (scope.config.click) {
                                            scope.config.click(indx);
                                        } else {
                                            scope.config.defaultActive = indx;
                                            setStates(undefined, indx, indx);
                                        }
                                    });
                                });
                            }
                        });
                        angular.forEach(scope.contentChildrens, function (cc, index) {
                            if (isInit) {
                                $(cc).attr({sindex: index});
                            }
                            if (cI != index)
                                $(cc).css({"display": "none"});
                            else
                                $(cc).css({"display": "block"});
                        });
                    }

                    scope.config.setActive = function (index, contentIndex) {

                        if (scope.childrens[index] && scope.contentChildrens[contentIndex]) {

                            scope.config.defaultActive = index;
                            setStates(undefined, index, contentIndex);
                        }
                    };
                    scope.childrens = [];
                    scope.contentChildrens = [];
                    if (stabSvcs.get("contentloaded")) {
                        scope.childrens = element[0].children;
                        scope.contentChildrens = angular.element(document.getElementById(scope.config.mapid))[0].children;
                        scope.config.indexces = scope.childrens;
                        scope.config.contents = scope.contentChildrens;
                        if (scope.config.init) {
                            setStates(true, scope.config.defaultActive, scope.config.defaultActive);
                            console.log("fghjkl");
                            scope.config.init();
                        } else {
                            setStates(true, scope.config.defaultActive, scope.config.defaultActive);
                        }

                    } else {
                        stabSvcs.on("contentloaded", function () {
                            scope.childrens = element[0].children;
                            scope.contentChildrens = angular.element(document.getElementById(scope.config.mapid))[0].children;
                            scope.config.indexces = scope.childrens;
                            scope.config.contents = scope.contentChildrens;
                            if (scope.config.init) {
                                setStates(true, scope.config.defaultActive, scope.config.defaultActive);
                                console.log("........");
                                scope.config.init();
                            } else {
                                setStates(true, scope.config.defaultActive, scope.config.defaultActive);
                            }

                        });
                    }
                    // unbind event listener to prevent memory leaks
                    scope.$on('$destroy', function () {
                        stabSvcs.remove("contentloaded");
                        stabSvcs.off('contentloaded');
                        // stabSvcs.off('contentloaded');
                    })
                }
            }
        };
    }]);

    commonApp.directive("stabContentDrct", ["$timeout", "http", "$compile", "stabSvcs", function ($timeout, http, $compile, stabSvcs) {
        return {
            restrict: 'A',
            replace: true,
            transclude: true,
            scope: {},
            template: "<div ng-transclude></div>",
            link: {
                pre: function (scope, element, attrs) {

                },
                post: function (scope, element, attrs) {
                    //console.log(element);

                    stabSvcs.set("contentloaded", true);

                }
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
