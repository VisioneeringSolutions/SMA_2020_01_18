/**
 * Created by Kundan Kumar on 26-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("studentRegistrationCtrl", ["$scope", "$rootScope","registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","studentData","$window", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,studentData,$window) {
        $rootScope.pageHeader = "Students";
        $rootScope.pageDescription = "Registration";

        var vm = this;
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        vm.studentData = studentData;
        vm.getDateFormat = function(enrollmentDate) {
            var newdate = enrollmentDate.split("-").reverse().join("-");
            return newdate;
        };

        for(var key in vm.studentData) {
            vm.firstName = vm.studentData[key].studentfullName;
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
            vm.paginationTotalItems = vm.studentData.length;
            vm.paginationCurrentPage = 1;

            vm.paginationMaxSize = vm.viewBy;
            vm.setItemsPerPage = function (num) {
                //console.log("num::::", num)
                vm.paginationMaxSize = num;
                vm.paginationCurrentPage = 1;
            };

        vm.pageScrollTop = function () {
            $window.scrollTo(0, angular.element(document.getElementById('pageTop')).offsetTop);
        };

        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.studentData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.studentData.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };
            //console.log("studentData:grtbgtghrt", studentData);

        vm.addStudentModal = function () {
            async.parallel({
                    MusicType: function (callback) {
                        lookupStoreSvcs.getMusicType().then(function (res) {
                            callback(null, res);
                        });
                    },
                    CourseList: function (callback) {
                        var reqMap = {};
                        registrationSvcs.getCourseList(reqMap, function (res) {
                            callback(null, res.data);
                        });
                    }
                },
                function (err, results) {
                    modalInstance = modalSvcs.open({
                        windowClass: "fullHeight",
                        size: "lg",
                        lkInstances: results,
                        templateUrl: "modules/registration/studentregistration/studentregistration.addmodal.html",
                        controller: "studentAddModalCtrl",
                        controllerAs: "studentAddModal"

                    });
                    modalInstance.rendered.then(function () {
                        ////console.log("modal template rendered");
                    });
                    modalInstance.opened.then(function () {
                        ////console.log("modal template opened");
                    });
                    modalInstance.closed.then(function () {
                        ////console.log("modal template closed");
                        modalInstance = undefined;
                    });
                });
        };

        vm.updateModal = function (instance) {
            ////console.log("instance:",instance);

            async.parallel({
                MusicType: function (callback) {
                    lookupStoreSvcs.getMusicType().then(function (res) {
                        callback(null, res);
                    });
                },
                CourseList: function (callback) {
                    var reqMap = {};
                    registrationSvcs.getCourseList(reqMap, function (res) {
                        callback(null, res.data);
                    });
                },
                StudentResponseData: function (callback) {
                    var model = {
                        objName: "EOStudentUser",
                        eoStudUserPK: instance.primaryKey + ""
                    };
                    registrationSvcs.getStudentsUserByPk(model, function (res) {
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
                    templateUrl: "modules/registration/studentregistration/studentregistration.updatemodal.html",
                    controller: "studentUpdateModalCtrl",
                    controllerAs: "studentUpdateModal"

                });
                modalInstance.rendered.then(function () {
                    ////console.log("modal template rendered");
                });
                modalInstance.opened.then(function () {
                    ////console.log("modal template opened");
                });
                modalInstance.closed.then(function () {
                    ////console.log("modal template closed");
                    modalInstance = undefined;
                });
            });

        };

        vm.deleteModal = function (d) {
            var locale = {
                OK: 'I Suppose',
                CONFIRM: 'Yes',
                CANCEL: 'No'
            };

            bootbox.addLocale('custom', locale);
            bootbox.prompt({
                title: "Do You want to delete <span style='color: blue; font-weight: 700'>" + d.studentfullName + "</span> ?",
                locale: 'custom',
                callback: function (result) {
                    ////console.log('This was logged in the callback: ' + result);

                    if (result != null) {
                        var reqMap = {
                            userPk: d.primaryKey + '',
                            reason: result,
                            className: 'EOStudentUser'
                        };
                        registrationSvcs.updateDeleteReasonByStdPk(reqMap, function (res) {

                            if (res.data == "Success") {
                                notifySvcs.success({
                                    content: "Student Successfully Removed"
                                });
                            }
                            $state.reload();

                        }, function (err) {
                            notifySvcs.error({
                                content: "something went wrong"
                            })
                        })
                    }
                }
            })
        };

        vm.resetPassword = function(d){
            bootbox.confirm({
                size: "medium",
                message: "Are you sure, You want to reset "+ d.studentfullNameJapanese +" password?" ,
                callback: function(result){

                    if(result == true){
                        var reqMap = {
                            primaryKey: d.primaryKey,
                            className: 'eostudent_user'
                        };
                        registrationSvcs.resetPasswordByAdmin(reqMap, function (res) {

                            if(res.data){
                                bootbox.alert({
                                    message: "The new password is '"+res.data +"' without quotes. Please take a note of it",
                                    callback: function () {

                                    }
                                })
                            }

                        }, function(err){
                            notifySvcs.error({
                                content: "Something went wrong while resetting your password."
                            })
                        });
                    }
                }
            });
        }

    }]);

})(window, window.document, window.jQuery, window.angular);