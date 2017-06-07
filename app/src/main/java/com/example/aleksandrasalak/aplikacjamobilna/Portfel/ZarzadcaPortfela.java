package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import android.text.Html;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



public class ZarzadcaPortfela {

    ArrayList<WpisPortfela> listaWpisowPortfela;
    public ZarzadcaPortfela(){
        listaWpisowPortfela = new ArrayList<WpisPortfela>();
    }

    public ArrayList<WpisPortfela> stworzPortfel(String wynikWpisy) {
        listaWpisowPortfela.clear();
        if(wynikWpisy.length()>=10) {
            Document doc = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ByteArrayInputStream input = new ByteArrayInputStream(wynikWpisy.replaceAll("&","%1%").getBytes("UTF-8"));
                doc = builder.parse(input);
                doc.getDocumentElement().normalize();
            } catch (Exception e) {

            }
            NodeList listaWezlow = doc.getElementsByTagName("pozycja");
            String data = "";
            String opis = "";
            String wartosc = "";
            String id = "";

            for (int nrPustuWxml = 0; nrPustuWxml < listaWezlow.getLength(); nrPustuWxml++) {
                Node wezel = listaWezlow.item(nrPustuWxml);
                if (wezel.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) wezel;
                    data = eElement
                            .getElementsByTagName("data")
                            .item(0)
                            .getTextContent();
                    opis = eElement
                            .getElementsByTagName("opis")
                            .item(0)
                            .getTextContent()
                            .replaceAll("%1%","&");
                    wartosc = eElement
                            .getElementsByTagName("wartosc")
                            .item(0)
                            .getTextContent();
                    id = eElement
                            .getElementsByTagName("id")
                            .item(0)
                            .getTextContent();
                }

                listaWpisowPortfela.add(new WpisPortfela(Html.fromHtml(opis).toString(), data.substring(0,10),
                        id, wartosc));
            }
        }else if(wynikWpisy.equals("4")) {
            listaWpisowPortfela.add(new WpisPortfela("Brak wpisow portfela","","",""));
        }else{
            listaWpisowPortfela.add(new WpisPortfela("Blad podczas laczenia z serwerem","blad","blad","blad"));

        }
        return listaWpisowPortfela;

    }

    public boolean dodajDoPortfela(String data,String opis,String wartosc,String id){
        listaWpisowPortfela.add(0, new WpisPortfela(opis,data,id,wartosc));
        return true;
        //adapter.notifyItemInserted(1);
    }



}