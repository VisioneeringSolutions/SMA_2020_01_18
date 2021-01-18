/**
 * Created by nikita on 2019-06-14.
 */
(function (window, document, $, angular) {

    var trialStudentsApp = angular.module('trialStudentsApp');

    trialStudentsApp.controller("trialStudentsCtrl", ["$scope", "$rootScope", "trialStudentsSvcs", "trialStudentsModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "trialStudentsData", "registrationSvcs", function ($scope, $rootScope, trialStudentsSvcs,trialStudentsModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, trialStudentsData, registrationSvcs) {

        var vm = this;
        vm.trialStudentsData = trialStudentsData;
        vm.phoneData = trialStudentsData.PhoneNumberList;

        vm.getDateFormat = function(Date) {
            var newdate = Date.split("-").reverse().join("-");

            return newdate;
        };
        for(var key in vm.trialStudentsData) {


            vm.firstName = vm.trialStudentsData[key].studentfullName;
            vm.column = 'studentfullName';
            vm.reverse = false;
            vm.sortColumn = function (col) {
                vm.column = col;
                if (vm.reverse) {
                    vm.reverse = false;
                    vm.reverseclass = 'arrow-up';
                } else {
                    vm.reverse = true;
                    vm.reverseclass = 'arrow-down';
                }
            };
            vm.sortClass = function (col) {
                if (vm.column == col) {
                    if (vm.reverse) {
                        return 'arrow-down';
                    } else {
                        return 'arrow-up';
                    }
                } else {
                    return '';
                }
            };
        }
        vm.viewBy = 10;
        vm.paginationTotalItems = vm.trialStudentsData.length;
        vm.paginationCurrentPage = 1;

        vm.paginationMaxSize = vm.viewBy;
        vm.setItemsPerPage = function (num) {
            vm.paginationMaxSize = num;
            vm.paginationCurrentPage = 1;
        };
        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.trialStudentsData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.trialStudentsData.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };

        vm.notConvertedStudList=[];
        vm.studList = [];
        vm.studName = [];
        vm.studentQryData = [];
        vm.addToStudList = function (d, checkStatus, index) {
            console.log("checkStatus::::",d)
            vm.notConvertedStudList.push(d.primary_key);


                var StudMap = {
                    primaryKey: d.primaryKey,
                    phone: d.phone,
                    email: d.email,
                    gender: d.gender,
                    isVisible:d.isVisible
                };
                vm.studentQryData.push(StudMap);

        };
        vm.updateModel = function (e, form) {

            vm.finalStudentQryData = [];

            var errorStudList = [];
            for (var k in vm.studentQryData) {
                vm.finalStudentQryData.push(vm.studentQryData[k]);
                vm.studList.push(vm.studentQryData[k].primaryKey);
            }

            var reqMap = {
                studList: vm.studList,
                studDataList: vm.finalStudentQryData
            };

            if (errorStudList.length > 0) {
                for(var i in errorStudList){
                    var x = document.getElementsByClassName("duplicate-found"+errorStudList[i].primaryKey);

                    for (var j = 0; j < x.length; j++) {
                        x[j].style.color = "red";
                    }
                    document.getElementById("duplicate"+errorStudList[i].primaryKey).innerHTML = 'Duplicate number';
                    document.getElementById("duplicate"+errorStudList[i].primaryKey).style.color = 'blue';
                }
            }
            trialStudentsSvcs.updateTrialStudents(reqMap, function (res) {
                    if (res.data == "Success") {
                        $state.reload();
                        setData();
                        notifySvcs.success({
                            title: "Students",
                            content: "Updated Successfully"
                        });
                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });

        };

        vm.studHostelArray = [];
        function setData(data) {
            angular.forEach(vm.trialStudentsData, function (value, key) {
                if(vm.studList.includes(value.primary_key)){
                    vm.trialStudentsData.splice(key,1);
                }
            });
        }





    }]);

})(window, window.document, window.jQuery, window.angular);