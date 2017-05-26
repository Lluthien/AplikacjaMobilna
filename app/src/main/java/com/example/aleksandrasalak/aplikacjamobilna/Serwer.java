package com.example.aleksandrasalak.aplikacjamobilna;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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

public class Serwer extends AsyncTask<Void, Void, String>{
    ProgressDialog pr;
    private String url;
    private HashMap<String, String> postParametry;
    private Context contextMain;

    public Serwer(Context context, String url, HashMap<String, String> postDataParams){
        contextMain=context;
        this.url=url;
        this.postParametry=postDataParams;
    }

    protected String doInBackground(Void... voids){
        String wynik = wykonajZapytaniePOST(url,postParametry);
        return wynik;
    }

    protected void onPreExecute() {
        pr = new ProgressDialog(contextMain);
        pr.setTitle("Zaczekaj");
        pr.setMessage("Pobieram dane z serwera");
        pr.setCancelable(false);
        pr.show();
    }

    @Override
    protected void onPostExecute(String wynik) {
        pr.dismiss();
    }

    public String wykonajZapytaniePOST(String urlRzadaniaStr,
                                       HashMap<String, String> parametryPOST) {
        URL url;
        String odpowiedzStr = "";
        try {
            url = new URL(urlRzadaniaStr);
            HttpURLConnection polaczenie = (HttpURLConnection) url.openConnection();
            polaczenie.setReadTimeout(15000);
            polaczenie.setConnectTimeout(15000);
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
