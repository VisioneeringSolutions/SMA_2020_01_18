/**
 * Created by nikita on 2019-06-14.
 */

(function (window, document, $, angular) {

    angular.module('trialStudentsApp').factory("trialStudentsModel", ["trialStudentsSvcs", function (trialStudentsSvcs) {

        var InstanceGenerator = new function () {
            var InstanceFns = {
                queryForm:function() {
                    this.className = "EOQueryForm";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.enquiryDate = "";
                    this.dateOfBirth = "";
                    this.email = "";
                    this.isVisible = false;
                    this.phone = "";
                    this.address = "";
                    this.remarks = "";
                    this.musicPk = "";
                    this.nextFollowupDate = "";
                    this.eoCourses = "";
                },
                updateStudent:function() {
                    this.className = "EOQueryForm";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.enquiryDate = "";
                    this.dateOfBirth = "";
                    this.email = "";
                    this.isVisible = "";
                    this.phone = "";
                    this.address = "";
                    this.remarks = "";
                    this.musicPk = "";
                    this.nextFollowupDate = "";
                    this.eoCourses = "";
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
