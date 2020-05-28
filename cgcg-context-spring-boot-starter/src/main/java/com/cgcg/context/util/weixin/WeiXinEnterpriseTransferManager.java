package com.cgcg.context.util.weixin;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.enums.RspCodeEnum;
import com.cgcg.context.model.PayResult;
import com.cgcg.context.util.StringUtils;
import com.cgcg.context.util.UUIDUtils;
import com.cgcg.context.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 微信企业付款通用工具类
 * @author xujinbang
 * @date 2019/10/29.
 */
@Slf4j
public class WeiXinEnterpriseTransferManager {

    private static WeiXinEnterpriseTransferManager weiXinEnterpriseTransferManager;

    /**
     * 要确保该类只有一个实例对象，避免产生过多对象消费资源，所以采用单例模式
     */
    private WeiXinEnterpriseTransferManager() {

    }

    public synchronized static WeiXinEnterpriseTransferManager getInstance() {
        if (weiXinEnterpriseTransferManager == null) {
            weiXinEnterpriseTransferManager = new WeiXinEnterpriseTransferManager();
        }
        return weiXinEnterpriseTransferManager;
    }

    /**
     * 封装基本参数
     * @param openId
     * @param amount
     * @param ip
     * @return
     */
    private SortedMap<String, Object> getPayParam(String openId, BigDecimal amount, String ip,Map<String,String> baseParamMap) throws Exception{
        SortedMap<String, Object> payParam = new TreeMap<>();
        log.info("ip地址："+ip);
        payParam.put("mch_appid", baseParamMap.get("mch_appid"));
        payParam.put("mchid", baseParamMap.get("mchid"));
        payParam.put("nonce_str", StringUtils.generateRandomDigitString(16).toUpperCase());
        payParam.put("partner_trade_no", UUIDUtils.getUUID());
        payParam.put("openid",openId);
        payParam.put("check_name", "NO_CHECK"); // 是否验证真实姓名呢

        String payAmount = amount.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_UP).toString();
        payParam.put("amount", payAmount); // 企业付款金额，单位为分
        String desc = "百步赚提现" + amount.toString() + "元";
        payParam.put("desc", desc); // 企业付款操作说明信息。必填。
        payParam.put("spbill_create_ip", ip); // 调用接口的机器Ip地址

        String sign = WXSignUtils.createSign("UTF-8", payParam,baseParamMap.get("key"));
        payParam.put("sign",sign);
        return payParam;
    }

    /**
     * 构造请求参数
     * @param payParam
     * @return
     */
    private String getXml(SortedMap<String, Object> payParam) throws Exception{
        String template = "<xml>\n" +
                "<mch_appid>${mch_appid}</mch_appid>\n" +
                "<mchid>${mchid}</mchid>\n" +
                "<nonce_str>${nonce_str}</nonce_str>\n" +
                "<partner_trade_no>${partner_trade_no}</partner_trade_no>\n" +
                "<openid>${openid}</openid>\n" +
                "<check_name>${check_name}</check_name>\n" +
                "<amount>${amount}</amount>\n" +
                "<desc>${desc}</desc>\n" +
                "<spbill_create_ip>${spbill_create_ip}</spbill_create_ip>\n" +
                "<sign>${sign}</sign>\n" +
                "</xml>\n";
        for(String key : payParam.keySet()) {
            template = template.replace("${" + key + "}",payParam.get(key).toString());
        }

        return template.trim();
    }

    /**
     * 请求微信接口企业付款
     * @param openId
     * @param amount
     * @param ip
     * @param baseParamMap
     * @return
     */
    public PayResult transfer(String openId, BigDecimal amount, String ip,Map<String,String> baseParamMap) {
        PayResult result = new PayResult();
        result.setRspCode(RspCodeEnum.FAILED);
        try {
            SortedMap<String, Object> payParam = getPayParam(openId,amount,ip,baseParamMap);
            String xmlStr = getXml(payParam);
            log.info("\n微信支付请求数据>>{}", xmlStr);
            InputStream in = this.getClass().getResourceAsStream("/cert/apiclient_cert.p12");
            CloseableHttpResponse response =  WeiXinHttpUtil.post(baseParamMap.get("pay_url"), xmlStr, true,in,baseParamMap.get("mchid"));
            String transfersXml = EntityUtils.toString(response.getEntity(), "utf-8");
            log.info("\n微信支付请求结果>>{}", transfersXml);
            if(StringUtils.isNotBlank(transfersXml)) {
                Map<String, String> transferMap = XmlUtil.xmlToMap(transfersXml);
                // 请求结果返回码
                String resultCode = transferMap.get("result_code");
                if (StringUtils.equals("SUCCESS", resultCode)) {
                    result.setRspCode(RspCodeEnum.SUCCESS);
                    log.info("微信支付封装结果>>{}", JSONObject.toJSONString(result));
                }
                result.setTransferMap(transferMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("支付失败，openId=" + openId);
        }

        return result;
    }


}
