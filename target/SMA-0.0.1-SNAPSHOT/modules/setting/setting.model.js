/**
 * Created by Tanuj on 16-05-2019.
 */
/**
 * Created by Tanuj on 15-05-2019.
 */
/**
 * Created by Kundan Kumar on 25-Apr-19.
 */
(function (window, document, $, angular) {

    angular.module('settingApp').factory("settingModel", ["settingSvcs", function (settingSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {

                updateProfile:function(){
                    this.className = "";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.email = "";
                    this.phone = "";
                    this.addressLine1 = "";
                    this.addressLine2 = "";
                    this.dateOfBirth = "";
                    this.alternateEmail = "";
                    this.alternatePhone = "";
                    this.guardianTitle = "";
                    this.guardianFirstName = "";
                    this.guardianLastName = "";
                    this.relationship = "";
                    this.guardianEmail = "";
                    this.guardianPhone = "";
                    this.guardianAddressLine1 = "";
                    this.guardianAddressLine2 = "";
                },

                changePasswordMgm:function(){
                    this.currentPassword = "";
                    this.newPassword = "";
                    this.confirmPassword = "";
                    this.userKey = "";
                    this.className = "EOManagementUser";
                },
                changePasswordTeacher:function(){
                    this.currentPassword = "";
                    this.newPassword = "";
                    this.confirmPassword = "";
                    this.userKey = "";
                    this.className = "EOTeacherUser";
                },
                changePasswordStudent:function(){
                    this.currentPassword = "";
                    this.newPassword = "";
                    this.confirmPassword = "";
                    this.userKey = "";
                    this.className = "EOStudentUser";
                }

            };
            this.create = function (type) {
                if (InstanceFns[type]) {
                    return new InstanceFns[type]();
                }
            };
        }();
        return {
            getInstance: InstanceGenerator.create
        }
    }]);

})(window, window.document, window.jQuery, window.angular);
