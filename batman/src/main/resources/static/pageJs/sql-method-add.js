//对选择项名字进行排序
$(document).ready(function () {
    var trList = $(".objTable tbody").children("tr");
    for(var i=0;i<trList.length;i++){
        var tr = trList[i];
        var selectList = $(tr).find("select");
        for(var j=0;j<selectList.length;j++){
            var selectAttrName = selectList.eq(j).attr("name");
            if(selectAttrName.indexOf("[]")>0){
                selectList.eq(j).attr("name",selectAttrName.replace("[]","["+i+"]"))
            }

        }
        var inputList = $(tr).find("input")
        for(var k=0;k<inputList.length;k++){
            var inputName = inputList.eq(k).attr("name");
            if(inputName.indexOf("[]")>0){
                inputList.eq(k).attr("name",inputName.replace("[]","["+i+"]"))
            }
        }
    }
})

function selectThisTable(it) {
    var isSelected = $(it).is(':checked');
    if(isSelected){
        $(it).parents('table').find(".result").removeProp('checked')
        $(it).parents('table').find(".result").prop('checked',true)
    }else{
        $(it).parents('table').find(".result").removeProp('checked')
        $(it).parents('table').find(".result").prop('checked',false)
    }
    
}