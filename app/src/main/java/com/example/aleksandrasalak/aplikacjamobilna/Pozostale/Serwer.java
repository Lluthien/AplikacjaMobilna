package com.example.aleksandrasalak.aplikacjamobilna.Pozostale;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class Serwer extends AsyncTask<Void, Void, Void>{
    ProgressDialog pr;
    private String url;
    private HashMap<String, String> postParametry;
    private Context contextMain;
    ZawolaniaZwrotne ujscieDanych;
    String cel;

    public Serwer(Context context, String url, HashMap<String, String> postDataParams,
                  ZawolaniaZwrotne ujscieDanych, String cel){
        contextMain=context;
        this.url=url;
        this.postParametry=postDataParams;
        pr = new ProgressDialog(context);
        this.ujscieDanych=ujscieDanych;
        this.cel=cel;

    }

    String wynik;
    protected Void doInBackground(Void... voids){

        wynik = wykonajZapytaniePOST(url,postParametry);

        return null;
    }

    protected void onPreExecute() {

       // pr = ProgressDialog.show(contextMain, "dialog title","dialog message", true);

        if(cel.equals("ml")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("mr"))
        { pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("tp")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("tz")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("tp2")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("dw")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}
        if(cel.equals("ppw")){ pr.setMessage("Pobieranie danych z serwera, prosze czekac");
            pr.show();}

    }

    @Override
    protected void onPostExecute(Void wyniks) {
        super.onPostExecute(wyniks);


        if(cel.equals("ma"))
            ujscieDanych.funkcjaZwrotnaMainAutoryzacja(wynik);
        if(cel.equals("ml")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaMainLogowanie(wynik);}
        if(cel.equals("mr")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaMainRejestracja(wynik);}
        if(cel.equals("tp")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaTablicaPobranieWpisow(wynik);}
        if(cel.equals("tz")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaTablicaZnajdowanieWpisu(wynik);}
        if(cel.equals("tp2")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaTablicaPobranieWpisow2(wynik);}
        if(cel.equals("dw")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaDowajWpis(wynik);}
        if(cel.equals("ppw")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaListujWpisyPortfela(wynik);
        }if(cel.equals("ppw2")){pr.dismiss();
            ujscieDanych.funkcjaZwrotnaDodajWpisyPortfela(wynik);
        }


    }

    public String wykonajZapytaniePOST(String urlRzadaniaStr,
                                       HashMap<String, String> parametryPOST) {
        URL url;
        String odpowiedzStr = "";
        try {
            url = new URL(urlRzadaniaStr);

            HttpURLConnection polaczenie = (HttpURLConnection) url.openConnection();
            polaczenie.setReadTimeout(22000);
            polaczenie.setConnectTimeout(22000);
            polaczenie.setRequestMethod("GET");
            polaczenie.setDoInput(true);
            polaczenie.setDoOutput(true);

            OutputStream os = polaczenie.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(sformatujParametry(parametryPOST));

            writer.flush();
            writer.close();
            os.close();
            int kodOdpowiedzi=polaczenie.getResponseCode();

            if (kodOdpowiedzi == HttpsURLConnection.HTTP_OK) {
                String linia;
                BufferedReader br=new BufferedReader(new InputStreamReader(polaczenie.getInputStream()));
                while ((linia=br.readLine()) != null) {
                    odpowiedzStr+=linia;
                }
            }
            else {
                odpowiedzStr="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return odpowiedzStr.trim();
    }

    private String sformatujParametry(HashMap<String, String> mapaParametrow) throws UnsupportedEncodingException {
        StringBuilder parametry = new StringBuilder();
        boolean pierwszy = true;
        for(Map.Entry<String, String> para : mapaParametrow.entrySet()){
            if (pierwszy)
                pierwszy = false;
            else
                parametry.append("&");

            parametry.append(URLEncoder.encode(para.getKey(), "UTF-8"));
            parametry.append("=");
            parametry.append(URLEncoder.encode(para.getValue(), "UTF-8"));
        }

        return parametry.toString();
    }



}
