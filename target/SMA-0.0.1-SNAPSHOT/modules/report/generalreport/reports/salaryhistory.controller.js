/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("salaryHistoryCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout","salarySvcs","modalSvcs","studentInvoiceSvcs", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout,salarySvcs,modalSvcs,studentInvoiceSvcs) {

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();
        //console.log("vm.yearArray :: ",vm.yearArray);
        vm.address1 = localStorage.get("address1");
        vm.address2 = localStorage.get("address2");
        vm.phone = localStorage.get("phone");

        vm.roundOffValue = function (value) {
            Math.round(value);
            return  Math.round(value);

        };

        vm.submit = function (e) {
            e.preventDefault();
            if(vm.eoTeacherUser){
                vm.teacherUser = JSON.parse(vm.eoTeacherUser);

                var reqMap = {
                    year: vm.year,
                    teacherPk: vm.teacherUser.primaryKey+""
                };
                reportSvcs.getSalaryDetails(reqMap, function (res) {
                    if(res.data.length > 0){
                        vm.salaryDetailsArray = res.data;
                    }
                    else{
                        vm.salaryDetailsArray = [];
                        notifySvcs.info({
                            content: "No salary details.",
                            delay: 1000
                        });
                    }
                    //console.log("res :: ", res.data);


                }, function (err) {
                    notifySvcs.error({
                        content: "Error!"
                    });
                });
            }else{
                notifySvcs.error({
                    content: "Teacher Name is missing.",
                    delay: 1000
                })
            }


        };

        vm.downloadSlip = function (instance) {
            if(instance.status == 'Released'){
                instance.className = "EOGenerateSlip";
                studentInvoiceSvcs.getStudentInvoicePdf(instance, function(res){
                    console.log("res:",res);
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
            textCenter(0, 46, pdfData.month+' '+pdfData.year, doc, true);                                //Invoice 2019
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

        vm.downloadExcel = function () {
            if (vm.salaryDetailsArray.length > 0) {
                vm.rows = [];
                var count = 0;
                var tobj = {};
                console.log("vm.salaryDetailsArray",vm.salaryDetailsArray);
                for (var key in vm.salaryDetailsArray) {
                    if(vm.salaryDetailsArray[key].salaryType == 'Per month'){
                        /* tobj.count = ++count;
                         tobj.month = vm.salaryDetailsArray[key].month;
                         tobj.duration = vm.salaryDetailsArray[key].duration;
                         tobj.session = vm.salaryDetailsArray[key].session;
                         tobj.rate = vm.salaryDetailsArray[key].rate;
                         tobj.total = vm.salaryDetailsArray[key].totalSalary;

                         tobj.additional = '(+ ' + vm.salaryDetailsArray[key].add + ') (- ' + vm.salaryDetailsArray[key].sub + ')';
                         tobj.transportAmount = ( vm.salaryDetailsArray[key].transportDays * vm.salaryDetailsArray[key].transportAmount);
                         tobj.totalSalary = '' + vm.salaryDetailsArray[key].totalSalary;
                         tobj.status = vm.salaryDetailsArray[key].status;

                         var header = [];
                         header = _.values(tobj);
                         vm.rows.push(header);
                         var blankObj = {};
                         header = _.values(blankObj);
                         vm.rows.push(header);*/

                        tobj.count = ++count;
                        tobj.month = vm.salaryDetailsArray[key].month;
                        tobj.rate = vm.salaryDetailsArray[key].rate;
                        tobj.session = vm.salaryDetailsArray[key].session;
                        tobj.mins = vm.salaryDetailsArray[key].totalMins;
                        tobj.total = vm.salaryDetailsArray[key].rate * vm.salaryDetailsArray[key].session;


                        tobj.additional = '(+ '+ vm.salaryDetailsArray[key].add +') (- '+ vm.salaryDetailsArray[key].sub +')';
                        tobj.transportAmount =  ( vm.salaryDetailsArray[key].transportDays * vm.salaryDetailsArray[key].transportAmount);
                        tobj.totalSalary = ''+ vm.salaryDetailsArray[key].totalSalary;
                        tobj.status = vm.salaryDetailsArray[key].status;

                        var header = [];
                        header = _.values(tobj);
                        vm.rows.push(header);
                        var blankObj = {};
                        header = _.values(blankObj);
                        vm.rows.push(header);



                    }
                    else if(vm.salaryDetailsArray[key].salaryType == 'Per session'){

                        tobj.count = ++count;
                        tobj.month = vm.salaryDetailsArray[key].month;
                        tobj.rate = vm.salaryDetailsArray[key].rate;
                        tobj.session = vm.salaryDetailsArray[key].session;
                        tobj.mins = vm.salaryDetailsArray[key].totalMins;
                        tobj.total = vm.salaryDetailsArray[key].rate * vm.salaryDetailsArray[key].session;


                        tobj.additional = '(+ '+ vm.salaryDetailsArray[key].add +') (- '+ vm.salaryDetailsArray[key].sub +')';
                        tobj.transportAmount =  ( vm.salaryDetailsArray[key].transportDays * vm.salaryDetailsArray[key].transportAmount);
                        tobj.totalSalary = ''+ vm.salaryDetailsArray[key].totalSalary;
                        tobj.status = vm.salaryDetailsArray[key].status;

                        var header = [];
                        header = _.values(tobj);
                        vm.rows.push(header);
                        var blankObj = {};
                        header = _.values(blankObj);
                        vm.rows.push(header);




                        /*var arr = [];
                         if(vm.salaryDetailsArray[key].fortyFiveMins > 0){
                         arr.push("fourtyFiveMin");
                         }
                         if(vm.salaryDetailsArray[key].sixtyMins > 0){
                         arr.push("sixtyMin");
                         }
                         if(arr.length > 0){
                         var subCount = 0;
                         for(var k in arr){
                         if(subCount == 0){
                         tobj.count = ++count;
                         tobj.month = vm.salaryDetailsArray[key].month;
                         }else{
                         tobj.count = '';
                         tobj.month = '';
                         }

                         if(arr[k] == 'fourtyFiveMin'){
                         tobj.lkClassDurationType = '45 mins';
                         tobj.session = vm.salaryDetailsArray[key].fortyFiveMins;
                         tobj.rate = vm.salaryDetailsArray[key].rate45mins;
                         tobj.total = vm.salaryDetailsArray[key].rate45mins * vm.salaryDetailsArray[key].fortyFiveMins;
                         }
                         else if(arr[k] == 'sixtyMin'){
                         tobj.lkClassDurationType = '60 mins';
                         tobj.session = vm.salaryDetailsArray[key].sixtyMins;
                         tobj.rate = vm.salaryDetailsArray[key].rate60mins;
                         tobj.total = vm.salaryDetailsArray[key].rate60mins * vm.salaryDetailsArray[key].sixtyMins;
                         }
                         if(subCount == 0){
                         tobj.additional = '(+ '+ vm.salaryDetailsArray[key].add +') (- '+ vm.salaryDetailsArray[key].sub +')';
                         tobj.transportAmount =  ( vm.salaryDetailsArray[key].transportDays * vm.salaryDetailsArray[key].transportAmount);
                         tobj.totalSalary = ''+ vm.salaryDetailsArray[key].totalSalary;
                         tobj.status = vm.salaryDetailsArray[key].status;
                         }else{
                         tobj.additional = '';
                         tobj.transportAmount = '';
                         tobj.totalSalary = '';
                         tobj.status = '';
                         }

                         var header = [];
                         header = _.values(tobj);
                         vm.rows.push(header);
                         subCount++;

                         }
                         var blankObj = {};
                         header = _.values(blankObj);
                         vm.rows.push(header);

                         }
                         else{
                         tobj.count = ++count;
                         tobj.month = vm.salaryDetailsArray[key].month;
                         tobj.lkClassDurationType = '';
                         tobj.session = '';
                         tobj.rate = '';
                         tobj.total = '';

                         tobj.additional = '(+ ' + vm.salaryDetailsArray[key].add + ') (- ' + vm.salaryDetailsArray[key].sub + ')';
                         tobj.transportAmount = ( vm.salaryDetailsArray[key].transportDays * vm.salaryDetailsArray[key].transportAmount);
                         tobj.totalSalary = '' + vm.salaryDetailsArray[key].totalSalary;
                         tobj.status = vm.salaryDetailsArray[key].status;

                         var header = [];
                         header = _.values(tobj);
                         vm.rows.push(header);
                         var blankObj = {};
                         header = _.values(blankObj);
                         vm.rows.push(header);
                         }*/

                    }

                }

                var excelHeader = ["Sr No.", "Month","Rate("+vm.salaryDetailsArray[key].duration+")", "No of Session", "Mins", "Total", "Additional","Transport Amount (JPY)","Total Salary (JPY)","Status"];
                var itrData = vm.rows;
                var sheetName = "Salary Report";
                var excelName = vm.teacherUser.teacherFullName +" Salary Report - "+vm.year;
                //vm.downloadConfig.generateExcel(sheetName, excelName, itrData, excelHeader);
                var excelArray = [];
                var headerRow = ["", "", "", "", vm.teacherUser.teacherFullName +" Salary Report - "+vm.year];
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


            } else {
                notifySvcs.info({
                    content: "No Salary Details are Available."
                });
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);