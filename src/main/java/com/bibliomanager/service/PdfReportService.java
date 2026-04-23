package com.bibliomanager.service;

import com.bibliomanager.model.Loan;
import com.bibliomanager.model.LoanStatus;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PdfReportService {

    private static final DeviceRgb NAVY = new DeviceRgb(0x2E, 0x3D, 0x60);
    private static final DeviceRgb ORANGE = new DeviceRgb(0xF2, 0x5F, 0x29);
    private static final DeviceRgb GREEN = new DeviceRgb(0x1A, 0x7A, 0x3C);
    private static final DeviceRgb RED = new DeviceRgb(0xB7, 0x1C, 0x1C);
    private static final DeviceRgb GRAY = new DeviceRgb(128, 128, 128);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(0xF5, 0xF6, 0xFA);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public void generateReport(String outputPath, List<Loan> loans, String period, String librarianName) throws IOException {
        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 50, 40);

        // Header & Main content
        addHeader(document, period);
        addStatisticsSection(document, loans);
        document.add(new Paragraph("\n"));
        addActivityTable(document, loans);

        // Footer
        addSimpleFooter(document);

        document.close();
    }

    private void addHeader(Document doc, String period) throws IOException {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3})).setWidth(UnitValue.createPercentValue(100));

        URL logoUrl = getClass().getResource("/com/bibliomanager/icons/logo.png");
        if (logoUrl != null) {
            Image logo = new Image(ImageDataFactory.create(logoUrl)).setWidth(55).setHeight(55);
            headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        Div titleDiv = new Div();
        titleDiv.add(new Paragraph("BiblioManager").setFontSize(20).setBold().setFontColor(NAVY));
        titleDiv.add(new Paragraph("Activity History Report • " + period).setFontSize(12).setFontColor(GRAY));

        headerTable.addCell(new Cell().add(titleDiv).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

        doc.add(headerTable);

        // Separator
        doc.add(new Paragraph().setBorderBottom(new SolidBorder(NAVY, 2)).setMarginTop(10).setMarginBottom(20));
    }

    private void addStatisticsSection(Document doc, List<Loan> loans) {
        int total = loans.size();
        int returned = (int) loans.stream().filter(l -> l.getStatus() == LoanStatus.RETURNED).count();
        int onTime = (int) loans.stream()
                .filter(l -> l.getStatus() == LoanStatus.RETURNED && l.getReturnDate() != null && !l.getReturnDate().isAfter(l.getDueDate()))
                .count();
        int overdue = (int) loans.stream().filter(l -> l.getStatus() == LoanStatus.OVERDUE).count();
        double punctuality = returned > 0 ? (onTime * 100.0) / returned : 0.0;

        Paragraph title = new Paragraph("KEY STATISTICS").setFontSize(16).setBold().setFontColor(NAVY).setMarginBottom(15);
        doc.add(title);

        Table stats = new Table(UnitValue.createPercentArray(4)).setWidth(UnitValue.createPercentValue(100));

        stats.addCell(buildStatCell("Total Records", String.valueOf(total), ORANGE));
        stats.addCell(buildStatCell("Returned", String.valueOf(returned), GREEN));
        stats.addCell(buildStatCell("Punctuality Rate", String.format("%.1f%%", punctuality), GREEN));
        stats.addCell(buildStatCell("Overdue", String.valueOf(overdue), RED));

        doc.add(stats);
    }

    private Cell buildStatCell(String label, String value, DeviceRgb color) {
        Div card = new Div().setBackgroundColor(LIGHT_GRAY).setPadding(12).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));

        card.add(new Paragraph(value).setFontSize(22).setBold().setFontColor(color).setTextAlignment(TextAlignment.CENTER));

        card.add(new Paragraph(label).setFontSize(9).setFontColor(GRAY).setTextAlignment(TextAlignment.CENTER));

        return new Cell().add(card).setBorder(Border.NO_BORDER).setPadding(4);
    }

    private void addActivityTable(Document doc, List<Loan> loans) {
        Paragraph title = new Paragraph("DETAILED ACTIVITY LOG").setFontSize(15).setBold().setFontColor(NAVY).setMarginBottom(12);
        doc.add(title);

        float[] widths = {2.2f, 3.2f, 1.8f, 1.8f, 1.8f, 1.6f, 1.6f};
        Table table = new Table(UnitValue.createPercentArray(widths)).setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Student", "Book Title", "Loan Date", "Due Date", "Return Date", "Status", "Librarian"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold().setFontSize(10))
                    .setBackgroundColor(NAVY)
                    .setFontColor(ColorConstants.WHITE)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        for (Loan loan : loans) {
            table.addCell(buildDataCell(loan.getStudent().getFirstName() + " " + loan.getStudent().getLastName()));
            table.addCell(buildDataCell(loan.getBook().getTitle()));
            table.addCell(buildDataCell(loan.getLoanDate().format(DATE_FMT)));
            table.addCell(buildDataCell(loan.getDueDate().format(DATE_FMT)));
            table.addCell(buildDataCell(loan.getReturnDate() != null ? loan.getReturnDate().format(DATE_FMT) : "—"));

            String statusText = getStatusText(loan);
            DeviceRgb statusColor = getStatusColor(loan);
            table.addCell(new Cell()
                    .add(new Paragraph(statusText).setBold().setFontColor(statusColor))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER));

            String lib = loan.getRegisteredBy() != null ? loan.getRegisteredBy().getUsername() : "-";
            table.addCell(buildDataCell(lib));
        }
        doc.add(table);
    }

    private Cell buildDataCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(9.5f))
                .setPadding(7)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }

    private String getStatusText(Loan loan) {
        if (loan.getStatus() == LoanStatus.RETURNED && loan.getReturnDate() != null && loan.getReturnDate().isAfter(loan.getDueDate())) {
            long days = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
            return "Late (+" + days + "j)";
        }
        return switch (loan.getStatus()) {
            case RETURNED -> "On Time";
            case OVERDUE -> "Overdue";
            case ONGOING -> "Active";
        };
    }

    private DeviceRgb getStatusColor(Loan loan) {
        if (loan.getStatus() == LoanStatus.RETURNED) {
            return (loan.getReturnDate() != null && loan.getReturnDate().isAfter(loan.getDueDate())) ? RED : GREEN;
        }
        return loan.getStatus() == LoanStatus.OVERDUE ? RED : NAVY;
    }

    private void addSimpleFooter(Document doc) {
        String footerText = "BiblioManager • Generated on " + LocalDate.now().format(FULL_DATE);
        doc.add(new Paragraph(footerText)
                .setFontSize(8)
                .setFontColor(GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40));
    }
}