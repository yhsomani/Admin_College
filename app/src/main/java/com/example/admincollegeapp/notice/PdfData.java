package com.example.admincollegeapp.notice;

public class PdfData {
    private String pdfTitle;
    private String pdfUrl;
    private String key; // Added key for deletion

    public PdfData() {
        // Default constructor required for Firebase
    }

    public PdfData(String pdfTitle, String pdfUrl, String key) {
        this.pdfTitle = pdfTitle;
        this.pdfUrl = pdfUrl;
        this.key = key;
    }

    public String getPdfTitle() {
        return pdfTitle;
    }

    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}