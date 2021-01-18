/**
 * Created by vishuja
 */

(function (window, document, $, angular) {

    angular.module('expenseApp').factory("expenseModel", ["expenseSvcs", function (expenseSvcs) {

        var InstanceGenerator = new function () {
            var InstanceFns = {
                addSubAccountType: function(){
                    this.className = "EOSubAccount";
                    this.accountName = "";
                    this.isActive =true;
                    this.descriptions = "";
                },
                addAccountType: function(){
                    this.className = "EOAccountType";
                    this.accountType = "";
                    this.accountName = "";
                    this.descriptions = "";
                    this.isActive =true;
                    this.subAccountList = [];
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
