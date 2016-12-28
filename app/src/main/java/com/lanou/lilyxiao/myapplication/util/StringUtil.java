
package com.lanou.lilyxiao.myapplication.util;


import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类。
 */
public class StringUtil {
    private static SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    /**
     * 字符串是否为空。
     * 
     * @param src 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String src) {
        return src == null || src.trim().length() == 0;
    }

    /**
     * target以src开始。
     * 
     * @param src
     * @param target
     * @return
     */
    public static boolean isMatched(String src, String target) {
        if (target == null || src == null) {
            return false;
        }

        return target.indexOf(src) == 0;
    }

    /**
     * target以src开始。
     * 
     * @param src
     * @param target
     * @return
     */
    public static boolean isContains(String src, String target) {
        if (target == null || src == null) {
            return false;
        }

        return target.contains(src);
    }

    /**
     * 生成多颜色文本字符。
     * 
     * @param texts 不同的颜色的字符串数组
     * @param colors colors对应texts相同下标位置的文本的颜色
     * @return
     */
    public static CharSequence buildMulColorText(String[] texts, int[] colors) {
        SpannableStringBuilder total = new SpannableStringBuilder();

        for (int index = 0; index < texts.length; index++) {
            SpannableStringBuilder single = new SpannableStringBuilder(texts[index]);
            single.setSpan(new ForegroundColorSpan(colors[index]), 0, single.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            total.append(single);
        }

        return total;
    }

    /**
     * 得到字符串的utf-8格式。
     * 
     * @param strOriginal 原字符串
     * @return 转换为utf-8格式的字符串
     */
    public static String toUTF8(String strOriginal) {
        if (strOriginal == null)
            return "";
        try {
            return URLEncoder.encode(strOriginal, "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到utf-8字符串的原字符串。
     * 
     * @param str utf-8字符串
     * @return 原字符串
     */
    public static String fromUTF8(String str) {
        if (str == null)
            return "";
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到字符串的gbk格式。
     * 
     * @param str 原字符串
     * @return 转换为gbk格式的字符串
     */
    public static String toGBK(String str) {
        if (str == null)
            return "";
        try {
            return URLEncoder.encode(str, "gbk");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }


    /**
     * byte转16进制字符串。
     * 
     * @param src byte
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        if (src != null && src.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(256);
            int v;
            String hv;
            for (int i = 0; i < src.length; i++) {
                v = src[i] & 0xFF;
                hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * 判断word是否表示我的位置。
     * 
     * @param word
     * @return
     */
    public static boolean isWordLikeMyLocation(String word) {
        if (isEmpty(word)) {
            return false;
        }
        return word.equals("我的位置") || word.equals("当前位置") || word.equals("我在哪")
                || word.equals("我在哪儿") || word.equals("我在的位置") || word.equals("我的位置在哪")
                || word.equals("我的位置在哪儿");
    }

    /**
     * 判断是字符串是否是数字。
     * 
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        try {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(s);
            return isNum.matches();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 比较版本大小。
     * 
     * @return negative ver1 < ver2, positive ver1 > ver2, 0 ver1 == ver2
     * @throws NullPointerException if {@code string} is {@code null}.
     */

    public static int compareVersion(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        for (int i = 0; i < (v1.length > v2.length ? v2.length : v1.length); i++) {
            int c1 = Integer.valueOf(v1[i]);
            int c2 = Integer.valueOf(v2[i]);
            if ((c1 - c2) != 0) {
                return c1 - c2;
            }
        }
        if (v1.length != v2.length) {
            return v1.length - v2.length;
        }
        return 0;
    }

    /**
     * 时间转换成正常日期格式显示的字符串
     * 
     * @param time: 毫秒级
     * @return
     */
    public static String toDateFormat(long time) {
        return sFormat.format(time);
    }
}
