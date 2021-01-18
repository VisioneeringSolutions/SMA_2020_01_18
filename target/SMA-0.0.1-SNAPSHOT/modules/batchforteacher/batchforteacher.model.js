/**
 * Created by nikita Saxena on 2019-05-17.
 */


(function (window, document, $, angular) {

    angular.module('batchForTeacherApp').factory("batchForTeacherModel", ["batchForTeacherSvcs", function (batchForTeacherSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                feedback:function(){
                    this.className = "EOTeacherBatch";
                    this.homeWork = "";
                    this.improvementComments = "";
                    this.comments = "";
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
