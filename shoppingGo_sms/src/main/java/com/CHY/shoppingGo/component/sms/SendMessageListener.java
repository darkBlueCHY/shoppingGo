package com.CHY.shoppingGo.component.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author CHY
 * @create 2018-12-08 13:19
 */
@Component
public class SendMessageListener {
    final static String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    final static String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
    final static String accessKeyId = "LTAIgvrElTNA5zbM";
    final static String accessKeySecret = "hyWpXX4C1PkiittraXvL2xtnWxWJwG";
    final static String signName = "聚秀商城";
    final static String templateCode = "SMS_106665009";

    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

    }

    @JmsListener(destination = "userAddSendMessage")
    public void sendMessage(Map<String,String> dataMap) throws ClientException {

        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setMethod(MethodType.POST);
        request.setPhoneNumbers(dataMap.get("phoneNumbers"));
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{ \"code\":\""+dataMap.get("code")+"\"}");
        //      回调信息接口 识别用户
        //      request.setOutId("yourOutId");
        try {
            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            //返回的其他信息可以在帮助文档查看
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                System.out.println("请求成功");
                System.out.println(sendSmsResponse.getCode());
                System.out.println(sendSmsResponse.getMessage());
            } else {
                System.out.println(sendSmsResponse.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
