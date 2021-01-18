/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("studentFreeInvoiceCtrl", ["$scope", "studentInvoiceModel","studentInvoiceSvcs","notifySvcs","$state","localStorage","modalSvcs","$uibModalInstance","metadata", function ($scope, studentInvoiceModel,studentInvoiceSvcs,notifySvcs,$state,localStorage,modalSvcs,$uibModalInstance,metadata) {

        var mvm = this;

        mvm.monthArray = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
        var tabMapForFields = [
            ["firstName", "lastName","enrollmentDate", "phone","email"]

        ];
        mvm.respData = metadata.data;
        mvm.year = metadata.year;
        mvm.month = metadata.month;

        mvm.formModel = studentInvoiceModel.getInstance("freeInvoice");

        var monthIndex = 0;
        for (var k in mvm.monthArray) {
            if (mvm.monthArray[k] == mvm.month) {
                monthIndex = k;
                break;
            }
        }
        mvm.tabConfig = {
            defaultActive: 0,
            mapid: "tabContainer",
            click: function (clickedIndex) {
                if (parseInt(clickedIndex) > parseInt(mvm.tabConfig.defaultActive)) {
                    mvm.validateTabSectionFieldsFn(mvm.tabConfig.defaultActive, function (status) {
                        if (status) {
                            mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                        }
                    });
                } else {
                    mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                }
            }
        };
        mvm.validateTabSectionFieldsFn = function (tabIndex, callback) {
            var formInputs = mvm.formConfig.vinputs;
            var formSelects = mvm.formConfig.vselect;
            var invaliFields = [];

            for (var fieldIndex in tabMapForFields[tabIndex]) {
                if (fieldIndex != "length") {
                    var fieldName = tabMapForFields[tabIndex][fieldIndex];
                    if (formInputs[fieldName]) {
                        if (!formInputs[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                    if (formSelects[fieldName]) {
                        if (!formSelects[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                }
            }

            if (invaliFields.length != 0) {
                callback(false);
                notifySvcs.error({
                    content: "Please Fill The Form Correctly."
                });
            } else {
                callback(true);
            }
        };

        mvm.getInvoiceNo = function(){
            var reqMap={"className":"eostudent_free_text_invoice"};
            studentInvoiceSvcs.getMaxInvoiceNo(reqMap, function (res) {
                if(res.data){
                    mvm.runningNo = res.data;
                }
            });
        }();

        mvm.studentfullname = mvm.respData.studentfullname;
        mvm.studentInvoiceNo = metadata.lkInstances.StudentResponseData.invoiceNo;
        mvm.textInvoiceMap = {};
        mvm.dataList = [];

        mvm.studentData =  metadata.lkInstances.StudentResponseData;
        mvm.disableValue = true;
        if(Object.keys(mvm.studentData).length == 0){
            mvm.disableValue = false;
            var freeTextInvoiceList = [];
            var studentPk = mvm.respData.eoStudentUser;
            mvm.dataList.push(mvm.formModel);

            mvm.textInvoiceMap["freeTextInvoiceList"] = freeTextInvoiceList;
            mvm.textInvoiceMap["studentPk"] = studentPk;
            mvm.textInvoiceMap["month"] = mvm.month;
            mvm.textInvoiceMap["year"] = mvm.year;

            mvm.textInvoiceMap.freeTextInvoiceList = angular.copy(mvm.dataList);
        }
        else{
            mvm.textInvoiceMap = mvm.studentData;
        }

        mvm.addField = function() {
            mvm.textInvoiceMap.freeTextInvoiceList.push(studentInvoiceModel.getInstance("freeInvoice"));
        };

        mvm.removeField = function(d, index) {
            if(mvm.textInvoiceMap.freeTextInvoiceList.length > 1){
                mvm.textInvoiceMap.freeTextInvoiceList.splice(index,1);
            }
        };

        mvm.formSubmit = function () {
            mvm.formConfig.formElement.trigger('submit');
        };

        mvm.formConfig = {
            preCompile: function (e) {
            },
            postCompile: function (e) {
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {
                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();
                var form = mvm.formConfig.formScope.freeTextInvoiceForm;
                if(mvm.runningNo[0].runningno > 0){
                    mvm.number = mvm.runningNo[0].runningno + 1;
                    var numberString = mvm.number+'';
                    if(numberString.length == 1){
                        mvm.invoiceNo = 'FT'+mvm.year+''+(parseInt(monthIndex)+1)+'-'+'00'+numberString;
                    }
                    if(numberString.length == 2){
                        mvm.invoiceNo = 'FT'+mvm.year+''+(parseInt(monthIndex)+1)+'-'+'0'+numberString;
                    }
                    if(numberString.length >= 3){
                        mvm.invoiceNo = 'FT'+mvm.year+''+(parseInt(monthIndex)+1)+'-'+numberString;
                    }
                }
                else{
                    mvm.number = 1;
                    mvm.invoiceNo = 'FT'+mvm.year+''+(parseInt(monthIndex)+1)+'-001';
                }

                mvm.textInvoiceMap.runningNo = mvm.number;
                mvm.textInvoiceMap.invoiceNo = mvm.invoiceNo;


                var blankInstanceForm = {};
                blankInstanceForm = mvm.textInvoiceMap;
                //mvm.printInvoice();
                console.log("blankInstanceForm:::",blankInstanceForm);
                if (validationResponse.invalidInputs.length == 0) {
                    mvm.printInvoice();
                    studentInvoiceSvcs.createFreeTextInvoiceByPk(blankInstanceForm , function (res){

                        if(res.data == 'Success'){
                            notifySvcs.success({
                                content:"Submitted Successfully."
                            });
                            $state.reload();
                            $uibModalInstance.close();
                        }else{
                            notifySvcs.error({
                                content:"Action not performed"
                            })
                        }
                     });
                }
                else {
                    notifySvcs.error({
                        content: "Please Fill The Form Correctly."
                    });
                }
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
            var pageWidth = 200;
            txtWidth = doc.getStringUnitWidth(myText) * fontSize / doc.internal.scaleFactor;
            if (flag)
                xx = ( pageWidth / 2 - (txtWidth - 100)) / 2;

            doc.text(xx, yy, myText);
        };

        mvm.printInvoice = function () {
            generatePDF(mvm.textInvoiceMap);
        };

        var generatePDF = function (pdfData) {
            console.log("pdfData:", pdfData);
            var doc = new jsPDF();

            var startX = 10;

            doc.addImage(suwayamaLogo(), 'JPEG', 155, 11, 45, 25);                            //logo
            doc.setDrawColor(0, 0, 0).rect(154, 10, 47, 27);                           //Logo border

            textCenter(0, 40, 'Invoice - '+mvm.year, doc, true);                                //Invoice 2019
            doc.setLineWidth(0.4);
            doc.line(80, 43, 125, 43);

            doc.setTextColor(0);
            doc.setFontSize(9).setFontType("normal");
            doc.text(startX, 50, 'Date ');                                         //Date
            doc.text(startX + 17, 50, ': ' + moment(new Date()).format('YYYY-MM-DD'));

            doc.text(startX, 54, 'Invoice No ');                                 //Invoice No.
            doc.text(startX + 17, 54, ': '+mvm.invoiceNo);

            doc.text(startX, 58, 'month ');                                    // month
            doc.text(startX + 17, 58, ': '+mvm.month);

            doc.text(startX, 78, 'To :');                                             // to
            doc.text(startX, 82, mvm.studentfullname);

            doc.text(startX + 150, 78, 'From :');
            doc.text(startX + 150, 82, 'Suwayama Music');

            //doc.text(10, 78, 'To');
            //doc.line(startX, 100, 200, 100);

            var tabelY = 100;
            doc.setFontSize(9).setFontType("normal");
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.text(startX + 10, tabelY + 6, 'Item No');
            doc.line(startX + 30, tabelY, startX + 30, tabelY + 10);

            textCenterItem(20, tabelY + 6, 'Item', doc, true);
            doc.line(startX + 150, tabelY, startX + 150, tabelY + 10);

            doc.text(startX + 160, tabelY + 6, 'Total Amount');

            var index = 0;
            var total = 0;
            for (var k in pdfData.freeTextInvoiceList) {                            // Attribute details
                index++;
                tabelY += 10;
                doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
                doc.text(startX + 10, tabelY + 6, index + '');
                doc.line(startX + 30, tabelY, startX + 30, tabelY + 10);

                doc.text(startX+32, tabelY + 6, pdfData.freeTextInvoiceList[k].description);
                doc.line(startX + 150, tabelY, startX + 150, tabelY + 10);

                doc.text(startX + 175, tabelY + 6, pdfData.freeTextInvoiceList[k].amount + "");
                total += parseInt(pdfData.freeTextInvoiceList[k].amount);
            }
            tabelY += 10;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.line(startX + 30, tabelY, startX + 30, tabelY + 10);
            doc.line(startX + 150, tabelY, startX + 150, tabelY + 10);

            tabelY += 10;
            doc.setDrawColor(0, 0, 0).rect(startX, tabelY, 190, 10);
            doc.setFontSize(9).setFontType("bold");
            doc.text(startX + 32, tabelY + 6, 'Total');
            doc.line(startX + 30, tabelY, startX + 30, tabelY + 10);

            doc.text(startX + 175, tabelY + 6, total+'');
            doc.line(startX + 150, tabelY, startX + 150, tabelY + 10);

            doc.save("Invoice");

        };
        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);
