/**
 * Created by SumitJangir on 6/25/16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("lookupStoreSvcs", ["storeSvcs", "http", "$q", function (storeSvcs, http, $q) {


        function LookupStore() {

            var lookupArray = ["MusicType","CategoryType","ClassDuration"];

            angular.forEach(lookupArray, function (value, key) {

                this["get" + lookupArray[key]] = function () {
                    var k = lookupArray[key];
                    //console.log(";k:",k);
                    var result = storeSvcs.get("LK" + k);
                    if (result) {
                        //console.log("result:",result);
                        var defer = $q.defer();
                        defer.resolve(result);
                        return defer.promise;
                    } else {
                        //console.log("elseeee");
                        var defer = $q.defer();
                        //console.log(";k:",k);
                        http["get" + k](function (data) {
                            storeSvcs.set("LK" + k, data.data);

                            angular.forEach(data.data, function (val, dKey) {
                                if (val.primaryKey)
                                    storeSvcs.set("LK" + k + "_" + val.primaryKey, val);
                            });

                            defer.resolve(storeSvcs.get("LK" + k));
                        }, function (err) {
                            defer.reject(err);
                        });
                        return defer.promise;
                    }
                };

                this["get" + lookupArray[key] + "ByPK"] = function (pk) {
                    var k = lookupArray[key];
                    var defer = $q.defer();
                    defer.resolve(storeSvcs.get("LK" + k + "_" + pk));
                    return defer.promise;
                };

                this["set" + lookupArray[key]] = function (val) {
                    if (val) {
                        var k = lookupArray[key];
                        storeSvcs.set("LK" + k, val);
                        return val;
                    }
                    return false;
                };

            }, this);

        }

        return new LookupStore();

    }]);

})(window, window.document, window.jQuery, window.angular);