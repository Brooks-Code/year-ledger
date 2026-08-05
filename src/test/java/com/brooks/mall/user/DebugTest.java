package com.brooks.mall.user;

import com.alibaba.fastjson.JSONObject;
import com.brooks.mall.user.util.SnowflakeIdGenerator;
import com.brooks.mall.user.util.ZhiPuAPIUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/22 10:29
 */
public class DebugTest {
    public static void main(String[] args) {
        /*Map<String, Object> map = new HashMap<>();
        map.put("userid", "admin");
        map.put("username", "管理员");
        map.put("password", "1");
        int i = DBApi.create("orguser", "admin", map);
        System.out.println(i);*/
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        long id = generator.nextId();
        System.out.println(id);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("11");
        System.out.println(encode);
    }
    private static void getSql() {
        String sql = "select RWBH,ZXZT,RWMC from BO_EU_HY_DBRW where ZXZT IN('2','3','6')";
        //SqlUtil.getSql(sql);
        //用stream流过滤掉zxzt为2的
        //dataList.stream().filter(map -> !map.get("ZXZT").equals("2")).forEach(System.out::println);
        /*for (Map map : dataList) {
            System.out.println(map.get("RWBH")+"  "+map.get("ZXZT")+"  "+map.get("RWMC"));
        }*/
    }

    private static void zhiPuApi1() {
        String filePath = "w27产销平衡委员会会议纪要.docx";
        String prompt = "你是一名资深的软件产品专家，请设计一个每天早上定时发送给领导查看的今日会议安排手机端html交互原型，具体要求如下：\n" +
                "1.原型默认页面需包含改名领导今日需参加的每个会议相关信息，包含会议名称、开始/结束时间、会议地点、预计参会人、查看会议议题按钮；\n" +
                "2.默认页面醒目位置需有今日领导会议日程及详情的AI汇总总结内容，并给出工作安排、日程安排建议以及人性化的关心问候语用来提高情绪价值；\n" +
                "3.查看每个会议的具体议题信息时，需要有当前会议所有议题的AI汇总总结内容方便领导快速了解该会议具体讨论什么、决策什么、有啥背景等关键信息；\n" +
                "4.查看每个会议的具体议题信息时，支持查看每个议题的议题材料附件；\n" +
                "5.整体交互逻辑要清晰，交互要简洁、便捷，尽量减少用户操作次数，且操作路径要符合逻辑和直觉；\n" +
                "6.生成一份带交互的手机端html原型给我；\n" +
                "7.遇到不清晰的地方请先暂停任务向我提问，得到答复后再继续任务；";
        ZhiPuAPIUtil.ZhiPuWithHuToolHttp(filePath, prompt);
    }

    private static void zhiPuApi2() {
        String filePath = "w27产销平衡委员会会议纪要.docx";
        String prompt = "把人名挑出来";
        String jsonStr = ZhiPuAPIUtil.ZhiPuWithHuToolHttp(filePath, prompt);
        //变成json对象
        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
        Boolean result = jsonObj.getBoolean("result");
        if (result) {
            //获取json对象中的data字段
            JSONObject dateJsonObj = jsonObj.getJSONObject("date");
            if (dateJsonObj != null) {
                Object answer = dateJsonObj.get("answer");
                System.out.println(answer);
            }
        }

    }

}
