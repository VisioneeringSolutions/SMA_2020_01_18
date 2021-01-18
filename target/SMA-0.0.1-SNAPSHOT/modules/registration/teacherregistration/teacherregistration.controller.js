/**
 * Created by Kundan on 26-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("teacherRegistrationCtrl", ["$scope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","teacherData", function ($scope,  registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,teacherData) {

        var vm = this;

        vm.teacherData = teacherData;

        vm.getDateFormat = function(Date) {
            var newdate = Date.split("-").reverse().join("-");

            return newdate;
        };
        for(var key in vm.teacherData) {
            vm.firstName = vm.teacherData[key].teacherFullName;
            vm.column = 'teacherFullName';
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
        vm.paginationTotalItems = vm.teacherData.length;
        vm.paginationCurrentPage = 1;

        vm.paginationMaxSize = vm.viewBy;
        vm.setItemsPerPage = function (num) {
            vm.paginationMaxSize = num;
            vm.paginationCurrentPage = 1;
        };

        vm.pageScrollTop = function () {
            $window.scrollTo(0, angular.element(document.getElementById('pageTop')).offsetTop);
        };

        vm.teacherDataList = function(){
            if(vm.teacherData){
                for(var k in vm.teacherData){
                    vm.teacherData[k].joiningDate = moment(vm.teacherData[k].joiningDate,"YYYY-MM-DD").format("DD-MM-YYYY");

                }
            }
        }();

        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.teacherData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.teacherData.length;
                vm.paginationCurrentPage = 1;
                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };

        vm.addTeacherModal = function(){
            async.parallel({
                    ColorList: function (callback) {
                        var model = {};
                        registrationSvcs.getColorCodeForTeacher(model, function (res) {
                            callback(null, res.data);
                        }, function (err) {
                            callback(err);
                        });
                    }
                },
                function (err, results) {
                    modalInstance = modalSvcs.open({
                        windowClass: "fullHeight",
                        size: "lg",
                        lkInstances: results,
                        templateUrl: "modules/registration/teacherregistration/teacherregistration.addmodal.html",
                        controller: "teacherAddModalCtrl",
                        controllerAs: "teacherAddModal"

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

            async.parallel({

                /*MusicType: function (callback) {
                 lookupStoreSvcs.getMusicType().then(function (res) {
                 callback(null, res);
                 });
                 },*/
                ColorList: function (callback) {
                    var model = {};
                    registrationSvcs.getColorCodeForTeacher(model, function (res) {
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                },
                TeacherResponseData: function (callback) {
                    var model = {
                        eoTeacherPK : instance.primaryKey + ""
                    };
                    registrationSvcs.getTeacherUserByPk(model, function (res) {
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                },
                CourseList: function (callback) {
                    var reqMap = {};
                    registrationSvcs.getCourseList(reqMap, function (res) {
                        callback(null, res.data);
                    });
                }
            },function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    templateUrl: "modules/registration/teacherregistration/teacherregistration.updatemodal.html",
                    controller: "teacherUpdateModalCtrl",
                    controllerAs: "teacherUpdateModal"

                });
                modalInstance.rendered.then(function () {
                    //console.log("modal template rendered");
                });
                modalInstance.opened.then(function () {
                    //console.log("modal template opened");
                });
                modalInstance.closed.then(function () {
                    //console.log("modal template closed");
                    modalInstance = undefined;
                });
            });

        };

        vm.deleteModal = function(d){
            var locale = {
                OK: 'I Suppose',
                CONFIRM: 'Yes',
                CANCEL: 'No'
            };

            bootbox.addLocale('custom', locale);

            bootbox.prompt({
                title: "Do You want to delete <span style='color: blue; font-weight: 700'>"+ d.teacherFullName + "</span> ?",
                locale: 'custom',
                callback: function (result) {

                    if(result != null){
                        var reqMap = {
                            userPk : d.primaryKey+'',
                            reason : result,
                            className : 'EOTeacherUser'
                        };
                        registrationSvcs.updateDeleteReasonByStdPk( reqMap, function (res){

                            if(res.data = "Success"){
                                notifySvcs.success({
                                    content : "Student Successfully Removed"
                                });
                            }
                            $state.reload();

                        },function(err){
                            notifySvcs.error({
                                content:"something went wrong"
                            })
                        })
                    }
                }
            })
        };

        vm.resetPassword = function(d){
            console.log(d)
            bootbox.confirm({
                size: "medium",
                message: "Are you sure, You want to reset "+ d.teacherFullNameJapanese +" password?" ,
                callback: function(result){

                    if(result == true){
                        var reqMap = {
                            primaryKey: d.primaryKey,
                            className: 'eoteacher_user'
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
