package com.example.onetap.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "gempa", strict = false)
public class EarthquakeItem {
    @Element(name = "Tanggal")
    private String tanggal;

    @Element(name = "Jam")
    private String jam;

    @Element(name = "Lintang")
    private String lintang;

    @Element(name = "Bujur")
    private String bujur;

    @Element(name = "Magnitude")
    private String magnitude;

    @Element(name = "Kedalaman")
    private String kedalaman;

    @Element(name = "Wilayah")
    private String wilayah;

    @Element(name = "Potensi")
    private String potensi;

    // Getters
    public String getTanggal() {
        return tanggal;
    }

    public String getJam() {
        return jam;
    }

    public String getLintang() {
        return lintang;
    }

    public String getBujur() {
        return bujur;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getKedalaman() {
        return kedalaman;
    }

    public String getWilayah() {
        return wilayah;
    }

    public String getPotensi() {
        return potensi;
    }

    // Helper method untuk mendapatkan level magnitude
    public String getMagnitudeLevel() {
        try {
            double mag = Double.parseDouble(magnitude);
            if (mag < 3.0) return "Ringan";
            else if (mag < 5.0) return "Sedang";
            else if (mag < 7.0) return "Kuat";
            else return "Sangat Kuat";
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }

    // Helper method untuk mendapatkan warna berdasarkan magnitude
    public int getMagnitudeColor() {
        try {
            double mag = Double.parseDouble(magnitude);
            if (mag < 3.0) return android.graphics.Color.parseColor("#4CAF50"); // Green
            else if (mag < 5.0) return android.graphics.Color.parseColor("#FF9800"); // Orange
            else if (mag < 7.0) return android.graphics.Color.parseColor("#F44336"); // Red
            else return android.graphics.Color.parseColor("#9C27B0"); // Purple
        } catch (NumberFormatException e) {
            return android.graphics.Color.parseColor("#757575"); // Gray
        }
    }
}
