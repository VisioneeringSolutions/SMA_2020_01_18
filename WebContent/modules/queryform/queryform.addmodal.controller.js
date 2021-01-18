/**
 * Created by nikita on 2019-06-14.
 */
(function (window, document, $, angular) {

    var queryFormApp = angular.module('queryFormApp');

    queryFormApp.controller("queryAddModalCtrl", ["$scope", "$rootScope","queryFormSvcs", "queryFormModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata","registrationSvcs", function ($scope, $rootScope, queryFormSvcs, queryFormModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata, registrationSvcs) {
        $rootScope.pageHeader = "Query";
        $rootScope.pageDescription = "Form";

        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName","enquiryDate", "phone", "email"]
        ];

        /*for email validation--------start---*/
        mvm.isValidEmail = function(text){
            //console.log("text:",text);
            if(text){
                //console.log("iffff");
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                //console.log("mvm.validEmail:",mvm.validEmail);
                //console.log("return :: ",/^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text) )
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }

        };
        /*for email  validation  -------end---*/

        mvm.musicList = metadata.lkInstances.MusicType;
        mvm.courseList = metadata.lkInstances.CourseList;

        mvm.formModel = queryFormModel.getInstance("queryForm");

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

        mvm.clearSectionFrom = function () {
            mvm.formModel.musicCategoryArray = {};
        }();
        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primary_key',
            labelField: 'music_type',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
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
        mvm.validatePhoneNo = function() {
            var  reqMap = {
                phone : mvm.formModel.phone
            };
            registrationSvcs.getPhoneDetailsByUserPk(reqMap, function(res){
                if(res.data.primaryKey =='phone not matched'){
                    mvm.allowPhoneNo = 'allowed';
                }
                else {
                    mvm.allowPhoneNo = 'not allowed';
                    notifySvcs.info({
                        title: "Phone",
                        content: "Phone No. already exist try another"
                    });
                }
            });
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
                    if(mvm.formModel.phone == undefined){
                        mvm.formModel.userName = '';
                    }else{
                        mvm.formModel.userName = mvm.formModel.phone + "_S" + primaryKey;
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
                var form = mvm.formConfig.formScope.queryForm;
                if (validationResponse.invalidInputs.length == 0) {
                    if ((Object.keys(mvm.formModel.musicCategoryArray)).length > 0) {

                        if (mvm.enquiryDate != undefined) {
                            mvm.formModel.enquiryDate = moment(mvm.enquiryDate).format("DD-MM-YYYY");
                            if (mvm.dateOfBirth != undefined) {
                                mvm.formModel.dateOfBirth = moment(mvm.dateOfBirth).format("DD-MM-YYYY");
                            }
                            if (mvm.nextFollowupDate != undefined) {
                                mvm.formModel.nextFollowupDate = moment(mvm.nextFollowupDate).format("DD-MM-YYYY");
                            }

                            var musicPkList = [];
                            var musicDesciption = [];
                            //console.log("mvm.courseList:",mvm.courseList);
                            //console.log("mvm.formModel.musicCategoryArray:",mvm.formModel.musicCategoryArray);

                            for(var k in mvm.courseList){

                                if(mvm.formModel.musicCategoryArray.includes(mvm.courseList[k].primary_key+"")){
                                    musicPkList.push(mvm.courseList[k].musicpk+"");
                                    var description = mvm.courseList[k].description.substring(0, 2);
                                    if(description === 'T_'){
                                        musicDesciption.push(mvm.courseList[k].primary_key+"");
                                    }

                                    // Kundan
                                    if(mvm.courseList[k].description.match("_T") != null ||
                                        mvm.courseList[k].description.match("T_") != null){
                                        musicDesciption.push(mvm.courseList[k].primary_key+"");
                                    }
                                }
                            }
                            mvm.formModel.musicPk = JSON.stringify(musicPkList);
                            if(musicDesciption.length>0){

                            mvm.formModel.musicDesc = JSON.stringify(musicDesciption);
                            }
                            else{
                                mvm.formModel.musicDesc = '';
                            }
                            mvm.formModel.coursePk = JSON.stringify(mvm.formModel.musicCategoryArray);
                            var blankFormInstance = angular.copy(mvm.formModel);
                            delete blankFormInstance.musicCategoryArray;
                            if (blankFormInstance.eoCourses == '' || blankFormInstance.eoCourses == null) {
                                delete  blankFormInstance.eoCourses;
                            }
                            if (blankFormInstance.dateOfBirth == '' || blankFormInstance.dateOfBirth == null) {
                                delete  blankFormInstance.dateOfBirth;
                            }
                            if (blankFormInstance.nextFollowupDate == '' || blankFormInstance.nextFollowupDate == null) {
                                delete  blankFormInstance.nextFollowupDate;
                            }
                            //console.log("blankFormInstance:::",blankFormInstance);
                            //return
                            if (mvm.allowPhoneNo == 'allowed') {
                                if (mvm.validEmail == true) {
                                    queryFormSvcs.createQueryForm(blankFormInstance, function (res) {
                                            if (res.data == 'Success') {
                                                $uibModalInstance.close();
                                                $state.reload();
                                                notifySvcs.success({
                                                    title: "Form",
                                                    content: "Added Successfully."
                                                });
                                            }
                                        },
                                        function (err) {
                                            notifySvcs.error({
                                                content: "Something went wrong"
                                            })
                                        });
                                } else {
                                    notifySvcs.info({
                                        title: "Email",
                                        content: "Please Enter Valid Email id..."
                                    });
                                }
                            }
                            else {
                                notifySvcs.info({
                                    title: "Phone",
                                    content: "Phone No. already exists try another"
                                });
                            }
                        }
                        else {
                            notifySvcs.info({
                                title: "Phone",
                                content: "Please select Enquiry Date."
                            });
                        }
                    } else {
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