package com.alertavecinal.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alertavecinal.client.IncidentClient;
import com.alertavecinal.dto.DashboardResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.IncidenteDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final IncidentClient incidentClient;
    private final DashboardService dashboardService;

    public byte[] generarReporteIncidentes() {
        List<IncidenteDTO> incidentes = obtenerIncidentes();
        DashboardResponse dashboard = dashboardService.obtenerDashboard();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("Reporte de Incidentes - Alerta Vecinal", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Paragraph fecha = new Paragraph("Fecha: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalFont);
            fecha.setSpacingAfter(20);
            document.add(fecha);

            Paragraph resumen = new Paragraph("Resumen", headerFont);
            resumen.setSpacingAfter(10);
            document.add(resumen);

            document.add(new Paragraph("Total incidentes: " + dashboard.getIncidentes().getTotal(), normalFont));
            document.add(new Paragraph("Pendientes: " + dashboard.getIncidentes().getPendientes(), normalFont));
            document.add(new Paragraph("Asignados: " + dashboard.getIncidentes().getAsignados(), normalFont));
            document.add(new Paragraph("En proceso: " + dashboard.getIncidentes().getEnProceso(), normalFont));
            document.add(new Paragraph("Atendidos: " + dashboard.getIncidentes().getAtendidos(), normalFont));
            document.add(new Paragraph("Cerrados: " + dashboard.getIncidentes().getCerrados(), normalFont));

            Paragraph patrullasTitle = new Paragraph("\nPatrullas", headerFont);
            patrullasTitle.setSpacingBefore(15);
            patrullasTitle.setSpacingAfter(10);
            document.add(patrullasTitle);

            document.add(new Paragraph("Total: " + dashboard.getPatrullas().getTotal(), normalFont));
            document.add(new Paragraph("Disponibles: " + dashboard.getPatrullas().getDisponibles(), normalFont));
            document.add(new Paragraph("Atendiendo: " + dashboard.getPatrullas().getAtendiendo(), normalFont));
            document.add(new Paragraph("Fuera de servicio: " + dashboard.getPatrullas().getFueraDeServicio(), normalFont));

            if (!incidentes.isEmpty()) {
                Paragraph detalleTitle = new Paragraph("\nDetalle de Incidentes", headerFont);
                detalleTitle.setSpacingBefore(15);
                detalleTitle.setSpacingAfter(10);
                document.add(detalleTitle);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);

                String[] headers = {"ID", "Tipo", "Dirección", "Estado", "Usuario"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    table.addCell(cell);
                }

                for (IncidenteDTO i : incidentes) {
                    table.addCell(new Phrase(String.valueOf(i.getId()), normalFont));
                    table.addCell(new Phrase(i.getTipo() != null ? i.getTipo() : "", normalFont));
                    table.addCell(new Phrase(i.getDireccion() != null ? i.getDireccion() : "", normalFont));
                    table.addCell(new Phrase(i.getEstado() != null ? i.getEstado() : "", normalFont));
                    table.addCell(new Phrase(i.getUsuarioId() != null ? String.valueOf(i.getUsuarioId()) : "",
                            normalFont));
                }

                document.add(table);
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    private List<IncidenteDTO> obtenerIncidentes() {
        GenericResponseDto<List<IncidenteDTO>> response = incidentClient.listarIncidentes();
        return response != null ? response.getResponse() : List.of();
    }
}
