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
