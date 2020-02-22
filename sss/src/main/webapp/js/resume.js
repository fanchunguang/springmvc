/**
 * 显示列表
 */
function getList(data){
    for (let i = 0; i < data.length; i++) {
        const argument = data[i];
        $("#resumeBody").append(
            '<tr>' +
                '<td hidden><input type="text" id="id" value="'+argument.id+'"></input></td>'+
                '<td><input  type="text" readonly style="border: none outline:0px" value="'+argument.name+'"></input></td>' +
                '<td><input  type="text" readonly style="border: none" value="'+argument.address+'"></input></td>' +
                '<td><input  type="text" readonly style="border: none" value="'+argument.phone+'"></input></td>'+
                '<td ><button id="edit1" >编辑</button></td>'+
                '<td ><a href="javascript:void(0)" onclick="del(\''+argument.id+'\')">删除</a></td>'+
            '</tr>'
        );
    }
    let isNew = false;
    $("#resumeBody td button[id='edit1']").click(function () {
        let children = $(this).parents("tr").children("td");
        children.each(function (index,item) {
            $(this).find("input:text").removeAttr("readonly");
            if(index==0){
                let id=$(this).find("input:text").attr("value");
                if(id==null || id==undefined){
                    isNew = true;
                }
            }
        })
        let str = $(this).text()=="编辑"?"保存":"编辑";
        $(this).text(str);
        if(str ==="编辑"){
            let id,name,address,phone
            children.each(function (index,item) {
                if(index==0)
                    id=$(this).find("input:text").val();
                if(index==1){
                    name=$(this).find("input:text").attr("value");
                }
                if(index==2){
                    address=$(this).find("input:text").attr("value");
                }
                if(index==3){
                    phone=$(this).find("input:text").attr("value");
                }
            })
            let resumeData={
                id:id,
                name:name,
                address:address,
                phone:phone
            }
            save(resumeData)
        }
    });
}

function save(resumeData){
    $.ajax({
        url:'/resume/save',
        type:'POST',
        dataType: 'json',
        data:resumeData,
        success:function(res){
            if(res.success){
                initResumeData();
            }
        }
    })
}

/**
 * 删除方法
 */
function del(resumeId){
    $.ajax({
        url:'/resume/del/'+resumeId,
        type:'post',
        dataType:'json',
        success:function(res){
            if(res.success){
                initResumeData();
            }

        }
    })
}

function initResumeData(){
    $.ajax({
        url:'/resume/list',
        type: 'post',
        dataType:'json',
        success:function(res){
            $("#resumeBody").html("");
            getList(res.data)
        }
    })
}