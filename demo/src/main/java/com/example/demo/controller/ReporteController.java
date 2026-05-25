package com.example.demo.controller;

import com.example.demo.service.ReporteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reportes descargables del panel del operador. Por ahora un solo endpoint:
 * GET /api/reportes/pagos.xlsx?desde=2026-05-01&hasta=2026-05-31
 * Devuelve un Excel con la lista de pagos confirmados (HistorialPago) del
 * rango. Si desde/hasta vienen vacios, exporta todo el historial.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/pagos.xlsx")
    public ResponseEntity<byte[]> reportePagos(
            @RequestParam(value = "desde", required = false) String desde,
            @RequestParam(value = "hasta", required = false) String hasta) {
        try {
            LocalDateTime desdeDt = parseFecha(desde, true);
            LocalDateTime hastaDt = parseFecha(hasta, false);

            byte[] xlsx = reporteService.generarReportePagos(desdeDt, hastaDt);

            String filename = "reporte-pagos-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(org.springframework.http.ContentDisposition
                    .attachment().filename(filename).build());
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(xlsx.length);

            return new ResponseEntity<>(xlsx, headers, 200);
        } catch (Exception e) {
            log.error("Error generando reporte de pagos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Acepta yyyy-MM-dd. Si es desde, lo lleva a 00:00; si es hasta, a 23:59:59.
    private LocalDateTime parseFecha(String s, boolean inicioDia) {
        if (s == null || s.isBlank()) return null;
        LocalDate d = LocalDate.parse(s);
        return inicioDia ? d.atStartOfDay() : d.atTime(23, 59, 59);
    }
}
