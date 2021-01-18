/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("teacherSalaryViewCtrl", ["$scope", "salaryModel","salarySvcs","typeData","notifySvcs","$state","localStorage","studentInvoiceSvcs","modalSvcs", function ($scope, salaryModel,salarySvcs,typeData,notifySvcs,$state,localStorage,studentInvoiceSvcs,modalSvcs) {

        var vm = this;

        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
        vm.duration = typeData.Duration;

        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();
        //console.log("vm.yearArray:",vm.yearArray);
        //console.log("vm.year:",vm.year);

        /*vm.yearArray = [2019];
        vm.getYearArray = function () {
            var val = Date.today().getFullYear() - 2019;
            ////console.log("val:",val);
            for (var k = 1; k <= val; k++) {
                vm.yearArray.push(Date.today().addYears(k).getFullYear());
            }
            vm.yearArray.push(Date.today().addYears(val + 1).getFullYear());
        }();*/

        vm.clearSection = function(){
            vm.year = null;
            vm.respData = null;
        };


        vm.getSelectedDateRange = function () {

            var lastDate = "";
            var month = 0;
            for (var k in vm.monthArray) {
                if (vm.monthArray[k] == vm.month) {
                    month = k;
                    break;
                }
            }
            var monthValue = parseInt(month) + 1;
            if (monthValue <= 10) {
                monthValue = "0" + monthValue;
            }
            var getDaysInMonth = function (month, year) {
                return new Date(year, month, 0).getDate();
            };
            ////console.log("month::",month+1);
            lastDate = getDaysInMonth(parseInt(month) + 1, vm.year);
            var endDate = vm.year + '-' + monthValue + '-' + lastDate;
            var startDate = vm.year + '-' + monthValue + '-' + 01 + '';

            var reqMap = {
                year : vm.year,
                teacherPk : localStorage.get("userKey")
            };
            salarySvcs.getSalaryByTeacherPk(reqMap, function (res) {
                if (res.data.length > 0) {
                    vm.respData = res.data;
                    console.log("teacher vm.respData:", vm.respData);

                }
                else{
                    notifySvcs.info({
                        content:"Salary not generated",
                        delay:1000
                    })
                }
            });
        };
        vm.defaultFunction = function(){
            vm.year = vm.yearArray[1];
            vm.getSelectedDateRange();
        }();

        vm.getRoundOff = function(amount, session){
            return(Math.round(parseFloat(amount) * parseFloat(session)))
        };
        vm.submit = function(data,instance){

            var blankInstanceForm = {};

            blankInstanceForm =  angular.copy(data);
            blankInstanceForm.status = instance;

            delete  blankInstanceForm.SalaryPrimaryKey;
            delete  blankInstanceForm.tempSalary;
            delete  blankInstanceForm.salStatus;
            delete  blankInstanceForm.teacherfullname;
            delete  blankInstanceForm.eoTeacherSalaryPk;
            delete  blankInstanceForm.lkClassDurationType;
            delete  blankInstanceForm.joiningDate;

            //console.log("blankInstanceForm:",blankInstanceForm);
            //return null;
            var message = '';
            if(instance == 'Rejected'){
                message = "Do You want to <span style='color: #F44336; font-weight: 600'>Reject </span>salary of the month " +
                    "<span style='color: #1565C0; font-weight: 600'>"+ data.month + "</span> ?"
            }
            if(instance == 'Verified'){
                message = "Do You want to <span style='color: #1565C0; font-weight: 600'>Verify </span>salary of the month " +
                    "<span style='color: #1565C0; font-weight: 600'>"+ data.month + "</span> ?"
            }
            console.log("blankInstanceForm::",blankInstanceForm);
            bootbox.confirm({
                size: "medium",
                message: message ,
                callback: function(result){

                    if(result == true){
                        salarySvcs.createSalaryByMonthForSlip(blankInstanceForm, function(res){
                            if(res.data == 'Success'){
                                if(blankInstanceForm.status == 'Verified'){
                                    notifySvcs.success({
                                        title: "Success",
                                        content: "Verified",
                                        delay:1000
                                    });
                                }
                                if(blankInstanceForm.status == 'Rejected'){
                                    notifySvcs.info({
                                        title: "Info",
                                        content: "Rejected",
                                        delay:1000
                                    });
                                }

                                vm.getSelectedDateRange ();
                            }
                        });
                    }
                     /* result is a boolean; true = OK, false = Cancel*/ }
            });

        };
        vm.downloadSlip = function (instance) {
            if(instance.status == 'Released'){
                instance.className = "EOGenerateSlip";
                studentInvoiceSvcs.getStudentInvoicePdf(instance, function(res){
                    if((res.data).split('.')[1] == "pdf"){

                        async.parallel({

                            },
                            function (err, results) {
                                modalInstance = modalSvcs.open({
                                    windowClass: "fullHeight",
                                    size: "lg",
                                    lkInstances: results,
                                    title: 'Salary',
                                    data: res.data,
                                    templateUrl: "modules/studentinvoice/invoicepdfviewer/invoicepdfviewer.html",
                                    controller: "invoicePdfViewerCtrl",
                                    controllerAs: "invoicePdfViewer"

                                });
                                modalInstance.rendered.then(function () {
                                    //console.log("modal template rendered");
                                });
                                modalInstance.opened.then(function () {
                                    //console.log("modal template opened");
                                });
                                modalInstance.closed.then(function () {
                                    //console.log("modal template closed");
                                    modalInstance = undefined;
                                });
                            });
                    }
                });
            }
            else{
                notifySvcs.info({
                    content:"Salary is not Released by Admin"
                })
            }
        };


    }]);

})(window, window.document, window.jQuery, window.angular);
