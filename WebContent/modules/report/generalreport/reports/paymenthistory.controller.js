/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("paymentHistoryCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout","$filter","studentInvoiceSvcs","modalSvcs", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout,$filter,studentInvoiceSvcs,modalSvcs) {

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();

        vm.submit = function (e) {
            e.preventDefault();

            if (vm.eoStudentUser) {
                vm.studentUser = JSON.parse(vm.eoStudentUser);

                var reqMap = {
                    year: vm.year,
                    studentPk: vm.studentUser.primaryKey + ""
                };
                reportSvcs.getPaymentDetails(reqMap, function (res) {

                    if (res.data.length > 0) {
                        vm.paymentDetailsArray = res.data;
                    } else {
                        notifySvcs.info({
                            content: "No Payment History.",
                            delay: 1000
                        });
                    }

                    console.log("res :: ", res.data);


                }, function (err) {
                    notifySvcs.error({
                        content: "Error!"
                    });
                });
            } else {
                notifySvcs.error({
                    content: "Student Name is missing.",
                    delay: 1000
                });
            }

        };

        vm.downloadInvoice = function (instance) {

            if(instance.status == 'Fix'){
                instance.className = "EOStudentInvoiceMain";
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
                    content:"No invoice available"
                })
            }
           /* console.log("instance::",instance);
            //console.log("instance::"+($filter("json")(instance)));

            var reqMap = {
                month : instance.month,
                year : instance.year,
                eoStudentUser : instance.eoStudentUser+"",
                studFullName : instance.studentfullname,
                status : instance.status
            };

            console.log("reqMap::",reqMap);
            studentInvoiceSvcs.createInvoicePdf(reqMap , function(res){

                if(res.data  != 'Cancelled'){
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
           /!* if (instance.status == 'fix') {
                console.log("instance :: ", instance);
                vm.printInvoice(instance);
            }
            else{
                notifySvcs.info({
                    content:"Invoice not generated"
                })
            }*!/*/
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

        vm.printInvoice = function (instance) {
           vm.detailList = [
                {"key": "Cancellation Amount","value": instance.cancellationAmount != null ? instance.cancellationAmount : 0},
                {"key": "Total", "value": instance.total != null ? instance.total : 0},
                {"key": "Consumption Tax", "value": instance.consumptionTax != null ? instance.consumptionTax : 0},
                {"key": "Grand Total", "value": instance.grandTotal != null ? instance.grandTotal : 0}
            ];
            generatePDF(instance);
        };

        var generatePDF = function (pdfData) {
            console.log("pdfData:", pdfData);
            var doc = new jsPDF();

            var startX = 10;

            doc.addImage(suwayamaLogo(), 'JPEG', 155, 11, 45, 25);                            //logo
            doc.setDrawColor(0, 0, 0).rect(154, 10, 47, 27);

            textCenter(0, 40, 'Invoice - '+pdfData.year, doc, true);                                //Invoice 2019
            doc.setLineWidth(0.4);
            doc.line(80, 43, 125, 43);

            doc.setTextColor(0);
            doc.setFontSize(9).setFontType("normal");
            doc.text(startX, 50, 'Date ');                                         //Date
            doc.text(startX + 17, 50, ': ' + moment(new Date()).format('YYYY-MM-DD'));

            doc.text(startX, 54, 'Invoice No ');                                 //Invoice No.
            doc.text(startX + 17, 54, ': '+pdfData.invoiceNo);

            doc.text(startX, 58, 'Month ');                                    // month
            doc.text(startX + 17, 58, ': '+pdfData.month);

            doc.text(startX, 78, 'To :');                                             // to
            doc.text(startX, 82, pdfData.studentfullname);

            doc.text(startX + 150, 78, 'From :');
            doc.text(startX + 150, 82, 'Suwayama Music');

            //doc.text(10, 78, 'To');
            //doc.line(startX, 100, 200, 100);

            var tabelY = 100;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.text(startX + 7, tabelY + 6, 'Item No');
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            textCenterItem(20, tabelY + 6, 'Item', doc, true);
            doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

            doc.text(startX + 105, tabelY + 6, 'Session Amount');
            doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

            doc.text(startX + 142, tabelY + 6, 'Session');
            doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

            doc.text(startX + 165, tabelY + 6, 'Total Amount');
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

            var index = 0;
            for (var k in pdfData.attributeList) {                            // Attribute details
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6, pdfData.attributeList[k].sessionDuration + ' ' + pdfData.attributeList[k].courseName);
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, pdfData.attributeList[k].fees + "");
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);
                if (pdfData.attributeList[k].feeType == 'Per session') {
                    doc.text(startX + 147, tabelY + 6, pdfData.attributeList[k].session + '');
                }
                if (pdfData.attributeList[k].feeType == 'Per course') {
                    doc.text(startX + 147, tabelY + 6, '');
                }
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                if (pdfData.attributeList[k].feeType == 'Per session') {
                    doc.text(startX + 178, tabelY + 6, (pdfData.attributeList[k].fees * pdfData.attributeList[k].session) + '');
                }
                if (pdfData.attributeList[k].feeType == 'Per course') {
                    doc.text(startX + 178, tabelY + 6, pdfData.attributeList[k].fees + '');
                }

                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
            }
            tabelY += 10;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
            doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);
            doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);
            doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);
            doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);



            for (var k in pdfData.editableList) {                                                //editable

                if(pdfData.editableList[k].amount != '' ){
                    tabelY += 10;
                    doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                    doc.text(startX + 10, tabelY + 6, '');
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                    doc.text(startX+27, tabelY + 6, pdfData.editableList[k].description);
                    doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                    doc.text(startX + 113, tabelY + 6, '');
                    doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                    doc.text(startX + 147, tabelY + 6, '');
                    doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);

                    doc.text(startX + 178, tabelY + 6, pdfData.editableList[k].amount + '');
                    doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
                    doc.setFontSize(9).setFontType("normal");
                }
            }

            for (var k in vm.detailList) {                                                //detailList

                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);

                doc.text(startX+27, tabelY + 6, vm.detailList[k].key);
                doc.line(startX + 100, tabelY, startX + 100, tabelY + 10);

                doc.text(startX + 113, tabelY + 6, '');
                doc.line(startX + 135, tabelY, startX + 135, tabelY + 10);

                doc.text(startX + 147, tabelY + 6, '');
                doc.line(startX + 160, tabelY, startX + 160, tabelY + 10);


                if (vm.detailList[k].key == 'Grand Total' || vm.detailList[k].key == 'Deposit Amount' || vm.detailList[k].key == 'Due Amount') {
                    doc.setFontSize(9).setFontType("bold");
                }
                doc.text(startX + 178, tabelY + 6, vm.detailList[k].value + '');
                doc.line(startX + 25, tabelY, startX + 25, tabelY + 10);
                doc.setFontSize(9).setFontType("normal");
            }
            doc.text(160, 235, "Approved by: ");
            doc.text(160, 237 + 6, "Suwayama Music ");
            doc.save(pdfData.studentfullname);

        };

        vm.downloadExcel = function () {
            if (vm.paymentDetailsArray.length > 0) {
                vm.rows = [];
                var count = 0;
                var tobj = {};
                for (var key in vm.paymentDetailsArray) {
                    var attrDetailsArr = vm.paymentDetailsArray[key].attributeList;
                    var subCount = 0;
                    for (var k in attrDetailsArr) {

                        if (subCount == 0) {
                            tobj.count = ++count;
                            tobj.month = vm.paymentDetailsArray[key].month;
                        } else {
                            tobj.count = '';
                            tobj.month = '';
                        }
                        tobj.courseName = attrDetailsArr[k].courseName;
                        tobj.feeType = attrDetailsArr[k].feeType;
                        tobj.sessionDuration = attrDetailsArr[k].feeType == 'Per session' ? attrDetailsArr[k].sessionDuration : '';
                        tobj.fees = attrDetailsArr[k].fees;
                        tobj.session = attrDetailsArr[k].session > 0 ? attrDetailsArr[k].session : '';

                        if (subCount == 0) {
                            tobj.cancellationAmount = vm.paymentDetailsArray[key].cancellationAmount;
                            tobj.total = vm.paymentDetailsArray[key].total;
                            tobj.consumptionTax = vm.paymentDetailsArray[key].consumptionTax + '%';
                            tobj.grandTotal = vm.paymentDetailsArray[key].grandTotal;
                            tobj.depositAmount = vm.paymentDetailsArray[key].depositAmount;
                            tobj.dueAmount = vm.paymentDetailsArray[key].dueAmount;
                            tobj.status = vm.paymentDetailsArray[key].status;
                        } else {
                            tobj.cancellationAmount = '';
                            tobj.total = '';
                            tobj.consumptionTax = '';
                            tobj.grandTotal = '';
                            tobj.depositAmount = '';
                            tobj.dueAmount = '';
                            tobj.status = '';
                        }


                        var header = [];
                        header = _.values(tobj);
                        vm.rows.push(header);
                        subCount++;

                    }
                    var blankObj = {};
                    var header = [];
                    header = _.values(blankObj);
                    vm.rows.push(header);

                }


                var excelHeader = ["Sr No.", "Month", "Course Name", "Fee Type", "Session Duration", "Fee", "Session", "Cancellation Amount (JPY)", "Total (JPY)","Consumption Tax (%)","Grand Total (JPY)","Deposit (JPY)","Due (JPY)", "Status"];
                var itrData = vm.rows;
                var sheetName = "Invoice Report";
                var excelName = vm.studentUser.studentfullName + " Invoice Report - " + vm.year;
                //vm.downloadConfig.generateExcel(sheetName, excelName, itrData, excelHeader);
                var excelArray = [];
                var headerRow = ["", "", "", "", vm.studentUser.studentfullName + " Invoice Report - " + vm.year];
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
                    content: "No Invoice reports are Available."
                });
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);