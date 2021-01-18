/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("sessionReportCtrl", ["$scope", "$rootScope","reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs,$state, notifySvcs, localStorage, $timeout) {

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        vm.year = vm.yearArray[0];
        console.log("vm.yearArray :: ",vm.yearArray);
        vm.getMusicType = function () {
            var reqMap = {
                "objName": "LKMusicType"
            };
            reportSvcs.getMusicType(reqMap, function (res) {
                vm.musicTypeArr = res.data;
            }, function (err) {

            });
        }();

        vm.getSessionDetails = function(){
            if(vm.year != undefined){
                var reqMap = {};
                if((vm.month != undefined && vm.month != '') && (vm.musicPk != undefined && vm.musicPk != '')){
                    reqMap = {
                        "year" : vm.year,
                        "month" : vm.month,
                        "musicPk": vm.musicPk+""
                    };
                }
                else if(vm.month != undefined && vm.month != ''){
                    reqMap = {
                        "year" : vm.year,
                        "month" : vm.month
                    };

                }else if(vm.musicPk != undefined && vm.musicPk != ''){
                    reqMap = {
                        "year" : vm.year,
                        "musicPk": vm.musicPk+""
                    };

                }else{
                    reqMap = {
                        "year" : vm.year
                    };
                }
                console.log("reqMap :: ",reqMap);

                reportSvcs.getMusicSessionDetails(reqMap, function(res){
                    vm.musicSessionDetails = res.data;
                    vm.musicSessionLength = Object.keys(vm.musicSessionDetails).length;
                    if(vm.musicSessionLength == 0){
                        notifySvcs.info({
                           content: "No Data found."
                        });
                    }
                    console.log("vm.musicSessionDetails : ",vm.musicSessionDetails);
                },function(err){
                    notifySvcs.error({
                        content: "Error!"
                    })
                });
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);