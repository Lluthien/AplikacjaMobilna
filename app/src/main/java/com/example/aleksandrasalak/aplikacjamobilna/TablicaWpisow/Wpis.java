package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;

public class Wpis {
    private String tresc;
    private String temat;
    private String autor;
    private String data;
    private String idSrv;

    public Wpis(String temat, String tresc, String autor, String data, String idSrv) {
        this.tresc = tresc;
        this.autor = autor;
        this.temat=temat;
        this.data=data;
        this.idSrv=idSrv;
    }

    public String pobierzTresc() {
        return tresc;
    }
    public String pobierzTemat() {
        return temat;
    }
    public String pobierzAutora() {
        return autor;
    }
    public String pobierzDate() {
        return data;
    }
    public String pobierzId() {
        return data;
    }

    //private static int idOstatniegoWpisu = 0;


}