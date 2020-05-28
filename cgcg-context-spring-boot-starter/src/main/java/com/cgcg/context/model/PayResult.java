package com.cgcg.context.model;

import com.cgcg.context.enums.RspCodeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PayResult implements Serializable {
    /**
     * 返回码
     */
    private RspCodeEnum rspCode;

    /**
     * 微信返回报文
     */
    private Map<String, String> transferMap;
}
