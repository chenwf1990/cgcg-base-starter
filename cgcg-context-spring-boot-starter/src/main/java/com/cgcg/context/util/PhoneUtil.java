package com.cgcg.context.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xujinbang
 * @date 2019/10/10.
 */
@Slf4j
public class PhoneUtil {

    /**
     * 根据手机号查询出手机归属地
     * @param phoneNumber 手机号
     * @return
     */
    public static JSONObject queryPhoneAscription(String phoneNumber) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tel", phoneNumber);
            String result = HttpUtils.doGet("http://mobsec-dianhua.baidu.com/dianhua_api/open/location", null, params);
            System.out.println(result);

            JSONObject getJson = JSONObject.parseObject(result).getJSONObject("response").getJSONObject(phoneNumber).getJSONObject("detail");

            JSONObject resultJson = new JSONObject();
            resultJson.put("operator", getJson.getString("operator"));
            resultJson.put("province", getJson.getString("province"));
            resultJson.put("city", getJson.getJSONArray("area").getJSONObject(0).getString("city"));
            return resultJson;
        } catch (Exception e) {
            log.error("根据手机号查询出手机归属地异常", e);
            return null;
        }
    }
}
