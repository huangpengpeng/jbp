package com.jbp.common.enums;

public enum SupplyRuleEnum {

    上级及更高级("上级及更高级"),
    更高级别("更高级别"),
    公司("公司");
    private final String name;

        SupplyRuleEnum( String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
}
