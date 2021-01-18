/**
 * Created by Kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardTeacherApp = angular.module('dashBoardTeacherApp');

    dashBoardTeacherApp.controller("dashboardTeacherCtrl", ["$scope", "$rootScope", "$window", "dashBoardTeacherSvcs", "dashBoardTeacherModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "studentData", "$timeout", "modalSvcs", function ($scope, $rootScope, $window, dashBoardTeacherSvcs, dashBoardTeacherModel, lookupStoreSvcs, $state, notifySvcs, localStorage, studentData, $timeout, modalSvcs) {

        //console.log("studentData:",studentData);

        var vm = this;
        var day = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
        vm.batchList = studentData;
        //console.log("batchList:",vm.batchList);
        if(vm.batchList.length == 0){
            notifySvcs.info({
                content : "You are assigned to any course yet"
            })
        }
        vm.getPreviousWeek = function () {
            var prevDate = vm.dateFrom.addDays(-7);
            vm.dateFrom = angular.copy(prevDate);
        };
        vm.getNextWeek = function () {
            var nextDate = vm.dateFrom.addDays(7);
            vm.dateFrom = angular.copy(nextDate);
        };

        vm.toggle = true;
        vm.toggleSwitch = function(toggleValue){
            //console.log("toggleValue:",toggleValue);
        };
        vm.role = localStorage.get('role');

        vm.getSlotListForMobile = function(){
            vm.mobileView = false;

            vm.todaysDate = moment(Date.today()).format("YYYY-MM-DD");
            vm.lastDate = moment((Date.today()).addDays(6)).format("YYYY-MM-DD");

            vm.mobileDateArray = [];
            var tempDate = new Date();
            tempDate = moment(vm.todaysDate).format('YYYY-MM-DD');
            var date = new Date(tempDate);

            vm.mobileDateArray.push(moment((date).addDays(0)).format("YYYY-MM-DD"));

            for (var k = 0; k < 6; k++) {
                vm.mobileDateArray.push(moment((date).addDays(1)).format("YYYY-MM-DD"));
            }

            var reqMap = {
                dateFrom : vm.todaysDate,
                dateTo : vm.lastDate,
                eoTeacherUser: localStorage.get("userKey")
            };

            dashBoardTeacherSvcs.getTimeSlotDetailsForMobileTech(reqMap, function(res){

                if(Object.keys(res.data).length > 0 && res.data != 'no data'){
                    vm.dataMobile = res.data;
                    vm.respData = {};
                    vm.mobileView = true;
                    for(var k in vm.mobileDateArray){

                        for(var m in vm.dataMobile){
                            if(vm.mobileDateArray[k] == m){
                                vm.respData[vm.mobileDateArray[k]] = vm.dataMobile[m];
                                break;
                            }
                        }
                    }
                }
                else{
                    vm.mobileView = false;
                }
            });
        }();

        vm.timeMap = [];
        var min = 00;
        var hour = 9;
        vm.timeArray = function () {
            for(var k = 1; k <= 52; k++){
                if(min < 60){
                    var temp1 = ((hour+"").length == 1 ? ('0'+hour): hour)+':'+(min !=0 ? min:'00');
                    min+=15;
                    if(min == 60){
                        min = 00;
                        hour += 1;
                    }
                    var temp2 = ((hour+"").length == 1 ? ('0'+hour): hour)+':'+(min !=0 ? min:'00');
                    var temp = temp1+'-'+temp2;
                    vm.timeMap.push(temp);
                }
            }
        }();
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

        /* --------------end-------------------- */
        vm.enableSlot = false;
        vm.getTimeSlotData = function () {
            vm.enableSlot = false;
            var reqMap = {
                dateFrom : moment(vm.dateFrom).format("YYYY-MM-DD"),
                dateTo : moment(vm.dateTo).format("YYYY-MM-DD"),
                userPk: localStorage.get("userKey")
            };
            dashBoardTeacherSvcs.getTimeSlotForTeacher(reqMap, function (res) {
                if (Object.keys(res.data).length > 0) {
                    vm.submitButton = 'Update';
                    vm.dataList = res.data;
                    var tempDataList = vm.dataList;
                    vm.dataList = {};
                    for (var k in vm.dateDayArray) {

                        for (var m in tempDataList) {
                            if (m == vm.dateDayArray[k]) {
                                vm.dataList[vm.dateDayArray[k]] = tempDataList[m];
                                break;
                            }
                        }
                    }
                    vm.enableSlot = true;
                }
                else {
                    modalInstance = modalSvcs.open({
                        callback: function () {
                        },
                        size: 'small',
                        type:'Confirm',
                        title: 'Time slot not created for current week',
                        events: {
                            success: function () {

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
                    vm.enableSlot = false;
                    vm.submitButton = 'submit';
                }
            },
            function (err) {

                notifySvcs.error({
                    content: "something went wrong"
                })
            });
        };

        vm.dayDateMap = function () {
            vm.dateDayArray = [];
            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);

            vm.dateDayArray.push(moment((date).addDays(0)).format("dddd") + '_' +
                (moment((date).addDays(0)).format("YYYY-MM-DD")));
            for (var k = 0; k < 6; k++) {
                vm.dateDayArray.push(moment((date).addDays(1)).format("dddd") + '_' +
                    (moment((date).addDays(0)).format("YYYY-MM-DD")));
            }
            vm.getTimeSlotData();
        };

        vm.dateRange = function () {
            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);
            vm.dateTo = date.addDays(6);
            if(vm.role == 'EOTeacherUser'){
                vm.dayDateMap();
            }
        };

        vm.setDate = function () {
            vm.dateFrom = Date.monday();
            if(vm.role == 'EOTeacherUser'){
                vm.dateRange();
            }
        }();

        vm.setStatus = function (nameKey, time, nameValue) {

            if(vm.toggle){

                if(vm.dataList[nameKey][time].teacherList){
                    for(var k in vm.dataList[nameKey][time].teacherList){
                        if(vm.dataList[nameKey][time].teacherList[k].visibility == true){
                            //delete vm.dataList[nameKey][time].teacherList[k].visibility;
                        }
                    }
                }
                if (vm.dataList[nameKey][time].status != 'NA') {
                    if (vm.dataList[nameKey][time].teacherList != null) {
                        console.log("vm.dataList[nameKey][time].teacherList::",vm.dataList[nameKey][time].teacherList);
                        if (vm.dataList[nameKey][time].teacherList[0].status == 'Requested') {
                            vm.dataList[nameKey][time].teacherList[0].status = 'canceled';
                        }else {
                            if (vm.dataList[nameKey][time].teacherList[0].status == 'canceled') {
                                vm.dataList[nameKey][time].teacherList[0].status = 'Requested'
                            }
                        }
                    }
                    else{
                        var teacherList = [];
                        var teacherMap = {};
                        teacherMap["eoTeacherUser"] = localStorage.get("userKey");
                        teacherMap["eoTimeSlot"] = vm.dataList[nameKey][time].primaryKey;
                        teacherMap["status"] = "Requested";
                        teacherList.push(teacherMap);
                        vm.dataList[nameKey][time].teacherList = teacherList;
                    }
                }
                else {
                    notifySvcs.info({
                        content: "Not Allowed"
                    })
                }
            }else{
                //console.log("elseee");
                if(vm.batchList.length == 0){
                    notifySvcs.info({
                        content: "No student Found"
                    });
                    return null;
                }
               // console.log("vm.dataList[nameKey][time].teacherList::",vm.dataList[nameKey][time].teacherList);
                if(!vm.dataList[nameKey][time].teacherList || !vm.dataList[nameKey][time].teacherList[0].primaryKey){
                    notifySvcs.info({
                        title: "First submit your marked slot"
                    });

                }else{
                    setTimeout(function(){
                        var dropDownStyle = document.getElementById(time+nameKey+'+drop-down').style.display;
                        var overlayStyle = document.getElementById(time+nameKey+'+overlay').style.display;
                        if((dropDownStyle == '' && overlayStyle == '') || (dropDownStyle == 'none' && overlayStyle == 'none')){
                            document.getElementById(time+nameKey+'+drop-down').style.display = 'block';
                            document.getElementById(time+nameKey+'+overlay').style.display = 'block';
                        }else{
                            document.getElementById(time+nameKey+'+drop-down').style.display = 'none';
                            document.getElementById(time+nameKey+'+overlay').style.display = 'none';
                        }
                    },50);
                }
            }
        };

        vm.overlay = function(m, nameKey){
            var id = m+nameKey;
        };

        vm.getData = function (time, nameKey) {
            for (var k in vm.dataList[nameKey]) {
                if ([k] == time) {
                    return vm.dataList[nameKey][k].status;
                }
            }
        };
        vm.getTeacherData = function (time, nameKey) {
            if (vm.dataList[nameKey][time]) {
                if (vm.dataList[nameKey][time].teacherList) {
                    return vm.dataList[nameKey][time].teacherList;
                }
            }

        };
        vm.setColor = function (nameKey, time) {

            if (vm.dataList[nameKey][time]) {
                if (vm.dataList[nameKey][time].teacherList) {
                    if (vm.dataList[nameKey][time].teacherList[0].status == 'Requested') {
                        return {"background": "#81C784"}
                    }/*else if((vm.dataList[nameKey][time].teacherList[0].status == 'Cancelled') && (vm.dataList[nameKey][time].teacherList[0].teacherCancellation == true || vm.dataList[nameKey][time].teacherList[0].studentCancellation == true)){
                        return {"background": "#81C784"}
                    }*/
                    else{
                        return {"background": "#fff"}
                    }
                    /*if (vm.dataList[nameKey][time].teacherList[0].status == 'Allocated') {
                     return {"background": "#42A5F5"}
                     }
                     if (vm.dataList[nameKey][time].teacherList[0].status == 'Partially Allocated') {
                     return {"background": "#F57C00"}
                     }*/
                }
                else {
                    if (vm.dataList[nameKey][time].status == 'A') {
                        return {"background": "#fff"}
                    }
                    if (vm.dataList[nameKey][time].status == 'NA') {
                        return {"background": "#cfd8dc", "cursor": "not-allowed"}
                    }
                }
            }

        };
        vm.getButtonType = function(data){

            if(data.status == 'Requested'){
                return "custom-text-requested"
            }if(data.status == 'Partially Allocated'){
                return "custom-text-part-allocated"
            }if(data.status == 'Allocated'){
                return "custom-text-allocated"
            }if(data.status == 'Cancelled'){
                return "custom-text-cancelled"
            }
        };

        vm.getAllocationData = function (data) {
            if (data.eoBatch != null) {
                if (data.status == 'Allocated') {
                    return data.studentId + " (" + data.roomId + ")";
                }
                if (data.status == 'Partially Allocated') {
                    return data.studentId;
                }
                if(data.status == 'Cancelled' && data.eoBatch != null && data.eoRoom != null){
                    return data.studentId + " (" + data.roomId + ")";
                }
                if(data.status == 'Cancelled' && data.eoBatch != null ){
                    return data.studentId ;
                }
                if(data.status == 'Requested' && data.eoBatch != null ){
                    return data.studentId ;
                }
            }
            else {
                return "";
            }
        };

        vm.makeNewSelection = function(data,nameKey,time){
            if(vm.toggle){
                if(data.visibility  == undefined || data.visibility == null || data.visibility == false){
                    data.visibility  = true;
                }
                else{
                    delete data.visibility;
                }
            }

        };
        vm.requestCancellation = function(data,type){
            delete data.visibility;
            if(type == 'self'){
                data.teacherCancellation = true;
            }
            else{

                modalInstance = modalSvcs.open({
                    callback: function () {
                    },
                    data: data,
                    type:'studentCancellationReq',
                    size: 'small',
                    title: 'ENTER CANCELLATION DATE',
                    events: {
                        studentCancellationReq: function (date) {
                            data.cancellationRequestDate = moment(date).format("YYYY-MM-DD");
                            data.studentCancellation = true;
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
            }

            data.eoTeacherUser = localStorage.get("userKey");
        };

        vm.allocateBatch = function(data,d,nameKey,time){
            var addNewStudent = true;
            for(var k in vm.dataList[nameKey][time].teacherList){
               if(vm.dataList[nameKey][time].teacherList[k].teacherCancellation == false &&
                   vm.dataList[nameKey][time].teacherList[k].studentCancellation == false &&   vm.dataList[nameKey][time].teacherList[k].adminCancellation == false ) {
                   addNewStudent = false;
               }
            }
            if(addNewStudent){
                var teacherList = [];
                var teacherMap = {};
                teacherMap["eoTeacherUser"] = localStorage.get("userKey");
                teacherMap["eoTimeSlot"] = vm.dataList[nameKey][time].primaryKey;
                teacherMap["status"] = "Partially Allocated";
                teacherMap["eoBatch"] = d.eobatch_primary_key;
                teacherMap["batchId"] = d.batch_id;
                teacherMap["studentId"] = d.student_id;
                teacherMap["lkMusicType"] = d.lkmusictype_primary_key;
                teacherMap["eoStudentUser"] = d.studentpk;
                teacherMap["studentCancellation"] = false;
                teacherMap["teacherCancellation"] = false;
                teacherMap["adminCancellation"] = false;
                teacherList.push(teacherMap);
                vm.dataList[nameKey][time].teacherList.push(teacherMap);
            }else{
                data.eoBatch = d.eobatch_primary_key;
                data.batchId = d.batch_id;
                data.studentId = d.student_id;
                data.lkMusicType = d.lkmusictype_primary_key;
                data.eoStudentUser = d.studentpk;
                data.studentFullName = d.studfullname;
                //delete data.visibility ;
                data.status = 'Partially Allocated';
            }
            data.visibility = false;
        };

        vm.submit = function () {
            var blankInstanceForm = vm.dataList;

            for(var k in blankInstanceForm){
                for(var m in blankInstanceForm[k]){
                    if(blankInstanceForm[k][m].teacherList != null){
                        for(var j in blankInstanceForm[k][m].teacherList){
                            delete blankInstanceForm[k][m].teacherList[j].batchName;
                            delete blankInstanceForm[k][m].teacherList[j].batchId;
                            delete blankInstanceForm[k][m].teacherList[j].roomId;
                            delete blankInstanceForm[k][m].teacherList[j].roomName;
                            delete blankInstanceForm[k][m].teacherList[j].studentFullName;
                            delete blankInstanceForm[k][m].teacherList[j].studentId;
                            delete blankInstanceForm[k][m].teacherList[j].studentId;
                            delete blankInstanceForm[k][m].teacherList[j].visibility;

                            if(blankInstanceForm[k][m].teacherList[j].primaryKey == null && blankInstanceForm[k][m].teacherList[j].status == 'canceled'){
                                delete blankInstanceForm[k][m].teacherList;
                            }
                        }
                    }
                }
            }

            dashBoardTeacherSvcs.createTimeSlotForTeacher(blankInstanceForm, function (res) {
                    if (res.data == "Success") {
                        vm.dateRange();
                        notifySvcs.success({
                            title: "Success"
                        });
                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Something went wrong."
                    })
             });
        };


    }]);

})(window, window.document, window.jQuery, window.angular);
