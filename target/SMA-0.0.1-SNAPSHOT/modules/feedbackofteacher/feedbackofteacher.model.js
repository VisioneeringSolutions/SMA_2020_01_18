/**
 * Created by nikita on 2019-05-29.
 */
/**
 * Created by nikita Saxena on 2019-05-17.
 */


(function (window, document, $, angular) {

    angular.module('feedbackOfTeacherApp').factory("feedbackOfTeacherModel", ["feedbackOfTeacherSvcs", function (feedbackOfTeacherSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                /*feedback:function(){
                    this.className = "EOTeacherBatch";
                    this.homeWork = "";
                    this.improvementComments = "";
                    this.comments = "";
                },
                ratings:function(){
                    this.className = "EOStudentCriteria";
                    this.optedRating = "";
                    /!*this.batchPk = "";
                     this.studentsPk = "";
                     this.teacherPk = "";*!/

                }*/
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
