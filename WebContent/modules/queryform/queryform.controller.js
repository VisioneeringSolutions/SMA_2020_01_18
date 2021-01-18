/**
 * Created by nikita on 2019-06-14.
 */
(function (window, document, $, angular) {

    var queryFormApp = angular.module('queryFormApp');

    queryFormApp.controller("queryFormCtrl", ["$scope", "$rootScope", "queryFormSvcs", "queryFormModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "queryFormData", "registrationSvcs", function ($scope, $rootScope, queryFormSvcs, queryFormModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, queryFormData, registrationSvcs) {

        var vm = this;
        vm.QueryFormData = queryFormData.QueryFormList;
        vm.phoneData = queryFormData.PhoneNumberList;

        vm.getDateFormat = function(Date) {
            var newdate = Date.split("-").reverse().join("-");

            return newdate;
        };

        for(var key in vm.QueryFormData) {
            vm.firstName = vm.QueryFormData[key].first_name;
            vm.column = 'stdfullname';
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
        vm.CourseList = function() {
            //phoneCheck = true;
            var  reqMap = {};
            registrationSvcs.getCourseList(reqMap, function(res){
                vm.courseList=res.data;

            });
        };
            vm.viewBy = 10;
            vm.paginationTotalItems = vm.QueryFormData.length;
            vm.paginationCurrentPage = 1;

            vm.paginationMaxSize = vm.viewBy;
            vm.setItemsPerPage = function (num) {
                vm.paginationMaxSize = num;
                vm.paginationCurrentPage = 1;
            };


        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.QueryFormData.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.QueryFormData.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };

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
                        templateUrl: "modules/queryform/queryform.addmodal.html",
                        controller: "queryAddModalCtrl",
                        controllerAs: "queryAddModal"

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

        vm.CheckALL = true;
        vm.checkAllData = function (selectedStudent) {
            vm.studName = [];
            for (var k in vm.studentArray) {
                if (selectedStudent == true) {
                    vm.CheckALL = false;
                    vm.studentArray[k].isSelected = true;
                    vm.studName.push(vm.studentArray[k].studentName);
                }
                if (selectedStudent == false) {
                    vm.CheckALL = true;
                    vm.studentArray[k].isSelected = false;
                    vm.studName = [];
                }
            }
        };

        vm.notConvertedStudList=[];
        vm.coursePkList=[];
        var notConvertedStudIDList=[];
        vm.studList = [];
        vm.studName = [];
        vm.studentQryData = [];
        vm.addToStudList = function (d, checkStatus, index) {
            vm.CourseList();

            if (checkStatus == true) {
                vm.studName.push(d.stdfullname);
                vm.notConvertedStudList.push(d.primary_key);

               // var musicPkList = [];
                var musicDesciption = [];
                for(var k in vm.courseList){
                    if(d.course_pk_list.includes(vm.courseList[k].primary_key+"")){
                        var description = vm.courseList[k].description.substring(0, 2);
                        if(description === 'T_'){
                           notConvertedStudIDList.push(d.user_name);
                        }
                    }
                }
                var StudMap = {
                    primaryKey: d.primary_key,
                    phone: d.phone,
                    email: d.email,
                    gender: d.gender,
                    firstName: d.first_name,
                    lastName: d.last_name,
                    musicPk: d.music_pk,
                    dateOfBirth: d.date_of_birth,
                    coursePkList: d.course_pk_list,
                    userName: d.user_name,
                    isVisible:d.isVisible
                };
                vm.studentQryData.push(StudMap);
            }
            if (checkStatus == false) {

                for (var obj3 in vm.notConvertedStudList) {
                 if (vm.notConvertedStudList[obj3] == d.primary_key) {
                        vm.notConvertedStudList.splice(obj3, 1);
                     }
                 }
                for (var obj3 in notConvertedStudIDList) {
                    if (notConvertedStudIDList[obj3] == d.user_name) {
                        notConvertedStudIDList.splice(obj3, 1);
                    }
                }
                for (var obj2 in vm.studentQryData) {
                    if (vm.studentQryData[obj2].primaryKey == d.primary_key) {
                        vm.studentQryData.splice(obj2, 1);
                    }
                }
                for (var obj2 in vm.studentQryData) {
                    if (vm.studentQryData[obj2].primaryKey == d.primary_key) {
                        vm.studentQryData.splice(obj2, 1);
                    }
                }

                for (var obj1 in vm.studName) {
                    if (vm.studName[obj1] == d.stdfullname && checkStatus == false) {
                        vm.studName.splice(obj1, 1);
                    }
                }
            }
        };

        vm.studHostelArray = [];
        function setData(data) {
            angular.forEach(vm.QueryFormData, function (value, key) {
                if(vm.studList.includes(value.primary_key)){
                    vm.QueryFormData.splice(key,1);
                }
            });
        }
        vm.moveStudentsFn = function (e, form) {

            vm.finalStudentQryData = [];
            vm.studList =[];
            var errorStudList = [];
            for (var k in vm.studentQryData) {
                if (!vm.phoneData.includes(vm.studentQryData[k].phone)) {
                    vm.finalStudentQryData.push(vm.studentQryData[k]);
                    vm.studList.push(vm.studentQryData[k].primaryKey);
                }
                else {
                    errorStudList.push({
                        primaryKey: vm.studentQryData[k].primaryKey,
                        fullName: vm.studentQryData[k].firstName + " " + vm.studentQryData[k].lastName,
                        phone: vm.studentQryData[k].phone
                    })
                }
            }
            if (vm.studList.length == 0) {
                if (errorStudList.length > 0) {
                    for(var i in errorStudList){
                        var x = document.getElementsByClassName("duplicate-found"+errorStudList[i].primaryKey);

                        for (var j = 0; j < x.length; j++) {
                            x[j].style.color = "red";
                        }
                        document.getElementsByClassName("duplicate-found"+errorStudList[i].primaryKey).innerHTML = 'Duplicate number';
                       /// document.getElementsByClassName("duplicate-found"+errorStudList[i].primaryKey).style.color = "red";
                    }
                    notifySvcs.success({
                        title: "Students",
                        content: "Moved Successfully"
                    });
                }
                else {
                    notifySvcs.error({
                        content: "Please select student to move."
                    });
                }
            }
            else {
                var reqMap = {
                    studList: vm.studList,
                    studDataList: vm.finalStudentQryData
                };
              if (errorStudList.length > 0) {

                    for (var i in errorStudList) {
                        var x = document.getElementsByClassName("duplicate-found" + errorStudList[i].primaryKey);

                        for (var j = 0; j < x.length; j++) {
                            x[j].style.color = "red";
                        }
                        notifySvcs.error({
                            content: "Column marked in red have duplicate phone from the list of registered users"
                        });
                    }
                }
                var studentList = [];
                for (var m in reqMap.studDataList) {
                    reqMap.studDataList[m].studentId = (reqMap.studDataList[m].firstName).charAt(0) + (reqMap.studDataList[m].lastName).charAt(0);
                    var tempList = [];
                    for (var k in queryFormData.CourseList) {
                        if (reqMap.studDataList[m].coursePkList.includes((queryFormData.CourseList[k].primary_key + ""))) {
                            tempList.push(queryFormData.CourseList[k].musicpk + "");
                        }
                    }
                    reqMap.studDataList[m].musicPk = JSON.stringify(tempList);
                }
                if (errorStudList.length > 0) {
                    notifySvcs.error({
                        content: "Please deselect trial student"
                    });
                } else {
                    queryFormSvcs.moveQryFormFn(reqMap, function (res) {
                        if (res.data == "Success") {
                            $state.reload();
                            setData();
                            notifySvcs.success({
                                title: "Students",
                                content: "Moved Successfully"
                            });
                        }
                    },
                    function (err) {
                        notifySvcs.error({
                            content: "Action not performed! Please Fill Form Correctly "
                        });
                    });
                }
            }
        };
        vm.updateModal = function (instance) {
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
                StudentQueryData: function (callback) {
                    var model = {
                        objName: "EOQueryForm",
                        eoStudUserPK: instance.primary_key + ""
                    };
                    queryFormSvcs.getQueryStudentsByPk(model, function (res) {
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
                    templateUrl: "modules/queryform/queryform.updatemodal.html",
                    controller: "queryFormUpdateModalCtrl",
                    controllerAs: "queryFormUpdateModal"
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

        vm.notConvertedFn = function (e, form) {

            if (vm.notConvertedStudList.length == 0 && notConvertedStudIDList.length == 0) {
                notifySvcs.error({
                    content: "Please select student to Remove."
                });
            }


            else {
                vm.userPkList = notConvertedStudIDList;
                var reqMap = {
                    studList: vm.notConvertedStudList ,
                    userNameList: vm.userPkList,
                    className: "EOQueryForm"
                };
                queryFormSvcs.notConvertedStudFn(reqMap, function (res) {
                    if (res.data == "Success") {
                        $state.reload();
                        notifySvcs.success({
                            title: "Students",
                            content: "Removed Successfully"
                        });
                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);