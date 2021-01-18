/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.factory("vformSvcs", ["http", "$rootScope", function (http, $rootScope) {


        var randomKeyGenerator = function (length, str) {
            var randomString = function (length, chars) {
                var result = '';
                for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
                return result;
            };
            return randomString(length || 32, str || '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ');
        };

        var inputPattern = {
            number: {
                regx: /^[+-]?\d*(\.\d+)?$/,
                pattern: "^[+-]?\d*(\.\d+)?$",
                type: "number",
                title: "Please type Number like \"1234\", \"1234.345\",\"-1234.345\""
            },
            positivenumber: {
                regx: /^\d*(\.\d+)?$/,
                pattern: "^\d*(\.\d+)?$",
                type: "number",
                title: "Please type Number like \"1234\", \"1234.34\""
            },
            doublenumber: {
                regx: /^\d*$/,
                pattern: "^\d*$",
                type: "number",
                title: "Please type Number like \"1234\""
            },
            email: {
                regx: /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/,
                pattern: "^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$",
                type: "email",
                title: "Please type Email like \"john@gmail.com\""
            },
            alpha: {
                regx: /^[A-Za-z]+$/,
                pattern: "^[A-Za-z]+$",
                type: "text",
                title: "Please type Alphabets Only like \"vison\""
            },
            alphaspace: {
                regx: /^[A-Za-z ]+$/,
                pattern: "^[A-Za-z ]+$",
                type: "text",
                title: "Please type Alphabets Only like \"vison\""
            },
            alphanumeric: {
                regx: /^[A-Za-z0-9]+$/,
                pattern: "^[A-Za-z0-9]+$",
                type: "text",
                title: "Please type Alphabets and Number Only like \"vison12345\""
            },
            alphanumericspace: {
                regx: /^[A-Za-z0-9 ]+$/,
                pattern: "^[A-Za-z0-9 ]+$",
                type: "text",
                title: "Please type Alphabets and Number Only like \"vison12345\""
            },
            url: {
                regx: /^[a-z][a-z\d.+-]*:\/*(?:[^:@]+(?::[^@]+)?@)?(?:[^\s:/?#]+|\[[a-f\d:]+\])(?::\d+)?(?:\/[^?#]*)?(?:\?[^#]*)?(?:#.*)?$/i,
                pattern: "^[a-z][a-z\d.+-]*:\/*(?:[^:@]+(?::[^@]+)?@)?(?:[^\s:/?#]+|\[[a-f\d:]+\])(?::\d+)?(?:\/[^?#]*)?(?:\?[^#]*)?(?:#.*)?$",
                type: "text",
                title: "Please type Url like \"http://www.visionsolutions.com\""
            },
            date: {
                regx: /^(\d{4,})-(\d{2})-(\d{2})$/,
                pattern: "^(\d{4,})-(\d{2})-(\d{2})$",
                type: "text",
                title: "Please type Date like \"2016-12-25\""
            },
            time: {
                regx: /^(\d\d):(\d\d)(?::(\d\d)(\.\d{1,3})?)?$/,
                pattern: "^(\d\d):(\d\d)(?::(\d\d)(\.\d{1,3})?)?$",
                type: "text"
            },
            datetimelocal: {
                regx: /^(\d{4,})-(\d\d)-(\d\d)T(\d\d):(\d\d)(?::(\d\d)(\.\d{1,3})?)?$/,
                pattern: "^(\d{4,})-(\d\d)-(\d\d)T(\d\d):(\d\d)(?::(\d\d)(\.\d{1,3})?)?$",
                type: "text"
            },
            isodate: {
                regx: /^\d{4,}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+(?:[+-][0-2]\d:[0-5]\d|Z)$/,
                pattern: "^\d{4,}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+(?:[+-][0-2]\d:[0-5]\d|Z)$",
                type: "text"
            },
            week: {
                regx: /^(\d{4,})-W(\d\d)$/,
                pattern: "^(\d{4,})-W(\d\d)$",
                type: "text"
            },
            month: {
                regx: /^(\d{4,})-(\d\d)$/,
                pattern: "^(\d{4,})-(\d\d)$",
                type: "text"
            }
        };


        var events = {},
            data = {};

        var eventManager = function () {
            this.set = function (key, fn) {
                if (events[key])
                    events[key].push(fn);
                events[key] = [fn];
                return events[key].length - 1;
            };
            this.del = function (key) {
                if (events[key]) {
                    delete events[key];
                    return true;
                }
                return false;
            };
        }();

        var dataManager = new function () {

            this.set = function (path, value) {
                var oldVal = deep(data, path);
                var newVal = deep(data, path, value);
                if (events[path]) {
                    for (var i in events[path])
                        events[path][i](newVal, oldVal, i);
                }
            };
            this.get = function (path) {
                return deep(data, path);
            };

        }();

        var uploadImage = function (metadata, success, error, url) {
            //console.log(metadata);
            var reqMap = {
                detail: metadata.metadata[0].baseData,
                entityName: metadata.metadata[0].entityName,
                className: metadata.metadata[0].className,
                type: metadata.metadata[0].type
            };

            if (metadata.metadata[0].primaryKey) {
                reqMap['primaryKey'] = metadata.metadata[0].primaryKey;
            }
            if (metadata.metadata[0].headerPk) {
                reqMap['headerPk'] = metadata.metadata[0].headerPk;
            }

            var requestMap = {
                method: "POST",
                url: $rootScope.baseAPIUrl + url,
                data: reqMap
            };

            //console.log(requestMap);

            http.request(requestMap, error);

        };

        return {
            inputPattern: inputPattern,
            randomKeyGenerator: randomKeyGenerator,
            data: dataManager,
            event: eventManager,
            uploadImage: uploadImage
        };

    }]);

})(window, window.document, window.jQuery, window.angular);
