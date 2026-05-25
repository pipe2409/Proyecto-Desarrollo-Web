package com.example.demo.service;

import com.example.demo.entities.HistorialPago;
import com.example.demo.entities.Huesped;
import com.example.demo.entities.Reserva;
import com.example.demo.repository.HistorialPagoRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private HistorialPagoRepository historialPagoRepository;

    /**
     * Genera un Excel (.xlsx) con la lista de pagos confirmados entre desde y
     * hasta. Cada fila = un HistorialPago. Si el rango es null, exporta todo.
     */
    public byte[] generarReportePagos(LocalDateTime desde, LocalDateTime hasta) throws IOException {
        List<HistorialPago> pagos;
        if (desde != null && hasta != null) {
            pagos = historialPagoRepository.findByFechaPagoBetweenOrderByFechaPagoDesc(desde, hasta);
        } else {
            pagos = historialPagoRepository.findAll();
        }

        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter fmtFechaCorta = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Pagos");

            // Estilos: header en negrita con fondo, montos con formato $#,##0
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle montoStyle = wb.createCellStyle();
            montoStyle.setDataFormat(wb.createDataFormat().getFormat("$#,##0"));

            // Encabezados
            String[] columnas = {
                "Fecha pago", "Tipo", "Reserva", "Huésped", "Habitación",
                "Check-in", "Check-out", "Monto (USD)", "Stripe Session ID"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(columnas[i]);
                c.setCellStyle(headerStyle);
            }

            // Filas
            int rowIdx = 1;
            for (HistorialPago p : pagos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(p.getFechaPago() == null ? "" : p.getFechaPago().format(fmtFecha));
                row.createCell(1).setCellValue(p.getTipo() == null ? "" : p.getTipo().name());

                Reserva r = p.getReserva();
                row.createCell(2).setCellValue(r == null ? "" : "#" + r.getId());

                Huesped h = r == null ? null : r.getHuesped();
                row.createCell(3).setCellValue(h == null ? "" :
                    (nvl(h.getNombre()) + " " + nvl(h.getApellido())).trim());

                row.createCell(4).setCellValue(r == null || r.getHabitacion() == null
                    ? "" : nvl(r.getHabitacion().getCodigo()));

                row.createCell(5).setCellValue(r == null || r.getFechaInicio() == null
                    ? "" : r.getFechaInicio().format(fmtFechaCorta));
                row.createCell(6).setCellValue(r == null || r.getFechaFin() == null
                    ? "" : r.getFechaFin().format(fmtFechaCorta));

                Cell montoCell = row.createCell(7);
                montoCell.setCellValue(p.getMonto() == null ? 0 : p.getMonto());
                montoCell.setCellStyle(montoStyle);

                row.createCell(8).setCellValue(nvl(p.getSessionIdStripe()));
            }

            // Fila total al final
            if (!pagos.isEmpty()) {
                Row totalRow = sheet.createRow(rowIdx + 1);
                Cell lbl = totalRow.createCell(6);
                lbl.setCellValue("TOTAL");
                CellStyle bold = wb.createCellStyle();
                Font bf = wb.createFont(); bf.setBold(true);
                bold.setFont(bf);
                bold.setAlignment(HorizontalAlignment.RIGHT);
                lbl.setCellStyle(bold);

                CellStyle totalMontoStyle = wb.createCellStyle();
                totalMontoStyle.cloneStyleFrom(montoStyle);
                totalMontoStyle.setFont(bf);
                Cell total = totalRow.createCell(7);
                int suma = pagos.stream().mapToInt(p -> p.getMonto() == null ? 0 : p.getMonto()).sum();
                total.setCellValue(suma);
                total.setCellStyle(totalMontoStyle);
            }

            // Auto-ancho por columna
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
