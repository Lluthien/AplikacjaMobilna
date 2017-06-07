package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;

public class Wpis {
    private String tresc;
    private String temat;
    private String autor;
    private String data;
    private long idSrv;

    public Wpis(String temat, String tresc, String autor, String data, long idSrv) {
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
    public long pobierzId() {
        return idSrv;
    }

    public String pobierzTrescShort() {
        return tresc;
    }
    public String pobierzTematShort() {
        return temat;
    }


    //private static int idOstatniegoWpisu = 0;


}