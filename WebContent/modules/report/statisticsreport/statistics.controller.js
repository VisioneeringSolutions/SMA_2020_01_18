/**
 * Created by dell on 29-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("statisticsReportCtrl", ["$scope", "$rootScope","reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs,$state, notifySvcs, localStorage, $timeout) {

        var vm = this;


        vm.enable = {};
        document.getElementById(1).classList.add('toggle-tab');
        vm.enable['overallTurnover'] = true;
        vm.enablePage = function (pageName ,id,e) {
            var length = e.target.parentNode.parentNode.children.length;

            if(pageName == 'overallTurnover'){
                vm.enable[pageName] = false;
            }
            vm.enable = {};
            vm.enable[pageName] = true;
            for(var k = 1; k <= length; k++){
                if(k == id){
                    document.getElementById(k).classList.add('toggle-tab');
                    document.getElementById(k).classList.remove('tab');
                }
                else{
                    document.getElementById(k).classList.add('tab');
                    document.getElementById(k).classList.remove('toggle-tab');
                }
            }
        };

        vm.getStudentDetails = function () {
            var reqMap = {
                className: "EOStudentUser"
            };
            reportSvcs.getStudentUser(reqMap,function(res){
                vm.studentDetailsArr = res.data;
            },function(err){
                notifySvcs.error({
                    content: "Error!"
                });
            });
        }();

        vm.getMusicType = function () {
            var reqMap = {
                "objName": "LKMusicType"
            };
            reportSvcs.getMusicType(reqMap, function (res) {
                vm.musicTypeArr = res.data;
            }, function (err) {

            });
        }();

        vm.monthArr = [
            {"sMonth" : "JAN", "monthName" : "January"},
            {"sMonth" : "FEB", "monthName" : "February"},
            {"sMonth" : "MAR", "monthName" : "March"},
            {"sMonth" : "APR", "monthName" : "April"},
            {"sMonth" : "MAY", "monthName" : "May"},
            {"sMonth" : "JUN", "monthName" : "June"},
            {"sMonth" : "JUL", "monthName" : "July"},
            {"sMonth" : "AUG", "monthName" : "August"},
            {"sMonth" : "SEP", "monthName" : "September"},
            {"sMonth" : "OCT", "monthName" : "October"},
            {"sMonth" : "NOV", "monthName" : "November"},
            {"sMonth" : "DEC", "monthName" : "December"}
        ];





    }]);

})(window, window.document, window.jQuery, window.angular);