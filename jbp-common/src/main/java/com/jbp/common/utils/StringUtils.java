package com.jbp.common.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final int SIZE = 6;
    private static final String SYMBOL = "*";
    private static String CHANS_STRINGS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String[] CHANS_STRINGS_ARRAYS = new String[]{"9", "2", "1", "5", "4", "7", "6", "3", "8", "0"};

    public StringUtils() {
    }

    public static String filterEmoji(String source) {
        if (isBlank(source)) {
            return source;
        } else if (!containsEmoji(source)) {
            return source;
        } else {
            StringBuilder buf = null;
            int len = source.length();

            for(int i = 0; i < len; ++i) {
                char codePoint = source.charAt(i);
                if (notisEmojiCharacter(codePoint) && codePoint != ' ') {
                    if (buf == null) {
                        buf = new StringBuilder(source.length());
                    }

                    buf.append(codePoint);
                }
            }

            if (buf == null) {
                return "";
            } else if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    public static boolean containsEmoji(String source) {
        int len = source.length();

        for(int i = 0; i < len; ++i) {
            char codePoint = source.charAt(i);
            if (!notisEmojiCharacter(codePoint)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(N_TO_10("A"));
    }

    public static boolean notisEmojiCharacter(char codePoint) {
        return codePoint == 0 || codePoint == '\t' || codePoint == '\n' || codePoint == '\r' || codePoint >= ' ' && codePoint <= '\ud7ff' || codePoint >= '\ue000' && codePoint <= '�' || codePoint >= 65536 && codePoint <= 1114111;
    }

    public static String toDRUG(String value) {
        if (value != null && !"".equals(value)) {
            int len = value.length();
            int pamaone = len / 2;
            int pamatwo = pamaone - 1;
            int pamathree = len % 2;
            StringBuilder stringBuilder = new StringBuilder();
            if (len <= 2) {
                if (pamathree == 1) {
                    return "*";
                }

                stringBuilder.append("*");
                stringBuilder.append(value.charAt(len - 1));
            } else if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append("*");
                stringBuilder.append(value.substring(len - 1, len));
            } else {
                int pamafive;
                int i;
                if (pamatwo >= 3 && 7 != len) {
                    pamafive = (len - 6) / 2;
                    stringBuilder.append(value.substring(0, pamafive));

                    for(i = 0; i < 6; ++i) {
                        stringBuilder.append("*");
                    }

                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                } else {
                    pamafive = len - 2;
                    stringBuilder.append(value.substring(0, 1));

                    for(i = 0; i < pamafive; ++i) {
                        stringBuilder.append("*");
                    }

                    stringBuilder.append(value.substring(len - 1, len));
                }
            }

            return stringBuilder.toString();
        } else {
            return value;
        }
    }

    protected static void ab(StringBuffer sb, int len) {
        for(int i = 0; i < len; ++i) {
            sb = sb.append("*");
        }

    }

    public static String toSemiangle(String src) {
        if (src == null) {
            return src;
        } else {
            StringBuilder buf = new StringBuilder(src.length());
            char[] ca = src.toCharArray();

            for(int i = 0; i < src.length(); ++i) {
                if (ca[i] >= '！' && ca[i] <= '～') {
                    buf.append((char)(ca[i] - 'ﻠ'));
                } else if (ca[i] == 12288) {
                    buf.append(' ');
                } else {
                    buf.append(ca[i]);
                }
            }

            return buf.toString();
        }
    }

    public static boolean chinese(String str) {
        Matcher matcher = Pattern.compile("[一-龥]").matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }

        return flg;
    }

    public static String toString(Map<String, ?> cache, String[] filters) {
        StringBuffer buffer = new StringBuffer();
        Iterator var4 = cache.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, ?> entry = (Map.Entry)var4.next();
            if (!ArrayUtils.contains(filters, entry.getKey()) && entry.getValue() != null) {
                buffer.append(entry.getValue()).append(" ");
            }
        }

        return buffer.toString();
    }

    public static String[] splitAndTrim(String str, String sep, String sep2) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            if (!StringUtils.isBlank(sep2)) {
                str = StringUtils.replace(str, sep2, sep);
            }

            String[] arr = StringUtils.split(str, sep);
            int i = 0;

            for(int len = arr.length; i < len; ++i) {
                arr[i] = arr[i].trim();
            }

            return arr;
        }
    }

    public static String txt2htm(String txt) {
        if (StringUtils.isBlank(txt)) {
            return txt;
        } else {
            StringBuilder sb = new StringBuilder((int)((double)txt.length() * 1.2));
            boolean doub = false;

            for(int i = 0; i < txt.length(); ++i) {
                char c = txt.charAt(i);
                if (c == ' ') {
                    if (doub) {
                        sb.append(' ');
                        doub = false;
                    } else {
                        sb.append("&nbsp;");
                        doub = true;
                    }
                } else {
                    doub = false;
                    switch (c) {
                        case '\n':
                            sb.append("<br/>");
                            break;
                        case '"':
                            sb.append("&quot;");
                            break;
                        case '&':
                            sb.append("&amp;");
                            break;
                        case '<':
                            sb.append("&lt;");
                            break;
                        case '>':
                            sb.append("&gt;");
                            break;
                        default:
                            sb.append(c);
                    }
                }
            }

            return sb.toString();
        }
    }

    public static String textCut(String s, int len, String append) {
        if (s == null) {
            return null;
        } else {
            int slen = s.length();
            if (slen <= len) {
                return s;
            } else {
                int maxCount = len * 2;
                int count = 0;

                int i;
                for(i = 0; count < maxCount && i < slen; ++i) {
                    if (s.codePointAt(i) < 256) {
                        ++count;
                    } else {
                        count += 2;
                    }
                }

                if (i < slen) {
                    if (count > maxCount) {
                        --i;
                    }

                    if (!StringUtils.isBlank(append)) {
                        if (s.codePointAt(i - 1) < 256) {
                            i -= 2;
                        } else {
                            --i;
                        }

                        return s.substring(0, i) + append;
                    } else {
                        return s.substring(0, i);
                    }
                } else {
                    return s;
                }
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public static String removeHtmlTagP(String inputString) {
        if (inputString == null) {
            return null;
        } else {
            String htmlStr = inputString;
            String textStr = "";

            try {
                String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
                String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
                String regEx_html = "<[^>]+>";
                Pattern p_script = Pattern.compile(regEx_script, 2);
                Matcher m_script = p_script.matcher(htmlStr);
                htmlStr = m_script.replaceAll("");
                Pattern p_style = Pattern.compile(regEx_style, 2);
                Matcher m_style = p_style.matcher(htmlStr);
                htmlStr = m_style.replaceAll("");
                htmlStr.replace("</p>", "\n");
                Pattern p_html = Pattern.compile(regEx_html, 2);
                Matcher m_html = p_html.matcher(htmlStr);
                htmlStr = m_html.replaceAll("");
                textStr = htmlStr;
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            return textStr;
        }
    }

    /** @deprecated */
    @Deprecated
    public static String removeHtmlTag(String inputString) {
        if (inputString == null) {
            return null;
        } else {
            String htmlStr = inputString;
            String textStr = "";

            try {
                String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
                String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
                String regEx_html = "<[^>]+>";
                Pattern p_script = Pattern.compile(regEx_script, 2);
                Matcher m_script = p_script.matcher(htmlStr);
                htmlStr = m_script.replaceAll("");
                Pattern p_style = Pattern.compile(regEx_style, 2);
                Matcher m_style = p_style.matcher(htmlStr);
                htmlStr = m_style.replaceAll("");
                Pattern p_html = Pattern.compile(regEx_html, 2);
                Matcher m_html = p_html.matcher(htmlStr);
                htmlStr = m_html.replaceAll("");
                textStr = htmlStr;
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            return textStr;
        }
    }

    public static boolean contains(String str, String search) {
        if (!StringUtils.isBlank(str) && !StringUtils.isBlank(search)) {
            String reg = StringUtils.replace(search, "*", ".*");
            Pattern p = Pattern.compile(reg);
            return p.matcher(str).matches();
        } else {
            return false;
        }
    }

    public static boolean containsKeyString(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        } else {
            return str.contains("'") || str.contains("\"") || str.contains("\r") || str.contains("\n") || str.contains("\t") || str.contains("\b") || str.contains("\f");
        }
    }

    /** @deprecated */
    @Deprecated
    public static String addCharForString(String str, int strLength, char c, int position) {
        int strLen = str.length();
        if (strLen < strLength) {
            while(strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                if (position == 1) {
                    sb.append(c).append(str);
                } else {
                    sb.append(str).append(c);
                }

                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
    }

    /** @deprecated */
    @Deprecated
    public static String replaceKeyString(String str) {
        return containsKeyString(str) ? str.replace("'", "\\'").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("\b", "\\b").replace("\f", "\\f") : str;
    }

    /** @deprecated */
    @Deprecated
    public static String replaceString(String str) {
        return containsKeyString(str) ? str.replace("'", "\"").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("\b", "\\b").replace("\f", "\\f") : str;
    }

    public static String N10_TO_10(String numberBuf) {
        char[] ch = numberBuf.toString().toCharArray();
        StringBuffer result = new StringBuffer();
        char[] var6 = ch;
        int var5 = ch.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            char c = var6[var4];
            String nj = Character.toString(c);
            String cj = CHANS_STRINGS_ARRAYS[Integer.valueOf(nj)];
            result.append(cj);
        }

        return result.toString();
    }

    public static String N_TO_10(String start) {
        StringBuffer numberBuf = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");

        for(int i = 0; i < 8; ++i) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            numberBuf.append(CHANS_STRINGS.toCharArray()[x % 62]);
        }

        char[] ch = numberBuf.toString().toCharArray();
        int len = ch.length;
        long result = 0L;
        int N = CHANS_STRINGS.length();
        long base = 1L;

        for(int i = len - 1; i >= 0; --i) {
            int index = CHANS_STRINGS.indexOf(ch[i]);
            result += (long)index * base;
            base *= (long)N;
        }

        return start + result;
    }

    public static String replaceAngle(String value) {
        return org.apache.commons.lang3.StringUtils.trimToEmpty(value.replaceAll("（", "(").replaceAll("）", ")"));
    }

    public static String substring(String str, int maxLength) {
        if (StringUtils.isBlank(str)) {
            return "";
        } else if (str.length() >= maxLength) {
            int gap = 5;
            return str.substring(0, maxLength - gap) + "...";
        } else {
            return str;
        }
    }
}
