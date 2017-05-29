package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DodajWpisActivity extends AppCompatActivity implements ZawolaniaZwrotne{
    static final String TOKEN = "com.example.arek.TOKEN";
    HashMap<String, String> parametryZapytaniaPOST;
    Serwer serwer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    static final String DODAWANIE_WPISU_URL="http://enecio.heliohost.org/dodajwpis.php/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_wpis);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void dodajWpis(View view){
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");

        String tytulStr = ((EditText) findViewById(R.id.tytulEditText)).getText().toString();
        String trescStr = ((EditText) findViewById(R.id.trescEditText)).getText().toString();


        parametryZapytaniaPOST = new HashMap<String, String>();
        parametryZapytaniaPOST.put("kod",ustawionyToken);
        parametryZapytaniaPOST.put("temat",tytulStr);
        parametryZapytaniaPOST.put("tresc",trescStr);

        serwer = new Serwer(DodajWpisActivity.this,DODAWANIE_WPISU_URL, parametryZapytaniaPOST,this,"dw");

        serwer.execute();

    }

    public void anuluj(View view){
        onBackPressed();
    }


    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}

    @Override
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {

        if(!wynikZserwera.equals("2x")&&!wynikZserwera.equals("2x2x")&&!wynikZserwera.equals("3x")
                &&!wynikZserwera.equals("2x3x")&&!wynikZserwera.equals("2x2x3x")
                &&!wynikZserwera.isEmpty()&&!wynikZserwera.equals("pusty")){

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",wynikZserwera);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }else{
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

    }


}
