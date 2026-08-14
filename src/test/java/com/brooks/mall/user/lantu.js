function hasten_dbrw_cb() {

    var rows = getGridSelectedRows();
    if (rows.length == 0) {
        $.simpleAlert("请选择一条数据", 'info', 2000);
        return;
    }

    var ids = "";
    var zxzts = "";
    for (var i = 0; i < rows.length; i++) {
        ids += getGridFieldValue(rows[i].rowData, 'ID')+",";
        zxzts += getGridFieldValue(rows[i].rowData, 'ZXZT')+",";
    }
    var sid = $("#sid").val();
    if (zxzts != "5,"){
        if (confirm("确定要发起催办吗？")) {
            //发送请求
            $.ajax({
                type: "post", async: true, cache: false, dataType: "text",
                url: './jd?',
                data: [
                    { name: "sid", value: sid },
                    { name: "cmd", value: "com.awspaas.user.apps.meetingmgt_promptResponsibleAndExecutor" },
                    { name: "ids", value: ids }
                ],
                success: function (r) {
                    //通过返回的r
                    var result = r;
                    if (result != null && result != "") {
                        debugger
                        if(result.includes("通知成功")){
                            $.simpleAlert(result, '', 2000);
                        }else{
                            $.simpleAlert(result, 'warning', 2000);
                        }

                    }else {
                        $.simpleAlert("无需催办", '', 2000);
                    }

                },
                error: function () {
                    $.simpleAlert("配置错误，请联系管理员", 'warning', 2000);
                }
            });
        }
    }else if (zxzts == "5,"){
        $.simpleAlert("无需催办", '', 2000);
    }




}