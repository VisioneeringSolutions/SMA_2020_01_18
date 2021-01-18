/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    angular.module('studentInvoiceApp').factory("studentInvoiceModel", ["studentInvoiceSvcs", function (studentInvoiceSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                creditNote:function(){
                    this.className = "EOStudentCreditNote";
                    this.description = "";
                    this.amount = "" ;
                    this.month = "" ;
                    this.year = "" ;
                },
                freeInvoice:function(){
                    this.className = "EOStudentFreeTextInvoiceMapping";
                    this.description = "";
                    this.amount = "" ;
                },
                editable:function(){
                    this.className = "EOStudentInvoiceEditable";
                    this.description = "";
                    this.amount = "" ;
                }

            };
            this.create = function (type) {
                if (InstanceFns[type]) {
                    return new InstanceFns[type]();
                }
            };
        }();
        return {
            getInstance: InstanceGenerator.create
        }
    }]);

})(window, window.document, window.jQuery, window.angular);
