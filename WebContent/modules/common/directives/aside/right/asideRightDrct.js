/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("asideRightDrct", ["$timeout", function ($timeout) {
        return {
            replace: true,
            scope: {},
            templateUrl: 'modules/common/directives/aside/right/asideRightDrct.html',
            link: function (scope, element, attrs) {
                var neg = $('.main-header').outerHeight() + $('.main-footer').outerHeight();
                var window_height = $(window).height();
                var sidebar_height = $(".sidebar").height();
                //Set the min-height of the content and sidebar based on the
                //the height of the document.
                if ($("body").hasClass("fixed")) {
                    $(".content-wrapper, .right-side").css('min-height', window_height - $('.main-footer').outerHeight());
                } else {
                    var postSetWidth;
                    if (window_height >= sidebar_height) {
                        $(".content-wrapper, .right-side").css('min-height', window_height - neg);
                        postSetWidth = window_height - neg;
                    } else {
                        $(".content-wrapper, .right-side").css('min-height', sidebar_height);
                        postSetWidth = sidebar_height;
                    }

                    //Fix for the control sidebar height
                    /*var controlSidebar = $($.AdminLTE.options.controlSidebarOptions.selector);
                    if (typeof controlSidebar !== "undefined") {
                        if (controlSidebar.height() > postSetWidth)
                            $(".content-wrapper, .right-side").css('min-height', controlSidebar.height());
                    }*/

                }
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
