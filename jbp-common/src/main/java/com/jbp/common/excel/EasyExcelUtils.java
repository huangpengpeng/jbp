package com.jbp.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Slf4j
public class EasyExcelUtils {

    /**
     *
     * @param data       报表数据
     * @param excelClass 报表实体类的Class（根据该Class的属性来设置Excel的头属性）
     */
    public static InputStream exportByExcel(List<?> data, Class<?> excelClass) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            log.info("报表导出Size: " + data.size() + "条。");
            // 把查询到的数据按设置的sheet的容量进行切割

            // 设置响应头
            ExcelWriter excelWriter = EasyExcel.write(os, excelClass).registerWriteHandler(ExcelUtils.formatExcel()).registerWriteHandler(new ExcelUtils.ExcelWidthStyleStrategy()).build();
            ExcelWriterSheetBuilder excelWriterSheetBuilder;
            excelWriterSheetBuilder = new ExcelWriterSheetBuilder(excelWriter);
            excelWriterSheetBuilder.sheetNo(1).sheetName("sheet" + 1);
            WriteSheet writeSheet = excelWriterSheetBuilder.build();
            excelWriter.write(data, writeSheet);
            // 必须要finish才会写入，不finish只会创建empty的文件
            excelWriter.finish();
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            return is;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static MultipartFile exportExcelToMultipartFile(List<?> data, Class<?> excelClass) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            log.info("报表导出Size: " + data.size() + "条。");
            // 把查询到的数据按设置的sheet的容量进行切割

            // 设置响应头
            ExcelWriter excelWriter = EasyExcel.write(os, excelClass).registerWriteHandler(ExcelUtils.formatExcel()).registerWriteHandler(new ExcelUtils.ExcelWidthStyleStrategy()).build();
            ExcelWriterSheetBuilder excelWriterSheetBuilder;
            excelWriterSheetBuilder = new ExcelWriterSheetBuilder(excelWriter);
            excelWriterSheetBuilder.sheetNo(1).sheetName("sheet" + 1);
            WriteSheet writeSheet = excelWriterSheetBuilder.build();
            excelWriter.write(data, writeSheet);
            // 必须要finish才会写入，不finish只会创建empty的文件
            excelWriter.finish();
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            MultipartFile file = null;
            try {
                file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return file;
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
