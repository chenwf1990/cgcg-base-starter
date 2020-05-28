package com.cgcg.context.util.weixin;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.constants.WXPayConstants;
import com.cgcg.context.enums.RspCodeEnum;
import com.cgcg.context.model.PayResult;
import com.cgcg.context.util.HttpUtil;
import com.cgcg.context.util.MD5Utils;
import com.cgcg.context.util.StringUtils;
import com.cgcg.context.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信个人支付给企业
 * @author xujinbang
 * @date 2019/10/29.
 */
@Slf4j
public class WeiXinPersonalTransferManager {

    private static WeiXinPersonalTransferManager weiXinPersonalTransferManager;

    /**
     * 要确保该类只有一个实例对象，避免产生过多对象消费资源，所以采用单例模式
     */
    private WeiXinPersonalTransferManager() {

    }

    public synchronized static WeiXinPersonalTransferManager getInstance() {
        if (weiXinPersonalTransferManager == null) {
            weiXinPersonalTransferManager = new WeiXinPersonalTransferManager();
        }
        return weiXinPersonalTransferManager;
    }

    /**
     * 参数封装
     * @param clientType
     * @param title
     * @param id
     * @param clientIp
     * @param wapName
     * @param amount
     * @return
     */
    protected Map<String, String> getPayParam(Integer clientType, String title, String id, String clientIp, String wapName, BigDecimal amount,Map<String,String> baseParamMap) {
        Map<String, String> payParam = new LinkedHashMap<>();
        payParam.put("appid", baseParamMap.get("appid"));
        payParam.put("body", title);
        payParam.put("device_info","WEB");
        payParam.put("mch_id", baseParamMap.get("mch_id"));
        payParam.put("nonce_str", StringUtils.generateRandomDigitString(16).toUpperCase());
        // 生成通知地址
        payParam.put("notify_url", baseParamMap.get("notify_url"));
        payParam.put("out_trade_no", id);

        Map<String,Object> h5Info = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wap_url",baseParamMap.get("wap_url"));
        jsonObject.put("wap_name",wapName);
        if(clientType != null && clientType == 1){
            jsonObject.put("type", "Android");
        } else {
            jsonObject.put("type", "IOS");
        }
        h5Info.put("h5_info",jsonObject);

        payParam.put("scene_info", JSONObject.toJSONString(h5Info));
        payParam.put("spbill_create_ip", clientIp);
        payParam.put("total_fee", amount.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_UP).toString());
        payParam.put("trade_type", "MWEB");
        payParam.put("sign", MD5Utils.createMD5Sign(baseParamMap.get("key"),payParam));
        return payParam;
    }

    /**
     * 构造请求参数
     * @param payParam
     * @return
     */
    private String getXml(Map<String, String> payParam) {
        String template = "<xml>\n" +
                "<appid>${appid}</appid>\n" +
                "<body>${body}</body>\n" +
                "<device_info>${device_info}</device_info>\n" +
                "<mch_id>${mch_id}</mch_id>\n" +
                "<nonce_str>${nonce_str}</nonce_str>\n" +
                "<notify_url>${notify_url}</notify_url>\n" +
                "<out_trade_no>${out_trade_no}</out_trade_no>\n" +
                "<scene_info>${scene_info}</scene_info>\n" +
                "<spbill_create_ip>${spbill_create_ip}</spbill_create_ip>\n" +
                "<total_fee>${total_fee}</total_fee>\n" +
                "<trade_type>${trade_type}</trade_type>\n" +
                "<sign>${sign}</sign>\n" +
                "</xml>\n";
        for(String key : payParam.keySet()) {
            template = template.replace("${" + key + "}",payParam.get(key));
        }

        return template.trim();
    }

    /**
     * 个人支付
     * @param clientType
     * @param title
     * @param id
     * @param clientIp
     * @param wapName
     * @param amount
     * @param baseParamMap
     * @return
     */
    public PayResult pay(Integer clientType, String title, String id, String clientIp, String wapName, BigDecimal amount,Map<String,String> baseParamMap) {
        Map<String, String> payParam = getPayParam(clientType, title,id,clientIp,wapName,amount,baseParamMap);

        PayResult result = new PayResult();
        result.setRspCode(RspCodeEnum.FAILED);
        try {
            String xmlStr = getXml(payParam);
            log.info("\n微信支付请求数据>>{}", xmlStr);
            String rspXml = HttpUtil.post(baseParamMap.get("pay_url"), xmlStr);
            log.info("\n微信支付请求结果>>{}", rspXml);

            if (StringUtils.isNotBlank(rspXml)) {
                // 解析结果
                Map<String, String> rspMap = XmlUtil.xmlToMap(rspXml);
                // 请求状态返回码
                String returnCode = rspMap.get("return_code");
                if (StringUtils.equals(WXPayConstants.SUCCESS, returnCode)) {
                    // 验签
                    if (XmlUtil.isSignatureValid(rspXml, baseParamMap.get("key"))) {
                        // 请求结果返回码
                        String resultCode = rspMap.get("result_code");
                        if (StringUtils.equals("SUCCESS", resultCode)) {
                            result.setRspCode(RspCodeEnum.SUCCESS);
                            result.setTransferMap(rspMap);
                            log.info("微信支付封装结果>>{}", JSONObject.toJSONString(result));
                        }
                    }
                }
            } else {
                log.error("微信支付请求结果为空...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("微信支付失败。。。");
        }
        return result;
    }
}
