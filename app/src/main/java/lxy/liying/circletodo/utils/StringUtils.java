package lxy.liying.circletodo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/26 11:10
 * 版本：1.0
 * 描述：字符串工具类
 * 备注：
 * =======================================================
 */
public class StringUtils {
    /**
     * 得到两位的字符串，不足位补零
     *
     * @param str
     * @return
     */
    public static String twoBitsString(String str) {
        String twoZero = "00";
        return twoZero.substring(0, 2 - str.length()) + str;
    }

    /**
     * 格式化日期字符串，结果为 0000-00-00，即年份为4位，月份和月中天数均为2位。 <br/>
     *
     * @param old 原始字符串，形如 0000-0(00)-0(00)
     * @return 格式化后的字符串
     */
    public static String formatDate(String old) {
        String[] str = old.split("-");
        String year = str[0];
        String month = str[1];
        String day = str[2];

        month = twoBitsString(month);
        day = twoBitsString(day);
        return year + "-" + month + "-" + day;
    }

    /**
     * 返回一个不重复的字符串
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * SHA1加密
     *
     * @param info 待加密字符串
     * @return
     */
    public static String encryptToSHA1(String info) {
        byte[] digesta = null;
        try {
            // 得到一个SHA-1的消息摘要
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes());
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        return rs;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
