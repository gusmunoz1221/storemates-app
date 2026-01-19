package com.storemates.report.exporter;

import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderStatus;
import com.storemates.report.builder.ExcelStreamBuilder;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;
import java.math.BigDecimal;

public class SalesExcelExporter extends ExcelStreamBuilder<OrderEntity> {

    private final CellStyle statusCompletedStyle;
    private final CellStyle statusPendingStyle;
    private final CellStyle highValueStyle;

    public SalesExcelExporter() throws IOException {
        super("templates/sales_report_template.xlsx", 2);

        statusCompletedStyle = createStyle(IndexedColors.LIGHT_GREEN, IndexedColors.DARK_GREEN);
        statusPendingStyle = createStyle(IndexedColors.LEMON_CHIFFON, IndexedColors.ORANGE);

        highValueStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.RED.getIndex());
        highValueStyle.setFont(font);
        highValueStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
    }

    @Override
    public void writeData(OrderEntity order) {
        Row row = sheet.createRow(currentRowIndex++);

        // Col 0: ID
        createCell(row, 0, order.getId(), null);

        // Col 1: Cliente
        createCell(row, 1, order.getCustomerName(), null);

        // Col 2: Fecha (Con estilo de fecha)
        createCell(row, 2, order.getCreatedAt().toString(), null);

        // Col 3: Estado (Con ESTILO CONDICIONAL)
        CellStyle statusStyle = switch (order.getStatus()) {
            case OrderStatus.PAID -> statusCompletedStyle;
            case OrderStatus.PENDING -> statusPendingStyle;
            default -> null;
        };
        createCell(row, 3, order.getStatus().toString(), statusStyle);

        // Col 4: Total (Si es venta grande > $100.000, sale en negrita roja)
        boolean isHighValue = order.getTotalAmount().compareTo(new BigDecimal("100000")) > 0;
        createCell(row, 4, order.getTotalAmount(), isHighValue ? highValueStyle : currencyStyle);
    }

    // HELPER
    private CellStyle createStyle(IndexedColors bg, IndexedColors fontColor) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(bg.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(fontColor.getIndex());
        style.setFont(font);
        return style;
    }
}