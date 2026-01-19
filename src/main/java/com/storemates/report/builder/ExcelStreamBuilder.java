package com.storemates.report.builder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ExcelStreamBuilder<T> {
    protected SXSSFWorkbook workbook;
    protected Sheet sheet;
    protected int currentRowIndex;
    protected CellStyle currencyStyle;
    protected CellStyle dateStyle;

    // Constructor que carga la plantilla
    public ExcelStreamBuilder(String templatePath, int startRowIndex) throws IOException {
        //  cargamos la plantilla XSSF
        InputStream templateFile = new ClassPathResource(templatePath).getInputStream();
        XSSFWorkbook templateWorkbook = new XSSFWorkbook(templateFile);

        // convertimos a Streaming manteniendo solo 100 filas en memoria RAM
        this.workbook = new SXSSFWorkbook(templateWorkbook, 100);
        this.sheet = this.workbook.getSheetAt(0);
        this.currentRowIndex = startRowIndex;

        initCommonStyles();
    }

    private void initCommonStyles() {
        DataFormat format = workbook.createDataFormat();

        currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("$#,##0.00"));

        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd/mm/yyyy h:mm"));
    }

    protected abstract void writeData(T data);

    protected void createCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);

        if (value == null) {
            cell.setBlank();
            return;
        }

        if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
         else if (value instanceof String) cell.setCellValue((String) value);
         else cell.setCellValue(value.toString());

        if (style != null) cell.setCellStyle(style);
    }

    public ByteArrayInputStream build() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.dispose();
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
