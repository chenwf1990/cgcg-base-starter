package com.cgcg.context.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {
    /**
     * 如果数字是整数，把小数点和小数点后面的0去掉
     *
     * @param obj
     * @return
     */
    public static String numberFormat(Object obj) {
        if (obj == null) {
            return "0";
        }
        return new BigDecimal(String.valueOf(obj)).stripTrailingZeros().toPlainString();
    }

    /**
     * 判读字符串是不是数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        } else {
            Pattern regex = Pattern.compile("(-)?\\d*(.\\d*)?");
            Matcher matcher = regex.matcher(str);
            return matcher.matches();
        }
    }

    /**
     * 把数字转出字符串，并且小数点保留2位
     *
     * @param number
     * @return
     */
    public static String decimalPoint(Double number) {
        int a = (int) (number * 100);
        BigDecimal m = BigDecimal.valueOf(a, 2);
        return m.toString();
    }

    public static String randomNumAlph(int len) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        byte[][] list = {
                {48, 57},
                {97, 122},
                {65, 90}
        };
        for (int i = 0; i < len; i++) {
            byte[] o = list[random.nextInt(list.length)];
            byte value = (byte) (random.nextInt(o[1] - o[0] + 1) + o[0]);
            sb.append((char) value);
        }
        return sb.toString();
    }


}
