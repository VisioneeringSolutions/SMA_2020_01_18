/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    var competitionApp = angular.module('competitionApp');

    competitionApp.controller("competitionCtrl", ["$scope", "$rootScope", "competitionSvcs", "competitionModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs","competitionData","registrationSvcs", function ($scope, $rootScope, competitionSvcs, competitionModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs,competitionData,registrationSvcs) {
        console.log("competitionApp");

        var vm = this;
        vm.getDateFormat = function(Date) {
            var newdate = Date.split("-").reverse().join("-");

            return newdate;
        };

        vm.CompetitionData = competitionData.CompetitionList;
        for(var key in vm.CompetitionData) {
            console.log("cajbcjncjs");
            vm.firstName = vm.CompetitionData[key].name;
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
        vm.paginationTotalItems = vm.CompetitionData.length;
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
                var searchPageSize = (vm.CompetitionData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.CompetitionData.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };
        console.log("CompetitionData:", vm.CompetitionData);

        vm.addCompetitionModal = function () {
            console.log("working..addCompetitionModal;;;.");
            async.parallel({
                    MusicType: function (callback) {
                        var reqMap = {};
                        registrationSvcs.getMusicTypeList(reqMap, function (res) {
                            callback(null, res.data);
                        });
                    },
                    StudentList: function (callback) {
                        var reqMap  = {
                        };
                        registrationSvcs.getStudentUser(reqMap,function (res) {
                            callback(null, res.data);
                        });
                    },
                    TeacherList: function (callback) {
                        var reqMap  = {
                        };
                        registrationSvcs.getTeacherUser(reqMap,function (res) {
                            callback(null, res.data);
                        });
                    }

                },
                function (err, results) {
                    modalInstance = modalSvcs.open({
                        windowClass: "fullHeight",
                        size: "lg",
                        lkInstances: results,
                        templateUrl: "modules/competition/competition.addmodal.html",
                        controller: "competitionAddModalCtrl",
                        controllerAs: "competitionAddModal"
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

        vm.updateModal = function (instance) {
            console.log("instance   updateModal:", instance);
            async.parallel({
                MusicType: function (callback) {
                    var reqMap = {};
                    registrationSvcs.getMusicTypeList(reqMap, function (res) {
                        callback(null, res.data);
                    });
                },
                StudentList: function (callback) {
                    var reqMap  = {
                    };
                    registrationSvcs.getStudentUser(reqMap,function (res) {
                        callback(null, res.data);
                    });
                },

                TeacherList: function (callback) {
                    var reqMap  = {
                    };
                    registrationSvcs.getTeacherUser(reqMap,function (res) {
                        callback(null, res.data);
                    });
                },
                CompData: function (callback) {
                    var model = {
                        objName: "EOCompetition",
                        eoCompetitionPK: instance.primary_key + ""
                    };
                    competitionSvcs.getCompetitionByPk(model, function (res) {
                        console.log(" res.data:::::", res.data);
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                }
            }, function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    templateUrl: "modules/competition/competition.updatemodal.html",
                    controller: "competitionUpdateModalCtrl",
                    controllerAs: "competitionUpdateModal"
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