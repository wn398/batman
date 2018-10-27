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
                url: getRootPath()+"/moduleCtl/delById",
                async:false,
                data: '{"id":"'+id+'"}',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                    deleteTr(obj);
                    //$(obj).parents("tr").remove();
                    layer.close(index);
                    layer.msg('删除成功', {
                        icon:1,
                        time: 2000, //2s后自动关闭
                    });
                },
                error: function (msg) {
                    alert("errror");
                }
            });
        },
        function () {
            layer.close();
        });
}



function deleteRemoteProjectDataSourceTr(id,obj) {
    layer.confirm('您确定要删除？', {
            btn: ['确定', '取消'] //按钮
        },

        function (index,layero) {

            $.ajax({
                type: "delete",
                url: getRootPath()+"/projectDataSourceCtl/delById",
                async:false,
                data: '{"id":"'+id+'"}',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                    deleteTr(obj);
                    //$(obj).parents("tr").remove();
                    layer.close(index);
                    layer.msg('删除成功', {
                        icon:1,
                        time: 2000, //2s后自动关闭
                    });
                },
                error: function (msg) {
                    alert("errror");
                }
            });
        },
        function () {
            layer.close();
        });
}

function testConnection(obj) {
    var form = $(obj).parents("tr");
    var dataBaseType = $(form).find("select[name='projectDataSources[][dataBaseType]']").val();
    var dataBaseName = $(form).find("input[name='projectDataSources[][dataBaseName]']").val();
    var username = $(form).find("input[name='projectDataSources[][username]']").val();
    var password = $(form).find("input[name='projectDataSources[][password]']").val();
    var hostName = $(form).find("input[name='projectDataSources[][hostName]']").val();
    var port = $(form).find("input[name='projectDataSources[][port]']").val();

    var data=new Object();
    data["dataBaseType"]=dataBaseType;
    data["dataBaseName"]=dataBaseName;
    data["username"]=username;
    data["password"]=password;
    data["hostName"]=hostName;
    data["port"]=port;

    data = JSON.stringify(data);
    $.ajax({
        type: "POST",
        url: getRootPath()+"/entitiesCtl/testDatabaseConnection",
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