package com.example.aleksandrasalak.aplikacjamobilna.Portfel;


public class WpisPortfela {
    private String opis;
    private String data;
    private String id;
    private String wartosc;


    public WpisPortfela(String opis, String data, String id, String wartosc) {
        this.opis = opis;
        this.data = data;
        this.id = id;
        this.wartosc = wartosc;
    }

    public String pobierzOpis() {
        return opis;
    }

    public String pobierztDate() {
        return data;
    }

    public String pobierzId() {
        return id;
    }

    public String pobierztWartosc() {
        return wartosc;
    }
}
