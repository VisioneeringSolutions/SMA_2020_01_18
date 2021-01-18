/**
 * Created by Kundan on 29-Apr-19.
 */
/**
 * Created by Kundan on 27-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("studentUpdateModalCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "$uibModalInstance", "metadata", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, $uibModalInstance, metadata) {
        $rootScope.pageHeader = "Students";
        $rootScope.pageDescription = "Registration";

        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName", "enrollmentDate", "phone"]
        ];

        mvm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        var imagePK = undefined;

        mvm.formModel = registrationModel.getInstance("updateStudent");
        mvm.formModel = angular.copy(metadata.lkInstances.StudentResponseData);
        mvm.musicArray = angular.copy(metadata.lkInstances.MusicType);
        mvm.courseList = angular.copy(metadata.lkInstances.CourseList);

        /********* email validation----start--------- */
        mvm.isValidEmail = function(text){
            if(text){
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }
        };
        /********* email validation------end------- */

        mvm.setMusicType = function () {
            var tempMap = [];
            if (mvm.formModel.musicCategoryList != null) {
                for (var k in mvm.formModel.musicCategoryList) {
                    tempMap.push(mvm.formModel.musicCategoryList[k].primary_key)
                }
            }
            mvm.formModel.musicCategoryArray = tempMap;
        }();

        mvm.setMusicTypeAndCategory = function () {
            var tempMap = [];
            if (mvm.formModel.musicCategoryArray != null) {
                for (var k in mvm.formModel.musicCategoryArray) {
                    tempMap.push(mvm.formModel.musicCategoryArray[k]+"");
                }
            }
            mvm.formModel.courseArray = tempMap;
            mvm.formModel.courseArrayCopy = angular.copy(tempMap);
            mvm.formModel.courseArrayNewCopy = angular.copy(tempMap);
        }();

        var dateOfBirth = moment(mvm.formModel.dateOfBirth, "DD-MM-YYYY").format("YYYY-MM-DD");
        var enrollmentDate = moment(mvm.formModel.enrollmentDate, "DD-MM-YYYY").format("YYYY-MM-DD");

        mvm.dateOfBirth = new Date(dateOfBirth);
        mvm.enrollmentDate = new Date(enrollmentDate);

        mvm.studTab = true;
        mvm.guardTab = false;
        mvm.scrollTop = function(val) {

            if (val == 'student-tab') {
                mvm.studTab = true;
                mvm.guardTab = false;
                document.getElementById('personal-form').style.display = 'block';
                document.getElementById('guardian-form').style.display = 'none';
                document.getElementById('student-tab').style.background = '#BF360C';
                document.getElementById('student-tab').style.color = '#fff';

                document.getElementById('guardian-tab').style.background = '#e6e6e6';
                document.getElementById('guardian-tab').style.color = '#474747';
            }
            if (val == 'guardian-tab') {
                mvm.studTab = false;
                mvm.guardTab = true;
                document.getElementById('personal-form').style.display = 'none';
                document.getElementById('guardian-form').style.display = 'block';
                document.getElementById('guardian-tab').style.background = '#BF360C';
                document.getElementById('guardian-tab').style.color = '#fff';

                document.getElementById('student-tab').style.background = '#e6e6e6';
                document.getElementById('student-tab').style.color = '#474747';
            }
        };

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
        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'musicType',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
            searchField: ['musicType'],
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

        function diff(arr1, arr2, callback) {
            var filteredArr1 = arr1.filter(function(ele) {
                return arr2.indexOf(ele) == -1;
            });

            var filteredArr2 = arr2.filter(function(ele) {
                return arr1.indexOf(ele) == -1;
            });
            callback(filteredArr1.concat(filteredArr2))
        }


        function checkCourseDeletion(item){
            $scope.$apply(function() {
                diff(item, mvm.formModel.courseArrayCopy, function(res){
                    if(res.length > 0){
                        var categoryPk = "";
                        var musicPk = "";
                        for(var k in mvm.courseList){
                            if(mvm.courseList[k].primary_key+"" == res[0]+""){
                                categoryPk = mvm.courseList[k].categorypk;
                                musicPk = mvm.courseList[k].musicpk;
                                break;
                            }
                        }
                        var reqMap = {
                            studentPk : mvm.formModel.primaryKey+"",
                            categoryPk : categoryPk+"",
                            musicPk : musicPk+""
                        };
                        registrationSvcs.checkCourseDeletionForStudent(reqMap,function(response){
                            if(response.data){
                                mvm.formModel.courseArray = mvm.formModel.courseArrayCopy;
                                notifySvcs.info({
                                    content : "The course is already assigned"
                                })
                            }else{
                                mvm.formModel.courseArrayCopy = item;
                            }
                        });
                    }else{

                    }

                });
            });
        }


        mvm.courseConfig = {
            create: true,
            valueField: 'primary_key',
            labelField: 'course_name',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music & Category:',
            searchField: ['musicCategory'],

            onChange: function(item){
                checkCourseDeletion(item);
            },
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
            primaryKey: metadata.lkInstances.StudentResponseData.eoImage || null,
            headerPk: metadata.lkInstances.StudentResponseData.primaryKey,
            entityName: "EOStudentUser",
            className: "EOImage",
            httpPath: "ajax/updateImgObject",
            init: function (elem) {
                if (metadata.lkInstances.StudentResponseData.imageUrl) {
                    document.getElementById("studentProfileImage").src = mvm.baseImgUrl + metadata.lkInstances.StudentResponseData.imageUrl;
                }
            },
            error: function (elm, scope) {

            },
            success: function (event, file, data, files, element, scope) {

                mvm.uploadConfig.upload(function errorCallback(err) {
                        //console.log(err);
                    },
                    function successCallback(result) {
                        imagePK = result.data.primaryKey;
                    });
            }
        };

        var phoneCheck = false;
        mvm.validatePhoneNo = function () {
            phoneCheck = true;
            var reqMap = {
                phone: mvm.formModel.phone,
                className: 'EOStudentUser'
            };
            registrationSvcs.getPhoneDetailsByUserPk(reqMap, function (res) {
                if (res.data.primaryKey != 'phone not matched' && res.data.className == 'EOStudentUser') {
                    if (res.data.primaryKey == mvm.formModel.primaryKey) {
                        mvm.allowPhoneNo = 'allowed';
                    }
                    if (res.data.primaryKey != mvm.formModel.primaryKey) {
                        mvm.allowPhoneNo = 'not allowed';
                        notifySvcs.info({
                            title: "Phone",
                            delay: 3000,
                            content: "Phone No. already exist try another"
                        });
                    }
                }
                else {
                    if (res.data.className == 'EOTeacherUser') {
                        mvm.allowPhoneNo = 'not allowed';
                        notifySvcs.info({
                            title: "Phone",
                            delay: 3000,
                            content: "Phone No. already exist try another"
                        });
                    }
                    else {
                        mvm.allowPhoneNo = 'allowed';
                    }
                }
            });
        };

        mvm.formSubmit = function () {
            mvm.formConfig.formElement.trigger('submit');
        };

        mvm.formConfig = {
            preCompile: function (e) {
                //console.log("preCompile ",e);
            },
            postCompile: function (e) {
                //console.log("postCompile ",e);
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();

                var form = mvm.formConfig.formScope.studentsRegistrationForm;
                if (validationResponse.invalidInputs.length == 0) {
                    if ((mvm.formModel.courseArray).length > 0) {

                        /*console.log("mvm.courseList:",mvm.courseList);
                         console.log("mvm.formModel.courseArray:",mvm.formModel.courseArray);
                         console.log("mvm.formModel.courseArrayCopy:",mvm.formModel.courseArrayCopy);*/

                        mvm.formModel.musicCategoryArray = [];
                        for(var k in mvm.formModel.courseArray){
                            for(var m in mvm.courseList){
                                if(mvm.formModel.courseArray[k]+"" == mvm.courseList[m].primary_key+""){
                                    mvm.formModel.musicCategoryArray.push(mvm.courseList[m].lkmusictype_primary_key+"");
                                    break;
                                }
                            }
                        }

                        mvm.formModel.enrollmentDate = moment(mvm.enrollmentDate).format("DD-MM-YYYY");
                        mvm.formModel.dateOfBirth = moment(mvm.dateOfBirth).format("DD-MM-YYYY");
                        mvm.formModel.className = 'EOStudentUser';
                        if (imagePK != undefined) {
                            mvm.formModel.eoImage = imagePK;
                        }

                        mvm.formModel.musicPk = JSON.stringify(mvm.formModel.musicCategoryArray);
                        var newCoursePk = [];
                        var oldCoursePk = [];
                        var batchPk = [];
                        for(var k in mvm.formModel.courseArray){
                            if(!mvm.formModel.courseArrayNewCopy.includes(mvm.formModel.courseArray[k])){
                                newCoursePk.push(mvm.formModel.courseArray[k]);
                            }
                        }
                        for(var k in mvm.formModel.courseArrayNewCopy){
                            if(!mvm.formModel.courseArray.includes(mvm.formModel.courseArrayNewCopy[k])){
                                oldCoursePk.push(mvm.formModel.courseArrayNewCopy[k]);
                            }
                        }

                        for(var k in mvm.formModel.batchAndCourse){
                            if(oldCoursePk.includes(mvm.formModel.batchAndCourse[k].eocourses_primary_key+"")){
                                batchPk.push(mvm.formModel.batchAndCourse[k].batchpk+"");
                            }
                        }

                        var blankFormInstance = angular.copy(mvm.formModel);
                        if (blankFormInstance.dateOfBirth == 'Invalid date') {
                            delete blankFormInstance.dateOfBirth;
                        }
                        delete blankFormInstance.imageUrl;
                        delete blankFormInstance.musicCategoryList;
                        delete blankFormInstance.musicCategoryArray;
                        delete blankFormInstance.courseArray;
                        delete blankFormInstance.courseArrayCopy;
                        delete blankFormInstance.courseArrayNewCopy;
                        delete blankFormInstance.userName;
                        delete blankFormInstance.batchAndCourse;

                        if (mvm.allowPhoneNo == 'allowed' || phoneCheck == false) {
                            var courseArray= {
                                courseList : newCoursePk,
                                type : 'update',
                                studentPk : mvm.formModel.primaryKey+""
                            };

                            var toUpdate= {
                                batchArray : batchPk
                            };
                            registrationSvcs.createUser(blankFormInstance, function (res) {
                                    if (res.data == 'Success') {
                                        registrationSvcs.createBatchAndStudentBatch(courseArray, function (resp) {
                                            if (resp.data == "Success") {
                                                registrationSvcs.deleteBatchAndStudentBatch(toUpdate, function(response){
                                                    if (response.data == "Success") {
                                                        notifySvcs.success({
                                                            content: "Updated Successfully"
                                                        });
                                                        $uibModalInstance.close();
                                                        $state.reload();
                                                    }
                                                });
                                            }
                                        }, function (err) {
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

                            /*registrationSvcs.createUser(blankFormInstance, function (res) {

                             if (res.data == 'Success') {
                             $uibModalInstance.close();
                             $state.reload();
                             notifySvcs.success({
                             title: "Student",
                             content: "Added Successfully."
                             });
                             }
                             if (res.data == 'phone no already exist') {
                             notifySvcs.info({
                             title: "Phone",
                             delay: 3000,
                             content: "Phone No. already exist try another"
                             });
                             }
                             },
                             function(err){
                             notifySvcs.error({
                             content: "Something went wrong"
                             })
                             });*/

                        }
                        else{
                            notifySvcs.info({
                                title: "Phone",
                                content: "Phone No. already exist try another"
                            });
                        }
                    }else{
                        notifySvcs.error({
                            content: "Please select Music Type"
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
