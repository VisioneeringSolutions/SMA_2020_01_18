/**
 * Created by dell on 30-05-19.
 */

(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("generalReportCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {
        $rootScope.pageHeader = "";
        $rootScope.pageDescription = "";

        var vm = this;
        vm.enable = {};
        document.getElementById(1).classList.add('toggle-tab');
        vm.enable['studentDetails'] = true;
        vm.enablePage = function (pageName ,id,e) {
            var length = e.target.parentNode.parentNode.children.length;

            if(pageName == 'studentDetails'){
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

        vm.getDateFormat = function(date) {
            var newdate = date.split("-").reverse().join("-");

            return newdate;
        };

      /* vm.getMusicTypeValue = function(musicType){
            console.log("musicType::::::::",musicType);

            return "vishuja";
        };*/

        vm.musicType = [];
        vm.getStudentDetails = function () {
            var reqMap = {
                className: "EOStudentUser"
            };
            reportSvcs.getStudentUser(reqMap,function(res){
                vm.studentDetailsArr = res.data;
                for(var k in  vm.studentDetailsArr){
                    vm.musicType.push(vm.studentDetailsArr[k].musicType);
                   // console.log(" vm.musicType=====", vm.musicType)

                }

                //var musictype = vm.studentDetailsArr.musicType;

            },function(err){
                notifySvcs.error({
                    content: "Error!"
                });
            });
        }();
        
      /*  vishuja added for student invoice*/
        vm.getStudentDataListForInvoice = function () {
            var reqMap = {
                className: "EOStudentUser"
            };
            reportSvcs.getStudentDetailsForInvoice(reqMap,function(res){
                vm.studentDetailsArrForInvoice = res.data;
                for(var k in  vm.studentDetailsArr){
                    vm.musicType.push(vm.studentDetailsArr[k].musicType);
                   // console.log(" vm.musicType=====", vm.musicType)

                }

                //var musictype = vm.studentDetailsArr.musicType;

            },function(err){
                notifySvcs.error({
                    content: "Error!"
                });
            });
        }();

        vm.getTeacherDetails = function () {
            var reqMap = {
                className: "EOTeacherUser"
            };
            reportSvcs.getTeacherUser(reqMap,function(res){
                vm.teacherDetailsArr = res.data;

            },function(err){
                notifySvcs.error({
                    content: "Error!"
                });
            });
        }();

        vm.getnotconvertedDetail = function () {
            var reqMap = {
                className: "EOQueryForm"
            };
            reportSvcs.getnotconvertedlist(reqMap,function(res){
                vm.notConvertstudentDetailsArr = res.data;
                //console.log("vm.notconvertlistttttt=====",vm.notConvertstudentDetailsArr)
            },function(err){
                notifySvcs.error({
                    content: "Error!"
                });
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