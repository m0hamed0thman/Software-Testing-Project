/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 3.5833333333333335, "KoPercent": 96.41666666666667};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.009285714285714286, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "https://parabank.parasoft.com/parabank/logout.htm"], "isController": false}, {"data": [0.0, 500, 1500, "https://parabank.parasoft.com/parabank/transfer.htm"], "isController": false}, {"data": [0.0, 500, 1500, "Test"], "isController": true}, {"data": [0.0, 500, 1500, "https://parabank.parasoft.com/parabank/openaccount.htm"], "isController": false}, {"data": [0.0, 500, 1500, "https://parabank.parasoft.com/parabank/billpay.htm"], "isController": false}, {"data": [0.065, 500, 1500, "https://parabank.parasoft.com/parabank/login.htm"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 1200, 1157, 96.41666666666667, 1508.6033333333341, 78, 154610, 208.0, 2766.1000000000035, 5015.9, 26813.450000000026, 5.571392755332287, 3.576772486373302, 2.9003660506602102], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["https://parabank.parasoft.com/parabank/logout.htm", 200, 200, 100.0, 526.8350000000002, 79, 17951, 199.0, 684.0, 1725.7999999999977, 15649.13000000011, 1.0956862831034218, 0.5039675399514612, 0.5557558369262713], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/transfer.htm", 200, 200, 100.0, 908.895, 78, 43708, 178.5, 709.8, 3634.2999999999997, 27801.820000000163, 1.0966716016888742, 0.5044260980424412, 0.5583975485962603], "isController": false}, {"data": ["Test", 200, 200, 100.0, 9051.62, 1052, 158297, 2725.5, 21923.700000000008, 41587.5, 99538.21000000014, 1.0838287333835508, 4.174836307991611, 3.3853295228850437], "isController": true}, {"data": ["https://parabank.parasoft.com/parabank/openaccount.htm", 400, 400, 100.0, 1110.1100000000004, 78, 154610, 146.0, 652.200000000001, 3640.0499999999997, 14023.040000000023, 2.1441283046377495, 1.1351369109677523, 1.09801773663136], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/billpay.htm", 200, 200, 100.0, 675.5799999999998, 78, 22891, 191.0, 925.2, 3326.65, 8718.340000000011, 1.0963349522546129, 0.5042658992550404, 0.557155495584511], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/login.htm", 200, 157, 78.5, 4720.090000000002, 258, 97148, 1302.5, 7575.900000000001, 20558.99999999998, 77325.47, 1.782737750363233, 2.519414083739649, 1.0245171009118703], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.SocketException/Non HTTP response message: Connection reset", 2, 0.17286084701815038, 0.16666666666666666], "isController": false}, {"data": ["500/Internal Server Error", 9, 0.7778738115816768, 0.75], "isController": false}, {"data": ["Non HTTP response code: org.apache.http.NoHttpResponseException/Non HTTP response message: parabank.parasoft.com:443 failed to respond", 6, 0.5185825410544511, 0.5], "isController": false}, {"data": ["429/Too Many Requests", 1140, 98.53068280034572, 95.0], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 1200, 1157, "429/Too Many Requests", 1140, "500/Internal Server Error", 9, "Non HTTP response code: org.apache.http.NoHttpResponseException/Non HTTP response message: parabank.parasoft.com:443 failed to respond", 6, "Non HTTP response code: java.net.SocketException/Non HTTP response message: Connection reset", 2, "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["https://parabank.parasoft.com/parabank/logout.htm", 200, 200, "429/Too Many Requests", 200, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/transfer.htm", 200, 200, "429/Too Many Requests", 200, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/openaccount.htm", 400, 400, "429/Too Many Requests", 391, "500/Internal Server Error", 9, "", "", "", "", "", ""], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/billpay.htm", 200, 200, "429/Too Many Requests", 200, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["https://parabank.parasoft.com/parabank/login.htm", 200, 157, "429/Too Many Requests", 149, "Non HTTP response code: org.apache.http.NoHttpResponseException/Non HTTP response message: parabank.parasoft.com:443 failed to respond", 6, "Non HTTP response code: java.net.SocketException/Non HTTP response message: Connection reset", 2, "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
