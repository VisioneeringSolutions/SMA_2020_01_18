/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("generateStudentInvoiceCtrl", ["$scope", "studentInvoiceModel", "studentInvoiceSvcs", "typeData", "notifySvcs", "$state", "localStorage", "modalSvcs", function ($scope, studentInvoiceModel, studentInvoiceSvcs, typeData, notifySvcs, $state, localStorage, modalSvcs) {

        var vm = this;

        vm.monthArray = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();

        var endDate ="";
        var startDate = "";
        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'yyyy-MM-dd';
                    this.popup = popup || {
                        opened: false
                    };
                    this.open = open || function () {
                        this.popup.opened = true;
                    };
                    this.dateOptions = dateOptions || {
                        formatYear: 'yyyy'
                    }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        vm.createDateMap = function () {
            return dateMaps.getMap();
        };

        vm.editableList = [
            {"description":'',
                "amount":''
            },
            {"description":'',
                "amount":''
            }
        ];

        var monthIndex = moment((Date.today()).addDays(0)).format("M");
        vm.month = vm.monthArray[monthIndex - 1];

        //console.log("vm.month::",vm.month);
        vm.showMonth = function(){
            //console.log("vm.month:",vm.month);
            //console.log("vm.year:",vm.year);
        };
        vm.getSelectedDateRange = function (callback) {
            endDate ="";
            startDate = "";
            var lastDate = "";
            var month = 0;
            for (var k in vm.monthArray) {
                if (vm.monthArray[k] == vm.month) {
                    month = k;
                    break;
                }
            }
            var monthValue = parseFloat(month) + 1;
            if (monthValue < 10) {
                monthValue = "0" + monthValue;
            }
            var getDaysInMonth = function (month, year) {
                return new Date(year, month, 0).getDate();
            };
            lastDate = getDaysInMonth(parseFloat(month) + 1, vm.year);
            endDate = vm.year + '-' + monthValue + '-' + lastDate;
            startDate = vm.year + '-' + monthValue + '-' + '01' + '';

            vm.showData = false;
            var reqMap = {
                year: vm.year,
                month: vm.month,
                startDate: startDate,
                endDate: endDate
            };
            studentInvoiceSvcs.getStudentForInvoice(reqMap, function (res) {

                if (res.data) {
                    if (Object.keys(res.data).length > 0) {
                        vm.showData = true;
                    }
                    vm.respData = res.data;
                    /* for(var key in vm.respData){
                         for(var key1 in vm.respData[key].attributeList){
                            if(vm.respData[key].attributeList[key1].viewTotal != 0){
                                var description = vm.respData[key].attributeList[key1].courseName.substring(0, 2);
                                //vm.respData[key].attributeList.viewTotal
                            }
                         }

                     }*/

                    for (var k in vm.respData) {
                        if(vm.respData[k].dueDate != '' && vm.respData[k].dueDate != null){
                            vm.respData[k].tempDueDate = new Date(vm.respData[k].dueDate);
                        }

                        if (vm.respData[k].total == null) {
                            vm.respData[k].total = 0;
                            vm.respData[k].tempTotal = 0;
                            vm.respData[k].dueAmount = 0;
                            vm.respData[k].consumptionTax = 10;
                            vm.respData[k].grandTotal = 0;
                            vm.respData[k].tempDueAmount = 0;
                            vm.respData[k].amountOne = 0;
                            vm.respData[k].amountTwo = 0;

                            vm.respData[k].editable = [
                                {"description":'', "amount":''},
                                {"description":'', "amount":''}
                            ];
                            vm.respData[k].substractional = [
                                {"description":'', "amount":''}
                            ];

                            for (var m in vm.respData[k].attributeList) {
                                vm.respData[k].attributeList[m].viewTotal = 0;
                                if (vm.respData[k].attributeList[m].feeType == 'Per session') {
                                    vm.respData[k].attributeList[m].viewTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees) * parseFloat(vm.respData[k].attributeList[m].session));
                                    vm.respData[k].total += Math.round(parseFloat(vm.respData[k].attributeList[m].fees) * parseFloat(vm.respData[k].attributeList[m].session));
                                    vm.respData[k].tempTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees) * parseFloat(vm.respData[k].attributeList[m].session));
                                }
                                if (vm.respData[k].attributeList[m].feeType == 'Per course') {
                                    vm.respData[k].attributeList[m].viewTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees));
                                    vm.respData[k].total += Math.round(parseFloat(vm.respData[k].attributeList[m].fees));
                                    vm.respData[k].tempTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees));
                                }
                            }
                            if (vm.respData[k].cancellationAmount != null) {

                                vm.respData[k].total += vm.respData[k].cancellationAmount;
                                vm.respData[k].grandTotal = Math.round(vm.respData[k].total + ((vm.respData[k].total ) * .10));
                                vm.respData[k].dueAmount = vm.respData[k].grandTotal;
                            }
                            else {
                                vm.respData[k].cancellationAmount = 0;
                                vm.respData[k].grandTotal = Math.round(vm.respData[k].total + ((vm.respData[k].total) * .10));
                                vm.respData[k].dueAmount = vm.respData[k].grandTotal;
                            }

                            for (var n in vm.respData[k].dueList) {
                                if (vm.respData[k].dueList[n].month != vm.month) {
                                    vm.respData[k].tempDueAmount += parseFloat(vm.respData[k].dueList[n].due_amount);
                                    vm.respData[k].dueAmount += vm.respData[k].tempDueAmount;
                                }
                            }
                        }
                        else {
                            vm.respData[k].tempDueAmount = 0;
                            vm.respData[k].tempTotal = 0;
                            vm.respData[k].substractional = [];
                            for (var m in vm.respData[k].attributeList) {
                                vm.respData[k].attributeList[m].viewTotal = 0;
                                if (vm.respData[k].attributeList[m].feeType == 'Per session') {
                                    vm.respData[k].attributeList[m].viewTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees) * parseFloat(vm.respData[k].attributeList[m].session));
                                    vm.respData[k].tempTotal += Math.round(parseFloat(vm.respData[k].attributeList[m].fees) * parseFloat(vm.respData[k].attributeList[m].session));
                                }
                                if (vm.respData[k].attributeList[m].feeType == 'Per course') {
                                    vm.respData[k].attributeList[m].viewTotal += parseFloat(vm.respData[k].attributeList[m].fees);
                                    vm.respData[k].tempTotal += parseFloat(vm.respData[k].attributeList[m].fees);
                                }
                            }
                            for (var n in vm.respData[k].dueList) {
                                if (vm.respData[k].dueList[n].month != vm.month) {
                                    vm.respData[k].tempDueAmount += parseFloat(vm.respData[k].dueList[n].due_amount);
                                    vm.respData[k].dueAmount += vm.respData[k].tempDueAmount;
                                }
                            }

                            if(vm.respData[k].editable != null){

                                var addCount = 0;
                                var subCount = 0;
                                var editableTemp = angular.copy(vm.respData[k].editable);
                                vm.respData[k].editable = [];
                                for(var e in editableTemp){
                                    if(parseInt(editableTemp[e].amount) > 0){
                                        addCount++;
                                        var tempMap = {"primaryKey":editableTemp[e].primaryKey, "description":editableTemp[e].description, "amount":editableTemp[e].amount};
                                        vm.respData[k].editable.push(tempMap);
                                    }else{
                                        subCount++;
                                        var tempMap = {"primaryKey":editableTemp[e].primaryKey, "description":editableTemp[e].description, "amount":editableTemp[e].amount};
                                        vm.respData[k].substractional.push(tempMap);
                                    }
                                }
                                var temp1 = {"description":'', "amount":''};
                                var temp2 = {"description":'', "amount":''};
                                if(addCount == 0){
                                    vm.respData[k].editable.push(temp1);
                                    vm.respData[k].editable.push(temp2);
                                }
                                if(addCount == 1){
                                    vm.respData[k].editable.push(temp1);
                                }
                                if(subCount == 0){
                                    vm.respData[k].substractional.push(temp1);
                                }
                            }else{
                                vm.respData[k].editable = [
                                    {"description":'', "amount":''},
                                    {"description":'', "amount":''}
                                ];
                                vm.respData[k].substractional = [
                                    {"description":'', "amount":''}
                                ];
                            }
                        }
                    }
                    callback("success");
                }
            }, function (err) {
                notifySvcs.error({
                    title: "Error",
                    content: "Something went wrong"
                });
                callback("error");
            });
        };

        vm.getInvoiceNo = function(){
            var reqMap={"className":"eostudent_invoice_main"};
            studentInvoiceSvcs.getMaxInvoiceNo(reqMap, function (res) {
                if(res.data){
                    vm.runningNo = res.data;
                }
            });
        };

        vm.getData = function () {
            vm.month = vm.monthArray[monthIndex - 1];
            vm.getSelectedDateRange(function(res){

            });
        }();
        vm.getDataBySelectedMonth = function () {
            vm.viewModalKey = false;
            vm.objList = {};
            vm.getSelectedDateRange(function(res){

            });
        };

        vm.objMap = {
            "Cancellation Amount": 0,
            "Total": 0,
            "Consumption Tax": 0,
            "Grand Total": 0,
            "Deposit Amount": 0,
            "Due Amount": 0
        };

        vm.viewModalKey = false;
        vm.viewModal = function (nameValue) {
            vm.setEditableAmount(nameValue);
            //console.log("nameVallue::",nameValue);
            vm.dueDate = null;
            vm.studentName = nameValue.studentfullname;
            vm.studentPk = nameValue.eoStudentUser;
            vm.dueDate = nameValue.tempDueDate;
            vm.viewModalKey = true;
            window.scrollTo(0,0);
            vm.enable = true;
            var attribTotalMins = 0;
            var editableAmount = 0;
            for(var k in nameValue.attributeList){
                attribTotalMins += parseInt(nameValue.attributeList[k].totalMins);
            }
            for(var m in nameValue.editable){
                if(nameValue.editable[m].amount != '') editableAmount += parseInt(nameValue.editable[m].amount);
            }
            if(attribTotalMins + editableAmount <= 0){
                notifySvcs.info({
                    content : "Time slot not allocated for "+nameValue.studentfullname
                });
                vm.enable = false;
                return null;
            }

            vm.detailedInvoice = false;
            vm.getInvoiceNo();
            vm.nameKey = nameValue.studentfullname+'_'+nameValue.eoStudentUser;
            vm.dataValue = {};

            if(nameValue.editable != null){
                if(nameValue.editable.length == 1){
                    var tempMap ={"description":'', "amount":''};
                    nameValue.editable.push(tempMap);
                }
            }
            else{
                nameValue.editable = [
                    {"description":'', "amount":''},
                    {"description":'', "amount":''}
                ];
            }
            if(nameValue.substractional == null){
                nameValue.substractional = [
                    {"description":'', "amount":''}
                ];
            }

            vm.objList = {};

            if(nameValue.primaryKey != null){
                vm.objList['primaryKey'] = nameValue.primaryKey;
            }
            if(nameValue.status != null){
                vm.objList['status'] = nameValue.status;
            }
            vm.objList['attributeList'] = nameValue.attributeList;
            vm.objList['editable'] = nameValue.editable;
            vm.objList['substractional'] = nameValue.substractional;
            vm.objList['cancellationAmount'] = nameValue.cancellationAmount;
            vm.objList['total'] = nameValue.total;
            vm.objList['consumptionTax'] = nameValue.consumptionTax;
            vm.objList['grandTotal'] = nameValue.grandTotal;
            vm.objList['creditNote'] = nameValue.creditNote;
            vm.objList['depositAmount'] = nameValue.depositAmount;
            vm.objList['dueAmount'] = nameValue.dueAmount;
            vm.objList['tempDueAmount'] = nameValue.tempDueAmount;
            vm.objList['tempTotal'] = nameValue.tempTotal;
            vm.objList['eoStudentUser'] = nameValue.eoStudentUser;
            vm.objList['isActive'] = nameValue.isActive;
        };

        vm.setEditableAmount = function(data){
            //console.log("data::",data);
            var editableSumAmount = 0;
            var subtractAmount = 0;
            for(var k in data.editable){
                if(data.editable[k].amount == ''){
                    data.editable[k].amount = null;
                }
                if(data.editable[k].amount != null){
                    editableSumAmount += parseFloat(data.editable[k].amount);
                }
            }
            for(var k in data.substractional){
                if(data.substractional[k].amount == '' || data.substractional[k].amount == '-'){
                    data.substractional[k].amount = null;
                }
                if(data.substractional[k].amount != null){
                    subtractAmount += parseFloat(data.substractional[k].amount);
                }
            }
            if (data.cancellationAmount == '') {
                data.cancellationAmount = 0;
            }
            if (data.total == '') {
                data.total = 0;
            }
            if (data.consumptionTax == '') {
                data.consumptionTax = 0;
            }
            data.total = data.tempTotal + parseFloat(data.cancellationAmount);
            if(subtractAmount > 0) {
                data.total = data.total + editableSumAmount - subtractAmount;
            }else{
                data.total = data.total + editableSumAmount + subtractAmount;
            }
            //data.total = data.total + editableSumAmount + subtractAmount;

            data.grandTotal = Math.round(parseFloat(data.total) + parseFloat(data.total) * ((parseFloat(data.consumptionTax)) / 100));
            data.dueAmount = data.grandTotal - parseFloat(data.depositAmount);
            data.dueAmount = data.grandTotal - parseFloat(data.depositAmount) + data.tempDueAmount;
        };

        vm.setCancellationAmount = function (data) {
            var subtractAmount = 0;
            var editableSumAmount = 0;
            for(var k in data.editable){
                if(data.editable[k].amount == ''){
                    data.editable[k].amount = null;
                }
                if(data.editable[k].amount != null){
                    editableSumAmount += parseFloat(data.editable[k].amount);
                }
            }
            for(var k in data.substractional){
                if(data.substractional[k].amount == ''){
                    data.substractional[k].amount = null;
                }
                if(data.substractional[k].amount != null){
                    subtractAmount += parseFloat(data.substractional[k].amount);
                }
            }

            if (data.cancellationAmount == '') {
                data.cancellationAmount = 0;
            }

            data.total = data.tempTotal + parseFloat(data.cancellationAmount);
            if (data.consumptionTax != null) {
                data.grandTotal = Math.round(parseFloat(data.total) + parseFloat(data.total) * ((parseFloat(data.consumptionTax)) / 100));

                if (data.depositAmount != null) {
                    data.dueAmount = data.grandTotal - parseFloat(data.depositAmount);
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount) + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount)
                    }
                }
                if (data.depositAmount == null) {
                    data.dueAmount = data.grandTotal;
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal;
                    }
                }
            }
            if (data.consumptionTax == null) {
                data.grandTotal = data.total;

                if (data.depositAmount != null) {
                    data.dueAmount = data.grandTotal - parseFloat(data.depositAmount);
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount) + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount)
                    }
                }
                if (data.depositAmount == null) {
                    data.dueAmount = data.grandTotal;
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal;
                    }
                }
            }

            vm.objMap['Total'] = data.total;
            vm.objMap['Consumption Tax'] = data.consumptionTax;
            vm.objMap['Grand Total'] = data.grandTotal;
            vm.objMap['Deposit Amount'] = data.depositAmount;
            vm.objMap['Due Amount'] = data.dueAmount;
            vm.reqData = data;
        };

        vm.setConsumptionTax = function (data, value) {
            data.consumptionTax = value;
            if (data.consumptionTax == '') {
                data.consumptionTax = null;
            }
            if (data.consumptionTax != null) {
                data.consumptionTax = parseFloat(value);

                data.grandTotal = Math.round(parseFloat(data.total) + (parseFloat(data.total) * ((parseFloat(data.consumptionTax)) / 100)));

                if (data.depositAmount != null) {
                    data.dueAmount = data.grandTotal - parseFloat(data.depositAmount);
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount) + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount)
                    }
                }
                if (data.depositAmount == null) {
                    data.dueAmount = data.grandTotal;
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal;
                    }
                }
            }
            if (data.consumptionTax == null) {
                data.grandTotal = data.total;

                if (data.depositAmount != null) {
                    data.dueAmount = data.grandTotal - parseFloat(data.depositAmount);
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount) + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal - parseFloat(data.depositAmount)
                    }
                }
                if (data.depositAmount == null) {
                    data.dueAmount = data.grandTotal;
                    if (data.tempDueAmount > 0) {
                        data.dueAmount = data.grandTotal + data.tempDueAmount;
                    }
                    else {
                        data.dueAmount = data.grandTotal;
                    }
                }
            }
            vm.objMap['Cancellation Amount'] = data.cancellationAmount;
            vm.objMap['Total'] = data.total;
            vm.objMap['Grand Total'] = data.grandTotal;
            vm.objMap['Deposit Amount'] = data.depositAmount;
            vm.objMap['Due Amount'] = data.dueAmount;

            vm.reqData = data;
        };

        vm.cancelTable = false;
        vm.cancelTransaction = function () {
            vm.cancelTable = false;
            modalInstance = modalSvcs.open({
                callback: function () {
                },
                type: 'Cancel Invoice',
                size: 'small',
                title: 'DO_YOU_WANT_TO_CANCEL_INVOICE_?',
                events: {
                    cancelInvoice: function () {
                        vm.objList.status = 'Cancelled';
                        vm.cancelTable = true;
                        var cancellationMap = [
                            {"description": "Credit Note", "amount":0 - vm.objList.grandTotal}
                        ];
                        vm.objList["creditNote"] = cancellationMap;
                        vm.submit('Cancelled');
                    }
                },
                templateUrl: "modules/custombootbox/custombootbox.html",
                controller: "customBootBoxCtrl",
                controllerAs: "customBootBox"
            });
            modalInstance.rendered.then(function () {
            });
            modalInstance.opened.then(function () {
            });
            modalInstance.closed.then(function () {
                modalInstance = undefined;
            });
        };

        vm.createNewInvoice = function () {
            vm.cancelTable = false;
            modalInstance = modalSvcs.open({
                callback: function () {
                },
                type: 'All',
                size: 'small',
                title: 'Do you want to Create New Invoice ?',
                events: {
                    success: function () {
                        vm.objList.isActive = false;
                        vm.submit('Cancelled');
                    }
                },
                templateUrl: "modules/custombootbox/custombootbox.html",
                controller: "customBootBoxCtrl",
                controllerAs: "customBootBox"
            });
            modalInstance.rendered.then(function () {
            });
            modalInstance.opened.then(function () {
            });
            modalInstance.closed.then(function () {
                modalInstance = undefined;
            });
        };

        vm.freeTextInvoice = function (nameValue) {

            async.parallel({
                StudentResponseData: function (callback) {
                    var model = {
                        studentPk: nameValue.eoStudentUser + "",
                        year: vm.year,
                        month: vm.month
                    };
                    studentInvoiceSvcs.getFreeTextInvoiceByPk(model, function (res) {
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                }
            },function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "md",
                    data:nameValue,
                    lkInstances: results,
                    month:vm.month,
                    year:vm.year,
                    templateUrl: "modules/studentinvoice/generatestudentinvoice/studentfreeinvoice.html",
                    controller: "studentFreeInvoiceCtrl",
                    controllerAs: "studentFreeInvoice"

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

        vm.print = function(){

            vm.dataValue = vm.respData[vm.nameKey];
            vm.invoiceNo = '';

            var reqMap = {
                month : vm.month,
                year : vm.year,
                eoStudentUser : vm.dataValue.eoStudentUser+"",
                studFullName : vm.dataValue.studentfullname,
                status : vm.dataValue.status,
                dueDate : vm.dataValue.dueDate,
                startDate: startDate,
                endDate: endDate
            };

            /*console.log("reqMap::",reqMap);
            console.log("old status::",vm.oldInvoiceStatus);
            console.log("New status::",vm.dataValue.status);
            console.log("objList::",vm.objList);*/

            if(vm.dataValue.status === 'Fix' && (vm.oldInvoiceStatus === 'Open' || vm.oldInvoiceStatus === '')){
                console.log("getting old data pdf::");
                var data = {
                    month: vm.month,
                    year: vm.year,
                    eoStudentUser: vm.dataValue.eoStudentUser
                };
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
            }else{
                /*vishuja change*/
                studentInvoiceSvcs.createInvoicePdf(reqMap , function(res){

                    if(res.data  != null){
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
                                    title: 'Invoice',
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
                //vm.printInvoice();
            }
        };
        vm.oldInvoiceStatus = "";
        vm.submit = function (instance) {

            if(vm.dueDate == undefined || vm.dueDate == '' || vm.dueDate == null){
                notifySvcs.info({
                    content: "Please select due date"
                });
                return null
            }else{
                vm.objList.dueDate = moment(vm.dueDate).format("YYYY-MM-DD")
            }

            vm.oldInvoiceStatus = angular.copy(vm.objList.status);
            vm.dataValue = vm.respData[vm.nameKey];
            vm.objList.status = instance;
            vm.invoiceNo = '';
            vm.number = 0;
            if((monthIndex+"").length == 1){
                monthIndex = "0" + monthIndex;
            }
            if (vm.objList.status == 'Fix') {

                if(vm.runningNo[0].runningno > 0){
                    vm.number = vm.runningNo[0].runningno + 1;
                    var numberString = vm.number+'';

                    if(numberString.length == 1){
                        vm.invoiceNo = vm.year+''+(monthIndex)+'-'+'00'+numberString;
                    }
                    if(numberString.length == 2){
                        vm.invoiceNo = vm.year+''+(monthIndex)+'-'+'0'+numberString;
                    }
                    if(numberString.length >= 3){
                        vm.invoiceNo = vm.year+''+(monthIndex)+'-'+numberString;
                    }
                }
                else{
                    vm.number = 1;
                    vm.invoiceNo = vm.year+''+(monthIndex)+'-001';
                }
                vm.objList.runningNo = vm.number;
                vm.objList.invoiceNo = vm.invoiceNo;
                //vm.printInvoice();
            }
            vm.objList.month = vm.month;
            vm.objList.year = vm.year;

            for(var d in vm.objList) {
                vm.dataValue[d] = angular.copy(vm.objList[d]);
            }

            delete vm.objList.dueAmount;
            delete vm.objList.tempTotal;
            delete vm.objList.tempDueAmount;
            delete vm.objList.depositAmount;
            //console.log("vm.objList::",vm.objList);

            for (var k in vm.objList.attributeList) {

                delete vm.objList.attributeList[k].courseName;
                delete vm.objList.attributeList[k].musicType;
                delete vm.objList.attributeList[k].lkClassDurationType;
                delete vm.objList.attributeList[k].viewTotal;

                /*if (vm.objList.attributeList[k].sessionDuration == '45 mins') {
                    vm.objList.attributeList[k].lkClassDuration = 1;
                    delete vm.objList.attributeList[k].sessionDuration;
                }
                if (vm.objList.attributeList[k].sessionDuration == '60 mins') {
                    vm.objList.attributeList[k].lkClassDuration = 2;
                    delete vm.objList.attributeList[k].sessionDuration;
                }*/
                delete vm.objList.attributeList[k].sessionDuration;
            }

            for (var k in vm.objList.editable) {
                if ((vm.objList.editable[k].amount == '' || vm.objList.editable[k].amount == null) && vm.objList.editable[k].primaryKey == null) {
                    delete vm.objList.editable[k];
                    vm.objList.editable.splice(k,1);
                }
            }
            for (var k in vm.objList.editable) {
                if ((vm.objList.editable[k].amount == '' || vm.objList.editable[k].amount == null) && vm.objList.editable[k].primaryKey != null) {
                    vm.objList.editable[k].isActive = false;
                }
            }
            if(vm.objList.editable[0].amount == '' || vm.objList.editable[0].amount == null  && vm.objList.editable[k].primaryKey == null){
                delete vm.objList.editable
            }

            //subtract
            for (var k in vm.objList.substractional) {
                if ((vm.objList.substractional[k].amount == '' || vm.objList.substractional[k].amount == null) && vm.objList.substractional[k].primaryKey == null) {
                    delete vm.objList.substractional[k];
                    vm.objList.substractional.splice(k,1);
                }else{
                    if(vm.objList.substractional[k].amount > 0)
                        vm.objList.substractional[k].amount = -1 * vm.objList.substractional[k].amount;
                }
            }
            for (var k in vm.objList.substractional) {
                if ((vm.objList.substractional[k].amount == '' || vm.objList.substractional[k].amount == null) && vm.objList.substractional[k].primaryKey != null) {
                    vm.objList.substractional[k].isActive = false;
                }
            }
            if(vm.objList.substractional.length > 0){
                if(vm.objList.substractional[0].amount == '' || vm.objList.substractional[0].amount == null  && vm.objList.substractional[k].primaryKey == null){
                    delete vm.objList.substractional
                }
            }else{
                delete vm.objList.substractional
            }
            //console.log("vm.objList::----",vm.objList);
            //return;
            studentInvoiceSvcs.createInvoice(vm.objList, function (res) {
                if (res.data === 'Success') {
                    if (vm.objList.status === 'Fix') {
                        vm.print();
                    }
                    notifySvcs.success({
                        title: "Success",
                        content: "Invoice",
                        delay: 1000
                    });
                    vm.viewModalKey = false;
                    vm.getSelectedDateRange(function(res){
                        if(res == 'success'){
                            vm.dataValue = vm.respData[vm.nameKey];
                            vm.viewModal(vm.dataValue);
                        }else{
                            notifySvcs({
                                content:"Something Went Wrong"
                            })
                        }
                    });
                }
            });
        };
        vm.detailedInvoice = false;
        vm.getInvoiceDetails = function(){
            var reqMap = {
                year : vm.year,
                month : vm.month,
                studentPk : vm.studentPk
            };
            studentInvoiceSvcs.getInvoiceDateWiseDetails(reqMap, function(res){
                if(res.data.length > 0){
                    vm.invoiceDetailedData = res.data;
                    vm.detailedInvoice = true;
                }else{
                    notifySvcs.info({
                        content: "No data"
                    })
                }
            })
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

        vm.printDetailedInvoice = function () {
            var doc = new jsPDF();
            var startX = 20;
            doc.addImage(suwayamaLogo(), 'JPEG', 82, 10, 46, 16);                 //logo


            doc.setFontSize(12).setFontType("normal");
            textCenter(0, 40, vm.month +" "+ vm.year, doc, true);                 //Invoice 2019
            /*doc.setLineWidth(0.4);
            doc.line(90, 40, 115, 40);*/

            textCenter(0, 45, vm.studentName, doc, true);                 //Invoice 2019
            doc.setLineWidth(0.4);
            doc.line(90, 46, 115, 46);

            var tableY = 55;
            doc.setFontSize(12).setFontType("bold");

            doc.rect(startX, tableY, 55.5, 7);
            doc.text(startX+15, tableY+5, "Date");

            doc.rect(startX+55.5, tableY, 55.5, 7);
            doc.text(startX+55.5+15, tableY+5, "Time");

            doc.rect(startX+111, tableY, 55.5, 7);
            doc.text(startX+111+15,tableY+5, "Amount in (JAP)");
            tableY += 7;

            for(var k in vm.invoiceDetailedData){
                doc.setFontSize(12).setFontType("normal");

                doc.rect(startX, tableY, 55.5, 7);
                doc.text(startX+15, tableY+5, vm.invoiceDetailedData[k].date);

                doc.rect(startX+55.5, tableY, 55.5, 7);
                doc.text(startX+55.5+15, tableY+5, vm.invoiceDetailedData[k].day);

                doc.rect(startX+111, tableY, 55.5, 7);
                doc.text(startX+111+15,tableY+5, vm.invoiceDetailedData[k].amount+"");

                tableY += 7;
            }
            doc.save(vm.studentName);
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
