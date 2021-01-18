/**
 * Created by Kundan kumar on 17-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("createSalaryCtrl", ["$scope", "salaryModel","salarySvcs","typeData","notifySvcs","$state", function ($scope, salaryModel,salarySvcs,typeData,notifySvcs,$state) {

        var vm = this;
        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
        var getDefaultData = function(callback){
            var reqMap  = {};
            salarySvcs.getTeacherForSalary(reqMap,function (res) {
                if(res.data){
                    vm.respData = res.data;
                    //console.log("vm.respData:",vm.respData);
                    callback(true);
                }
            });
        };
        getDefaultData(function(res){

        });

        vm.setTransportAmount = function(data){
            if(data.transportAmount == ''){
                data.transportAmount = null;
            }
        };
        vm.setAmount = function(data){
            if(data.rate == ''){
                data.rate = null;
            }
        };

        vm.getColor = function(data){
            if(data.status == null){
                return {"color":"blue"}
            }
            else{
                return {"color":"green"}
            }
        };

        vm.teacherData = [];
        vm.showTeacherSalary = function(teacherPk){
            //console.log("teacherPk::",teacherPk);
            vm.teacherData = [];
            if(teacherPk == 'All'){
                vm.teacherData = angular.copy(vm.respData);
                return null;
            }
            for(var k in vm.respData){
                if(vm.respData[k].eoTeacherUser+"" == teacherPk+""){
                    vm.teacherData.push(angular.copy(vm.respData[k]));
                }
            }
        };

        vm.createSalary = function(data,instance){
            var blankInstanceForm = {};
            blankInstanceForm =  angular.copy(data);
            blankInstanceForm.status = instance;

            if(blankInstanceForm.primaryKey == null){
                delete  blankInstanceForm.primaryKey;
            }
            delete  blankInstanceForm.joiningDate;
            delete  blankInstanceForm.teacherfullname;
            for(var k in blankInstanceForm.salaryDetail){
                delete blankInstanceForm.salaryDetail[k].musicCategory;
            }
            //console.log("blankInstanceForm::",blankInstanceForm);
            if((blankInstanceForm.courseData == null && blankInstanceForm.salaryType === 'Per session')){
                notifySvcs.info({
                    content: "Please asign course to Teacher"
                });
                return;
            }

            //console.log("blankInstanceForm::",blankInstanceForm);
            //return;
            salarySvcs.createSalary(blankInstanceForm, function(res){
                if(res.data == 'Success'){
                    notifySvcs.success({
                        title: "Success",
                        content: "Created Successfully."
                    });
                    getDefaultData(function(res){
                        if(res){
                            vm.showTeacherSalary(vm.eoTeacherUser);
                        }
                    });
                }
            },function(err){
                notifySvcs.error({
                    content: "Something went wrong"
                });
            });
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
