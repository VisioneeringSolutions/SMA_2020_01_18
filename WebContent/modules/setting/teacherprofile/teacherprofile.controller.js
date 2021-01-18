/**
 * Created by nikita on 2019-06-06.
 */

(function (window, document, $, angular) {

    var settingApp = angular.module('settingApp');
    //console.log("IN");
    settingApp.controller("teacherProfileCtrl", ["$scope", "$rootScope","settingSvcs", "settingModel",  "$state", "notifySvcs", "localStorage", "http","modalSvcs","teacherData", function ($scope, $rootScope ,settingSvcs, settingModel, $state, notifySvcs, localStorage, http,modalSvcs,teacherData) {
        console.log("update profile");

        $rootScope.pageHeader = "Update";
        $rootScope.pageDescription = "Teacher Profile";

        var vm=this;
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        var imagePK = undefined;

        vm.role = localStorage.get("role");
        console.log("vm.role::",vm.role);

        vm.teacherData = teacherData.teacherList;
        //vm.managementData = teacherData.teacherDataList;
        console.log("update vm.teacherData:::",vm.teacherData);

        vm.getProfileImage = function(){
            var reqMap = {
                userPk : localStorage.get("userKey"),
                className : "eoteacher_user"
            }
            settingSvcs.getProfileImage(reqMap, function(res){
                if(res.data.length > 0){
                    console.log("res.data::",res.data)
                    if(res.data[0].image_url != null){
                        $rootScope.imageUrl = vm.baseImgUrl + res.data[0].image_url;
                        console.log(" scope.imageUrl::"+ $rootScope.imageUrl);
                    }else{
                        $rootScope.imageUrl = "images/avatar.png";
                        console.log(" scope.imageUrl::"+ $rootScope.imageUrl);
                    }

                }else{
                    $rootScope.imageUrl = "images/avatar.png";
                    console.log(" scope.imageUrl::"+ $rootScope.imageUrl);
                }
            });
         }();

        vm.formModel={};
        for(var key in vm.teacherData) {
            vm.formModel.addressLine1=vm.teacherData[key].address_line_1;
            vm.formModel.addressLine2=vm.teacherData[key].address_line_2;
            vm.formModel.email=vm.teacherData[key].email;
            vm.formModel.title=vm.teacherData[key].title;
            vm.eoimage=vm.teacherData[key].eoimage;
            vm.formModel.firstName=vm.teacherData[key].first_name;
            vm.formModel.lastName=vm.teacherData[key].last_name;
            vm.formModel.phone=vm.teacherData[key].phone;
            vm.formModel.imageUrl=vm.teacherData[key].image_url;
            vm.formModel.primaryKey=vm.teacherData[key].primary_key;
            vm.formModel.gender=vm.teacherData[key].gender;
        };
        console.log("formModel vm.formModel ::::",vm.formModel);
        /*for(var key in vm.managementData) {
            vm.imageUrl =vm.managementData[key].imageUrl;
            console.log("imageUrl",   vm.imageUrl);
        }*/

        vm.toggle = function(){
            vm.formPassword = {};
            vm.formPassword = settingModel.getInstance("changePasswordTeacher");
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
                console.log("vm.formPassword::",vm.formPassword);

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
            entityName: "EOTeacherUser",
            className: "EOImage",
            httpPath: "ajax/updateImgObject",
            init: function (elem) {
                if (vm.formModel.imageUrl) {
                    document.getElementById("teacherProfileImage").src = vm.baseImgUrl + vm.formModel.imageUrl;
                    console.log("dooc iD::", document.getElementById("teacherProfileImage").src);
                }
            },
            error: function (elm, scope) {

            },
            success: function (event, file, data, files, element, scope) {

                vm.uploadConfig.upload(function errorCallback(err) {
                        //console.log(err);
                    },
                    function successCallback(result) {
                        imagePK = result.data.primaryKey;
                        console.log("imagePK:",imagePK)
                    });
            }
        };


        vm.formConfig = {
            name: "teacherRegistrationForm",
            submit: function (e) {
                console.log("vm.formModel",vm.formModel);
                if(imagePK != undefined){
                    vm.formModel.eoImage =  imagePK;
                }
                e.preventDefault();
                vm.formModel.className = "EOTeacherUser";
                settingSvcs.updateProfileObject(vm.formModel, function (res) {
                    $state.reload();

                    notifySvcs.success({
                        content: " Teacher Profile Updated successfully"
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