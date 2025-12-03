package com.project.model;

public class dateModel { // Pastikan nama class SAMA dengan nama file

    // 1. Definisikan variabel
    private String tanggal;
    private String bulan;
    private boolean isToday;

    // 2. INI BAGIAN PENTING YANG HILANG (CONSTRUCTOR)
    // Ini adalah "resep" yang menerima String, String, dan boolean
    public dateModel(String tanggal, String bulan, boolean isToday) {
        this.tanggal = tanggal;
        this.bulan = bulan;
        this.isToday = isToday;
    }

    // 3. Getter (ini dibutuhkan oleh Adapter)
    public String getTanggal() {
        return tanggal;
    }

    public String getBulan() {
        return bulan;
    }

    public boolean isToday() {
        return isToday;
    }
}