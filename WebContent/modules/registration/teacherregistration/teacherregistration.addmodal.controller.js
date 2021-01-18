/**
 * Created by Kundan on 27-Apr-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("teacherAddModalCtrl", ["$scope", "$rootScope","registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        $rootScope.pageHeader = "Students";
        $rootScope.pageDescription = "Registration";

        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName","joiningDate", "phone"]

        ];
        /*for email validation----start--------- */
        mvm.isValidEmail = function(text) {
            if(text){
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }
        };
        /*mvm.isValidColorCode = function(code) {
         if(code){
         mvm.validColorCode = /^#(?:[0-9a-fA-F]{3}){1,2}$/.test(code);
         return /^#(?:[0-9a-fA-F]{3}){1,2}$/.test(code);
         }

         };*/
        /*for email validation------end------- */


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


        mvm.formModel = registrationModel.getInstance("addTeacher");

        mvm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            entityName: "EOTeacherUser",
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
                        delay:3000,
                        content: "Phone No. already exist try another"
                    });
                }
            });
        };

        mvm.setId = function(){
            if(mvm.formModel.firstName == undefined && mvm.formModel.lastName != undefined){
                mvm.formModel.teacherId = mvm.formModel.lastName.charAt(0);
            }
            if(mvm.formModel.lastName == undefined && mvm.formModel.firstName != undefined){
                mvm.formModel.teacherId = mvm.formModel.firstName.charAt(0);
            }
            if(mvm.formModel.firstName != undefined && mvm.formModel.lastName != undefined){
                mvm.formModel.teacherId = (mvm.formModel.firstName != undefined ? mvm.formModel.firstName.charAt(0) : "")
                    + (mvm.formModel.lastName != undefined ? mvm.formModel.lastName.charAt(0): "");
            }

            if(mvm.formModel.firstName == undefined && mvm.formModel.lastName == undefined){
                mvm.formModel.teacherId = "";
            }
            mvm.setUserName();
        };

        mvm.setUserName = function(){
            var primaryKey = "";
            var reqMap = {
                className: "eoteacher_user"
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

           /* if(mvm.formModel.phone == undefined){
                mvm.formModel.userName = '';
            }else{
                mvm.formModel.userName = mvm.formModel.phone;
            }*/
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


        mvm.showColorList = function(){
            document.onclick = function(e){
                if(e.target.id != 'colorCode' || e.target.id == null){
                    if(document.getElementById('scroll-bar-3') != null){
                        document.getElementById('scroll-bar-3').style.display = "none";
                    }
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

        mvm.hexCodeColorList = metadata.lkInstances.ColorList;

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
                ////console.log("preCompile ",e);
            },
            postCompile: function (e) {
                ////console.log("postCompile ",e);
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {
                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();

                var form = mvm.formConfig.formScope.studentsRegistrationForm;
                if (validationResponse.invalidInputs.length == 0) {
                    var message = "";
                    if(mvm.validEmail == false ){
                        if(!mvm.validEmail){
                            message = "Please Enter valid Email id.";
                        }else{
                            message = "Please Enter valid Color code"
                        }

                    }
                    if(mvm.joiningDate != undefined){
                        mvm.formModel.joiningDate = moment(mvm.joiningDate).format("YYYY-MM-DD");

                        var blankFormInstance = angular.copy(mvm.formModel);
                        if(mvm.isUserName == true){
                            if(mvm.validEmail == true) {
                                registrationSvcs.createUser(blankFormInstance, function (res) {

                                    if (res.data == 'Success') {
                                        $uibModalInstance.close();
                                        $state.reload();
                                        notifySvcs.success({
                                            title: "Teacher",
                                            content: "Added Successfully."
                                        });
                                    }
                                },function (err) {
                                    notifySvcs.error({
                                        content: "Something went wrong"
                                    })
                                });
                            }else{
                                notifySvcs.info({
                                    content: message
                                });
                            }
                        }
                        else{
                            notifySvcs.info({
                                content: "username is already taken"
                            });
                        }
                    }
                    else{
                        notifySvcs.error({
                            content:"Please choose JoiningDate Date."
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
