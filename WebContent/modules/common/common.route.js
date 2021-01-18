/**
 * Created by SumitJangir on 6/5/16.
 */
(function (window, document, $, angular) {

    var commonApp = angular.module("commonApp");

    commonApp.run(["$rootScope", "amMoment", function ($rootScope, amMoment) {

        //QA and LOCAL
        $rootScope.baseAPIUrl = window.sInstances.origin + "/SMA/rest/";
        $rootScope.baseUrl = window.sInstances.origin+"/SMA/";

        //PROD
        //$rootScope.baseAPIUrl = window.sInstances.origin + "/rest/";
        //$rootScope.baseUrl = window.sInstances.origin +"/";

    }]);

    commonApp.config(['$httpProvider', '$provide', '$stateProvider', '$urlRouterProvider','$translateProvider', function ($httpProvider, $provide, $stateProvider, $urlRouterProvider,$translateProvider) {

        var httpRequestCounter = 0,
            pageLoader = new sInstances.pageLoader();
        /*
         * i18n language defining
         * */
        $provide.factory('asyncLoader', ["$q", "$timeout", "http", function ($q, $timeout, http) {

            return function (options) {
                var deferred = $q.defer();
                http.request({
                    method: "GET",
                    url: "lang/" + options.key + ".json"
                }, function (res) {
                    deferred.resolve(res.data || {});
                }, function (err) {
                    deferred.resolve({});
                });
                return deferred.promise;
            };
        }]);
        $translateProvider.useLoader('asyncLoader');
        $translateProvider.useSanitizeValueStrategy('sanitize');
        $translateProvider.preferredLanguage('en-US');

        // register the interceptor as a service
        $provide.factory('rootHttpInterceptor', function ($q,notifySvcs,$rootScope, localStorage) {
            return {
                // optional method
                'request': function (config) {

                    /***********For stopping loader for these two methods only********************/
                    if(config.url.split('/').reverse()[0] == 'getMessageByUser' || config.url.split('/').reverse()[0] == 'getContactListByUserKey'){
                        //getMessageByUser && getContactListByUserKey in message module.
                    }else{
                        ++httpRequestCounter;
                    }
                    if (httpRequestCounter == 1)
                        pageLoader.start();

                    return config;
                },

                // optional method
                'requestError': function (rejection) {
                    // do something on error
                    // if (canRecover(rejection)) {
                    //     return responseOrNewPromise
                    // }
                    return $q.reject(rejection);
                },

                // optional method
                'response': function (response) {
                    // do something on success
                    --httpRequestCounter;
                    if (httpRequestCounter == 0)
                        pageLoader.stop();
                    return response;
                },

                // optional method
                'responseError': function (rejection) {
                    --httpRequestCounter;
                    if (httpRequestCounter == 0)
                        pageLoader.stop();
                    // do something on error
                    // if (canRecover(rejection)) {
                    //     return responseOrNewPromise
                    // }
                    if (rejection.status == 401 || rejection.statusText == "Unauthorized") {
                        localStorage.remove('visionKey');
                        localStorage.remove('userID');
                        localStorage.remove('firstName');
                        localStorage.remove('userKey');
                        notifySvcs.error({title: "Session Timeout", content: "Please Login Again"});
                        setTimeout(function () {
                            window.location.href = $rootScope.baseUrl + "login";
                        }, 2000);
                    }
                    return $q.reject(rejection);
                }
            };
        });


        $httpProvider.interceptors.push('rootHttpInterceptor');


    }]);

})(window, window.document, window.jQuery, window.angular);