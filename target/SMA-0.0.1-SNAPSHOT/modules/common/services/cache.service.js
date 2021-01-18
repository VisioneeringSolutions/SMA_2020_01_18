(function (window, document, $, angular) {

    angular.module('commonApp').factory("cacheSvcs", [function () {

        return new (function () {
            var cache = {
                ext: {
                    ".png": "image/png",
                    ".jpeg": "image/jpeg",
                    ".jpe": "image/jpeg",
                    ".jpg": "image/jpeg",
                    ".pdf": "application/pdf",
                    ".csv": "text/csv",
                    ".txt": "text/plain",
                    ".doc": "application/msword",
                    ".docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    ".xls": "application/vnd.ms-excel",
                    ".xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
            };
            this.get = function (key) {
                return cache[key];
            };
            this.set = function (key, val) {
                cache[key] = val;
            };
            this.remove = function (key) {
                if (cache[key])
                    delete cache[key];

            };
        })();

    }]);

})(window, window.document, window.jQuery, window.angular);