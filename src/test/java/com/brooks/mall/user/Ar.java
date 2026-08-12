package com.brooks.mall.user;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/10 17:13
 */
public class Ar {
    //重置会议
    /*public String resetMeeting(UserContext uc, RequestParams params) {
        // 流程表单链接
        String rtnUrl = "";
        String processDefId = params.get("processDefId");
        if (processDefId != null && !processDefId.equals("")) {
            BO hyBo = SDK.getBOAPI().query("BO_EU_HY_HYK").addQuery("ID=", params.get("id")).detail();
            //拷贝数据
            BO bgBo = BOUtils.copyDefault(hyBo);
            //流程标题名称
            String title = uc.getUserName() + "-重置会议[" + hyBo.getString("HYZHUT") + "]-会议编号[" + hyBo.getString("DJBH") + "]";
            //创建流程
            ProcessInstance processInst = SDK.getProcessAPI().createProcessInstance(processDefId, uc.getUID(), title);
            //启动流程
            SDK.getProcessAPI().start(processInst);
            //创建表单数据
            SDK.getBOAPI().create("BO_EU_HY_CZHY", bgBo, processInst, uc);

            //获取流程启动任务实例
            TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(processInst.getStartTaskInstId());
            // 拼接流程表单链接
            rtnUrl = "./" + SDK.getFormAPI().getFormURL("", uc.getSessionId(), processInst.getId(), taskInstance.getId(), taskInstance.getState(), "", "", "");

        }

        // 返回流程表单链接
        return rtnUrl;
    }*/
}
