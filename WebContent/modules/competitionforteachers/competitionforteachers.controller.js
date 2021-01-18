/**
 * Created by mydell on 2019-06-25.
 */
(function (window, document, $, angular) {

    var competitionForTeachersApp = angular.module('competitionForTeachersApp');

    competitionForTeachersApp.controller("competitionForTeachersCtrl", ["$scope", "$rootScope", "competitionForTeachersSvcs", "competitionForTeachersModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs","competitionData", function ($scope, $rootScope, competitionForTeachersSvcs, competitionForTeachersModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs,competitionData) {

        var vm = this;
        vm.getDateFormat = function(date) {
            var newdate = date.split("-").reverse().join("-");

            return newdate;
        };

        vm.TeacherCompetitionData = competitionData.TeacherCompList;
        for(var key in vm.TeacherCompetitionData) {
            console.log("cajbcjncjs");
            vm.firstName = vm.TeacherCompetitionData[key].name;
            vm.column = 'name';
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
        vm.paginationTotalItems = vm.TeacherCompetitionData.length;
        console.log("vm.paginationTotalItems:::::", vm.paginationTotalItems);
        vm.paginationCurrentPage = 1;

        vm.paginationMaxSize = vm.viewBy;
        vm.setItemsPerPage = function (num) {
            console.log("num::::", num)
            vm.paginationMaxSize = num;
            vm.paginationCurrentPage = 1;
        };

        /*  vm.setItemsPerPageOnSearch = function () {
         if (vm.customFilter.$.length > 0) {
         var searchPageSize = (vm.consentsData.length) / vm.viewBy;

         vm.viewBy = angular.copy(vm.viewBy);
         vm.paginationMaxSize = vm.viewBy * searchPageSize;
         vm.paginationCurrentPage = 1;
         }
         else {
         vm.viewBy = 10 + "";
         vm.paginationTotalItems = vm.consentsData.length;
         vm.paginationCurrentPage = 1;


         vm.paginationMaxSize = vm.viewBy;
         vm.setItemsPerPage = function (num) {
         vm.paginationMaxSize = num;
         vm.paginationCurrentPage = 1;
         };
         }
         };*/


        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.TeacherCompetitionData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.TeacherCompetitionData.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };

        vm.viewDetailsModel = function(d) {
            console.log("d::",d);
            async.parallel({
                CompetitionDetails: function (callback) {
                    var reqMap = {
                        teacherPk : d.primary_key+""
                    };
                    competitionForTeachersSvcs.getCompListForTeacherPk(reqMap,function(res) {
                        console.log("res.data--",res.data);
                        vm.compData = res.data;
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                }
            },function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    templateUrl: "modules/competitionforteachers/competitionforteachers.viewmodal.html",
                    controller: "compTeacherViewModalCtrl",
                    controllerAs: "compTeacherViewModal"
                });
                modalInstance.rendered.then(function () {
                });
                modalInstance.opened.then(function () {
                });
                modalInstance.closed.then(function () {
                    modalInstance = undefined;
                });
            });

        };

    }]);

})(window, window.document, window.jQuery, window.angular);