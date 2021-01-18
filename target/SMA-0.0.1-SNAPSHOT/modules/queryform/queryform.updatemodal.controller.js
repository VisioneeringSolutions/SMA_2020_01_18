/**
 * Created by nikita on 2019-06-20.
 */
(function (window, document, $, angular) {

    var queryFormApp = angular.module('queryFormApp');

    queryFormApp.controller("queryFormUpdateModalCtrl", ["$scope", "$rootScope","queryFormSvcs", "queryFormModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata","registrationSvcs", function ($scope, $rootScope, queryFormSvcs, queryFormModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata, registrationSvcs) {
        console.log("In update model");
        var mvm = this;
        var tabMapForFields = [
            ["firstName", "lastName","enquiryDate", "dateOfBirth", "phone", "email"]
        ];
        mvm.formModel = queryFormModel.getInstance("updateStudent");
        mvm.formModel = angular.copy(metadata.lkInstances.StudentQueryData);
        mvm.musicArray = angular.copy(metadata.lkInstances.MusicType);
        mvm.courseList = angular.copy(metadata.lkInstances.CourseList);

        mvm.setMusicType = function () {
            var tempMap = [];
            if (mvm.formModel.coursePk != null) {
                for (var k in mvm.formModel.coursePk) {
                    tempMap.push(mvm.formModel.coursePk[k])
                }
            }
            mvm.formModel.musicCategoryArray = tempMap;
        }();

       /* mvm.courseType = function () {
            var tmpMap = [];
            console.log("mvm.formModel.eoCourses::",mvm.formModel.eoCourses);
            if (mvm.formModel.eoCourses != null) {
                for (var k in mvm.formModel.eoCourses) {
                    tmpMap.push(mvm.formModel.eoCourses[k].primaryKey)
                }
                console.log("tmpMap------if-------:",tmpMap);
            }

            mvm.formModel.courseArray = tmpMap;
            console.log("v.tmpMap.tmpMap::",tmpMap);
        }();*/

        /*for email validation----strt--------- */

        mvm.isValidEmail = function(text){
            if(text){
                mvm.validEmail= /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
                return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
            }

        };
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

        var dateOfBirth = moment(mvm.formModel.dateOfBirth, "DD-MM-YYYY").format("YYYY-MM-DD");
        var enquiryDate = moment(mvm.formModel.enquiryDate, "DD-MM-YYYY").format("YYYY-MM-DD");
        var nextFollowupDate = moment(mvm.formModel.nextFollowupDate, "DD-MM-YYYY").format("YYYY-MM-DD");

        mvm.dateOfBirth = new Date(dateOfBirth);
        mvm.enquiryDate = new Date(enquiryDate);
        mvm.nextFollowupDate = new Date(nextFollowupDate);

        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primary_key',
            labelField: 'music_type',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
            searchField: ['musicType'],
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
        var musicPkList = [];
         mvm.musicDesciption = [];
         var musicDesc=[];
        for(var k in mvm.courseList){

            if(mvm.formModel.musicCategoryArray.includes(mvm.courseList[k].primary_key+"")){
                musicPkList.push(mvm.courseList[k].musicpk+"");
                var description = mvm.courseList[k].description.substring(0, 2);
                if(description === 'T_'){
                    mvm.musicDesciption.push(mvm.courseList[k].primary_key+"");
                   musicDesc.push(mvm.courseList[k].primary_key+"");
                }
            }
        }
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

                    if((Object.keys(mvm.formModel.musicCategoryArray)).length > 0) {

                        if(mvm.enquiryDate != undefined) {

                            mvm.formModel.enquiryDate = moment(mvm.enquiryDate).format("DD-MM-YYYY");

                            if(mvm.dateOfBirth != undefined) {
                                mvm.formModel.dateOfBirth = moment(mvm.dateOfBirth).format("DD-MM-YYYY");
                            }
                            if(mvm.nextFollowupDate != undefined) {
                                mvm.formModel.nextFollowupDate = moment(mvm.nextFollowupDate).format("DD-MM-YYYY");
                            }
                           // console.log("musicDesc:::",musicDesc.length)
                            //var musicPkList = [];
                            var musicDescvall=[];
                            for(var k in mvm.courseList){

                                if(mvm.formModel.musicCategoryArray.includes(mvm.courseList[k].primary_key+"")){
                                    //musicPkList.push(mvm.courseList[k].musicpk+"");
                                    var description = mvm.courseList[k].description.substring(0, 2);
                                    if(description === 'T_'){
                                        musicDescvall.push(mvm.courseList[k].primary_key+"");
                                    }
                                }
                            }
                            var musicVal =[];
                            for(var k in musicDesc){
                                for(var k1 in musicDescvall){
                                   if( musicDesc[k] === musicDescvall[k1]){
                                       musicVal.push(musicDesc[k]);
                                   }
                                }
                            }
                            if(musicVal.length >0 ){
                                mvm.formModel.musicValue = JSON.stringify(musicVal);
                            }
                            else{
                                mvm.formModel.musicValue= '';
                            }
                            if(musicDesc.length > 0 ){
                                mvm.formModel.musicDesc = JSON.stringify(musicDesc);
                            }
                            else{
                                mvm.formModel.musicDesc= '';
                            }

                            mvm.formModel.musicPk = JSON.stringify(musicPkList);
                            mvm.formModel.coursePk = JSON.stringify(mvm.formModel.musicCategoryArray);
                            mvm.formModel.className = 'EOQueryForm';

                            var blankFormInstance = angular.copy(mvm.formModel);
                            delete blankFormInstance.musicCategoryArray;

                            if(mvm.allowPhoneNo == 'allowed' || phoneCheck == false) {
                                if(mvm.validEmail == true ) {
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
                                }  else {
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
                                content:"Please select Enquiry Date."
                            });
                        }
                    } else{
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