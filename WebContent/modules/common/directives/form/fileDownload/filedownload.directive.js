/**
 * Created by my on 2017-04-26.
 */


(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("vfileDownloadDrct", ["$timeout", "http", "vformSvcs", "$log", "$document", function ($timeout, http, vformSvcs, $log, $document) {
        return {
            replace: true,
            scope: {
                config: '='
            },
            templateUrl: 'modules/common/directives/form/fileDownload/filedownload.html',
            link: function (scope, elm, attr) {
                //console.log(scope, elm, attr);
                //scope.viewOptions = scope.config.view || ['pdf', 'excel'];
                scope.clickEvent = function (e, type) {
                    if (scope.config.click && typeof(scope.config.click) == "function") {
                        scope.config.click (e, type, {scope: scope, element: elm, attribute: attr});
                    }
                };
                scope.clickEventExcel = function (e, type) {
                    if (scope.config.clickExcel && typeof(scope.config.clickExcel) == "function") {
                        scope.config.clickExcel(e, type, {scope: scope, element: elm, attribute: attr});
                    }
                };
                var defaultSettings = {
                    pageBreak: 'auto',  // 'auto', 'avoid' or 'always'
                    tableWidth: 'auto', // 'auto', 'wrap' or a number,
                    margin: {top: 80},  // Margin from top
                    theme: 'grid',      // layout of table
                    styles: {
                        overflow: 'linebreak'
                    }
                };

                /* scope.config.exportData = function (tableId, tableName) {
                 console.log(document.getElementById('exportable'));
                 var blob = new Blob([tableId.innerHTML], {
                 type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
                 });
                 saveAs(blob, tableName);
                 };*/
                scope.config.generatePdf = function (title, tableName, rows, columns, options) {
                    //console.log("title, tableName", title, tableName, rows, columns);
                    //Only pt supported (not mm or in)
                    var doc = new jsPDF('p', 'pt');
                    var xx = 0;
                    var yy = 60;
                    var fontSize = doc.internal.getFontSize();
                    var pageWidth = doc.internal.pageSize.width;
                    var txtWidth = doc.getStringUnitWidth(tableName) * fontSize / doc.internal.scaleFactor;
                    xx = ( pageWidth - txtWidth ) / 2;
                    doc.text(xx, yy, tableName); // Name of Table
                    doc.setProperties({
                        title: title // Title of Pdf File
                    });
                    doc.autoTable(columns, rows, (options || defaultSettings));
                    doc.output("dataurlnewwindow");
                };
            }
        };
    }]);
})(window, window.document, window.jQuery, window.angular);
