/**
 * Created by wangn20 on 2017/7/10.
 */
function deleteRemoteTr(id,mainEntityId,otherEntityId,obj) {
    layer.confirm('您确定要删除？', {
            btn: ['确定', '取消'] //按钮
        },

        function (index,layero) {

            $.ajax({
                type: "delete",
                url: getRootPath()+"/relationShipCtl/delById?id="+id+"&mainEntityId="+mainEntityId+"&otherEntityId="+otherEntityId,
                async:false,
                //data: '{"id":"'+id+'"}',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                    if(data.status=="SUCCESS") {
                        deleteTr(obj);
                        //$(obj).parents("tr").remove();
                        layer.close(index);
                        layer.msg('删除成功', {
                            icon:1,
                            time: 2000, //2s后自动关闭
                        });
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
                    showInfo(msg)
                }
            });
        },
        function () {
            layer.close();
        });
}

//删除字段关联关系
function deleteFieldRemoteTr(id,mainEntityId,otherEntityId,obj) {
    layer.confirm('您确定要删除？', {
            btn: ['确定', '取消'] //按钮
        },

        function (index,layero) {

            $.ajax({
                type: "delete",
                url: getRootPath()+"/fieldRelationShipCtl/delById?id="+id+"&mainEntityId="+mainEntityId+"&otherEntityId="+otherEntityId,
                async:false,
                //data: '{"id":"'+id+'"}',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    if(data.status=="SUCCESS") {
                        // alert(JSON.stringify($("#projectTable").DataTable().row($(obj).parents("tr")).data()));
                        deleteTr(obj);
                        //$(obj).parents("tr").remove();
                        layer.close(index);
                        layer.msg('删除成功', {
                            icon:1,
                            time: 2000, //2s后自动关闭
                        });
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
                    showInfo(msg)
                }
            });
        },
        function () {
            layer.close();
        });
}

//级联根据上一个实体类获取这个实体类的所有属性
function changeFields(obj) {
    var otherEntity = $(obj).val();
    if(otherEntity ==''){
        return;
    }

    $.ajax({
        url:getRootPath()+'/fieldCtl/getFieldsByEntity/'+otherEntity,
        async:false,
        dataType:"json",
        contentType: "application/json; charset=utf-8",
        type:'get',
        success:function(data){
            var t2 = $(obj).parent().next().find("select")
            t2.empty()
            for ( var i = 0; i < data.data.length; i++) {
                t2.append("<option value='"+data.data[i].id+"'>"+data.data[i].description+"("+data.data[i].name+")"+"</option>");
            }
        }
    });
}