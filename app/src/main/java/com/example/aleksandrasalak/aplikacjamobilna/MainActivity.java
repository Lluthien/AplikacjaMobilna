package com.example.aleksandrasalak.aplikacjamobilna;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    static final String TOKEN = "com.example.arek.TOKEN";
    Intent tablicaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");

        if((!ustawionyToken.equals("Brak"))){
            // AUTORYZACJA KODU Z WYKORZYSTANIEM SERWERA
            // JEZELI SIE POWIEDZIE
            tablicaActivity = new Intent(this,TablicaActivity.class);
            startActivity(tablicaActivity);

            // JEZELI SIE NIE POWIEDZIE - setContentView(R.layout.main_jesli_nie_zalogowany);

        }else{
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }
    }


    public void obslugaZdarzenAktywnosciGlownej(View view) {
        String login = ((EditText) findViewById(R.id.loginEdit)).getText().toString();
        String hasloPodane1 = ((EditText) findViewById(R.id.hasloEdit)).getText().toString();

        switch (view.getId()) {
            case R.id.zalogujBtn:
                // SPRAWDZENIE CZY NA SERWERZE HASLO I LOGIN SIE ZGADZAJA I POBRANIE TOKENA
                editor.putString(TOKEN, "TOKEN_POBRANY_Z_SERWERA");
                editor.apply();

                tablicaActivity = new Intent(this,TablicaActivity.class);
                startActivity(tablicaActivity);

                break;
            case R.id.zarejestrujBtn:
                String hasloPodane2 = ((EditText) findViewById(R.id.haslo2Edit)).getText().toString();

                if(hasloPodane1.equals(hasloPodane2)) {
                    boolean loginPoprawny = login.matches("^.*[^a-zA-Z0-9 ].*$");
                    if(loginPoprawny){
                        // REJESTRACJA KONTA NA SERWERZE
                        // O ILE SIE POWIEDZIE POBRANY JEST TOKEN I WCHODZIMY DO APLIKACJI
                        editor.putString(TOKEN, "TOKEN_POBRANY_Z_SERWERA");
                        editor.apply();

                        tablicaActivity = new Intent(this,TablicaActivity.class);
                        startActivity(tablicaActivity);
                    }else{
                        okienkoDialogowe("Podany login jest nie poprawny","Login powinien skladac sie ze znakow alfanumerycznych");
                    }
                }else{
                    okienkoDialogowe("Podano dwa rozne hasla","Jako haslo wprowadz dwa identyczne ciagi znakow");
                }
                break;
        }
    }
    public void okienkoDialogowe(String tytul,String wiadomosc){
        new AlertDialog.Builder(this)
                .setTitle(tytul)
                .setMessage(wiadomosc)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
