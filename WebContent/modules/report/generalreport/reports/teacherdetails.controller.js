/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("teacherDetailsCtrl", ["$scope", "$rootScope","reportSvcs","reportModel", "$state", "notifySvcs", "localStorage", "$timeout","modalSvcs","registrationSvcs", function ($scope, $rootScope, reportSvcs, reportModel,$state, notifySvcs, localStorage, $timeout,modalSvcs,registrationSvcs) {
        var vm = this;

        vm.yearArray = window.sInstances.yearArray;

        vm.downloadExcel = function(teacherDetails){
            console.log("studentDetails :: ",teacherDetails);
            vm.rows = [];
            var count = 0;
            if ((teacherDetails.length) !== 0) {
                for (var key in teacherDetails) {

                    var tobj = {};
                    tobj.count = ++count;
                    tobj.teacherFullName = teacherDetails[key].teacherFullName;
                    tobj.teacherId = teacherDetails[key].teacherId;
                    tobj.joiningDate = teacherDetails[key].joiningDate;
                    tobj.musicCategory = teacherDetails[key].musicCategory != null ? teacherDetails[key].musicCategory : '';
                   /* tobj.email = teacherDetails[key].email != null ? teacherDetails[key].email : '';
                    tobj.phone = teacherDetails[key].phone != null ? teacherDetails[key].phone : '';
                    tobj.alternatePhone = teacherDetails[key].alternatePhone != null ? teacherDetails[key].alternatePhone : '';
                    tobj.profile = teacherDetails[key].profile != null ? teacherDetails[key].profile : '';
                    tobj.qualification = teacherDetails[key].qualification != null ? teacherDetails[key].qualification : '';
                    tobj.experience = teacherDetails[key].experience != null ? teacherDetails[key].experience : '';
                    tobj.address = teacherDetails[key].addressLine1 != null ? (teacherDetails[key].addressLine2 != null ?
                        teacherDetails[key].addressLine1+' '+teacherDetails[key].addressLine2 : teacherDetails[key].addressLine1 ): '';*/




                    var header = [];
                    header = _.values(tobj);
                    vm.rows.push(header);

                }
            }
            var excelHeader = ["Sr No.", "Teacher Name", "Teacher ID", "Joining Date", "Music Category"/*, "Email","Phone","Alternate Phone","Profile","Qualification","Experience","Address"*/];
            var itrData = vm.rows;
            var sheetName = "Teacher Report";
            var excelName = "Teacher Details Report ";
            //vm.downloadConfig.generateExcel(sheetName, excelName, itrData, excelHeader);
            var excelArray = [];
            var headerRow = ["", "", "", "", "Teacher Details Report"];
            excelArray.push(headerRow);
            excelArray.push(excelHeader);
            //excelArray.push(headerRow11);
            for (var key in itrData) {
                excelArray.push(itrData[key]);
            }

            var ep = new ExcelPlus();
            ep.createFile(sheetName)
                .write({"content": excelArray})
                .saveAs(excelName);
        };
        vm.viewDetailsModel = function(d) {
            console.log("in model::::::::::::::::::::::::::");
            console.log("d:",d);
            async.parallel({
                TeacherViewDetails: function (callback) {
                    var reqMap = {
                        eoTeacherPK : d.primaryKey + ""
                    };
                    registrationSvcs.getTeacherUserByPk(reqMap,function(res) {
                        vm.teacherData = res.data;
                        console.log("vm.teacherData:::::",vm.teacherData);
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                }
            },function (err, results) {
                    modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    templateUrl: "modules/report/generalreport/reports/teacherdetailsviewmodel.html",
                    controller: "teacherDetailsViewModelCtrl",
                    controllerAs: "teacherDetailsViewModel"
                });
                modalInstance.rendered.then(function () {
                });
                modalInstance.opened.then(function () {
                });
                modalInstance.closed.then(function () {
                    modalInstance = undefined;
                });
            });

        };
    
    }]);

})(window, window.document, window.jQuery, window.angular);