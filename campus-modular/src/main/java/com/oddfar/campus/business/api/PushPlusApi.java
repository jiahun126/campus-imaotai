package com.oddfar.campus.business.api;

import cn.hutool.http.HttpUtil;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * @author zhiyuan
 */
public class PushPlusApi {
    public enum OperTypeEnum{
        APPOINTMENT(1,"-i茅台预约",true),
        STAMINA(2,"-i茅台获取耐力值",false),
        TRAVEL(3,"-i茅台旅行",false),
        APPOINTMENT_RESULE(4,"-i茅台预约结果",true)
        ;

        private Integer code;

        private String value;

        private boolean isSend;

        private OperTypeEnum(Integer code,String value , boolean isSend){
            this.code = code;
            this.value = value;
            this.isSend = isSend;
        }


        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isSend() {
            return isSend;
        }

        public void setSend(boolean send) {
            isSend = send;
        }
    }

    public static void sendNotice(IUser iUser, ILog operLog , OperTypeEnum operTypeEnum) {
        String token = iUser.getPushPlusToken();
        if (StringUtils.isEmpty(token)) {
            return;
        }
        if(!operTypeEnum.isSend()){
            return;
        }
        String title, content;
        if (operLog.getStatus() == 0) {
            //预约成功
            title = iUser.getRemark() + operTypeEnum.getValue() + "执行成功";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        } else {
            //预约失败
            title = iUser.getRemark() + operTypeEnum.getValue() + "执行失败";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        }


    }

    /**
     * push推送
     *
     * @param token    token
     * @param title    消息标题
     * @param content  具体消息内容
     * @param template 发送消息模板
     */
    public static TimerTask sendNotice(String token, String title, String content, String template) {
        return new TimerTask() {
            @Override
            public void run() {
                String url = "http://www.pushplus.plus/send";
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);
                map.put("title", title);
                map.put("content", content);
                if (StringUtils.isEmpty(template)) {
                    map.put("template", "html");
                }
                HttpUtil.post(url, map);
            }
        };
    }

}
