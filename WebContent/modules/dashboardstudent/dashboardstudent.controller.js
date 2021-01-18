/**
 * Created by Kundan on 25-Apr-19.
 */
(function (window, document, $, angular) {

    var dashBoardStudentApp = angular.module('dashBoardStudentApp');

    dashBoardStudentApp.controller("dashboardStudentCtrl", ["$scope", "$rootScope","$window","dashBoardStudentSvcs", "dashBoardStudentModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","typeData", function ($scope, $rootScope, $window, dashBoardStudentSvcs, dashBoardStudentModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,typeData){

        var vm = this;
        var day = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

        vm.imageUrl = localStorage.get('imageUrl');
        console.log("typeData:",typeData);

        vm.idStudent = localStorage.get('id');
        vm.userKey = localStorage.get('userKey');

        vm.musicRoomList = typeData.RoomList;
        vm.batchList = typeData.BatchList;
        vm.studentList = typeData.StudentList;
        vm.batchListByPk = typeData.BatchListByPk.Individual;
        vm.batchListByPkGroup = typeData.BatchListByPk.Group;

        vm.role = localStorage.get('role');
        vm.getPreviousWeek = function(){
            var prevDate = vm.dateFrom.addDays(-7);
            vm.dateFrom = angular.copy(prevDate);
        };
        vm.getNextWeek = function(){
            var nextDate = vm.dateFrom.addDays(7);
            vm.dateFrom = angular.copy(nextDate);
        };

        vm.getSlotListForMobile = function () {
            vm.todaysDate = moment(Date.today()).format("YYYY-MM-DD");
            vm.tomorrowsDate = moment((Date.today()).addDays(1)).format("YYYY-MM-DD");
            vm.mobileView = false;
            var reqMap = {
                dateFrom: vm.todaysDate,
                dateTo: vm.tomorrowsDate,
                eoStudentUser: localStorage.get("userKey")
            };
            //console.log("reqMap:",reqMap);
            dashBoardStudentSvcs.getTimeSlotDetailsForMobileStd(reqMap, function (res) {
                //if (Object.keys(res.data).length > 0) {
                if ((res.data).length > 0) {
                    vm.dataMobile = res.data;
                    //console.log("vm.dataMobile::::", vm.dataMobile);
                    vm.mobileView = true;
                    for (var k in vm.dataMobile) {
                        if (k == vm.todaysDate) {
                            vm.todaysData = vm.dataMobile[k];
                        }
                        if (k == vm.tomorrowsDate) {
                            vm.tomorrowsData = vm.dataMobile[k];
                        }
                    }
                    //console.log("vm.todaysData::", vm.todaysData);
                    //console.log("vm.tomorrowsData::", vm.tomorrowsData);
                } else {
                    vm.mobileView = false;
                }
            });
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
        vm.getTimeSlotData = function(){
            vm.enableSlot = false;
            var reqMap = {
                dateFrom : moment(vm.dateFrom).format("YYYY-MM-DD"),
                dateTo : moment(vm.dateTo).format("YYYY-MM-DD"),
                userPk : localStorage.get("userKey")+''
            };

            dashBoardStudentSvcs.getTimeSlotForStudent(reqMap, function(res){
                    if(Object.keys(res.data).length >0 && res.data != 'no data'){
                        vm.dataList = res.data;
                        console.log("vm.dataListstu::",vm.dataList);
                        var tempDataList = vm.dataList;
                        vm.enableSlot = true;
                        vm.dataList = {};
                        for(var k in vm.dateDayArray){
                            for(var m in tempDataList){

                                if(m == vm.dateDayArray[k]){
                                    vm.dataList[vm.dateDayArray[k]] = tempDataList[m];
                                    break;
                                }
                            }
                        }
                        if(Object.keys(vm.dataList).length >0){
                            vm.enableSlot = true;
                        }
                        else{
                            vm.enableSlot = false;
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
                        }
                    }
                    else{
                        vm.enableSlot = false;
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
                    }
                },
                function(err){
                    notifySvcs.error({
                        content:"Something went wrong"
                    })
                });
            dashBoardStudentSvcs.getTimeSlotCount(reqMap,function (res) {
                vm.slotCount = res.data;
            });
        };

        vm.dayDateMap = function (){
            vm.dateDayArray = [];
            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);
            vm.dateDayArray.push(moment((date).addDays(0)).format("dddd")+'_'+
                (moment((date).addDays(0)).format("YYYY-MM-DD")));
            for(var k = 0; k < 6; k++){
                vm.dateDayArray.push(moment((date).addDays(1)).format("dddd")+'_'+
                    (moment((date).addDays(0)).format("YYYY-MM-DD")));
            }
            vm.getTimeSlotData();
        };

        vm.dateRange = function(){
            var tempDate = new Date();
            tempDate = moment(vm.dateFrom).format('YYYY-MM-DD');
            var date = new Date(tempDate);
            vm.dateTo = date.addDays(6);
            if(vm.role == 'EOStudentUser'){
                vm.dayDateMap();
            }
        };

        vm.setDate = function(){
            vm.dateFrom = Date.monday();
            if(vm.role == 'EOStudentUser'){
                vm.dateRange();
            }

        }();

        vm.dayArray = function(){
            for(var k in vm.dateDayArray){
                var temp = {};
                for(var m in vm.timeMap){
                    var statusMap = {};
                    statusMap["status"] = 'A';
                    temp[vm.timeMap[m]] = statusMap;
                }
                vm.dataList[vm.dateDayArray[k]] = temp;
            }
        };

        vm.timeMap =[];
        var min = 00;
        var hour = 9;
        vm.timeArray = function(){
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

       vm.allocation = true;

        //don't delete this commented code *******************************/
       /*vm.makeNewSelection = function(data){
            //console.log("data::",data);
           if(vm.batchListByPk.length == 0){

               notifySvcs.info({
                   content:"you are not registered in Individual batch",
                   delay:1500
               })
           }
           vm.allocation = true;
           for(var k in vm.slotCount){
               if(vm.slotCount[k].eotimeslot_primary_key+'' == data.eoTimeSlot+''){

                   if(parseInt(vm.slotCount[k].count) >= vm.musicRoomList.length){
                       vm.allocation = true;
                       notifySvcs.info({
                           content:"No rooms left"
                       });
                       break;
                   }
                   else{
                       vm.allocation = true;
                   }
                   break;
               }
           }
           if(vm.allocation == true){
               //console.log("allocation ::",vm.allocation);
               //console.log("data.visibility ::",data.visibility);
               if(data.visibility  == null){
                   data.visibility  = 'true';
               }
               else{
                   delete data.visibility;
               }
           }
        };*/
     
        vm.getTeacherData = function(time,nameKey){
            if(vm.dataList[nameKey][time] != null){
                //console.log("vm.dataList[nameKey][time]::",vm.dataList[nameKey][time].teacherList);
                return vm.dataList[nameKey][time].teacherList;
            }
        };
        vm.getTeacherDetails = function(data){

            //console.log("data======",data);
           /* if(data.batchId != null){
                if(data.batchId != null && data.roomId != null){
                    data.status = 'Allocated';
                    return data.teacherId + " (" + data.batchId + ")"  + " (" + data.roomId + ")";
                }
                else{
                    data.status = 'Partially Allocated';
                    return data.teacherId + " (" + data.batchId + ")" ;
                }
            }*/
            if(data.batchId != null) {
                if (data.status == 'Allocated') {
                    return data.teacherId + " (" + data.studentId + ")" + " (" + data.roomId + ")";
                }
                if (data.tempStatus == 'Partial' || data.status == 'Partially Allocated') {
                    //delete data.tempStatus;
                    return data.teacherId + " (" + data.studentId + ")";
                }
                if(data.status == 'Cancelled' && data.eoBatch != null && data.eoRoom != null){
                    return data.teacherId + " (" + data.studentId + ")" + " (" + data.roomId + ")";
                }
                if(data.status == 'Cancelled' && data.eoBatch != null ){
                    return data.teacherId + " (" + data.studentId + ")";
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
                return data.teacherId;
            }
        };
        vm.getButtonType = function(data){

            if(data.status == 'Requested' && !data.tempStatus){
                return "custom-text-requested"
            }if(data.tempStatus == 'Partial' && data.status == 'Requested'){
                return "custom-text-part-allocated"
            }if(data.status == 'Partially Allocated'){
                return "custom-text-part-allocated"
            }if(data.status == 'Allocated'){
                return "custom-text-allocated"
            }if(data.status == 'Cancelled'){
                return "custom-text-cancelled"
            }
        };

        vm.allocateBatch = function(data,d){
            data.eoBatch = d.eoBatch;
            data.batchId = d.batchId;
            data.studentId = localStorage.get("id");
            data.eoStudentUser = localStorage.get("userKey");
            delete data.visibility;
            if(data.status == 'Requested'){
                data.tempStatus = 'Partial';
            }
        };
        vm.requestCancellation = function(data){
            var flag = false;
            for(var k in vm.batchListByPkGroup){
                if(vm.batchListByPkGroup[k].eoBatch == data.eoBatch){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                console.log(data);
                delete data.visibility;
                data.studentCancellation = true;
                data.eoStudentUser = localStorage.get("userKey");
            }else{
                delete data.visibility;
                notifySvcs.info({
                    content:"Group Batch cannot be requested for Cancellation.",
                    delay:2000
                });

            }
        };
        vm.removeBatch = function(data,d){
            delete data.eoBatch;
            delete data.batchId;
            delete data.visibility;
            if(data.tempStatus == 'Partial'){
                delete data.tempStatus ;
            }
        };

        vm.submit = function(){

            var blankInstanceForm = vm.dataList;
            for(var k in blankInstanceForm){
                for(var m in blankInstanceForm[k]){
                    console.log("vm.dataList[k].teacherList::",blankInstanceForm[k][m].teacherList);
                    for(var n in blankInstanceForm[k][m].teacherList){
                        delete blankInstanceForm[k][m].teacherList[n].teacherId;
                        delete blankInstanceForm[k][m].teacherList[n].roomId;
                        delete blankInstanceForm[k][m].teacherList[n].batchId;
                        delete blankInstanceForm[k][m].teacherList[n].teacherFullName;
                        delete blankInstanceForm[k][m].teacherList[n].batchName;
                        delete blankInstanceForm[k][m].teacherList[n].studentFullName;
                        delete blankInstanceForm[k][m].teacherList[n].roomName;
                        delete blankInstanceForm[k][m].teacherList[n].studentId;

                        if(blankInstanceForm[k][m].teacherList[n].tempStatus == 'Partial'){
                            blankInstanceForm[k][m].teacherList[n].status = 'Partially Allocated';
                        }
                        delete blankInstanceForm[k][m].teacherList[n].tempStatus;
                    }
                }
            }
            console.log("blankInstanceForm:",blankInstanceForm );

            var blankMap = {};
            var finalForm = {
                "dataForm": blankInstanceForm,
                "timeSlotData": blankMap
            };
            dashBoardStudentSvcs.createTimeSlot(finalForm, function(res){
                    if(res.data =='Success'){
                        //$state.reload();
                        vm.dateRange();
                        notifySvcs.success({
                            title: "Updated",
                            content: "Successfully."
                        });
                    }
                },function(err){
                    notifySvcs.error({
                        title: "Error",
                        content: "Action not performed."
                    });
                }
            );
        };

    }]);

})(window, window.document, window.jQuery, window.angular);
