/**
 * Created by wangn20 on 2017/6/21.
 */
//在一个指定id的div内加载提交url的页面
function divLoadUrl(id,url) {
    $("#"+id).load(getRootPath()+url);
}
//获取请求的根路径
function getRootPath(){
    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
    var curWwwPath=window.document.location.href;
    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht=curWwwPath.substring(0,pos);
    //获取带"/"的项目名，如：/uimcardprj
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName);
}


//layer弹出层显示成功信息，2秒后自动关闭
function layerSuccessMsg(msg){
    layer.msg(msg, {
        icon:1,
        time: 1800, //2s后自动关闭
    });
}
//layer弹出层显示失败信息
function layerFailMsg(msg){
    layer.open({
        title:'提示信息',
        type: 1,
        skin: 'layui-layer-rim', //加上边框
        area: ['420px', '240px'], //宽高
        content: msg
    });
}


//表格增加一行，tableId为要增加的表格id，demoTableId为demo表格的id(为的是获取其中的<tr>..</tr>添加到tableId表格里面)
function addTr(tableId,demoTableId) {
    $("#"+tableId+ " tbody").append($("#"+demoTableId+" tbody").html());
    //$("#"+tableId+ " tbody").html($("#"+tableId+ " tbody").html()+$("#"+trId+" tbody").html())
    //alert($("#"+tableId+ " tbody").html())

}
//表格删除一行数据 obj为this,一行表格里的任意元素，如按钮
function deleteTr(obj) {
    $(obj).parents("tr").remove();
}

// html提示框
function showInfo(data) {
    layer.open({
        title:'提示信息',
        type: 1,
        skin: 'layui-layer-rim', //加上边框
        area: ['420px', '240px'], //宽高
        content: data
    });
    
}