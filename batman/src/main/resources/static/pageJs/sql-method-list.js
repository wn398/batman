$(document).ready(function () {
    //var tablePrefix = "#entityTable_";
    var entityId = $("#entityId").val();
    $('#methodTable').DataTable({
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
        "order": [[ 7, "desc" ]],//默认首次展示时从按第8列倒序展示
        "serverSide": false,//是否开启服务端
        "ajax": {
            url: getRootPath()+'/entitiesCtl/getEntitySqlMethodData/'+entityId,
            dataSrc: 'data',
            type: "POST"
        },
        //组织数据
        "columns": [
            {"data": "name"},
            {"data": "description"},
            {"data": "isRestful"},
            {"data": "paramClassName"},
            {"data": "resultClassName"},
            {"data": "isPage"},
            {"data": "createDate"},
            {"data": "updateDate"},
            {"data": "version"},
            {
                "data": function (e) {//这里给最后一列返回一个操作列表 e是得到的json数组中的一个item ，可以用于控制标签的属性。
                    return '<a class="btn btn-success btn-xs" onclick=showUpdate("'+e.id+'",this,"'+entityId+'")>更新</a>'+'&nbsp;&nbsp;'
                        +'<a  class="btn btn-danger btn-xs" onclick=deleteById("'+e.id+'",this)>删除</a>';
                }
            }
        ],
        "columnDefs":[
            {
                //设置第4列不参与搜索
                "targets":[2],
                "searchable":false
            },
            {
                //设置第5列不参与搜索
                "targets":[3],
                "searchable":false
            },
            {
                //设置第6列不参与搜索
                "targets":[4],
                "searchable":false
            },
            {
                //设置第7列不参与搜索
                "targets":[5],
                "searchable":false
            }
        ]
    });

});

//到增加方法页面
function showAddSqlMethod(entityId) {
    layer.open({
        type: 2,
        title: '新增',
        shade: 0.8,
        maxmin:true,
        area: ['75%', '75%'],
        zIndex: 1999,
        shadeClose: false, //点击遮罩关闭
        content: getRootPath()+'/entitiesCtl/goAddSqlMethod/'+entityId,
        btn: ['提交', '关闭'], //只是为了演示
        yes: function(index,layero){
            var form = layer.getChildFrame('form',index);//父页面获取子frame中DOM元素
            //console.log(form);
            var data = $(form).serializeJSON();//把表单数据转化成json数据
            //console.log(data);
            //var data = $(form).serializeObject();
            saveMethod(index,getRootPath()+'/sqlMethodCtl/doAdd',data,entityId);

        },
        btn2: function(index,layero){
            layer.close(index);
            //layer.closeAll();
        }
    });
}

//保存方法
function saveMethod(index,url,data,entityId) {
    $.ajax({
        type: "POST",
        url: url,
        async:false,
        data: data,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            if(data.status=="SUCCESS"){
               // divLoadUrl('entities','/projectCtl/goEntitiesList/'+projectId)//重新加载
                $("#methodTable").DataTable().ajax.reload();
                layer.close(index); //关闭窗口
                layerSuccessMsg("新增成功！")
            }else if(data.status=="FAILURE"){
                layerFailMsg(data.info);

            }else if(data.status=="NOT_VALID"){
                var message = data.validMessage;
                var result='';
                for(var key in message)
                    result=result+message[key];
                layer.alert(result);

            }
        },
        error: function (msg) {
            alert("保存失败!"+msg.responseText)
        }
        // error: function(XMLHttpRequest, textStatus, errorThrown) {
        //     alert(XMLHttpRequest.status);
        //     alert(XMLHttpRequest.readyState);
        //     alert(textStatus);
        // }
    });
}

//按id删除方法
function deleteById(methodId,obj,entityId) {
    layer.confirm('您确定要删除？', {
            btn: ['确定', '取消'] //按钮
        },

        function (index,layero) {

            $.ajax({
                type: "post",
                url: getRootPath()+"/sqlMethodCtl/deleteById/"+methodId,
                async:false,
                //data: '{"id":"'+methodId+'"}',
                contentType: "application/json; charset=utf-8",
                //dataType: "json",
                success: function (data) {
                    // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                    $("#methodTable").DataTable().row($(obj).parents("tr")).remove().draw(false);
                    //$(obj).parents("tr").remove();
                    layer.close(index);
                    layer.msg('删除成功', {
                        icon:1,
                        time: 2000, //2s后自动关闭
                    });
                },
                error: function (msg) {
                    alert("删除出错:"+msg.responseText);
                }
            });


        },

        function () {
            layer.close();
        });
}

//到更新方法页面
function showUpdate(id,obj,enitityId) {
    layer.open({
        type: 2,
        title: '更新方法',
        shade: 0.8,
        maxmin:true,
        area: ['1024px', '600px'],
        shadeClose: true, //点击遮罩关闭
        content: getRootPath()+'/sqlMethodCtl/goUpdate?id='+id+"&entityId="+enitityId,
        btn: ['更新', '取消'], //只是为了演示
        yes: function(index,layero){
            var form = layer.getChildFrame('form',index);//父页面获取子frame中DOM元素
            var data = $(form).serializeObject();//把表单数据转化成json数据
            doUpdate(index,getRootPath()+'/sqlMethodCtl/doUpdate',JSON.stringify(data),enitityId);

        },
        btn2: function(){
            layer.close();
            //layer.closeAll();
        }
    });
}
//更新方法
function doUpdate(index,url,data,enitityId){
    $.ajax({
        type: "POST",
        url: url,
        async:false,
        data: data,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            if(data.status=="SUCCESS") {
                //divLoadUrl('entities','/projectCtl/goEntitiesList/'+projectId);//重新加载
                $("#methodTable").DataTable().ajax.reload();
                layer.close(index); //关闭窗口
                layerSuccessMsg("更新成功！")
            }else if(data.status=="FAILURE"){
                layerFailMsg(data.info);
            }else if(data.status=="NOT_VALID"){
                var message = data.validMessage;
                var result='';
                for(var key in message)
                    result=result+message[key]+"&nbsp;";
                showInfo(result)
            }
        },
        error: function (msg) {
            alert("更新失败"+msg.responseText)
        }
    });
}

