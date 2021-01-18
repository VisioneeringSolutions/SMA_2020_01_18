/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    angular.module('newsApp').factory("newsModel", ["newsSvcs", function (newsSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                news:function(){
                    this.className = "EONews";
                    this.newsDate = "";
                    this.newsDesc = "" ;
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
