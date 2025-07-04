package com.example.onetap.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Infogempa", strict = false)
public class InfoGempa {
    @ElementList(name = "gempa", inline = true)
    private List<EarthquakeItem> gempaList;

    public List<EarthquakeItem> getGempaList() {
        return gempaList;
    }
}
