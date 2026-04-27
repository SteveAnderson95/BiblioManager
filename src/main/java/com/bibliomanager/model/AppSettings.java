package com.bibliomanager.model;

public class AppSettings {
    private String libraryName;
    private String libraryAddress;
    private int defaultLoanDuration;
    private int maxLoansPerStudent;

    public AppSettings(String libraryName, String libraryAddress, int defaultLoanDuration, int maxLoansPerStudent) {
        this.libraryName = libraryName;
        this.libraryAddress = libraryAddress;
        this.defaultLoanDuration = defaultLoanDuration;
        this.maxLoansPerStudent = maxLoansPerStudent;
    }

    public String getLibraryName() { return libraryName; }
    public String getLibraryAddress() { return libraryAddress; }
    public int getDefaultLoanDuration() { return defaultLoanDuration; }
    public int getMaxLoansPerStudent() { return maxLoansPerStudent; }
    public void setLibraryName(String v) { this.libraryName = v; }
    public void setLibraryAddress(String v) { this.libraryAddress = v; }
    public void setDefaultLoanDuration(int v) { this.defaultLoanDuration = v; }
    public void setMaxLoansPerStudent(int v) { this.maxLoansPerStudent = v; }
}