package com.cgcg.context.util;

import tool.util.StringUtil;

import java.security.SecureRandom;

/**
 * 字符串管理工具类
 * @author xujinbang
 * @date 2019/10/11.
 */
public class StringUtils extends StringUtil{

    /**
     * 生成随机数
     * @param aLength 随机数长度
     * @return
     */
    public static String generateRandomDigitString(int aLength) {
        SecureRandom tRandom = new SecureRandom();
        long tLong;
        tRandom.nextLong();
        tLong = Math.abs(tRandom.nextLong());
        String aString = (String.valueOf(tLong)).trim();
        while (aString.length() < aLength) {
            tLong = Math.abs(tRandom.nextLong());
            aString += (String.valueOf(tLong)).trim();
        }
        aString = aString.substring(0, aLength);
        return aString;
    }
}
