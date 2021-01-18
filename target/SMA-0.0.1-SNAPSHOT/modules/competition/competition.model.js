/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    angular.module('competitionApp').factory("competitionModel", ["competitionSvcs", function (competitionSvcs) {

        var InstanceGenerator = new function () {
            var InstanceFns = {
                competition:function() {
                    this.className = "EOCompetition";
                    this.name = "";
                    this.competitionDate = "";
                    this.details = "";
                    this.prerequisite = "";
                    this.organizingAuthority = "";
                    this.venue = "";
                    this.brochureUrl = "";
                    this.musicPk = "";
                    /*this.studentsPk = "";
                    this.teachersPk = "";*/
                    this.eoCompetitionMapping = [];

                },
                updateCompetition:function() {
                    this.className = "EOCompetition";
                    this.name = "";
                    this.competitionDate = "";
                    this.details = "";
                    this.prerequisite = "";
                    this.organizingAuthority = "";
                    this.venue = "";
                    this.brochureUrl = "";
                    this.musicPk = "";
                    /*this.studentsPk = "";
                    this.teachersPk = "";*/

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
