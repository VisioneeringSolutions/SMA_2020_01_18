/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    angular.module('competitionForTeachersApp').factory("competitionForTeachersModel", ["competitionForTeachersSvcs", function (competitionForTeachersSvcs) {

        var InstanceGenerator = new function () {
            var InstanceFns = {

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
