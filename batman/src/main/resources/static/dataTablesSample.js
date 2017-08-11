/**
 * Created by wangn20 on 2017/7/6.
 */
/**
 * Created by wangn20 on 2017/7/4.
 */
$(document).ready(function () {
    var tablePrefix = "#projectTable_";
    $('#projectTable').DataTable({
        language: {
            lengthMenu: '<select class="form-control input-xsmall">' + '<option value="5">5</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="50">50</option>' + '<option value="100">100</option>' + '</select>条记录',//左上角的分页大小显示。
            search: '搜索',//右上角的搜索文本，可以写html标签
            processing: "载入中",//处理页面数据的时候的显示
            paginate: {//分页的样式内容。
                previous: "上一页",
                next: "下一页",
                first: "第一页",
                last: "最后"
            },
            zeroRecords: "没有内容",//table tbody内容为空时，tbody的内容。
            //下面三者构成了总体的左下角的内容。
            info: "共_TOTAL_条，当前_START_-_END_条，共_PAGES_页，初始_MAX_条",//左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",//筛选为空时左下角的显示。
            infoFiltered: ""//筛选之后的左下角筛选提示，
        },
        "pagingType": "full_numbers",//分页形式
        "stateSave": true,//保存最后一次分页信息、排序信息，当页面刷新，或者重新进入这个页面，恢复上次的状态。这个状态的保存用了html5的本地储存和session储存，这样更加有效率。如果你的数据是异步获取的，你可以使用 stateSaveCallback和 stateLoadCallback选项.默认情况下，这个状态会保存2小时，如果你希望设置的时间更长，通过设置参数 stateDuration来初始化表格
        "processing": true,//加载数据时是否显示进度条
        "ordering": false,//排序操作在服务端进行，所以可以关了。
        "order": [[ 4, "desc" ]],//默认首次展示时从按第5列倒序展示
        "serverSide": true,//是否开启服务端
        "ajax": {
            url: getRootPath() + "/projectCtl/getByPage",
            dataSrc: 'data',
            type: "POST",
            data: function (d) {//d 是原始的发送给服务器的数据，默认很长。
                var param = {};//因为服务端排序，可以新建一个参数对象
                param.start = d.start;//数据开始的序号
                param.pageSize = d.length;//要取的数据的长度，每页的大小
                param.draw = d.draw;//重绘的次数
                var formData = $("#filter_form").serializeArray();//把form里面的数据序列化成数组
                formData.forEach(function (e) {
                    param[e.name] = e.value;
                });
                return param;//自定义需要传递的参数。
            }
        },
        //初始化完成之后执行
        "initComplete": function (setting, json) {
            //初始化完成之后替换原先的搜索框。
            //本来想把form标签放到hidden_filter 里面，因为事件绑定的缘故，还是拿出来。
            $(tablePrefix + "filter").html("<form id='filter_form'>" + $("#hidden_filter").html() + "</form>");
        },
        //组织数据
        "columns": [
            {"data": "name"},
            {"data": "description"},
            {"data": "packageName"},
            {"data": "port"},
            {
                "data": function (e) {//这里给最后一列返回一个操作列表
                    //e是得到的json数组中的一个item ，可以用于控制标签的属性。
                    return '<button class="btn btn-default btn-xs show-detail-json"><i class="icon-edit"></i>显示详细</button>';
                }
            },
            {
                data: "port",
                render: function (data, type, row) {
                    if (data == 9000) {
                        return "未删除";
                    } else {
                        return "删除";
                    }

                }
            }
        ],
        "columnDefs":[
        {
            //设置第4列不参与搜索
            "targets":[3],
            "searchable":false
        },
        {
            //设置第5列不参与搜索
            "targets":[4],
            "searchable":false
        }
    ]
    });

});

$(document).on("submit", "#filter_form", function () {
    return false;
});
$(document).on("click", "#go_search", function () {
    $("#projectTable").DataTable().draw();//点搜索重新绘制table。
});
$(document).on("click", ".show-detail-json", function () {//取出当前行的数据
    alert(JSON.stringify($("#projectTable").DataTable().row($(this).parents("tr")).data()));
});

//返回数据格式
// private int start;
// private int pageSize;
// //dataTable特殊用,重绘次数
// private int draw;
// //总记录数
// private Long recordsTotal;
// //查询过滤出来的条数
// private Long recordsFiltered;
// private Object data;
// //存放错误信息
// private String error;