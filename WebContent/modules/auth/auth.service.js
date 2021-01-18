/**
 * Created by abc on 16/6/18.
 */
(function (window, document, $, angular) {

    angular.module('musicAuth').service('localStorage', function () {
        this.add = window.sInstances.localStorage.add;
        this.remove = window.sInstances.localStorage.remove;
        this.get = window.sInstances.localStorage.get;
    });

    angular.module('musicAuth').factory("authSvcs", ["$http", "$rootScope", "localStorage", function ($http, $rootScope, localStorage) {

        //QA
        $rootScope.baseAPIUrl = window.sInstances.origin + "/SMA/rest/";
        $rootScope.baseUrl = window.sInstances.origin+"/SMA/";

        //PROD
        //$rootScope.baseAPIUrl = window.sInstances.origin + "/rest/";
        //$rootScope.baseUrl = window.sInstances.origin +"/";

        var logout = function () {
            var k = angular.copy({key: localStorage.get('visionKey')});
            localStorage.remove('visionKey');
            localStorage.remove('userID');
            localStorage.remove('firstName');
            localStorage.remove('userKey');
            localStorage.remove('role');
            localStorage.remove('id');
            localStorage.remove('address1');
            localStorage.remove('address2');
            localStorage.remove('phone');
            localStorage.remove('imageUrl');
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "auth/logout",
                data: {
                    token: k.key
                }
            }).then(function (res) {
                redirect("login");
            }, function (err) {
                redirect("login");
            });
        };

        var login = function (model) {

            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "auth/loginUser",
                data: model
            }).then(function (response) {
                if (response.data) {
                    if (response.data.token && response.data.url) {
                        localStorage.add('visionKey', response.data.token);
                        localStorage.add('userID', response.data.userId);
                        localStorage.add('firstName', response.data.firstName);
                        localStorage.add('userKey', response.data.userKey);
                        localStorage.add('role', response.data.role);
                        localStorage.add('id', response.data.id);
                        localStorage.add('address1', response.data.address1);
                        localStorage.add('address2', response.data.address2);
                        localStorage.add('phone', response.data.phone);
                        localStorage.add('imageUrl', response.data.imageUrl);
                        window.location.href = response.data.url;
                    }
                }
            }, function (err) {
                bootbox.alert("Error In Sign Up fill the details correctly");
            });
        };

        var signup = function (model) {
            //window.location.href = $rootScope.baseUrl+"login";
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "ajaxRegistration/createUser",
                data: model
            }).then(function (response) {
                if (response.data == 'Success') {
                    window.location.href = $rootScope.baseUrl;
                    /*if (response.data.token && response.data.url) {
                     localStorage.add('visionKey', response.data.token);
                     localStorage.add('userID', response.data.userId);
                     localStorage.add('firstName', response.data.firstName);
                     localStorage.add('userKey', response.data.userKey);
                     window.location.href = response.data.url;
                     }*/
                }
            }, function (err) {
                bootbox.alert("Error In Sign Up fill the details correctly");
            });
        };

        var changePassword = function (model) {
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "auth/changePassword",
                data: model												// data:password
            }).then(function (response) {
                if (response.data) {
                    if (response.data.token && response.data.url) {
                        window.location.href = response.data.url;

                    }
                }
            }, function (err) {
                alert("Server respond With Error!!!!!");
            });
        };
        var forgotPassword = function (model) {
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "auth/forgotPassword",
                data: model
            })
         .then(function (res) {
             if(res.data=="No Data"){
                 bootbox.dialog({
                     message: "Please retry or write to us at info@visioneering.co.in",
                     size: 'small',
                     closeButton: true,
                     title: "<span style='color: red; font-size: medium'>Sorry,username does not match.</span>",
                     timeOut: 1000

                 });
             }
             else {
                 bootbox.dialog({
                     message: "New Password has been sent to the registered email id.Please check.",
                     size: 'small',
                     closeButton: true,
                     title: "<span style='color: red; font-size: medium'>Success!!</span>",
                     timeOut: 1000
                 });

             }

            }, function (err) {
                alert("Server respond With Error");
            });

        };


        var getLKObject = function(model,callback){
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "auth/getLKObject",
                data: model
            }).then(function (response){
                //console.log(response);
                callback(response.data);
            }, function(err){
                alert("Server respond With Error");
            });
        };


        function redirect(url) {
            window.location.href = $rootScope.baseUrl + url;
        }

        return {
            login: login,
            signup: signup,
            //isLoggedIn: isLoggedIn,
            logout: logout,
            changePassword: changePassword,
            forgotPassword:forgotPassword,
            getLKObject : getLKObject,
            redirect: redirect
        }
    }]);
})(window, window.document, window.jQuery, window.angular);