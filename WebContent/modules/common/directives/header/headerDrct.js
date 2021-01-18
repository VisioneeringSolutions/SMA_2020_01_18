/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("headerDrct", ["$state","$rootScope","$http", "$timeout", "localStorage","$translate", function ($state,$rootScope,$http, $timeout, localStorage,$translate) {
        var reqMap = {
            "userKey" : localStorage.get("userKey")+""
        };
        var getMessageCount = function(){
            $http({
                method: "POST",
                url: $rootScope.baseAPIUrl + "ajaxMessage/getHeaderMessageCount",
                data: reqMap
            }).then(function (res) {
                $rootScope.messageCount = res.data;
            }, function (err) {

            });
        }();


        return {
            replace: true,
            scope: {},
            templateUrl: 'modules/common/directives/header/headerDrct.html',
            link: function (scope, element, attrs) {
                scope.firstName = localStorage.get("firstName");
                scope.role = localStorage.get("role");
                $translate.use(localStorage.get("language"));

                scope.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
                //scope.imageUrl = localStorage.get('imageUrl');

                if(localStorage.get('imageUrl') != 'null'){
                    $rootScope.imageUrl = scope.baseImgUrl + localStorage.get('imageUrl');
                }
                else{
                    $rootScope.imageUrl = "images/avatar.png";
                }

                scope.changeLanguage = function (langKey) {
                    localStorage.add('language', langKey);
                    $translate.use(localStorage.get("language"));
                };
                scope.hideHeader = function(){
                    var x = document.getElementById("main-header");
                    x.className = "main-header animate-mobile";

                };
                scope.showHeader = function(length){
                    var x = document.getElementById("main-header");
                    if(x.className == "main-header animate-mobile ng-scope"){
                        x.className = "main-header animate-mobile";
                    }
                    if (x.className == "main-header animate-mobile") {

                        x.className = "main-header animate-mobile responsive";
                    }
                    else{
                        x.className = "main-header animate-mobile";
                    }

                    for(var k = 1 ; k <= length; k++){
                        var y = document.getElementById("sub-menu-"+k);
                        if(y != null){
                            y.className = "sub-menu";
                        }
                    }

                    for(var k = 1 ; k <= length; k++){
                        var n = document.getElementById("side-sub-menu-"+k);
                        if(n != null){
                            n.className = "side-sub-menu";
                        }
                    }
                };

                scope.showSubMenu = function(id, length){

                    var x = document.getElementById(id);
                    var flag1 = true;

                    if(x != null){
                        if (x.className == "sub-menu") {
                            flag1 = false;
                            x.className = "sub-menu responsive-sub";
                        }
                        if(x.className == "sub-menu responsive-sub" && flag1 == true){
                            x.className = "sub-menu";
                        }
                    }

                    for(var k = 1 ; k <= length; k++){
                        if(id != "sub-menu-"+k){
                            var y = document.getElementById("sub-menu-"+k);
                            if(y != null){
                                y.className = "sub-menu";
                            }
                        }
                    }

                    for(var k = 1 ; k <= length; k++){
                        var n = document.getElementById("side-sub-menu-"+k);
                        if(n != null){
                            n.className = "side-sub-menu";
                        }
                    }
                };

                scope.showSideSubMenu = function(id, length, length2){
                    var x = document.getElementById(id);
                    var flag1 = true;

                    if(x != null){
                        if (x.className == "side-sub-menu") {
                            flag1 = false;
                            x.className = "side-sub-menu responsive-side-sub";
                        }
                        if(x.className == "side-sub-menu responsive-side-sub" && flag1 == true){
                            x.className = "side-sub-menu";
                        }
                    }

                    for(var k = 1 ; k <= length; k++){
                        if(id != "side-sub-menu-"+k){
                            var y = document.getElementById("side-sub-menu-"+k);
                            if(y != null){
                                y.className = "side-sub-menu";
                            }
                        }
                    }

                    for(var k = 1 ; k <= length2; k++) {
                        var m = document.getElementById("sub-menu-" + k);
                        if (m != null) {
                            m.className = "sub-menu";
                        }
                    }
                };
                scope.route = function (instance) {
                    switch (instance) {
                        case 'Dashboard':
                            scope.role == 'null' ? $state.go("dashboard") : "";
                            break;
                        case 'course':
                            $state.go("registration.courseregistration");
                            break;
                        case 'musicRoom':
                            $state.go("registration.musicroom");
                            break;
                        case 'batchRegistration':
                            $state.go("registration.batchregistration");
                            break;
                        case 'studentBatchRegistration':
                            $state.go("registration.studentbatchregistration");
                            break;
                        case 'StudentRegistration':
                            $state.go("registration.studentregistration");
                            break;
                        case 'TeacherRegistration':
                            $state.go("registration.teacherregistration");
                            break;
                        case 'DashboardAdmin':
                            $state.go("dashboardadmin");
                            break;
                        case 'DashboardTeacher':
                            $state.go("dashboardteacher");
                            break;
                        case 'DashboardStudent':
                            $state.go("dashboardstudent");
                            break;
                        case 'CreateSalary':
                            $state.go("salary.createsalary");
                            break;
                        case 'GenerateSlip':
                            $state.go("salary.generateslip");
                            break;
                        case 'SalaryView':
                            $state.go("salary.teachersalaryview");
                            break;
                        case 'BatchForTeacher':
                            $state.go("batchforteacher");
                            break;
                        case 'News':
                            $state.go("news");
                            break;
                        case 'FeedBackOfTeacher':
                            $state.go("feedbackofteacher");
                            break;
                        case 'StudentInvoice':
                            $state.go("studentinvoice.generatestudentinvoice");
                            break;
                        case 'Statistics':
                            $state.go("report.statistics");
                            break;
                        case 'Profile':
                            $state.go("setting.profile");
                            break;
                        case 'StudentProfile':
                            $state.go("setting.studentprofile");
                            break;
                        case 'TeacherProfile':
                            $state.go("setting.teacherprofile");
                            break;
                        case 'General':
                            $state.go("report.general");
                            break;
                        case 'StudentInvoiceView':
                            $state.go("studentinvoice.studentinvoiceview");
                            break;
                        case 'QueryForm':
                            $state.go("queryform");
                            break;
                        case 'TrialStudents':
                            $state.go("trialstudents");
                            break;
                        case 'Message':
                            $state.go("message");
                            break;
                        case 'Competition':
                            $state.go("competition");
                            break;
                        case 'CompetitionForStudents':
                            $state.go("competitionforstudents");
                            break;
                        case 'CompetitionForTeachers':
                            $state.go("competitionforteachers");
                            break;
                        case 'NewsForTeachers':
                            $state.go("newsforteachers");
                            break;
                        case 'NewsForStudents':
                            $state.go("newsforstudents");
                            break;
                        case 'Register Expenses':
                            $state.go("expense.registerexpenses");
                            break;
                        case 'Add Expenses':
                            $state.go("expense.addexpenses");
                            break;
                        case 'Expense Sheet':
                            $state.go("expense.expensesheet");
                            break;

                        default :
                            break;
                    }
                };
            }
        };
    }
    ]);

})(window, window.document, window.jQuery, window.angular);
