/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("notConvertStudentReportCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {


        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        console.log("vishujaaaaaaaaa");



        vm.downloadExcel = function(studentDetails){
            console.log("studentDetails :: ",studentDetails);
            vm.rows = [];
            var count = 0;
            if ((studentDetails.length) !== 0) {
                for (var key in studentDetails) {

                    var tobj = {};
                    tobj.count = ++count;
                    tobj.studentName = studentDetails[key].getFullName;
                    tobj.email = studentDetails[key].mail_id;
                    tobj.phone = studentDetails[key].phone_nmber;
                    tobj.address = studentDetails[key].address;
                    tobj.remarks = studentDetails[key].remarks;
                    tobj.enquiryDate = studentDetails[key].enquiryDate;


                    var header = [];
                    header = _.values(tobj);
                    vm.rows.push(header);

                }
            }
            var excelHeader = ["Sr No.", "Student Name", "Email", "Phone No","ADDRESS" , "REMARKS" ,"ENQUERY DATE"];
            var itrData = vm.rows;
            var sheetName = "Student Not Convert Report";
            var excelName = "Student Not Convert Details Report ";
            //vm.downloadConfig.generateExcel(sheetName, excelName, itrData, excelHeader);
            var excelArray = [];
            var headerRow = ["", "", "", "", "Student Not Convert Report"];
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