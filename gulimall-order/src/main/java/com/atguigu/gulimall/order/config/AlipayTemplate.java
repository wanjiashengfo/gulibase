package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016102900777637";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDHD8yZJ153B3k6jUP/tScV21Fe1K9Qx5T41pOQacrolH6u3AhrOTQZDXbpzDnaN9YY6hqNYkEIvV9XxdaI0p4mZnN1o9hRMtOHxCuvWr+9040C5URWuhIUFplfMYvSj2C2C61atYLBh/o6b9JcVn62U021qyNYkmsVf2+DbVdrOO7FP8x+O2PN9xbIb7erUktpTj1JLC4JqxKckINARJrlOjHN06vpGBRuGVWYVAe2/YP1uLoeukjFdVWA3m4tpXZ5KdOmj87aFFI5+OepuDsYo9Uk7VIDTbiz8syaRuPLhAqhVCupvKaytOs3EFxellnHvACelZGe5nQIt/ATWlfvAgMBAAECggEBAJHp09iD0iu/763qtOhwOkk8LUYs8qFs2pcYeqxFz/VyO/Yi1Yz7LmQayYqRJ+r8ONSkbXgriG5Gmee13g8HC+Qr/wBBbKQAdKu1MSxlgd08f8WaluBXnj7ZXe04XTyn2oU2xAiUulbwPIyvQOxsfo9ZOsIqw0r+cL4eXnhhIs41bVijYbwhfotSoSM2qsyWh9kYcSC6k5XWzyBCG1Peb58K6T5v34rHNY0U8SieQUxJCK0CZWZ4n25igd2+S12RRdbFge6W+bHYMLljrSNe0XNLUb9vSHubYzfgmspOhLr6QSTLRPD/1WELuNtv2rmZlsHufFZOhVOr5PipckC3MqkCgYEA/FLjX2dPj6eIPf8/c+IKg70OKzZiR6v93UryMpgEYfFSZ4go3dtFbSeEbxskjPNBKiqAOSl8WSr4emo/4+kdG8Ygq86yu71M8V7v2KwbU5IcErGWKmnn0e/Wbh8RqjoFk+k4+70oRV1/Lj6tILObBcPD/gHfoLlmljpqQyKSz70CgYEAyfZBXQvnqcUAfGyoIUeTdczPFtYsPrU64qZOQni3qKgig6PBZmVlTO563I7exw6bQgKgQH5k9fSiKwx41jph/9F73TBN4KMWGGj4pHVURr+2NPo7vMZLrEocC7eNSVWPDAWHcoORRXk08HgKRROVRUG4UQeQNVHiiOrOWERjmxsCgYBL0ehq6ZNZumDRKFr7ymeYcH3KdFwR51ewYG3o0A85ExfM7nhBOjthcmd0L3bLN+lT71N6WcH41VW3CyhJpunNR7rXhZPloBt3yCxQMl8/vH7dCynxcsNrLSOFEabwPOEiMgmic5OfTFoEHmdNlUq27718HToChNOTvtfhdokhDQKBgQCA8gRLlxVDFG+6AVnbXaqtlKVH7Fh1dRpyqUHgONjyCt92cvBBI03pL34sId/k8dN48tpEMy7cxmNf3uerx7zYK3TNBVFDsnyh+yt9PRggz7hS3M2VOnoIN4zugjPn9YOOZl1TZOM0my2Mtue/sMAmZs56lbyh/nUHe/tg6dE6rwKBgBWYAMMRoOJB+esed6PF7ZrRnw0db1Jerh6hxunTYFQzqZTwz3SpYui0GhnAVoXNpW4iPdfm/wLuUI9T8eJw2T0/jufF5NCot5pG2qi1v/DOcUvxFon1UfBobUdpzLsZf9KRFEG4GlxLPazbPrOJHrh0dkPHOTKnIjU/ndKUJeYO";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmKuDk/QNy4xBvLyPCmc+tGe/eORdiZBC1slytk4f8MAUD+uq0PAuuuZTl6s6BHI3DtRTUs3isWrMMArPl3VAwGN/LPJrbkA1SOUsH5PrtjuktKQ8B2wlDhLG9FesJK/bJ2W6WcKTwBfMx/CCLdmaUtWcfE+FWJXLCnEuUYRn8JV6ffgp88azoDC1F4Fa9dXEGCgI8iiYFaBfj142a64rntct2nicgXC0nj4wEFZV5A1oUry0D42rC77ErB7s+m/9ieR8R3W8L1HOurqnTES2uEUzaPB5UtuRLh+N+FXcpo79NAjjdfM1G09ltVj+nHvydncxb54FiN4vx720MAvjkwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://eljn7nlm4l.52http.tech/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url= "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
