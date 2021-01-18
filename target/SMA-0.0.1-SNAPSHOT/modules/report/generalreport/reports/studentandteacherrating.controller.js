(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("studentTeacherRatingCtrl", ["$scope", "$rootScope","reportSvcs", "localStorage", "notifySvcs", function ($scope, $rootScope,reportSvcs, localStorage, notifySvcs) {

        var vm = this;
        vm.monthArr = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];

        console.log("hrryryryyrry");
        $rootScope.pageHeader = "MUSIC ACADEMY";
        $rootScope.pageDescription = "";


        vm.userRole = localStorage.get("role");
        vm.totalStudentCount = 500;
        vm.totalPaymentReceivable = 50000;
        vm.totalSalaryDue = 4000;
        vm.currentMonth = "MAY";

        vm.getStudentUser = function () {
            var reqMap = {
                className: "EOStudentUser"
            };
            reportSvcs.getStudentUser(reqMap, function(res){
                vm.studentList = res.data;
                console.log("vm.studentList=====",vm.studentList)
            },function(err){

            })
        }();

        vm.getTeacherUser = function(){
            var reqMap = {
                className: "EOTeacherUser"
            };
            reportSvcs.getTeacherUser(reqMap, function(res){
                vm.teacherList = res.data;
                console.log("vm.teacherList ::: ",vm.teacherList);
            },function(err){

            })
        }();

        /***********Teacher Ratings start*********************************/

        var teacherRatingChart = function (teacherName) {
            var ctx = document.getElementById("teacherRating");


            if (vm.yAxisTeacherLabelRating) {
                vm.ratingData = {
                    labels: vm.xAxisLabel,
                    datasets: [{
                        label: 'Maximum Rating',
                        tension: 0.000001,
                        borderColor: "#d81b1b",
                        backgroundColor: "#d81b1b",
                        fill: false,
                        data: vm.yAxisMaxLabelRating,
                        yAxisID: 'y-axis',
                    }, {
                        label: 'Minimum Rating',
                        tension: 0.000001,
                        borderColor: "#1e1bd8",
                        backgroundColor: "#1e1bd8",
                        fill: false,
                        data: vm.yAxisMinLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Average Rating',
                        tension: 0.000001,
                        borderColor: "#05562e",
                        backgroundColor: "#05562e",
                        fill: false,
                        data: vm.yAxisAvgLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label:  teacherName+' Rating',
                        tension: 0.000001,
                        borderColor: "#f9687f",
                        backgroundColor: "#f9687f",
                        fill: false,
                        data: vm.yAxisTeacherLabelRating,
                        yAxisID: 'y-axis'
                    }]
                };
            }
            else {
                vm.ratingData = {
                    labels: vm.xAxisLabel,
                    datasets: [{
                        label: 'Maximum Rating',
                        tension: 0.000001,
                        borderColor: "#d81b1b",
                        backgroundColor: "#d81b1b",
                        fill: false,
                        data: vm.yAxisMaxLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Minimum Rating',
                        tension: 0.000001,
                        borderColor: "#1e1bd8",
                        backgroundColor: "#1e1bd8",
                        fill: false,
                        data: vm.yAxisMinLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Average Rating',
                        tension: 0.000001,
                        borderColor: "#05562e",
                        backgroundColor: "#05562e",
                        fill: false,
                        data: vm.yAxisAvgLabelRating,
                        yAxisID: 'y-axis'
                    }]
                };
            }

            var chartOptions = {
                scales: {
                    xAxes: [vm.xAxisLabel],
                    yAxes: [{id: "y-axis"}
                    ]
                }
            };
            if (ctx != null) {
                if(vm.barChart1){
                    vm.barChart1.destroy();
                }
                vm.barChart1 = new Chart(ctx, {
                    type: 'line',
                    data: vm.ratingData,
                    options: chartOptions
                });
            }

        };

        vm.teacherRating = function (teacherRatingData,teacherName) {

            var xAxis = [];
            for (var key in teacherRatingData) {
                xAxis.push(teacherRatingData[key].month);
            }
            vm.xAxisLabel = xAxis;


            var yAxisMaxRating = [];
            var yAxisMinRating = [];
            var yAxisAvgRating = [];
            var yAxisTeacherRating = [];


            for (var key in teacherRatingData) {

                yAxisMaxRating.push(parseFloat(teacherRatingData[key].maxRating).toFixed(2));
                yAxisMinRating.push(parseFloat(teacherRatingData[key].minRating).toFixed(2));
                yAxisAvgRating.push(parseFloat(teacherRatingData[key].avgRating).toFixed(2));
                if (teacherRatingData[key].teacherRating != null) {
                    yAxisTeacherRating.push(parseFloat(teacherRatingData[key].teacherRating).toFixed(2));
                }

            }
            vm.yAxisMaxLabelRating = yAxisMaxRating;
            vm.yAxisMinLabelRating = yAxisMinRating;
            vm.yAxisAvgLabelRating = yAxisAvgRating;
            if (yAxisTeacherRating.length > 0) {
                vm.yAxisTeacherLabelRating = yAxisTeacherRating;
            }

            teacherRatingChart(teacherName);


        };

        vm.getTeacherRating = function () {
            var reqMap = {
                className: "EOTeacherRating"
            };
            reportSvcs.getTeacherRating(reqMap, function (res) {
                vm.teacherRatingData = res.data;
                vm.teacherRating(vm.teacherRatingData,"");
            }, function (err) {
                notifySvcs.error({
                    content: "Error Occured."
                })
            })
        }();

        function setTeacherData(teacherData, callback) {

            for (var key in teacherData) {
                vm.teacherRatingData[key]['teacherRating'] = teacherData[key].maxRating;
            }
            callback(vm.teacherRatingData);
        }

        vm.getRatingByTeacher = function (instance) {

            var teacherPk = JSON.parse(instance).primaryKey;
            vm.teacherName = JSON.parse(instance).teacherFullName;

            var reqMap = {
                teacherPk: teacherPk
            };
            reportSvcs.getTeacherRatingByPk(reqMap, function (res) {
                setTeacherData(res.data, function (res) {
                    vm.teacherRating(res,vm.teacherName);
                });


            }, function (err) {
                notifySvcs.error({
                    content: "Error Occured."
                })
            })


        };

        /***********************Teacher Rating End**********************************/

        /***********************Student Rating Start**********************************/

        var studentRatingChart = function (studentName) {
            var ctx = document.getElementById("studentRating");

            if (vm.yAxisStudentLabelRating) {
                vm.ratingData = {
                    labels: vm.xAxisLabel,
                    datasets: [{
                        label: 'Maximum Rating',
                        tension: 0.000001,
                        borderColor: "#d81b1b",
                        backgroundColor: "#d81b1b",
                        fill: false,
                        data: vm.yAxisMaxLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Minimum Rating',
                        tension: 0.000001,
                        borderColor: "#1e1bd8",
                        backgroundColor: "#1e1bd8",
                        fill: false,
                        data: vm.yAxisMinLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Average Rating',
                        tension: 0.000001,
                        borderColor: "#05562e",
                        backgroundColor: "#05562e",
                        fill: false,
                        data: vm.yAxisAvgLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label:  studentName+' Rating',
                        tension: 0.000001,
                        borderColor: "#f9687f",
                        backgroundColor: "#f9687f",
                        fill: false,
                        data: vm.yAxisStudentLabelRating,
                        yAxisID: 'y-axis'
                    }]
                };
            } else {
                vm.ratingData = {
                    labels: vm.xAxisLabel,
                    datasets: [{
                        label: 'Maximum Rating',
                        tension: 0.000001,
                        borderColor: "#d81b1b",
                        backgroundColor: "#d81b1b",
                        fill: false,
                        data: vm.yAxisMaxLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Minimum Rating',
                        tension: 0.000001,
                        borderColor: "#1e1bd8",
                        backgroundColor: "#1e1bd8",
                        fill: false,
                        data: vm.yAxisMinLabelRating,
                        yAxisID: 'y-axis'
                    }, {
                        label: 'Average Rating',
                        tension: 0.000001,
                        borderColor: "#05562e",
                        backgroundColor: "#05562e",
                        fill: false,
                        data: vm.yAxisAvgLabelRating,
                        yAxisID: 'y-axis'
                    }]
                };
            }


            var chartOptions = {
                scales: {
                    xAxes: [vm.xAxisLabel],
                    yAxes: [{id: "y-axis"}
                    ]
                }
            };
            if (ctx != null) {
                if(vm.barChart2){
                    vm.barChart2.destroy();
                }

                vm.barChart2 = new Chart(ctx, {
                    type: 'line',
                    data: vm.ratingData,
                    options: chartOptions
                });

            }

        };

        vm.studentRating = function (studentRatingData,studentName) {

            var xAxis = [];
            for (var key in studentRatingData) {
                xAxis.push(studentRatingData[key].month);
            }
            vm.xAxisLabel = xAxis;


            var yAxisMaxRating = [];
            var yAxisMinRating = [];
            var yAxisAvgRating = [];
            var yAxisStudentRating = [];


            for (var key in studentRatingData) {

                yAxisMaxRating.push(parseFloat(studentRatingData[key].maxRating).toFixed(2));
                yAxisMinRating.push(parseFloat(studentRatingData[key].minRating).toFixed(2));
                yAxisAvgRating.push(parseFloat(studentRatingData[key].avgRating).toFixed(2));
                if (studentRatingData[key].studentRating != null) {
                    yAxisStudentRating.push(parseFloat(studentRatingData[key].studentRating).toFixed(2));
                }

            }
            vm.yAxisMaxLabelRating = yAxisMaxRating;
            vm.yAxisMinLabelRating = yAxisMinRating;
            vm.yAxisAvgLabelRating = yAxisAvgRating;
            if (yAxisStudentRating.length > 0) {
                vm.yAxisStudentLabelRating = yAxisStudentRating;
            }

            studentRatingChart(studentName);


        };

        vm.getStudentRating = function () {
            var reqMap = {
                className: "EOStudentRating"
            };
            reportSvcs.getStudentRating(reqMap, function (res) {
                vm.studentRatingData = res.data;
                vm.studentRating(vm.studentRatingData,"");
            }, function (err) {
                notifySvcs.error({
                    content: "Error Occured."
                })
            })
        }();

        function setStudentData(studentData, callback) {

            for (var key in studentData) {
                vm.studentRatingData[key]['studentRating'] = studentData[key].maxRating;
            }
            callback(vm.studentRatingData);
        }

        vm.getRatingByStudent = function (instance) {

            var studentPk = JSON.parse(instance).primaryKey;
            vm.studentName = JSON.parse(instance).studentfullName;

            var reqMap = {
                studentPk: studentPk+""    //convert studentPk from int to string(nikita)
            };
            reportSvcs.getStudentRatingByPk(reqMap, function (res) {
                setStudentData(res.data, function (res) {
                    vm.studentRating(res,vm.studentName);
                });
            }, function (err) {
                notifySvcs.error({
                    content: "Error Occured."
                });
            })


        };

        /***********************Student Rating End**********************************/

        /*$scope.pageLoader.stop();*/

    }]);

})(window, window.document, window.jQuery, window.angular);
