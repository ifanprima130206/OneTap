package com.example.onetap.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "gempa", strict = false)
public class Gempa {

    @Element(name = "Tanggal")
    public String tanggal;

    @Element(name = "Jam")
    public String jam;

    @Element(name = "Lintang")
    public String lintang;

    @Element(name = "Bujur")
    public String bujur;

    @Element(name = "Magnitude")
    public String magnitude;

    @Element(name = "Kedalaman")
    public String kedalaman;

    @Element(name = "Wilayah")
    public String wilayah;

    @Element(name = "Potensi")
    public String potensi;
}
