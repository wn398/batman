/**
 * Created by wangn20 on 2017/7/10.
 */
function deleteRemoteTr(id,obj) {

    layer.confirm('您确定要删除？', {
            btn: ['确定', '取消'] //按钮
        },

        function (index,layero) {

            $.ajax({
                type: "delete",
                url: getRootPath()+"/fieldCtl/delById",
                async:false,
                data: '{"id":"'+id+'"}',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                    deleteTr(obj);
                    //$(obj).parents("tr").remove();
                    layer.close(index);
                    layerSuccessMsg("删除成功");
                },
                error: function (msg) {
                    showInfo(msg);
                }
            });
        },
        function () {
            layer.close();
        });
}

//到配置页面
function showConfig(projectId) {
    layer.open({
        type: 2,
        title: '数据源配置',
        shade: 0.8,
        maxmin:true,
        area: ['60%', '60%'],
        shadeClose: false, //点击遮罩关闭
        content: getRootPath()+'/entitiesCtl/showConfig/'+projectId,
        btn: ['确认','测试连接','关闭'], //只是为了演示
        btn1: function(index,layero){
            var form = layer.getChildFrame('form', index);//父页面获取子frame中DOM元素
            var data = $(form).serializeJSON();//把表单数据转化成json数据
            getTableColums(index,getRootPath()+"/entitiesCtl/getTableColums",data);

        },
        btn2: function (index,layero) {
            var form = layer.getChildFrame('form',index);//父页面获取子frame中DOM元素
            var data = $(form).serializeJSON();//把表单数据转化成json数据
            testDataBaseConnection(index,getRootPath()+"/entitiesCtl/testDatabaseConnection",data);
            return false;
        },
        btn3: function (index,layero) {
            layer.close(index);
        }
    });
}


//测试数据库连接
function testDataBaseConnection(index,url,data) {
    $.ajax({
        type: "POST",
        url: url,
        async:false,
        data: data,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            if(data.status=="SUCCESS") {
                layerSuccessMsg("连接成功！")
            }else{
                layerFailMsg(data.info+":"+data.data);
            }
        },
        error: function (msg) {
            alert("失败"+msg.responseText);
        }
    });
}


//获取表信息
function getTableColums(index,url,data) {
    $.ajax({
        type: "POST",
        url: url,
        async:false,
        data: data,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data) {
            if(data.status=="SUCCESS") {

                for(var i=0;i<data.data.length;i++){
                    var field=data.data[i];
                    var html = $("#demoTr tbody").html();

                    html = html.replace('name="fields[][name]"','name="fields[][name]" value="'+field.name+'" ');
                    if(field.description!=null) {
                        html = html.replace('name="fields[][description]"', 'name="fields[][description]" value="' + field.description + '" ');
                    }
                    html = html.replace('option value="'+field.dataType+'"','option value="'+field.dataType+'" selected="selected" ');
                    $("#moduleTable tbody").append(html);
                }
                layer.close(index);
            }else{
                layerFailMsg(data.info+":"+data.data);
            }
        },
        error: function (msg) {
            alert("失败"+msg.responseText);
        }
    });
}


