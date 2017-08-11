$(document).ready(function () {
    //var tablePrefix = "#entityTable_";
    $('#projectTableForCode').DataTable({
        language: {
            lengthMenu: '<select class="form-control input-xsmall">' + '<option value="5">5</option>' + '<option value="10">10</option>' + '<option value="20">20</option>' + '<option value="50">50</option>' + '<option value="100">100</option>' + '</select>条记录',//左上角的分页大小显示。
            search: '搜 索',//右上角的搜索文本，可以写html标签
            processing: "载入中",//处理页面数据的时候的显示
            paginate: {//分页的样式内容。
                previous: "上一页",
                next: "下一页",
                first: "第一页",
                last: "最后"
            },
            zeroRecords: "没有内容",//table tbody内容为空时，tbody的内容。
            //下面三者构成了总体的左下角的内容。
            info: "共_TOTAL_条，当前_START_-_END_条，共_PAGES_页，初始_MAX_条 ",//左下角的信息显示，大写的词为关键字。
            infoEmpty: "0条记录",//筛选为空时左下角的显示。
            infoFiltered: ""//筛选之后的左下角筛选提示，
        },
        "pagingType": "full_numbers",//分页形式
        "stateSave": false,//保存最后一次分页信息、排序信息，当页面刷新，或者重新进入这个页面，恢复上次的状态。这个状态的保存用了html5的本地储存和session储存，这样更加有效率。如果你的数据是异步获取的，你可以使用 stateSaveCallback和 stateLoadCallback选项.默认情况下，这个状态会保存2小时，如果你希望设置的时间更长，通过设置参数 stateDuration来初始化表格
        "processing": true,//加载数据时是否显示进度条
        "ordering": true,//排序操作在服务端进行，所以可以关了。
        "order": [[ 4, "desc" ]],//默认首次展示时从按第5列倒序展示
        "serverSide": false,//是否开启服务端
        // "ajax": {
        //     url: getRootPath() + "/projectCtl/getAll",
        //     dataSrc: 'data',
        //     type: "POST"
        // },
        // //组织数据
        // "columns": [
        //     {"data": "name"},
        //     {"data": "description"},
        //     {"data": "packageName"},
        //     {"data": "createDate"},
        //     {"data": "updateDate"},
        //     {"data": "version"},
        //     {
        //         "data": function (e) {//这里给最后一列返回一个操作列表 e是得到的json数组中的一个item ，可以用于控制标签的属性。
        //             return '<a class="btn btn-success btn-xs" onclick=showUpdate("'+e.id+'",this)>更新</a>'+'&nbsp;&nbsp;'
        //                 +'<a  class="btn btn-danger btn-xs" onclick=deleteById("'+e.id+'",this)>删除</a>';
        //         }
        //     }
        // ],
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
            },
            {
                //设置第6列不参与搜索
                "targets":[5],
                "searchable":false
            },
            {
                //设置第7列不参与搜索
                "targets":[6],
                "searchable":false
            }
        ]
    });

});