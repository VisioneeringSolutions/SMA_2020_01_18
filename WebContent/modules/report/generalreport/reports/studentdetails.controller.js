/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("studentDetailsCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {


        var vm = this;

        vm.yearArray = window.sInstances.yearArray;


        vm.downloadExcel = function(studentDetails){
            console.log("studentDetails :: ",studentDetails);
            vm.rows = [];
            var count = 0;
            if ((studentDetails.length) !== 0) {
                for (var key in studentDetails) {

                    var tobj = {};
                    tobj.count = ++count;
                    tobj.studentName = studentDetails[key].studentfullName;
                    tobj.enrollmentDate = studentDetails[key].enrollmentDate;
                    tobj.email = studentDetails[key].email;
                    tobj.phone = studentDetails[key].phone;
                    tobj.studentId = studentDetails[key].studentId;
                    tobj.courseName = studentDetails[key].courseName != null ? studentDetails[key].courseName : "";
                    tobj.musicType = studentDetails[key].musicType != null ? studentDetails[key].musicType : "";

                    var header = [];
                    header = _.values(tobj);
                    vm.rows.push(header);

                }
            }
            var excelHeader = ["Sr No.", "Student Name", "Enrollment Date", "Email", "Phone No", "Student ID","Courses","Music Type"];
            var itrData = vm.rows;
            var sheetName = "Student Report";
            var excelName = "Student Details Report ";
            //vm.downloadConfig.generateExcel(sheetName, excelName, itrData, excelHeader);
            var excelArray = [];
            var headerRow = ["", "", "", "", "Student Details Report"];
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

        //console.log("studentDetails")

    }]);

})(window, window.document, window.jQuery, window.angular);