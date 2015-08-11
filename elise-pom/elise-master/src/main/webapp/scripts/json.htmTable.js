/************************************************/
/* Build a Dynamic HTML table from JSON results */
/* By: Zachary Hunter                           */
/* On: April 1, 2010                            */
/************************************************/

function CreateTableView(objArray, theme, enableHeader) {
    // set optional theme parameter
    if (theme === undefined) {
        theme = 'mediumTable'; //default theme
    }

    if (enableHeader === undefined) {
        enableHeader = true; //default enable headers
    }

    // If the returned data is an object do nothing, else try to parse
    var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;

    var str = '<table class="' + theme + '">';
    
    // table head
    if (enableHeader) {
        str += '<thead><tr>';
        for (var index in array[0]) {
            str += '<th scope="col">' + index + '</th>';
        }
        str += '</tr></thead>';
    }
    
    // table body
    str += '<tbody>';
    for (var i = 0; i < array.length; i++) {
        str += (i % 2 == 0) ? '<tr class="alt">' : '<tr>';
        for (var index in array[i]) {
            str += '<td>' + array[i][index] + '</td>';
        }
        str += '</tr>';
    }
    str += '</tbody>'
    str += '</table>';
    return str;
}

function CreateDetailView(objArray, theme, enableHeader) {
    // set optional theme parameter
    if (theme === undefined) {
        theme = 'mediumTable';  //default theme
    }

    if (enableHeader === undefined) {
        enableHeader = true; //default enable headers
    }
    
    var array = JSON.parse(objArray);

    var str = '<table class="' + theme + '">';
    str += '<tbody>';


    for (var i = 0; i < array.length; i++) {
        var row = 0;
        for (var index in array[i]) {
            str += (row % 2 == 0) ? '<tr class="alt">' : '<tr>';

            if (enableHeader) {
                str += '<th scope="row">' + index + '</th>';
            }
            
            str += '<td>' + array[i][index] + '</td>';
            str += '</tr>';
            row++;
        }
    }
    str += '</tbody>'
    str += '</table>';
    return str;
}
