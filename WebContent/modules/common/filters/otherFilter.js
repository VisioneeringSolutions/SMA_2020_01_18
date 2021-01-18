/**
 * Created by Kundan Kumar on 01-Jun-20.
 */
(function (window, document, $, angular) {

    angular.module('commonApp').factory("sorting", [function () {

        return new (function () {
            this.listOfObject = function (tempData, typeOfSorting) {
                for(i = 0; i < tempData.length; i++){
                    for(j = i+1; j < tempData.length; j++){
                        if(tempData[i].sortingKey > tempData[j].sortingKey){
                            temp  =  tempData[i];
                            tempData[i] =  tempData[j];
                            tempData[j] = temp;
                        }
                    }
                }
                var dscData = [];
                for(k = tempData.length - 1 ; k >= 0 ; k--){
                    dscData.push(tempData[k]);
                }
                if(typeOfSorting === "DSC"){
                    //console.log("dscData::",dscData);
                    return dscData;
                }else{
                    //console.log("tempData::",tempData);
                    return tempData;
                }
            };
        })()

    }]);

})(window, window.document, window.jQuery, window.angular);


