/**
 * Created by nikita on 2019-06-06.
 */

(function (window, document, $, angular) {

        var settingApp = angular.module('settingApp');
        settingApp.controller("studentCtrl", ["$scope", "$rootScope","settingSvcs", "settingModel",  "$state", "notifySvcs", "localStorage", "http","modalSvcs","studData", function ($scope, $rootScope ,settingSvcs, settingModel, $state, notifySvcs, localStorage, http,modalSvcs,studData) {
        console.log("update profile");

        $rootScope.pageHeader = "Update";
        $rootScope.pageDescription = "Stud Profile";

        var vm=this;
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        var imagePK = undefined;
        vm.studData = studData.studentList;
        vm.role = localStorage.get("role");
        vm.imageUrl = localStorage.get("imageUrl");
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        console.log("vm.role::",vm.role);

        vm.getProfileImage = function(){
            var reqMap = {
                userPk : localStorage.get("userKey"),
                className : "eostudent_user"
            }
            settingSvcs.getProfileImage(reqMap, function(res){
                 if(res.data.length > 0){
                    if(res.data[0].image_url != null){
                        $rootScope.imageUrl = vm.baseImgUrl + res.data[0].image_url;
                    }else{
                        $rootScope.imageUrl = "images/avatar.png";
                    }

                }else{
                    $rootScope.imageUrl = "images/avatar.png";
                }
            });
        }();

        vm.formModel={};
        for(var key in vm.studData) {
            vm.formModel.addressLine1=vm.studData[key].address_line_1;
            vm.formModel.addressLine2=vm.studData[key].address_line_2;
            vm.formModel.email=vm.studData[key].email;
            vm.eoimage=vm.studData[key].eoimage;
            vm.formModel.firstName=vm.studData[key].first_name;
            vm.formModel.lastName=vm.studData[key].last_name;
            vm.formModel.phone=vm.studData[key].phone;
            vm.formModel.imageUrl=vm.studData[key].image_url;
            vm.formModel.primaryKey=vm.studData[key].primary_key;
            vm.formModel.gender=vm.studData[key].gender;
        }

        vm.toggle = function(){
            vm.formPassword = {};
            vm.formPassword = settingModel.getInstance("changePasswordStudent");
            document.getElementById("confirmPass").style.border = "none";
            document.getElementById("newPass").style.border = "none";

            if(document.getElementById("update-form").style.display == "block"){
                document.getElementById("update-form").style.display = "none";
            }else{
                document.getElementById("update-form").style.display = "block";
            }

            if(document.getElementById("change-pass-form").style.display == "block"){
                document.getElementById("change-pass-form").style.display = "none";
            }else{
                document.getElementById("change-pass-form").style.display = "block";
            }
        };

        vm.matchNewPassword = function(){
            if(vm.formPassword.newPassword != vm.formPassword.confirmPassword){
                document.getElementById("confirmPass").style.border = "1px solid red";
            }else{
                document.getElementById("confirmPass").style.border = "1px solid #07AA00";
                document.getElementById("newPass").style.border = "1px solid #07AA00";
            }
        };

        vm.changePassword =  function(){
            var regexp = /\s/g;

            var flag = false;
            for( var k in vm.formPassword){
                console.log("k::::",vm.formPassword[k]);
                if(regexp.test(vm.formPassword[k])){
                    flag = true;
                }
            }

            if(flag){
                notifySvcs.info({
                    content:"please enter correct password format"
                })
            }
            else{

                vm.formPassword.userKey = localStorage.get("userKey");

                settingSvcs.resetPassword(vm.formPassword , function(res){
                    if(res.data == "Success"){

                        modalInstance = modalSvcs.open({
                            callback: function () {
                            },
                            size: 'small',
                            type:'Confirm',
                            title: 'Password changed successfully !',
                            events: {
                                success: function () {

                                }
                            },
                            templateUrl: "modules/custombootbox/custombootbox.html",
                            controller: "customBootBoxCtrl",
                            controllerAs: "customBootBox"
                        });
                        modalInstance.rendered.then(function () {
                        });
                        modalInstance.opened.then(function () {
                        });
                        modalInstance.closed.then(function () {
                            modalInstance = undefined;
                        });
                        $state.reload();
                    }
                    else{
                        notifySvcs.error({
                            content:"Current Password doesn't match"
                        })
                    }
                });
            }
        };

        vm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            primaryKey:  vm.eoimage || null,
            entityName: "EOStudentUser",
            className: "EOImage",
            httpPath: "ajax/updateImgObject",
            init: function (elem) {
                if ( vm.formModel.imageUrl) {
                    document.getElementById("studstudentProfileImage").src = vm.baseImgUrl + vm.formModel.imageUrl;
                }
            },
            error: function (elm, scope) {

            },
            success: function (event, file, data, files, element, scope) {

                vm.uploadConfig.upload(function errorCallback(err) {

                },
                function successCallback(result) {
                    imagePK = result.data.primaryKey;
                });
            }
        };


        vm.formConfig = {
            name: "studentsRegistrationForm",
            submit: function (e) {

                if(imagePK != undefined){
                    vm.formModel.eoImage =  imagePK;
                }
                e.preventDefault();
                vm.formModel.className = "EOStudentUser";
                delete vm.formModel.phone;

                console.log("vm.formModel::",vm.formModel);

                settingSvcs.updateProfileObject(vm.formModel, function (res) {
                    $state.reload();
                    notifySvcs.success({
                        content: "Profile Updated successfully"
                    });
                }, function (err) {
                    notifySvcs.error({
                        content: "Action not performed!"
                    });
                });
            }
        };


    }]);

})(window, window.document, window.jQuery, window.angular);