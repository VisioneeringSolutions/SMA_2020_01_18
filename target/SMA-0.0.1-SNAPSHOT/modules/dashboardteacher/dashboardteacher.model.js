/**
 * Created by Kundan Kumar on 25-Apr-19.
 */
(function (window, document, $, angular) {

    angular.module('dashBoardTeacherApp').factory("dashBoardTeacherModel", ["dashBoardTeacherSvcs", function (manageslotsSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                addStudent:function(){
                    this.className = "EOStudentUser";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.enrollmentDate = "";
                    this.dateOfBirth = "";
                    this.email = "";
                    this.phone = "";
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
