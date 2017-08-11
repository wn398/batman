/**
 * Created by wangn20 on 2017/7/7.
 */
$(document).ready(function() {
    var table = $('#example').DataTable();
    $('button').click(function() {
        var data = table.$('input, select').serialize();
        alert("The following data would have been submitted to the server: \n\n" + data.substr(0, 120) + '...');
        return false;
    });
});
