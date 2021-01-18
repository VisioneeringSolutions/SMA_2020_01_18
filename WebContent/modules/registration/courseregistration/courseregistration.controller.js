/**
 * Created by Kundan Kumar on 06-May-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("courseregistrationCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "typeData", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, typeData) {
        $rootScope.pageHeader = "course";
        $rootScope.pageDescription = "Registration";

        var vm = this;
        vm.categoryType = typeData.CategoryType;
        vm.musicType = typeData.MusicType;
        vm.courseList = typeData.CourseList;
        vm.duration = typeData.Duration;
        console.log("typeData::",typeData);

        for(var key in vm.courseList) {
            vm.firstName = vm.courseList[key].course_name;
            vm.column = 'course_name';
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

        vm.formModel = registrationModel.getInstance('course');
        if (vm.formModel.sessionDuration == '') {
            vm.formModel.sessionDuration = '2';
        }
        if (vm.formModel.feeType == '') {
            vm.formModel.feeType = 'Per session';
        }


        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'yyyy-MM-dd';
                    this.popup = popup || {
                            opened: false
                        };
                    this.open = open || function () {
                            this.popup.opened = true;
                        };
                    this.dateOptions = dateOptions || {
                            formatYear: 'yy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        vm.createDateMap = function () {
            return dateMaps.getMap();
        };
        /* --------------end-------------------- */


        vm.updateModal = function(instance){
            var reqMap = {
                eoCoursePK:instance.primary_key +''
            };
            registrationSvcs.getCourseByPk(reqMap, function(res){
                if(res.data != null){
                    vm.formModel = res.data;
                    var startDate = moment(vm.formModel.startDate,"DD-MM-YYYY").format("YYYY-MM-DD");
                    vm.startDate = new Date(startDate);
                }
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
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.music_type + "</span> ?",
                locale: 'custom',
                callback: function (result) {

                    if(result != null){
                        var reqMap = {
                            coursePk : d.primary_key+'',
                            reason : result
                        };
                        registrationSvcs.deleteCourseByPk( reqMap, function (res){

                            if(res.data == "Success"){
                                notifySvcs.success({
                                    content : "Course Successfully Removed"
                                });
                            }if(res.data == "failure"){
                                notifySvcs.info({
                                    content : "Course is already in use"
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
            });
        };

        vm.addCourse = function(){
            vm.formModel = registrationModel.getInstance('course');
            if (vm.formModel.sessionDuration == '') {
                vm.formModel.sessionDuration = '2';
            }
            if (vm.formModel.courseType == '') {
                vm.formModel.courseType = 'Individual';
            }
            if (vm.formModel.feeType == '') {
                vm.formModel.feeType = 'Per session';
            }
        };

        vm.tabConfig = {
            defaultActive: 0,
            mapid: "tabContainer",
            click: function (clickedIndex) {
                if (parseInt(clickedIndex) > parseInt(vm.tabConfig.defaultActive)) {
                    vm.validateTabSectionFieldsFn(vm.tabConfig.defaultActive, function (status) {
                        if (status) {
                            vm.tabConfig.setActive(clickedIndex, clickedIndex);
                        }
                    });
                } else {
                    vm.tabConfig.setActive(clickedIndex, clickedIndex);
                }
            }
        };
        vm.validateTabSectionFieldsFn = function (tabIndex, callback) {
            var formInputs = vm.formConfig.vinputs;
            var formSelects = vm.formConfig.vselect;
            var invaliFields = [];

            for (var fieldIndex in tabMapForFields[tabIndex]) {
                if (fieldIndex != "length") {
                    var fieldName = tabMapForFields[tabIndex][fieldIndex];
                    if (formInputs[fieldName]) {
                        if (!formInputs[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                    if (formSelects[fieldName]) {
                        if (!formSelects[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                }
            }

            if (invaliFields.length != 0) {
                callback(false);
                notifySvcs.error({
                    content: "Please Fill The Form Correctly."
                });
            } else {
                callback(true);
            }
        };

        var courseName = "";
        var category = "";

        vm.setCourseNameCode = function(){

            for(var k in vm.musicType){
                if(vm.musicType[k].primaryKey+"" == vm.formModel.lkMusicType){
                    courseName = vm.musicType[k].musicType;
                    break;
                }
            }

            for(var k in vm.categoryType){
                if(vm.categoryType[k].primaryKey+"" == vm.formModel.lkCategoryType){
                    category = vm.categoryType[k].description;
                    break;
                }
            }
            //setting courseName
            vm.formModel.courseName = courseName + " " + category;

            //setting course ID
            vm.formModel.courseCode = (courseName != "" ? courseName.charAt(0):"") + (category != "" ? category.charAt(0):"");
        };
        vm.setCourseId = function(){
            if(vm.formModel.firstName == undefined && vm.formModel.lastName != undefined){
                vm.formModel.studentId = vm.formModel.lastName.charAt(0);
            }
            if(vm.formModel.lastName == undefined && vm.formModel.firstName != undefined){
                vm.formModel.studentId = vm.formModel.firstName.charAt(0);
            }
            if(vm.formModel.firstName != undefined && vm.formModel.lastName != undefined){
                vm.formModel.studentId = (vm.formModel.firstName != undefined ? vm.formModel.firstName.charAt(0) : "")
                    + (vm.formModel.lastName != undefined ? vm.formModel.lastName.charAt(0): "");
            }
            if(vm.formModel.firstName == undefined && vm.formModel.lastName == undefined){
                vm.formModel.studentId = "";
            }
        };

        vm.formSubmit = function () {
            vm.formConfig.formElement.trigger('submit');
        };

        vm.formConfig = {
            preCompile: function (e) {
                ////console.log("preCompile ",e);
            },
            postCompile: function (e) {
                ////console.log("postCompile ",e);
                vm.formConfig.formScope = e.scope;
                vm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = vm.formConfig.validateFormInputs();

                var form = vm.formConfig.formScope.courseregistration;
                if (validationResponse.invalidInputs.length == 0) {

                    if (vm.formModel.lkMusicType != '' && vm.formModel.lkCategoryType != '' && vm.startDate != undefined) {
                        vm.formModel.startDate = moment(vm.startDate).format("DD-MM-YYYY");
                        vm.formModel.className = 'EOCourses';
                        if(vm.formModel.session != ''){
                            vm.formModel.session = parseInt(vm.formModel.session);
                        }if(vm.formModel.fees != ''){
                            vm.formModel.fees = parseInt(vm.formModel.fees);
                        }

                        vm.formModel.courseName = courseName + " " + category;
                        vm.formModel.courseCode = (courseName != "" ? courseName.charAt(0):"") + (category != "" ? category.charAt(0):"");

                        if(courseName == "" || category == ""){
                            vm.setCourseNameCode();
                        }
                        var blankFormInstance = angular.copy(vm.formModel);
                        if(blankFormInstance.session == ''){
                            delete blankFormInstance.session;
                        }

                        console.log("blankFormInstance:",blankFormInstance);
                        registrationSvcs.createCourse(blankFormInstance, function (res) {
                            if (res.data == 'Success') {
                                $state.reload();
                                notifySvcs.success({
                                    title: "Course",
                                    content: "Success"
                                });
                            }
                        },function (err) {
                            notifySvcs.error({
                                content: "Something went wrong"
                            })
                        });
                    }
                    else{
                        notifySvcs.error({
                            title: "Course",
                            content: "Please fill mandatory fields"
                        });
                    }
                }
                else {
                    notifySvcs.error({
                        content: "Please Fill The Form Correctly."
                    });
                }
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);