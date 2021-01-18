/**
 * Created by Kundan Kumar on 29-Apr-19.
 */

(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("teacherUpdateModalCtrl", ["$scope", "$rootScope","registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        $rootScope.pageHeader = "Students";
        $rootScope.pageDescription = "Registration";

        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName","enrollmentDate", "phone"]

        ];

        mvm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        var imagePK = undefined;
        mvm.musicCategory = metadata.lkInstances.CourseList;

        mvm.formModel = registrationModel.getInstance("updateTeacher");
        mvm.clearSectionFrom = function () {
            mvm.formModel.courseArray = [];
        }();
        mvm.formModel = angular.copy(metadata.lkInstances.TeacherResponseData);

        /*for email validation----strt--------- */
        mvm.validColorCode = true;
        mvm.isValidEmail = function(text){
            if(text){
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }
        };
        mvm.isValidColorCode = function(code) {
            if(code){
                mvm.validColorCode = /^#(?:[0-9a-fA-F]{3}){1,2}$/.test(code);
                return /^#(?:[0-9a-fA-F]{3}){1,2}$/.test(code);
            }

        };
        /*for email validation------end------- */


        mvm.setValueForMusicCategory = function(){
            mvm.formModel.courseArray = JSON.parse(mvm.formModel.courseArray);
        }();

        mvm.joiningDate = new Date(mvm.formModel.joiningDate);

        mvm.teachTab = true;
        mvm.appSal = false;
        mvm.scrollTop = function(val) {
            if(val == 'teacher-tab') {
                mvm.teachTab = true;
                mvm.appSal = false;
                document.getElementById('personal-form').style.display='block';
                document.getElementById('appSal-form').style.display='none';
                document.getElementById('teacher-tab').style.background='#BF360C';
                document.getElementById('teacher-tab').style.color='#fff';

                document.getElementById('appSal-tab').style.background='#e6e6e6';
                document.getElementById('appSal-tab').style.color='#474747';
            }
            if(val == 'appSal-tab'){
                mvm.teachTab = false;
                mvm.appSal = true;
                document.getElementById('personal-form').style.display='none';
                document.getElementById('appSal-form').style.display='block';
                document.getElementById('appSal-tab').style.background='#BF360C';
                document.getElementById('appSal-tab').style.color='#fff';

                document.getElementById('teacher-tab').style.background='#e6e6e6';
                document.getElementById('teacher-tab').style.color='#474747';
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

        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primary_key',
            labelField: 'course_name',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Course:',
            searchField: ['course_name'],
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

        /*document.getElementById("studentProfileImage").src = mvm.baseImgUrl + metadata.lkInstances.TeacherResponseData.imageUrl;
         ////console.log("dooc iD::", document.getElementById("studentProfileImage").src);*/

        mvm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            primaryKey: metadata.lkInstances.TeacherResponseData.eoImage || null,
            headerPk: metadata.lkInstances.TeacherResponseData.primaryKey,
            entityName: "EOTeacherUser",
            className: "EOImage",
            httpPath: "ajax/updateImgObject",
            init: function (elem) {
                if (metadata.lkInstances.TeacherResponseData.imageUrl) {
                    document.getElementById("studentProfileImage").src = mvm.baseImgUrl + metadata.lkInstances.TeacherResponseData.imageUrl;

                }
            },
            error: function (elm, scope) {
            },
            success: function (event, file, data, files, element, scope) {

                mvm.uploadConfig.upload(function errorCallback(err) {
                        //////console.log(err);
                    },
                    function successCallback(result) {
                        imagePK = result.data.primaryKey;
                    });
            }
        };

        var phoneCheck = false;
        mvm.validatePhoneNo = function(){
            phoneCheck = true;
            var  reqMap = {
                phone : mvm.formModel.phone,
                className : 'EOTeacherUser'
            };
            registrationSvcs.getPhoneDetailsByUserPk(reqMap, function(res){
                if(res.data.primaryKey !='phone not matched' && res.data.className == 'EOTeacherUser'){
                    if(res.data.primaryKey == mvm.formModel.primaryKey){
                        mvm.allowPhoneNo = 'allowed';
                    }
                    if(res.data.primaryKey != mvm.formModel.primaryKey){
                        notifySvcs.info({
                            title: "Phone",
                            delay:3000,
                            content: "Phone No. already exist try another??????"
                        });
                    }
                }
                else{
                    if(res.data.className == 'EOStudentUser'){
                        mvm.allowPhoneNo = 'not allowed';
                        notifySvcs.info({
                            title: "Phone",
                            delay:3000,
                            content: "Phone No. already exist try another??????"
                        });
                    }
                    else{
                        mvm.allowPhoneNo = 'allowed';
                    }
                }
            });
        };


        mvm.showColorList = function(){
            document.onclick = function(e){
                if(e.target.id != 'colorCode' || e.target.id == null){
                    document.getElementById('scroll-bar-3').style.display = "none";
                }else{
                    if(document.getElementById(e.target.parentNode.children[1].id).style.display == ''
                        || document.getElementById(e.target.parentNode.children[1].id).style.display != 'inline-block'){
                        document.getElementById(e.target.parentNode.children[1].id).style.display = "inline-block";
                    }else{
                        document.getElementById(e.target.parentNode.children[1].id).style.display = "none";
                    }
                }
            };
        };

        mvm.hexCodeColorList = mvm.hexCodeColorList = metadata.lkInstances.ColorList;;

        mvm.getColor = function(color){
            var colorValue = {background: color};
            return colorValue;
        };
        mvm.setSavedColor = function(color){
            document.getElementById('colorCode').style.background = color;
        };
        mvm.setColorValue = function(color){
            mvm.formModel.colorCode = color;
            mvm.setSavedColor(color);
        };
        mvm.formSubmit = function () {
            mvm.formConfig.formElement.trigger('submit');
        };

        mvm.formConfig = {
            preCompile: function (e) {
                //////console.log("preCompile ",e);
            },
            postCompile: function (e) {
                //////console.log("postCompile ",e);
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();

                var form = mvm.formConfig.formScope.studentsRegistrationForm;
                if (validationResponse.invalidInputs.length == 0) {
                    var message = "";
                    /*if(mvm.validEmail == false || mvm.validColorCode == false){
                        if(!mvm.validEmail){
                            message = "Please Enter valid Email id.";
                        }else{
                            message = "Please Enter valid Color code"
                        }
                    }*/


                    mvm.formModel.joiningDate = moment(mvm.joiningDate).format("YYYY-MM-DD");
                    mvm.formModel.className =  'EOTeacherUser';
                    if(imagePK != undefined){
                        mvm.formModel.eoImage =  imagePK;
                    }

                    var musicCategoryPk = [];
                    if(!mvm.formModel.courseArray){
                        notifySvcs.info({
                            content:"Please select course"
                        });
                        return
                    }
                    for(var k in mvm.musicCategory){
                        if(mvm.formModel.courseArray.includes(mvm.musicCategory[k].primary_key+"")){
                            musicCategoryPk.push(mvm.musicCategory[k].musicpk+"_"+mvm.musicCategory[k].categorypk+"")
                        }
                    }
                    console.log(JSON.stringify(musicCategoryPk));
                    mvm.formModel.musicCategoryPk = JSON.stringify(musicCategoryPk);
                    var blankFormInstance = angular.copy(mvm.formModel);
                    blankFormInstance['courseArray'] = JSON.stringify(mvm.formModel.courseArray);
                    delete blankFormInstance.imageUrl;
                    //delete blankFormInstance.courseArray;
                    delete blankFormInstance.musicCategoryList;
                    delete blankFormInstance.userName;

                    if(mvm.allowPhoneNo == 'allowed' || phoneCheck == false){
                        if(mvm.validColorCode == true) {
                            if(mvm.formModel.courseArray.length > 0){
                                registrationSvcs.createUser(blankFormInstance, function (res) {

                                    if (res.data == 'Success') {
                                        $uibModalInstance.close();
                                        $state.reload();
                                        notifySvcs.success({
                                            title: "Teacher",
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
                                },function(err){
                                    notifySvcs({
                                        content:"Something went wrong"
                                    })
                                });
                            }
                            else{
                                notifySvcs.error({
                                    title: "Music Category",
                                    content: "Please Select Music Category"
                                });
                            }
                        } else {
                            notifySvcs.info({
                                content: "Please Enter valid Color code"
                            });
                        }
                    }
                    else{
                        notifySvcs.info({
                            title: "Phone",
                            content: "Phone No. already exist try another"
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
        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);
