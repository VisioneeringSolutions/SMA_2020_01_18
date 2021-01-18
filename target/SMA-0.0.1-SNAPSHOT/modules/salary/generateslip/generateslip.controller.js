/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("generateSlipCtrl", ["$scope", "salaryModel","salarySvcs","typeData","notifySvcs","$state","localStorage","modalSvcs","studentInvoiceSvcs", function ($scope, salaryModel,salarySvcs,typeData,notifySvcs,$state,localStorage,modalSvcs,studentInvoiceSvcs) {

        var vm = this;

        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
        vm.duration = typeData.Duration;

        vm.address1 = localStorage.get("address1");
        vm.address2 = localStorage.get("address2");
        vm.phone = localStorage.get("phone");
        //console.log("vm.duration::",vm.duration);
        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();
        var endDate = "";
        var startDate = "";
        vm.clearSection = function(){
            vm.month = null;
            vm.respData = null;
        };
        vm.roundOffValue = function (value) {
            //Math.round(value);
            return  Math.round(value);
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
            if (monthValue < 10) {
                monthValue = "0" + monthValue;
            }
            var getDaysInMonth = function (month, year) {
                return new Date(year, month, 0).getDate();
            };
            ////console.log("month::",month+1);
            lastDate = getDaysInMonth(parseInt(month) + 1, vm.year);
            endDate = vm.year + '-' + monthValue + '-' + lastDate;
            startDate = vm.year + '-' + monthValue + '-' + '01' + '';

            var reqMap = {
                month : vm.month,
                year : vm.year,
                startDate : startDate,
                endDate : endDate
            };
            salarySvcs.getTeacherForSalaryByMonth(reqMap, function (res) {
                if (res.data) {
                    vm.respData = res.data;
                    vm.getTotalSalary();
                }
            });
        };

        vm.defaultFunction = function(){
            var monthIndex = moment((Date.today()).addDays(0)).format("M");
            vm.month = vm.monthArray[monthIndex - 1];
            vm.getSelectedDateRange();
        }();

        vm.getButtonType = function (data ) {
            if(data.status == null){
                return "btn-custom-sm btn-custom-warning font-14"
            }if(data.status == 'Created'){
                return "btn-custom-sm btn-custom-primary font-14"
            }if(data.status == 'Verified'){
                return "btn-custom-sm btn-custom-success font-14"
            }if(data.status == 'Rejected'){
                return "btn-custom-sm btn-custom-danger font-14"
            }if(data.status == 'Generated'){
                return "btn-custom-sm btn-custom-voilet font-14"
            }
        };

        vm.getTotalSalary = function(){
            //console.log("vm.respData::",vm.respData);
            for(var m in vm.respData){
                vm.respData[m].tempSalary = 0;
                if (vm.respData[m].primaryKey == null) {
                    if (vm.respData[m].salaryType == 'Per month') {
                        vm.respData[m].tempSalary = vm.respData[m].amount;
                        vm.respData[m].transport = vm.respData[m].transportDays * vm.respData[m].transportAmount;
                        vm.respData[m].totalSalary = vm.respData[m].transport +vm.respData[m].amount;
                    }
                    if (vm.respData[m].salaryType == 'Per session' && vm.respData[m].courseData) {
                        for(var k in vm.respData[m].courseData){
                            if(vm.respData[m].courseData[k].totalMins != null){
                                vm.respData[m].tempSalary += Math.round((vm.respData[m].courseData[k].amount * vm.respData[m].courseData[k].session)
                                    - vm.respData[m].courseData[k].cancellationAmount);
                            }
                        }
                        vm.respData[m].transport = vm.respData[m].transportDays * vm.respData[m].transportAmount;
                        vm.respData[m].totalSalary = Math.round((vm.respData[m].transport) + vm.respData[m].tempSalary);
                    }
                }
                else {
                    vm.respData[m].tempSalary = Math.round(vm.respData[m].totalSalary - (vm.respData[m].transportDays * vm.respData[m].transportAmount) - vm.respData[m].add + vm.respData[m].sub);
                    vm.respData[m].transport = vm.respData[m].transportDays * vm.respData[m].transportAmount;
                    //vm.respData[m].totalSalary = (vm.respData[m].transport) + vm.respData[m].tempSalary;
                }
            }
        };

        vm.setAdditionalAmount = function(data){
            if(data.add == '' || data.add == null){
                data.add = 0;
            }
            if(data.sub == '' || data.sub == null){
                data.sub = 0;
            }
            data.totalSalary = data.tempSalary + data.transport + parseInt(data.add) - parseInt(data.sub);
        };

        vm.setUpdatedDuration = function(data){
            if(data.updatedDuration == ''){
                data.updatedDuration = null;
            }
        };

        vm.released = function(data){
            data.className = "EOGenerateSlip";
            studentInvoiceSvcs.getStudentInvoicePdf(data, function(res){
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
        };

        vm.print = function(data, view){
            data["startDate"] = startDate;
            data["endDate"] = endDate;
            if(view) data["isViewPdf"] = true;
            else data["isViewPdf"] = false;
            salarySvcs.createSalaryPdf(data, function(res){
                if((res.data).split('.')[1] == "pdf"){
                    notifySvcs.success({
                        content:"Pdf generated successfully"
                    });
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
        };

        vm.viewData = function(data, viewType){
            console.log("data::",data);
            if(viewType === 'viewPdf'){
                // Show PDF
            }else{
                // Show slod details by student name
                if(data.studentList){
                    async.parallel({

                        },
                        function (err, results) {
                            modalInstance = modalSvcs.open({
                                windowClass: "fullHeight",
                                size: "lg",
                                lkInstances: results,
                                title: 'Salary',
                                data: data.studentList,
                                month: vm.month,
                                year: vm.year,
                                templateUrl: "modules/salary/detailedsalaryview/detailedsalaryview.html",
                                controller: "detailedSalaryViewCtrl",
                                controllerAs: "detailedSalaryView"
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
                }else{
                    notifySvcs.info({
                        content: "No data to show"
                    })
                }
            }

        };
        vm.generateSlip = function(data,instance){
            data.month = vm.month ;
            data.year = vm.year ;
            var blankInstanceForm = {};
            blankInstanceForm =  angular.copy(data);
            if(instance !== 'viewPdf'){
                blankInstanceForm.status = instance;
            }
            blankInstanceForm.month = vm.month ;
            blankInstanceForm.year = vm.year ;
            //blankInstanceForm.session = (blankInstanceForm.session).toFixed(2);

            //delete  blankInstanceForm.SalaryPrimaryKey;
            delete  blankInstanceForm.tempSalary;
            delete  blankInstanceForm.transport;
            delete  blankInstanceForm.salStatus;
            delete  blankInstanceForm.teacherfullname;
            delete  blankInstanceForm.eoTeacherSalaryPk;
            delete  blankInstanceForm.joiningDate;
            delete  blankInstanceForm.lkClassDurationType;
            delete  blankInstanceForm.duration;
            delete  blankInstanceForm.teacherfullnamePdf;

            for(var k in blankInstanceForm.studentList){
                delete blankInstanceForm.studentList[k].description;
                delete blankInstanceForm.studentList[k].studFullName;
                delete blankInstanceForm.studentList[k].musicType;
                delete blankInstanceForm.studentList[k].roomName;
                delete blankInstanceForm.studentList[k].sortingKey;
            }
            //console.log("blankInstanceForm:",blankInstanceForm);
            //return null;


            if(instance !== 'viewPdf'){
                bootbox.confirm({
                    size: "medium",
                    message: "<span style='color: red; font-weight: 900'>Are you sure? </span> As submitting the salary will freeze the PDF of this month and no further changes will reflect." ,
                    callback: function(result) {
                        if (result == true) {
                            salarySvcs.createSalaryByMonthForSlip(blankInstanceForm, function (res) {
                                if (res.data == 'Success') {
                                    notifySvcs.success({
                                        title: "Success",
                                        content: "Created Successfully",
                                        delay: 1000
                                    });
                                    if (instance == 'Released') {
                                        vm.print(data);
                                    }
                                    vm.getSelectedDateRange ();
                                }
                            });
                        }
                    }
                });
            }else{
                vm.print(data, true);
            }

        };

        vm.viewPDF = function(d){
            var pdfName = d.teacherfullname+"_"+ d.month+"_"+ d.year+"_"+ d.eoTeacherUser+".pdf";
            async.parallel({

            },
            function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    title: 'Salary',
                    data: pdfName,
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

        };

        var textCenter = function (xx, yy, myText, doc, flag) {
            var fontSize = doc.internal.getFontSize();
            var pageWidth = 210;
            txtWidth = doc.getStringUnitWidth(myText) * fontSize / doc.internal.scaleFactor;
            if (flag)
                xx = ( pageWidth / 2 - (txtWidth - 100)) / 2;

            doc.text(xx, yy, myText);
        };
        var textCenterItem = function (xx, yy, myText, doc, flag) {
            var fontSize = doc.internal.getFontSize();
            var pageWidth = 90;
            txtWidth = doc.getStringUnitWidth(myText) * fontSize / doc.internal.scaleFactor;
            if (flag)
                xx = ( pageWidth / 2 - (txtWidth - 100)) / 2;

            doc.text(xx, yy, myText);
        };

        vm.printInvoice = function (data) {

            generatePDF(data);
        };

        var generatePDF = function (pdfData) {
            var doc = new jsPDF();

            var startX = 10;

            doc.addImage(suwayamaLogo(), 'PNG', 10, 11, 45, 25);                            //logo
            doc.setDrawColor(0, 0, 0).rect(10, 10, 47, 27);                           //Logo border

            doc.setFontSize(9).setFontType("normal");
            doc.text(160, 10, 'Suwayama Music Academy');
            doc.line(160, 12, 200, 12);

            var addressY = 16;
            if(vm.address1 != 'null'){
                doc.text(160, addressY, vm.address1);

                addressY += 6;
            } if(vm.address2 != 'null'){
                doc.text(160, addressY, vm.address2);

                addressY += 6;
            } if(vm.phone != 'null'){
                doc.text(160, addressY, '03-5428-3566');

                addressY += 6;
            }

            doc.setFontSize(15).setFontType("normal");
            textCenter(0, 40, 'Salary Slip', doc, true);                                //Invoice 2019
            textCenter(0, 46, pdfData.month+' '+pdfData.year, doc, true);               //Invoice 2019
            doc.setLineWidth(0.4);
            doc.line(80, 49, 125, 49);

            doc.setTextColor(0);
            doc.setFontSize(9).setFontType("normal");
            doc.text(startX, 64, 'Date ');                                         //Date
            doc.text(startX + 25, 64, ': ' + moment(new Date()).format('YYYY-MM-DD'));

            doc.text(startX, 71, 'Employee Name');                                 //Invoice No.
            doc.text(startX + 25, 71, ': '+pdfData.teacherfullname);

            doc.text(startX, 78, 'Designation ');                                    // month
            doc.text(startX + 25, 78, ': '+"Teacher");

            /*doc.text(startX, 78, 'To :');                                             // to
             doc.text(startX, 82, pdfData.studentfullname);

             doc.text(startX + 150, 78, 'From :');
             doc.text(startX + 150, 82, 'Suwayama Music');*/

            //doc.text(10, 78, 'To');
            //doc.line(startX, 100, 200, 100);

            var tabelY = 100;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.text(startX + 7, tabelY + 6, 'Item No');
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            textCenterItem(20, tabelY + 6, 'Item', doc, true);
            doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

            doc.text(startX + 102, tabelY + 6, 'Session Amount (JPY)');
            doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

            doc.text(startX + 138, tabelY + 6, 'Session (JPY)');
            doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

            doc.text(startX + 161, tabelY + 6, 'Total Amount (JPY)');
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            var index = 0;
            if(pdfData.salaryType == 'Per month'){
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6,"Per month");
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, pdfData.rate+"");
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, "");
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                doc.text(startX + 178, tabelY + 6, pdfData.rate+"");
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
            }
            if(pdfData.salaryType == 'Per session'){
                // Attribute details
                if(pdfData.fortyFiveMins != null && pdfData.rate45mins != null){
                    index++;
                    tabelY += 10;
                    doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                    doc.text(startX + 10, tabelY + 6, index + '');
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                    doc.text(startX+27, tabelY + 6,"45 mins");
                    doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                    doc.text(startX + 113, tabelY + 6, pdfData.rate45mins+"");
                    doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                    doc.text(startX + 147, tabelY + 6, pdfData.fortyFiveMins+"");
                    doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                    doc.text(startX + 178, tabelY + 6, parseInt(pdfData.fortyFiveMins) * parseInt(pdfData.rate45mins)+"");
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                }
                if(pdfData.sixtyMins != null && pdfData.rate60mins != null){
                    index++;
                    tabelY += 10;
                    doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                    doc.text(startX + 10, tabelY + 6, index + '');
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                    doc.text(startX+27, tabelY + 6,"60 mins");
                    doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                    doc.text(startX + 113, tabelY + 6, pdfData.rate60mins+'');
                    doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                    doc.text(startX + 147, tabelY + 6, pdfData.sixtyMins+'');
                    doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                    doc.text(startX + 178, tabelY + 6, parseInt(pdfData.sixtyMins) * parseInt(pdfData.rate60mins)+"");
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                }
            }

            if(pdfData.transportDays != null && pdfData.transportAmount != null){
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6,"Monthly Transport Amount");
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, pdfData.transportAmount+'');
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, pdfData.transportDays+'');
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                doc.text(startX + 178, tabelY + 6, parseInt(pdfData.transportDays) * parseInt(pdfData.transportAmount)+"");
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            }

            if(pdfData.add != null){
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6,"Additional 1");
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, '');
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, '');
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                doc.text(startX + 178, tabelY + 6, pdfData.add+"");
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            }if(pdfData.sub != null){
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6,"Additional 2");
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, '');
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, '');
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                doc.text(startX + 178, tabelY + 6, '-'+pdfData.sub+"");
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            }
            tabelY += 10;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
            doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);
            doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);
            doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            if(pdfData.totalSalary != null){
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6,"Grand Total");
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, '');
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, '');
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                doc.text(startX + 178, tabelY + 6, pdfData.totalSalary+"");
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
            }

            doc.text(160, 210, "Approved by: ");
            doc.text(160, 212 + 6, "Suwayama Music ");
            doc.save(pdfData.teacherfullname);
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
