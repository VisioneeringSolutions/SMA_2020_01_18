/**
 * Created by Kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardAdminApp = angular.module('dashBoardAdminApp');

    dashBoardAdminApp.controller("dashboardAdminCtrl", ["$scope", "$rootScope","$window","dashBoardAdminSvcs", "dashBoardAdminModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","typeData","queryFormSvcs","registrationSvcs", function ($scope, $rootScope, $window, dashBoardAdminSvcs, dashBoardAdminModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,typeData,queryFormSvcs,registrationSvcs){

        var vm = this;
        var day = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

        vm.musicRoomList = typeData.RoomList;
        //vm.batchList = typeData.BatchList;
        vm.studentList = typeData.StudentList;

        vm.imageUrl = localStorage.get('imageUrl');

        /*followUpDateList =function () {
            var reqMap  = {
                currentDate: moment.format("DD-MM-YYYY")
            };
            //console.log("currentDate:",currentDate);
            dashBoardAdminSvcs.getFollowUpDate(reqMap,function (res) {
                vm.followUpDateList = res.data;
                //console.log("currentDate  res.data---:",res.data);
                callback(null, res.data);
            });
        };*/

        vm.followUpDateList = typeData.FollowUpDateList;   //for showing followUp date
        vm.viewAllFollowUpList = typeData.AllFollowUpList;   //for showing  All followUp date
        $scope.route = function (instance) {
            switch (instance) {
                case 'course':
                    $state.go("registration.courseregistration");
                    break;
                case 'musicRoom':
                    $state.go("registration.musicroom");
                    break;
                case 'StudentRegistration':
                    $state.go("registration.studentregistration");
                    break;
                case 'TeacherRegistration':
                    $state.go("registration.teacherregistration");
                    break;
                case 'musicType':
                    $state.go("registration.musictype");
                    break;
                case 'musicCategory':
                    $state.go("registration.musiccategory");
                    break;
                default :
                    break;
            }
        };

        vm.generatePdf = function(){
            var reqMap ={
                pdfUrl: vm.dateDayArrayForPdf[0]+"_"+vm.dateDayArrayForPdf[6],
                date : vm.dateDayArrayForPdf
            };
            ////console.log("reqMap::",reqMap);
            dashBoardAdminSvcs.createTimeTablePdf(reqMap, function(res){

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
                            ////console.log("modal template rendered");
                        });
                        modalInstance.opened.then(function () {
                            ////console.log("modal template opened");
                        });
                        modalInstance.closed.then(function () {
                            ////console.log("modal template closed");
                            modalInstance = undefined;
                        });
                    });
            });
        };
        vm.getAllFollowUpDate=function(){
            var reqMap  = {
            };
            dashBoardAdminSvcs.getAllFollowUpDate(reqMap,function (res) {
                    vm.allFollowUpDateList = res.data;

                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });
        };


        vm.updateModal = function (instance) {
            async.parallel({
                MusicType: function (callback) {
                    lookupStoreSvcs.getMusicType().then(function (res) {
                        callback(null, res);
                    });
                },
                CourseList: function (callback) {
                    var reqMap = {};
                    registrationSvcs.getCourseList(reqMap, function (res) {
                        callback(null, res.data);
                    });
                },
                StudentQueryData: function (callback) {
                    var model = {
                        objName: "EOQueryForm",
                        eoStudUserPK: instance.primary_key + ""
                    };
                    queryFormSvcs.getQueryStudentsByPk(model, function (res) {
                        callback(null, res.data);
                    }, function (err) {
                        callback(err);
                    });
                }
            }, function (err, results) {
                modalInstance = modalSvcs.open({
                    windowClass: "fullHeight",
                    size: "lg",
                    lkInstances: results,
                    templateUrl: "modules/queryform/queryform.updatemodal.html",
                    controller: "queryFormUpdateModalCtrl",
                    controllerAs: "queryFormUpdateModal"
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

        vm.role = localStorage.get('role');
        //vm.role = 'EOManagement';

        vm.getPreviousWeek = function(){
            var prevDate = vm.dateFrom.addDays(-7);
            vm.dateFrom = angular.copy(prevDate);
        };
        vm.getNextWeek = function(){
            var nextDate = vm.dateFrom.addDays(7);
            vm.dateFrom = angular.copy(nextDate);
        };

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
        vm.getTimeSlotData = function(){
            vm.enableSlot = false;
            vm.dataList = {};
            var reqMap = {
                dateFrom : moment(vm.dateFrom).format("YYYY-MM-DD"),
                dateTo : moment(vm.dateTo).format("YYYY-MM-DD")
            };
            dashBoardAdminSvcs.getTimeSlotForAdmin(reqMap, function(res){

                    if(Object.keys(res.data).length >0){
                        console.log("res.data:",res.data);
                        vm.dataList = res.data;
                        vm.dayArray();
                        vm.enableSlot = true;
                    }
                    else{
                        vm.enableSlot = true;
                        vm.dayArray();
                    }
                },
                function(err){
                    notifySvcs.error({
                        content:"Something went wrong"
                    })
                });
        };

        vm.dayDateMap = function (){
            vm.dateDayArray = [];
            vm.dateDayArrayForPdf = [];

            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);

            vm.dateDayArrayForPdf.push(moment((date).addDays(0)).format("YYYY-MM-DD"));

            vm.dateDayArray.push(moment((date).addDays(0)).format("dddd")+'_'+
                (moment((date).addDays(0)).format("YYYY-MM-DD")));

            for(var k = 0; k < 6; k++){
                vm.dateDayArray.push(moment((date).addDays(1)).format("dddd")+'_'+
                    (moment((date).addDays(0)).format("YYYY-MM-DD")));

                vm.dateDayArrayForPdf.push(moment((date).addDays(0)).format("YYYY-MM-DD"));
            }
            vm.getTimeSlotData();
        };

        vm.dateRange = function(){

            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);
            vm.dateTo = date.addDays(6);
            vm.dayDateMap();
        };

        vm.setDate = function(){
            vm.dateFrom = Date.monday();
            vm.dateRange();
        }();


        vm.dayArray = function(){
            if(Object.keys(vm.dataList).length == 0){
                for(var k in vm.dateDayArray){
                    var temp = {};
                    for(var m in vm.timeMap){
                        var statusMap = {};
                        statusMap["status"] = 'A';
                        temp[vm.timeMap[m]] = statusMap;
                    }
                    vm.dataList[vm.dateDayArray[k]] = temp;
                }
            }
            else{
                for(var k in vm.dateDayArray) {
                    if (!(Object.keys(vm.dataList)).includes(vm.dateDayArray[k])) {
                        var temp = {};
                        for(var m in vm.timeMap){
                            var statusMap = {};
                            statusMap["status"] = 'A';
                            temp[vm.timeMap[m]] = statusMap;
                        }
                        vm.dataList[vm.dateDayArray[k]] = temp;
                    }
                }
            }
            var tempDataList = vm.dataList;
            vm.dataList = {};
            for(var k in vm.dateDayArray){
                for(var m in tempDataList){

                    if(m == vm.dateDayArray[k]){
                        vm.dataList[vm.dateDayArray[k]] = tempDataList[m];
                    }
                }
            }
        };

        vm.timeMap =[];
        var min = 0;
        var hour = 9;
        vm.timeArray = function(){
            for(var k = 1; k <= 52; k++){
                if(min < 60){
                    var temp1 = ((hour+"").length == 1 ? ('0'+hour): hour)+':'+(min !=0 ? min:'00');
                    min+=15;
                    if(min == 60){
                        min = 0;
                        hour += 1;
                    }
                    var temp2 = ((hour+"").length == 1 ? ('0'+hour): hour)+':'+(min !=0 ? min:'00');
                    var temp = temp1+'-'+temp2;
                    vm.timeMap.push(temp);
                }
            }
        }();

        vm.setStatus = function(nameKey,time){
            if(vm.dataList[nameKey][time].teacherList == null){
                document.getElementById("note").style.display = 'contents';
                if(vm.dataList[nameKey][time].status == 'A'){
                    vm.dataList[nameKey][time].status = 'NA';
                }
                else{
                    vm.dataList[nameKey][time].status = 'A';
                }
            }
        };

        vm.overlay = function(m, nameKey,data){
            if(data != undefined){
                var id = m+nameKey+"_"+data.primaryKey;
                if(document.getElementById(id+'+drop-down') != null){
                    document.getElementById(id+'+drop-down').style.display = 'none';
                }
                document.getElementById(id+'+overlay').style.display = 'none';
            }
        };
        vm.makeNewSelection = function(data,e){

            var reqMap = {
                eoTeacherUser: data.eoTeacherUser+""
            };
            dashBoardAdminSvcs.getStudentBatchByTecherPk(reqMap , function(res){
                if(res.data.length > 0){
                    vm.batchList = res.data;
                }
                else{
                    notifySvcs.info({
                        title:"No student found."
                    })
                }
            },function(err){
                notifySvcs.info({
                    title:"Please assign course to "+data.teacherFullName
                });
                return null
            });
            var length = (e.target.parentNode.children.length) - 1;
            var dropDownId = e.target.parentNode.children[length].id;
            var idCode = dropDownId.split('+')[0];
            var dropDownStyle = document.getElementById(dropDownId).style.display;
            var overlayStyle = document.getElementById(idCode+'+overlay').style.display;
            if((dropDownStyle == '' && overlayStyle == '') || (dropDownStyle == 'none' && overlayStyle == 'none')){
                document.getElementById(dropDownId).style.display = 'block';
                document.getElementById(idCode+'+overlay').style.display = 'block';
            }
        };

        vm.getTeacherData = function(time,nameKey){
            if(vm.dataList[nameKey][time]){
                if(vm.dataList[nameKey][time].teacherList){
                    return vm.dataList[nameKey][time].teacherList;
                }
            }
        };
        vm.getTeacherDetails = function(data){
            if(data.studentId != null){
                if(data.roomId != null){

                    if(data.cancellationStatus == undefined && data.status != 'Cancelled'){
                        data.status = 'Allocated';
                        data.displayName = data.teacherId + " (" + data.studentId + ")"  + " (" + data.roomId + ")" ;
                        /* return data.teacherId + " (" + data.studentId + ")"  + " (" + data.roomId + ")" ;*/
                        return {
                            teacherId: data.teacherId,
                            studentId: " (" + data.studentId + ")",
                            roomId: " (" + data.roomId + ")"
                        }

                    }
                    else{
                        data.displayName = data.teacherId + " (" + data.studentId + ")"  + " (" + data.roomId + ")";
                        //return data.teacherId + " (" + data.studentId + ")"  + " (" + data.roomId + ")";
                        return {
                            teacherId: data.teacherId,
                            studentId: " (" + data.studentId + ")",
                            roomId: " (" + data.roomId + ")"
                        }
                    }
                }
                else{
                    if(data.cancellationStatus == undefined && data.status != 'Cancelled'){
                        data.status = 'Partially Allocated';
                        data.displayName = data.teacherId + " (" + data.studentId + ")" ;
                        //return data.teacherId + " (" + data.studentId + ")" ;
                        return {
                            teacherId: data.teacherId,
                            studentId: " (" + data.studentId + ")"
                        }
                    }
                    else{
                        data.displayName = data.teacherId + " (" + data.studentId + ")" ;
                        // return data.teacherId + " (" + data.studentId + ")" ;
                        return {
                            teacherId: data.teacherId,
                            studentId: " (" + data.studentId + ")"
                        }
                    }
                }
            }
            /*if(data.studentId != null) {
                if (data.studentId != null && data.roomId != null) {
                    data.status = 'Allocated';
                    return data.teacherId + " (" + data.studentId + ")" + " (" + data.roomId + ")";
                }
                else {
                    data.status = 'Partially Allocated';
                    return data.teacherId + " (" + data.studentId + ")";
                }
            }*/
            else{
                //return data.teacherId;
                return {
                    teacherId: data.teacherId
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

        vm.stripeThrough = function(data){
            if(data.isActive == false)return {"text-decoration": "line-through"};
        };
        vm.setColor = function(nameKey,time){
            if(vm.dataList[nameKey][time]){
                if(vm.dataList[nameKey][time].status == 'A'){
                    return {"background":"#fff"}
                }
                if(vm.dataList[nameKey][time].status == 'NA'){
                    return {"background":"#cfd8dc"}
                }
            }
        };

        vm.allocateBatch = function(data,d){
            document.getElementById("note").style.display = 'contents';
            data.eoBatch = d.eobatch_primary_key;
            data.batchId = d.batch_id;
            data.studentId = d.student_id;
            data.lkMusicType = d.lkmusictype_primary_key;
            data.eoStudentUser = d.studentpk;
            data.lkCategoryType = d.lkcategorytype_primary_key;
        };

        vm.allocateRoom = function(data,d){
            document.getElementById("note").style.display = 'contents';
            data.eoRoom = d.primary_key;
            data.roomId = d.room_id;
        };
        vm.allocateStudent = function(data,d){
            delete data.eoBatch;
            delete data.batchId;
            data.eoStudentUser = d.primaryKey;
            data.studentId = d.studentId;
        };

        vm.getWeekName = function(week){
            if(week == "Monday"){
                return "MON";
            }if(week == "Tuesday"){
                return "TUE";
            }if(week == "Wednesday"){
                return "WED";
            }if(week == "Thursday"){
                return "THU";
            }if(week == "Friday"){
                return "FRI";
            }if(week == "Saturday"){
                return "SAT";
            }if(week == "Sunday"){
                return "SUN";
            }
        };

        /*vm.removeBatch = function(data,d){
            delete data.eoBatch;
            delete data.batchId;
            delete data.visibility;
            if(data.tempStatus == 'Partial'){
                delete data.tempStatus ;
            }
        };*/

        vm.removeBatch = function(data){
            delete data.visibility;
            modalInstance = modalSvcs.open({
                callback: function () {
                },
                data: data,
                size: 'small',
                type:'General',
                title: 'DO_YOU_WANT_TO_REMOVE_BATCH_?',
                events: {
                    success: function () {
                        delete data.eoBatch;
                        delete data.eoRoom;
                        delete data.batchId;
                        delete data.roomId;
                        data.status = 'Requested';
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
        vm.requestCancellation = function(data,type){
            document.getElementById("note").style.display = 'contents';
            delete data.visibility;
            if(type == 'self'){
                data.adminCancellation = true;
                vm.acceptAdminCancellation(data);
            }
            else if(type == 'student'){

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
                            data.adminCancellation = true;
                            vm.acceptStudentCancellation(data);
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
            else if(type == 'teacher') {
                data.teacherCancellation = true;
                vm.acceptTeacherCancellation(data);
            }
            //data.eoTeacherUser = localStorage.get("userKey");
        };

        vm.acceptStudentCancellation = function(data,time){
            document.getElementById("note").style.display = 'contents';

            delete data.visibility;
            var duration = parseFloat((data.duration).split(' ')[0]);
            var val = duration / 15;

            data.newFees = (parseFloat(data.fees))/val;
            modalInstance = modalSvcs.open({
                callback: function () {

                },
                data: data,
                type:'Student',
                size: 'small',
                title: 'ACCEPT_CANCELLATION_?',
                events: {
                    save: function (modalData,status, isTeacherPaid, cancellationPercentage) {
                        data.cancellationAmount = modalData;
                        if(data.adminCancellation == true){
                            //  if(data.cancellationAmount != 0){
                            data.studentCancellation = true;
                            // }
                            //else{
                            // data.teacherCancellation = true;
                            //  }
                        }
                        delete data.status;
                        data.status = status;
                        data.cancellationStatus = true;
                        data.isTeacherPaid = isTeacherPaid;
                        data.cancellationPercentage = cancellationPercentage;
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

        vm.acceptTeacherCancellation = function(data){
            document.getElementById("note").style.display = 'contents';

            delete data.visibility;

            modalInstance = modalSvcs.open({
                callback: function () {
                },
                data: data,
                type:'Teacher',
                size: 'small',
                title: 'ACCEPT_CANCELLATION_?',
                events: {
                    saveTeacher: function (modalData,status) {
                        delete modalData.status;
                        modalData.status = status;
                        modalData.cancellationStatus = true;
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

        vm.acceptAdminCancellation = function(data){

            delete data.visibility;

            modalInstance = modalSvcs.open({
                callback: function () {
                },
                data: data,
                type:'Admin',
                size: 'small',
                title: 'ACCEPT_CANCELLATION_?',
                events: {
                    saveAdmin: function (modalData,status) {
                        delete modalData.status;
                        modalData.status = status;
                        modalData.cancellationStatus = true;
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
        function redefinedSlot(resultMap, callback) {
            var refinedMap = {};
            for (var i in resultMap) {
                refinedMap[i] = {};
                if (Object.keys(resultMap[i]).length > 0) {
                    for (var j in resultMap[i]) {
                        refinedMap[i][j] = {};
                        if (Object.keys(resultMap[i][j]).length > 0) {
                            var mapList = resultMap[i][j];
                            for (var k in mapList) {
                                if (mapList[k].startTime == j.split("-")[0] && mapList[k].endTime == j.split("-")[1]) {
                                    refinedMap[i][j][k] = mapList[k];
                                }
                            }
                        }
                    }
                }
            }
            callback(refinedMap);
        }

        function createSlots(finalMap, callback) {
            var resultMap = {};
            var resultMap2 = {};
            for (var key in finalMap) {
                var slotList = [];
                for (var k in finalMap[key]) {
                    slotList.push(finalMap[key][k]);
                }
                function compare(a, b) {
                    if (a.startTime < b.startTime) {
                        return -1;
                    }
                    if (a.startTime > b.startTime) {
                        return 1;
                    }
                    return 0;
                }
                slotList.sort(compare);
                resultMap2[key] = slotList;
            }

            for (var key in resultMap2) {
                var tempMap = {};
                var timeList = [];
                for (var k in resultMap2[key]) {
                    if (resultMap2[key][k].teacherList) {
                        if (Object.keys(tempMap).length > 0) {
                            var keyArray = Object.keys(tempMap);

                            function compare2(a, b) {
                                if (a < b) {
                                    return -1;
                                }
                                if (a > b) {
                                    return 1;
                                }
                                return 0;
                            }

                            keyArray.sort(compare2);
                            var flag = true;
                            for (var l in keyArray) {
                                if (keyArray[l].split("-")[1] == resultMap2[key][k].startTime) {
                                    var slotData = {};
                                    for (var j in resultMap2[key][k].teacherList) {

                                        if (resultMap2[key][k].teacherList[j].eoBatch && resultMap2[key][k].teacherList[j].status != 'Cancelled') {
                                            var tmpMapSlot = tempMap[keyArray[l]];
                                            if (Object.keys(tmpMapSlot).includes(resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch)) {
                                                var map = tmpMapSlot[resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch];
                                                map['endTime'] = resultMap2[key][k].endTime;
                                                map['eoSlot'].push(resultMap2[key][k].primaryKey);
                                                tmpMapSlot[resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch] = map;
                                                //tempMap[map['startTime'] + "-" + resultMap2[key][k].startTime] = {};
                                                tempMap[map['startTime'] + "-" + resultMap2[key][k].endTime] = tmpMapSlot;
                                                flag = true;
                                            }
                                            else {
                                                var find = false;
                                                angular.forEach(tempMap, function (value, keySlot) {
                                                    for (var keyPk in value) {
                                                        if (keyPk == resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch) {
                                                            if (resultMap2[key][k].startTime >= keySlot.split("-")[0] && resultMap2[key][k].startTime <= keySlot.split("-")[1]) {
                                                                find = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                });

                                                if (!find) {
                                                    var eoSlotList = [];
                                                    eoSlotList.push(resultMap2[key][k].primaryKey);
                                                    var map = {
                                                        "date": resultMap2[key][k].date,
                                                        "day": resultMap2[key][k].day,
                                                        "startTime": resultMap2[key][k].startTime,
                                                        "endTime": resultMap2[key][k].endTime,
                                                        "eoSlot": eoSlotList,
                                                        "lkMusicType": resultMap2[key][k].teacherList[j].lkMusicType,
                                                        "lkCategoryType": resultMap2[key][k].teacherList[j].lkCategoryType,
                                                        "eoTeacherUser": resultMap2[key][k].teacherList[j].eoTeacherUser,
                                                        "eoBatch": resultMap2[key][k].teacherList[j].eoBatch,
                                                        "eoStudentUser": resultMap2[key][k].teacherList[j].eoStudentUser != 'undefined' ? resultMap2[key][k].teacherList[j].eoStudentUser : "",
                                                        "eoRoom": resultMap2[key][k].teacherList[j].eoRoom != 'undefined' ? resultMap2[key][k].teacherList[j].eoRoom : ""

                                                    };
                                                    slotData[resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch] = map;
                                                    tempMap[resultMap2[key][k].startTime + "-" + resultMap2[key][k].endTime] = slotData;
                                                }
                                            }
                                        }
                                    }
                                    flag = true;

                                } else {
                                    flag = false;
                                }
                            }
                            if (!flag) {
                                var slotData = {};
                                for (var j in resultMap2[key][k].teacherList) {
                                    if (resultMap2[key][k].teacherList[j].eoBatch && resultMap2[key][k].teacherList[j].status != 'Cancelled') {
                                        var eoSlotList = [];
                                        eoSlotList.push(resultMap2[key][k].primaryKey);
                                        var map = {
                                            "date": resultMap2[key][k].date,
                                            "day": resultMap2[key][k].day,
                                            "startTime": resultMap2[key][k].startTime,
                                            "endTime": resultMap2[key][k].endTime,
                                            "eoSlot": eoSlotList,
                                            "lkMusicType": resultMap2[key][k].teacherList[j].lkMusicType,
                                            "lkCategoryType": resultMap2[key][k].teacherList[j].lkCategoryType,
                                            "eoTeacherUser": resultMap2[key][k].teacherList[j].eoTeacherUser,
                                            "eoBatch": resultMap2[key][k].teacherList[j].eoBatch,
                                            "eoStudentUser": resultMap2[key][k].teacherList[j].eoStudentUser != 'undefined' ? resultMap2[key][k].teacherList[j].eoStudentUser : "",
                                            "eoRoom": resultMap2[key][k].teacherList[j].eoRoom != 'undefined' ? resultMap2[key][k].teacherList[j].eoRoom : ""

                                        };
                                        slotData[resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch] = map;
                                    }
                                }
                                tempMap[resultMap2[key][k].startTime + "-" + resultMap2[key][k].endTime] = slotData;
                            }
                        } else {
                            var slotData = {};
                            for (var j in resultMap2[key][k].teacherList) {
                                if (resultMap2[key][k].teacherList[j].eoBatch && resultMap2[key][k].teacherList[j].status != 'Cancelled') {

                                    var eoSlotList = [];
                                    eoSlotList.push(resultMap2[key][k].primaryKey);
                                    var map = {
                                        "date": resultMap2[key][k].date,
                                        "day": resultMap2[key][k].day,
                                        "startTime": resultMap2[key][k].startTime,
                                        "endTime": resultMap2[key][k].endTime,
                                        "eoSlot": eoSlotList,
                                        "lkMusicType": resultMap2[key][k].teacherList[j].lkMusicType,
                                        "lkCategoryType": resultMap2[key][k].teacherList[j].lkCategoryType,
                                        "eoTeacherUser": resultMap2[key][k].teacherList[j].eoTeacherUser,
                                        "eoBatch": resultMap2[key][k].teacherList[j].eoBatch,
                                        "eoStudentUser": resultMap2[key][k].teacherList[j].eoStudentUser != 'undefined' ? resultMap2[key][k].teacherList[j].eoStudentUser : "",
                                        "eoRoom": resultMap2[key][k].teacherList[j].eoRoom != 'undefined' ? resultMap2[key][k].teacherList[j].eoRoom : ""
                                    };
                                    slotData[resultMap2[key][k].teacherList[j].eoTeacherUser + "_" + resultMap2[key][k].teacherList[j].eoBatch] = map;
                                }
                            }
                            tempMap[resultMap2[key][k].startTime + "-" + resultMap2[key][k].endTime] = slotData;
                        }
                        //resultMap[key] = tempMap;
                    }
                }
                resultMap[key] = tempMap;
            }

            redefinedSlot(resultMap, function (res) {
                angular.forEach(res, function (value, key) {
                    if (Object.keys(value).length == 0) {
                        delete res[key];
                    } else {
                        angular.forEach(value, function (value2, key2) {
                            if (Object.keys(value2).length == 0) {
                                delete value[key2];
                            }
                        });
                    }
                });
                callback(res);
            });
        }
        vm.submit = function(){

            var blankInstanceForm = vm.dataList;
            for (var k in blankInstanceForm) {
                for (var m in blankInstanceForm[k]) {
                    for (var n in blankInstanceForm[k][m].teacherList) {
                        delete blankInstanceForm[k][m].teacherList[n].teacherId;
                        delete blankInstanceForm[k][m].teacherList[n].studentId;
                        delete blankInstanceForm[k][m].teacherList[n].roomId;
                        delete blankInstanceForm[k][m].teacherList[n].batchId;
                        delete blankInstanceForm[k][m].teacherList[n].teacherFullName;
                        delete blankInstanceForm[k][m].teacherList[n].batchName;
                        delete blankInstanceForm[k][m].teacherList[n].studentFullName;
                        delete blankInstanceForm[k][m].teacherList[n].roomName;
                        delete blankInstanceForm[k][m].teacherList[n].musicType;
                        delete blankInstanceForm[k][m].teacherList[n].cancellationStatus;
                        delete blankInstanceForm[k][m].teacherList[n].duration;
                        delete blankInstanceForm[k][m].teacherList[n].fees;
                        delete blankInstanceForm[k][m].teacherList[n].newFees;
                        delete blankInstanceForm[k][m].teacherList[n].displayName;
                        delete blankInstanceForm[k][m].teacherList[n].isActive;
                    }
                }
            }

            createSlots(blankInstanceForm, function (res) {
                var finalForm = {
                    "dataForm": blankInstanceForm,
                    "timeSlotData": res
                };

                console.log("finalForm::",finalForm);
                //console.log("dataform::",JSON.stringify(finalForm.dataForm));
                //return;

                dashBoardAdminSvcs.createTimeSlot(finalForm, function (res) {
                    if (res.data == 'Success') {
                        //$state.reload();
                        vm.dateRange();
                        /******* code for triggering mail to registered teachers when new time slot is created ********/

                        /*var dateListForMail = [];
                        for(var k in blankInstanceForm){
                            for(var m in blankInstanceForm[k]){
                                if(!blankInstanceForm[k][m].primaryKey){
                                    dateListForMail.push(k.split('_')[1]);
                                    break;
                                }
                            }
                        }
                        var blankForm = {dateList : dateListForMail};

                        dashBoardAdminSvcs.sendEmailOnNewSlotCreation(blankForm, function(res){

                        });*/

                        /***********************************************************************/


                        notifySvcs.success({
                            title: "Updated",
                            content: "Successfully."
                        });
                    }else if(res.data == 'Failure'){
                        //$state.reload();
                        notifySvcs.error({
                            content: "Error."
                        });
                    }
                }, function (err) {
                    notifySvcs.error({
                        title: "Error",
                        content: "Action not performed."
                    });

                });
            });
        };

        /*ScrollRate = 1;

        vm.scrollDiv_init = function (m, nameKey) {
            var id = m + nameKey+"+drop-down";
            //console.log("id::"+id);
            DivElmnt = document.getElementById('scroll-bar-2');
            window.onload = function(){

            };
            //console.log("vm.DivElmnt ::",document.getElementById('scroll-bar-2') );
            ReachedMaxScroll = false;

            DivElmnt.scrollTop = 0;
            PreviousScrollTop  = 0;

            ScrollInterval = setInterval(scrollDiv(), ScrollRate);
        };

        function scrollDiv() {

            //console.log("scroll div");
            if (!ReachedMaxScroll) {
                //console.log("ifff");
                DivElmnt.scrollTop = PreviousScrollTop;
                PreviousScrollTop++;

                //console.log("DivElmnt.scrollHeight::",DivElmnt.scrollHeight);
                //console.log("DivElmnt.offsetHeight::",DivElmnt.offsetHeight);
                //console.log("DivElmnt.scrollTop::",DivElmnt.scrollTop);
                ReachedMaxScroll =DivElmnt.scrollTop >= (DivElmnt.scrollHeight - 130);
            }
            else {
                //console.log("elsee");
                ReachedMaxScroll = (DivElmnt.scrollTop == 0)?false:true;

                DivElmnt.scrollTop = PreviousScrollTop;
                PreviousScrollTop--;
            }
        }

        function pauseDiv() {
            clearInterval(vm.ScrollInterval);
        }

        function resumeDiv() {
            vm.PreviousScrollTop = vm.DivElmnt.scrollTop;
            vm.ScrollInterval    = setInterval('scrollDiv()', vm.ScrollRate);
        }*/

    }]);

})(window, window.document, window.jQuery, window.angular);
