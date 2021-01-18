/**
 * Created by Kundan Kumar on 27-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("studentAddModalCtrl", ["$scope", "$rootScope","registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        $rootScope.pageHeader = "Students";
        $rootScope.pageDescription = "Registration";

        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName","enrollmentDate", "phone","email"]
        ];
        /*for email validation--------start---*/
        mvm.isValidEmail = function(text){
            if(text){
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }
        };
        /*for email  validation  -------end---*/
        console.log("date:",moment(new Date()).format("DDMMYY"));

        mvm.musicList = metadata.lkInstances.MusicType;
        mvm.courseList = metadata.lkInstances.CourseList;

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
                            formatYear: 'yyyy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        mvm.createDateMap = function () {
            return dateMaps.getMap();
        };

        /* --------------end-------------------- */

        mvm.tabConfig = {
            defaultActive: 0,
            mapid: "tabContainer",
            click: function (clickedIndex) {
                if (parseInt(clickedIndex) > parseInt(mvm.tabConfig.defaultActive)) {
                    mvm.validateTabSectionFieldsFn(mvm.tabConfig.defaultActive, function (status) {
                        if (status) {
                            mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                        }
                    });
                } else {
                    mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                }
            }
        };
        mvm.validateTabSectionFieldsFn = function (tabIndex, callback) {
            var formInputs = mvm.formConfig.vinputs;
            var formSelects = mvm.formConfig.vselect;
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

        mvm.formModel = registrationModel.getInstance("addStudent");
        mvm.clearSectionFrom = function () {
            mvm.formModel.musicCategoryArray = {};
        }();
        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'musicType',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
            searchField: ['musicCategory'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.musicType ? '<span class="subjectTitle">' + escape(item.musicType) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="subjectTitle">' + escape(item.musicType) + '</span>' + '</div>';
                }
            }
        };

        mvm.courseConfig = {
            create: true,
            valueField: 'primary_key',
            labelField: 'course_name',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music & Category:',
            searchField: ['musicCategory'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.course_name ? '<span class="subjectTitle">' + escape(item.course_name) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="subjectTitle">' + escape(item.course_name) + '</span>' + '</div>';
                }
            }
        };

        mvm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            entityName: "EOStudentUser",
            className: "EOImage",
            httpPath: "ajax/createImgObject",

            error: function (elm, scope) {
            },
            success: function (event, file, data, files, element, scope) {
                mvm.uploadConfig.upload(function errorCallback(err) {
                    },
                    function successCallback(result) {
                        mvm.formModel.eoImage = result.data.primaryKey + "";
                    });
            }
        };

        mvm.setId = function(){
            if(mvm.formModel.firstName == undefined && mvm.formModel.lastName != undefined){
                mvm.formModel.studentId = mvm.formModel.lastName.charAt(0);
            }
            if(mvm.formModel.lastName == undefined && mvm.formModel.firstName != undefined){
                mvm.formModel.studentId = mvm.formModel.firstName.charAt(0);
            }
            if(mvm.formModel.firstName != undefined && mvm.formModel.lastName != undefined){
                mvm.formModel.studentId = (mvm.formModel.firstName != undefined ? mvm.formModel.firstName.charAt(0) : "")
                    + (mvm.formModel.lastName != undefined ? mvm.formModel.lastName.charAt(0): "");
            }
            if(mvm.formModel.firstName == undefined && mvm.formModel.lastName == undefined){
                mvm.formModel.studentId = "";
            }
            //mvm.setUserName();
        };

        mvm.setUserName = function(){
            var primaryKey = "";
            var reqMap = {
                className: "eostudent_user"
            };
            registrationSvcs.getMaxPrimaryKey(reqMap, function(res){
                if(res.data){
                    if(res.data.max == null){
                        primaryKey = 1+"";
                    }else{
                        primaryKey = (parseInt(res.data.max) + 1)+"";
                    }
                    if(mvm.formModel.firstNameJap == undefined){
                        mvm.formModel.userName = '';
                    }else{
                        mvm.formModel.userName = mvm.formModel.firstNameJap + moment(new Date()).format("YYDDMM");
                        mvm.formModel.userName2 = mvm.formModel.firstName + '_' + mvm.formModel.lastName + moment(new Date()).format("YYDDMM");
                    }
                }
            },function(err){
                notifySvcs.error({
                    content: "something went wrong"
                })
            });
        };
        mvm.validateUserName = function(){
            mvm.isUserName = false;
            var reqMap = {
                userName : mvm.formModel.userName
            };
            registrationSvcs.validateUserName(reqMap, function(res){
                if(res.data == 'user name exists'){
                    mvm.isUserName = false;
                    notifySvcs.info({
                        title: "User Name",
                        content: "username is already taken"
                    });
                }else{
                    mvm.isUserName = true;
                }
            },function(err){
                notifySvcs.info({
                    content: "something went wrong"
                });
            })
        };

        mvm.validatePhoneNo = function(){
            var  reqMap = {
                phone : mvm.formModel.phone
            };
            registrationSvcs.getPhoneDetailsByUserPk(reqMap, function(res){
                if(res.data.primaryKey =='phone not matched'){
                    mvm.allowPhoneNo = 'allowed';
                }
                else{
                    mvm.allowPhoneNo = 'not allowed';
                    notifySvcs.info({
                        title: "Phone",
                        content: "Phone No. already exist try another"
                    });
                }
            });
        };

        mvm.formSubmit = function () {
            mvm.formConfig.formElement.trigger('submit');
        };

        mvm.formConfig = {
            preCompile: function (e) {

            },
            postCompile: function (e) {
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {
                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();
                var form = mvm.formConfig.formScope.studentsRegistrationForm;
                mvm.formModel.musicCategoryArray = [];
                //console.log("course array::",mvm.formModel.courseArray);
                if (validationResponse.invalidInputs.length == 0) {
                    for(var k in mvm.formModel.courseArray){
                        for(var m in mvm.courseList){
                            if(mvm.formModel.courseArray[k]+"" == mvm.courseList[m].primary_key+""){
                                mvm.formModel.musicCategoryArray.push(mvm.courseList[m].lkmusictype_primary_key+"");
                                break;
                            }
                        }
                    }

                    if((Object.keys(mvm.formModel.musicCategoryArray)).length > 0){
                        if(mvm.enrollmentDate != undefined){

                            mvm.formModel.enrollmentDate = moment(mvm.enrollmentDate).format("DD-MM-YYYY");
                            if(mvm.dateOfBirth != undefined){
                                mvm.formModel.dateOfBirth = moment(mvm.dateOfBirth).format("DD-MM-YYYY");
                            }
                            mvm.formModel.musicPk = JSON.stringify(mvm.formModel.musicCategoryArray);
                            var blankFormInstance = angular.copy(mvm.formModel);
                            delete blankFormInstance.musicCategoryArray;
                            delete blankFormInstance.courseArray;

                            var courseArray = {
                                courseList : angular.copy(mvm.formModel.courseArray),
                                type : 'create'
                            };

                            if(mvm.isUserName == true){

                                registrationSvcs.createUser(blankFormInstance, function (res) {
                                        if (res.data == 'Success'){
                                            registrationSvcs.createBatchAndStudentBatch(courseArray, function(res){
                                                if(res.data == "Success"){
                                                    notifySvcs.success({
                                                        content:"Registered Successfully"
                                                    });
                                                    $uibModalInstance.close();
                                                    $state.reload();
                                                }
                                            },function (err) {
                                                notifySvcs.error({
                                                    content: "Something went wrong"
                                                })
                                            });
                                        }
                                    },
                                    function (err) {
                                        notifySvcs.error({
                                            content: "Something went wrong"
                                        })
                                        });

                            }
                            else{
                                notifySvcs.info({
                                    content: "username is already taken"
                                });
                            }
                        }
                        else{
                            notifySvcs.error({
                                content:"Please select Enrollment Date."
                            })
                        }
                    }else{
                        notifySvcs.error({
                            content:"Please select Music Type"
                        })
                    }
                }
                else {
                    notifySvcs.error({
                        content: "Please Fill The Form Correctly."
                    });
                }
            }
        };
        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);
