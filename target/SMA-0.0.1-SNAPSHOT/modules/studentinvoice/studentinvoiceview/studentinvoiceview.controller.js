/**
 * Created by Kundan kumar on 26-May-19.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("studentInvoiceViewCtrl", ["$scope", "studentInvoiceModel","studentInvoiceSvcs","notifySvcs","$state","localStorage","modalSvcs", function ($scope, studentInvoiceModel,studentInvoiceSvcs,notifySvcs,$state,localStorage,modalSvcs) {

        var vm = this;

        vm.monthArray = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];

        vm.yearArray = window.sInstances.yearArray;

        vm.showData = false;
        vm.getData = function () {
            var reqMap  = {
                stdPk : localStorage.get("userKey"),
                year : vm.year
            };
            console.log("reqMap::",reqMap);
            studentInvoiceSvcs.getInvoiceByStudentPk(reqMap,function (res) {

                if(Object.keys(res.data).length > 0){
                    console.log("respData:",res.data);
                    vm.respData = res.data;
                    vm.month = vm.respData.nameKey;
                    vm.showData  = true;
                }
                else{
                    vm.showData  = false;
                    modalInstance = modalSvcs.open({
                        callback: function () {
                        },
                        size: 'small',
                        type:'Confirm',
                        title: 'No Invoice Generated',
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
            });
        };

        vm.defaultFunction = function(){
            vm.year = vm.yearArray[0];
            vm.getData();
        }();



        vm.getInvoiceDetails = function(month){
            console.log("month:",month);

            var reqMap = {
                stdPk : localStorage.get("userKey"),
                year : vm.year,
                month :  month

            };
            console.log("reqMapvishuja:",reqMap);
            studentInvoiceSvcs.getStudentInvoicePdf(reqMap, function(res){
                if(res){
                    vm.invoiceDetailedData = res.data;
                    console.log("vm.invoiceDetailedData:",vm.invoiceDetailedData);

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
                }else{
                    notifySvcs.info({
                        content: "No data"
                    })
                }
            })
        };

            //vm.printInvoice();




    }]);

})(window, window.document, window.jQuery, window.angular);