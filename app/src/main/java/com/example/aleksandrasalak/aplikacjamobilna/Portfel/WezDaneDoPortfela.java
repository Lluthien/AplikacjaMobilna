package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.util.HashMap;

public class WezDaneDoPortfela extends AppCompatActivity implements ZawolaniaZwrotne {
    static final String WYSYLANIE_WPISOW_PORTFELA_URL="http://enecio.heliohost.org/dodajpozycjeportfela.php/";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Serwer serwer;
    HashMap<String, String> parametryZapytaniaPOST;
    static final String TOKEN = "com.example.arek.TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wez_dane_do_portfela);
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void anuluj(View view){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }


    String opis,data,wartosc;
    String ustawionyToken;

    public void dodaj(View view){

        ustawionyToken = sharedPref.getString(TOKEN, "o");
        opis = ((EditText)findViewById(R.id.opisTx)).getText().toString();
        wartosc = ((EditText)findViewById(R.id.wartoscTx)).getText().toString();
        data = ((EditText)findViewById(R.id.dataTx)).getText().toString();

        parametryZapytaniaPOST = new HashMap<String, String>();
        parametryZapytaniaPOST.put("kod",ustawionyToken);
        parametryZapytaniaPOST.put("opis", opis);
        parametryZapytaniaPOST.put("wartosc",wartosc );
        parametryZapytaniaPOST.put("data",data );

        serwer = new Serwer(WezDaneDoPortfela.this,WYSYLANIE_WPISOW_PORTFELA_URL, parametryZapytaniaPOST,WezDaneDoPortfela.this,"ppw2");
        serwer.execute();



    }


    @Override
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("wartosc",wartosc);
            returnIntent.putExtra("data",data);
            returnIntent.putExtra("opis",opis);

            setResult(Activity.RESULT_OK,returnIntent);
            finish();

    }
}
