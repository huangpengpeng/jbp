package com.jbp.common.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements Converter<LocalDate> {

    @Override
    public Class supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return LocalDate.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    @Override
    public CellData convertToExcelData(LocalDate value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    }
}