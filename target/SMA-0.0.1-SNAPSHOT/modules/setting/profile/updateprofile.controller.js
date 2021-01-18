/**
 * Created by Kundan on 29-Apr-19.
 */
/**
 * Created by Kundan on 27-Apr-19.
 */
(function (window, document, $, angular) {

    var settingApp = angular.module('settingApp');

    settingApp.controller("profileCtrl", ["$scope", "$rootScope","settingSvcs", "settingModel",  "$state", "notifySvcs", "localStorage", "http","modalSvcs","studentData", function ($scope, $rootScope ,settingSvcs, settingModel, $state, notifySvcs, localStorage, http,modalSvcs,studentData) {

        $rootScope.pageHeader = "Update";
        $rootScope.pageDescription = "Profile";
        var vm=this;
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        vm.getProfileImage = function(){
                    var reqMap = {
                        userPk : localStorage.get("userKey"),
                        className : "eomanagement_user"
                    }
                    settingSvcs.getProfileImage(reqMap, function(res){
                        if(res.data.length > 0){
                            $rootScope.imageUrl = vm.baseImgUrl + res.data[0].image_url;
                        }else{
                            $rootScope.imageUrl = "images/avatar.png";
                        }
                    });
        }();
        var imagePK = undefined;

        vm.role = localStorage.get("role");
        console.log("role::;",vm.role);

        vm.studentData = studentData.managementList;
        vm.managementData = studentData.dataList;
        vm.formModel={};
        for(var key in vm.studentData) {
            vm.formModel.address1=vm.studentData[key].address_1;
            vm.formModel.address2=vm.studentData[key].address_2;
            vm.formModel.email=vm.studentData[key].email;
            vm.eoimage=vm.studentData[key].eoimage;
            vm.formModel.firstName=vm.studentData[key].first_name;
            vm.formModel.lastName=vm.studentData[key].last_name;
            vm.formModel.phone=vm.studentData[key].phone;
            vm.formModel.prefix=vm.studentData[key].prefix;
            vm.formModel.primaryKey=vm.studentData[key].primary_key;
            vm.formModel.gender=vm.studentData[key].gender;
        }
        for(var key in vm.managementData) {
            vm.imageUrl =vm.managementData[key].imageUrl;

        }
        vm.toggle = function(){
            vm.formPassword = {};
            vm.formPassword = settingModel.getInstance("changePasswordMgm");
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
              document.getElementById("confirmPass").style.border = "1px solid green";
              document.getElementById("newPass").style.border = "1px solid green";
          }
        };
        vm.changePassword =  function(){
            var regexp = /\s/g;
            vm.formPassword.userKey = localStorage.get("userKey");
            var flag = false;
            for( var k in vm.formPassword){
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

                settingSvcs.resetPassword(vm.formPassword , function(res){
                    if(res.data === 'Success'){

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
                            content:res.data
                        })
                    }
                },function(err){
                    notifySvcs.error({
                        content: "Something went wrong."
                    })
                });
            }
        };



        vm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            primaryKey:  vm.eoimage || null,
            entityName: "EOManagementUser",
            className: "EOImage",
            httpPath: "ajax/updateImgObject",
            init: function (elem) {
                if ( vm.imageUrl) {
                    document.getElementById("managementProfileImage").src = vm.baseImgUrl + vm.imageUrl;
                    console.log("dooc iD::", document.getElementById("managementProfileImage").src);
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
                vm.formModel.className = "EOManagementUser";
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