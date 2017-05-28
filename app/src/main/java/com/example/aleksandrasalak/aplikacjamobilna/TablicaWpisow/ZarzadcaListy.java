package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;


import android.text.Html;
import android.util.Log;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ZarzadcaListy {


    ArrayList<Wpis> listaWpisow;
    public ArrayList<Wpis> stworzListeWpisow(String wynikWpisy) {

        if(wynikWpisy.length()!=0) {
            listaWpisow = new ArrayList<Wpis>();

            Document doc = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(wynikWpisy.replaceAll("&","%1%").getBytes("UTF-8"));
                doc = builder.parse(input);
                doc.getDocumentElement().normalize();
            } catch (Exception e) {

            }
            NodeList listaWezlow = doc.getElementsByTagName("post");
            String data = "";
            String uzytkownik = "";
            String temat = "";
            String tresc = "";
            String id = "";

            for (int nrPustuWxml = 0; nrPustuWxml < listaWezlow.getLength(); nrPustuWxml++) {
                Node wezel = listaWezlow.item(nrPustuWxml);
                if (wezel.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) wezel;
                    data = eElement
                            .getElementsByTagName("data")
                            .item(0)
                            .getTextContent();
                    uzytkownik = eElement
                            .getElementsByTagName("uzytkownik")
                            .item(0)
                            .getTextContent();
                    temat = eElement
                            .getElementsByTagName("temat")
                            .item(0)
                            .getTextContent()
                            .replaceAll("%1%","&");
                    tresc = eElement
                            .getElementsByTagName("tresc")
                            .item(0)
                            .getTextContent()
                            .replaceAll("%1%","&");
                    id = eElement
                            .getElementsByTagName("id")
                            .item(0)
                            .getTextContent();
                }

                listaWpisow.add(new Wpis(Html.fromHtml(temat).toString(), Html.fromHtml(tresc).toString(),
                                uzytkownik, data, id));
            }

            return listaWpisow;
        }else{
            listaWpisow = new ArrayList<Wpis>();
            listaWpisow.add(new Wpis("Blad podczas laczenia z serwerem","blad","blad","blad","blad"));
            return listaWpisow;

        }
    }

    public boolean dodajNaListe(String wpisXml){

        if(wpisXml.length()!=0) {
            Document doc = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(wpisXml.replaceAll("&","%1%").getBytes("UTF-8"));
                doc = builder.parse(input);
                doc.getDocumentElement().normalize();
            } catch (Exception e) {

            }

            NodeList listaWezlow = doc.getElementsByTagName("post");
            String data = "";
            String uzytkownik = "";
            String temat = "";
            String tresc = "";
            String id = "";


            Node wezel = listaWezlow.item(0);
            if (wezel.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) wezel;
                data = eElement
                        .getElementsByTagName("data")
                        .item(0)
                        .getTextContent();
                uzytkownik = eElement
                        .getElementsByTagName("uzytkownik")
                        .item(0)
                        .getTextContent();
                temat = eElement
                        .getElementsByTagName("temat")
                        .item(0)
                        .getTextContent()
                        .replaceAll("%1%","&");
                tresc = eElement
                        .getElementsByTagName("tresc")
                        .item(0)
                        .getTextContent()
                        .replaceAll("%1%","&");
                id = eElement
                        .getElementsByTagName("id")
                        .item(0)
                        .getTextContent();
            }

            listaWpisow.add(0, new Wpis(Html.fromHtml(temat).toString(), Html.fromHtml(tresc).toString(), uzytkownik, data, id));
            return true;
        }else{
            Log.d("logik", "zarzadca dostal pusty");

            return false;
        }

        //adapter.notifyItemInserted(1);
    }

    public void odfiltruj(String wzor){

        for (Iterator<Wpis> iterator = listaWpisow.iterator(); iterator.hasNext(); ) {
            Wpis wpis = iterator.next();
            if(wpis.pobierzTemat().contains(wzor) || wpis.pobierzTresc().contains(wzor) || wpis.pobierzAutora().contains(wzor))
            {}else
                iterator.remove();
        }

    }

}
