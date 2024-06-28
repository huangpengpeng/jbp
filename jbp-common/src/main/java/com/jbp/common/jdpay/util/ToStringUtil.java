package com.jbp.common.jdpay.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/*************************************************
 *
 * toString方法工具类
 *
 *************************************************/
public class ToStringUtil {

    public static String mapToString(String attrName, Map<?, ?> map) {
        if (map == null) {
            return buildJsonKey(attrName) + "null";
        }
        if (map.isEmpty()) {
            return buildJsonKey(attrName) + "{}";
        }
        return buildJsonKey(attrName) + toJson(map);
    }

    /**
     * 将对象转换为Json字符串输出
     *
     * @param value
     * @return
     */
    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return "\"" + ((String) value).replaceAll("\\\"", "\\\\\"") + "\"";
        }
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return "\"" + sdf.format(value) + "\"";
        }
        if (value instanceof List) {
            return listToString((List) value);
        }
        if (value instanceof Set) {
            return setToString((Set<?>) value);
        }
        if (value instanceof Map) {
            return mapToString((Map<?, ?>) value);
        }
        Class className = value.getClass();
        if (className.isArray()) {
            return arrayToString(value);
        }
        if (className.isPrimitive() ||
                className.equals(Integer.class) ||
                className.equals(Byte.class) ||
                className.equals(Long.class) ||
                className.equals(Double.class) ||
                className.equals(Float.class) ||
                className.equals(Character.class) ||
                className.equals(Short.class) ||
                className.equals(Boolean.class)
        ) {
            return value.toString();
        }
        return value.toString();
    }

    private static String userDefinedClassToString(Object object) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        List<Field> fields = getAllField(object);
        for (int i = 0; i < fields.size(); i++) {
            String fieldName = fields.get(i).getName();
            if ("serialVersionUID".equals(fieldName)) {
                continue;
            }
            Object fieldValue = getFieldValueByName(fieldName, object);
            if (null == fieldValue) {
                fieldValue = getBooleanFieldValueByName(fieldName, object);
                if (null == fieldValue) {
                    continue;
                }
            }

            sb.append(buildJsonKey(fieldName)).append(toJson(fieldValue));

            if (i == fields.size() - 1) {
                sb.append('}');
            } else {
                sb.append(",");
            }
        }
        if (sb.length() > 1 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
        }
        return sb.toString();
    }

    private static List<Field> getAllField(Object object) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> cls = object.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            Collections.addAll(fields, cls.getDeclaredFields());
        }
        return fields;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getBooleanFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "is" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            return null;
        }
    }

    private static String mapToString(Map<?, ?> map) {
        if (map.isEmpty()) {
            return "{}";
        }
        Iterator<? extends Map.Entry<?, ?>> i = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        while (i.hasNext()) {
            Map.Entry e = i.next();
            String key = (String) e.getKey();
            Object value = e.getValue();
            if (null == value) {
                continue;
            }

            if (first) {
                sb.append(buildJsonKey(key)).append(toJson(value));
                first = false;
            } else {
                sb.append(",").append(buildJsonKey(key)).append(toJson(value));
            }
        }
        return sb.append('}').toString();
    }

    private static String setToString(Set<?> set) {
        if (set.isEmpty()) {
            return "[]";
        }
        Iterator<?> i = set.iterator();
        return objToString(i);
    }

    private static String listToString(List<?> list) {
        if (list.isEmpty()) {
            return "[]";
        }
        Iterator<?> i = list.iterator();
        return objToString(i);
    }

    private static String objToString(Iterator<?> i) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (; ; ) {
            Object value = i.next();
            if (null == value) {
                continue;
            }
            sb.append(toJson(value));
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(",");
        }
    }

    private static String arrayToString(Object value) {
        if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        }
        if (value instanceof Integer[]) {
            return Arrays.toString((Integer[]) value);
        }
        if (value instanceof char[]) {
            return Arrays.toString((char[]) value);
        }
        if (value instanceof Character[]) {
            return Arrays.toString((Character[]) value);
        }
        if (value instanceof long[]) {
            return Arrays.toString((long[]) value);
        }
        if (value instanceof Long[]) {
            return Arrays.toString((Long[]) value);
        }
        if (value instanceof double[]) {
            return Arrays.toString((double[]) value);
        }
        if (value instanceof Double[]) {
            return Arrays.toString((Double[]) value);
        }
        if (value instanceof String[]) {
            return arrayToString((String[]) value);
        }
        if (value instanceof Object[]) {
            return arrayToString((Object[]) value);
        }
        return value.toString();
    }

    private static String arrayToString(String[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("\"" + (a[i]) + "\"");
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    private static String arrayToString(Object[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(toJson(a[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    private static String buildJsonKey(String key) {
        return "\"" + key + "\":";
    }

    static class A {
        private int a1;
        private Integer a2;
        private long a3;
        private Long a4;
        private double a5;
        private Double a6;
        private String a7;
        private int[] a8;
        private List<String> a9;
        private Map<String, String> a10;
        private String[] a11;
        private Set<String> a12;

        @Override
        public String toString() {
            return "{\"a1\":" + a1
                    + ", \"a2\":" + a2
                    + ", \"a3\":" + a3
                    + ", \"a4\":" + a4
                    + ", \"a5\":" + a5
                    + ", \"a6\":" + a6
                    + ", \"a7\":\"" + a7 + "\""
                    + ", \"a8\":" + ToStringUtil.toJson(a8)
                    + ", \"a9\":" + ToStringUtil.toJson(a9)
                    + ", \"a10\":" + ToStringUtil.toJson(a10)
                    + ", \"a11\":" + ToStringUtil.toJson(a11)
                    + ", \"a12\":" + ToStringUtil.toJson(a12)
                    + "}"
                    ;
        }

        public int getA1() {
            return a1;
        }

        public void setA1(int a1) {
            this.a1 = a1;
        }

        public Integer getA2() {
            return a2;
        }

        public void setA2(Integer a2) {
            this.a2 = a2;
        }

        public long getA3() {
            return a3;
        }

        public void setA3(long a3) {
            this.a3 = a3;
        }

        public Long getA4() {
            return a4;
        }

        public void setA4(Long a4) {
            this.a4 = a4;
        }

        public double getA5() {
            return a5;
        }

        public void setA5(double a5) {
            this.a5 = a5;
        }

        public Double getA6() {
            return a6;
        }

        public void setA6(Double a6) {
            this.a6 = a6;
        }

        public String getA7() {
            return a7;
        }

        public void setA7(String a7) {
            this.a7 = a7;
        }

        public int[] getA8() {
            return a8;
        }

        public void setA8(int[] a8) {
            this.a8 = a8;
        }

        public List<String> getA9() {
            return a9;
        }

        public void setA9(List<String> a9) {
            this.a9 = a9;
        }

        public Map<String, String> getA10() {
            return a10;
        }

        public void setA10(Map<String, String> a10) {
            this.a10 = a10;
        }

        public String[] getA11() {
            return a11;
        }

        public void setA11(String[] a11) {
            this.a11 = a11;
        }

        public Set<String> getA12() {
            return a12;
        }

        public void setA12(Set<String> a12) {
            this.a12 = a12;
        }
    }

    static class SubA extends A {
        private String subA1;

        @Override
        public String toString() {
            return "{\"subA1\":\"" + subA1 + "\""
                    + "}"
                    + super.toString()
                    ;
        }

        public String getSubA1() {
            return subA1;
        }

        public void setSubA1(String subA1) {
            this.subA1 = subA1;
        }
    }

    static class B {
        private String b1;
        private Map<String, Object> b2;
        private SubA subA;

        @Override
        public String toString() {
            return "{\"b1\":\"" + b1 + "\""
                    + ", \"b2\":" + ToStringUtil.toJson(b2)
                    + ", \"subA\":" + subA
                    + "}"
                    ;
        }

        public String getB1() {
            return b1;
        }

        public void setB1(String b1) {
            this.b1 = b1;
        }

        public Map<String, Object> getB2() {
            return b2;
        }

        public void setB2(Map<String, Object> b2) {
            this.b2 = b2;
        }

        public SubA getSubA() {
            return subA;
        }

        public void setSubA(SubA subA) {
            this.subA = subA;
        }
    }

    public static void main(String[] args) {
        A a = new A();
        a.a1 = 1;
        a.a2 = 2;
        a.a3 = 3;
        a.a4 = 4L;
        a.a5 = 5;
        a.a6 = 6D;
        a.a7 = "7";
        a.a8 = new int[]{8, 8, 8};
        a.a9 = Arrays.asList("9", "9", "9");
        a.a10 = new HashMap<String, String>();
        a.a10.put("a10.key", "a10.\"value\"");
        a.a11 = new String[]{"11", "11", "11"};
        a.a12 = new HashSet<String>();
        a.a12.add("a12.value");
        System.out.println(a);

        SubA subA = new SubA();
        subA.setSubA1("subA1");
        subA.setA1(1);
        subA.setA2(2);
        subA.setA3(3);
        subA.setA4(4L);
        subA.setA5(5);
        subA.setA6(6D);
        subA.setA7("7");
        subA.setA8(new int[]{8, 8, 8});
        subA.setA9(Arrays.asList("9", "9", "9"));
        subA.setA10(new HashMap<String, String>());
        subA.getA10().put("a10.key", "a10.\"value\"");
        subA.getA10().put("null", null);
        subA.setA11(new String[]{"11", "11", "11"});
        System.out.println(toJson(subA));

        B b = new B();
        b.setB1("b1");
        b.setB2(new HashMap<String, Object>());
        b.getB2().put("a", a);
        b.setSubA(subA);
        System.out.println(toJson(b));


        Set<String> set = new HashSet<String>();
        set.add("a");
        set.add("b");
        System.out.println(toJson(set));

        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        System.out.println(toJson(list));
    }
}
