
(function (window, document, $, angular) {

    angular.module('musicAuth').factory("authModel", [function () {


        function isValidEmail(text) {
            return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
        }

        function isValidNumber(text) {
            return /^(?=.{1,254}$)(?=.{1,64}@)[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+(\.[-!#$%&'*+\/0-9=?A-Z^_`a-z{|}~]+)*@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\.[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*$/.test(text);
        }

        var InstanceGenerator = new function () {

            var InstanceFns = {
                addPersonalAccount: function () {
                    this.className = "EOCustomer";
                    this.prefix = "Mr.";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.email = "";
                    this.phone = "";
                    this.emailSubscription = "true";
                    this.userName = "";
                    this.password = "";
                    this.lkSecurityQuestion = "";
                    this.securityAnswer = "";

                },
                addBusinessAccount: function () {
                    this.className = "EOCustomer";
                    this.prefix = "Mr.";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.email = "";
                    this.phone = "";
                    this.businessType = "";
                    this.businessName ="";
                    this.address1 ="";
                    this.address2 = "";
                    this.postalCode = "";
                    this.businessID = "";
                    this.emailSubscription = "true";
                    this.userName = "";
                    this.password = "";
                    this.lkSecurityQuestion = "";
                    this.securityAnswer = "";

                }
            };

            this.create = function (type) {
                if (InstanceFns[type]) {
                    return new InstanceFns[type]();
                }
            };
        }();

        return {
            getInstance: InstanceGenerator.create,
            isValidEmail: isValidEmail,
            isValidNumber: isValidNumber
        }
    }]);
})(window, window.document, window.jQuery, window.angular);