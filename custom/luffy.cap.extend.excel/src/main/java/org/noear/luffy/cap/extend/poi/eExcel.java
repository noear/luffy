package org.noear.luffy.cap.extend.poi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.ValHolder;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/10/9 created
 */
public class eExcel {
    public static final eExcel instance = new eExcel();

    @Note("将Json转为Excel输出流")
    public void transfer(List<Map> list, OutputStream outputStream) throws IOException {
        if (list == null || list.size() == 0) {
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        //init name
        ValHolder<Row> row = new ValHolder<>();
        int rowIndex = 0;
        ValHolder<Integer> colIdx = new ValHolder<>(0);
        Map colTmp = list.get(0);
        {
            row.value = sheet.createRow(rowIndex);
            colTmp.forEach((name, tmp) -> {
                row.value.createCell(colIdx.value++).setCellValue(name.toString());
            });
        }

        for (Map item : list) {
            rowIndex++;
            colIdx.value = 0;
            row.value = sheet.createRow(rowIndex);

            colTmp.forEach((name, tmp) -> {
                Object value = item.get(name);

                Cell cell = row.value.createCell(colIdx.value++);

                if (value == null) {
                    cell.setCellValue("");
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else if (value instanceof LocalDate) {
                    cell.setCellValue((LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    cell.setCellValue((LocalDateTime) value);
                } else if (value instanceof Calendar) {
                    cell.setCellValue((Calendar) value);
                } else if (value instanceof RichTextString) {
                    cell.setCellValue((RichTextString) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else {
                    cell.setCellValue(value.toString());
                }

            });
        }

        workbook.write(outputStream);
    }
}
