package com.bibliomanager.service;

import com.bibliomanager.model.Loan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void exportLoans(String outputPath, List<Loan> loans) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // Header
            writer.write("Student,Book,Loan Date,Due Date,Return Date,Status,Registered By");
            writer.newLine();

            for (Loan loan : loans) {
                String student = loan.getStudent().getFirstName() + " " + loan.getStudent().getLastName();
                String book = loan.getBook().getTitle();
                String loanDate = loan.getLoanDate().format(FMT);
                String dueDate = loan.getDueDate().format(FMT);
                String returnDate = loan.getReturnDate() != null ? loan.getReturnDate().format(FMT) : "";
                String status = loan.getStatus().toString();
                String lib = loan.getRegisteredBy() != null ? loan.getRegisteredBy().getUsername() : "";

                writer.write(String.join(",",
                        escape(student), escape(book),
                        loanDate, dueDate, returnDate,
                        status, escape(lib)));
                writer.newLine();
            }
        }
    }

    //Escape commas and quotation marks for valid CSV
    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}