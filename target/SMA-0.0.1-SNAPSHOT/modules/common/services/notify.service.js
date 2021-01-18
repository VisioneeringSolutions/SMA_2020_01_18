/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("notifySvcs", [function () {

        return new (function () {
            PNotify.prototype.options.styling = "bootstrap3";
            this.open = function (settings) {
                new PNotify({
                    title: settings.title || 'Notification',
                    text: settings.content || '',
                    delay: settings.delay || 800,
                    type: settings.type || 'info',
                    animate: {
                        animate: true,
                        in_class: 'slideInDown',
                        out_class: 'slideOutUp'
                    }
                });
            };
            this.success = function (map) {
                map.type = "success";
                map.title = map.title || "Success";
                this.open(map);
            }
            this.info = function (map) {
                map.type = "info";
                map.title = map.title || "Info";
                this.open(map);
            }
            this.error = function (map) {
                map.type = "error";
                map.title = map.title || "Error";
                this.open(map);
            }
        })()

    }]);

})(window, window.document, window.jQuery, window.angular);