package com.example.aleksandrasalak.aplikacjamobilna.Logowanie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow.TablicaActivity;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements ZawolaniaZwrotne {
    Serwer serwer;
    HashMap<String, String> parametryZapytaniaPOST;
    static final String TOKEN = "com.example.arek.TOKEN";

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    static final String AUTORYZACJA_URL="http://enecio.heliohost.org/autoryzuj.php/";
    static final String REJESTRACJA_URL="http://enecio.heliohost.org/rejestracja.php/";
    static final String LOGOWANIE_URL="http://enecio.heliohost.org/logowanie.php/";

    Intent tablicaActivity;

    // Na poczatku zycia aktywnosci
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Uzyskujemy dostep do preferencji
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Pobieramy zawartosc preferencji TOKEN, jesli nie istnieje w miejsce jej tresci pojawia sie wartosc 'Brak'
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");

        // Sprawdzamy czy TOKEN jest juz zapisany na urzadzeniu
        if(!ustawionyToken.equals("Brak")){

            // Jesli TOKEN jest zapisany tworzymy mape do zapytania POST do serwera
            parametryZapytaniaPOST = new HashMap<String, String>();

            // Ustawiamy parametr kod - jest to wspomniany juz TOKEN
            parametryZapytaniaPOST.put("kod",ustawionyToken);

            // Tworzymy obiekt Serwera - umożliwii on nam odpytanie zdalnego serwera o to czy uzytkownik ma konto
            serwer = new Serwer(MainActivity.this,AUTORYZACJA_URL, parametryZapytaniaPOST,this,"ma");

            String wynikAutoryzacji="0";

            // Odpytujemy serwer o to czy zalogowany uzytkownik ma na pewno konto na serwerze
            serwer.execute();

            // Odpowiedz przyjdzie do funkcji zwrotnej na samym dole
        }else{
            // Jezeli TOKEN nie jest zapisany w pamieci urzadzenia ustawiamy w obecnej aktywnosci layout logowania i rejestracji
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }
    }

    // W przypadku klikniecia na przycisk Rejestracji lub Logowania
    public void obslugaZdarzenAktywnosciGlownej(View view) {
        String pobranyToken="0";
        // Pobieramy login i piersze haslo
        String login = ((EditText) findViewById(R.id.loginEdit)).getText().toString();
        String hasloPodane1 = ((EditText) findViewById(R.id.hasloEdit)).getText().toString();

        // Sprawdzamy ktory przycisk zostal nacisniety
        switch (view.getId()) {
            case R.id.zalogujBtn:
                // W przypadku nacisniecia przycisku logowania

                // tworzymy mape wykorzystywana do zapytania POST do serwera
                parametryZapytaniaPOST = new HashMap<String, String>();

                // Ustawiamy parametr loginu i hasla uzytkownika
                parametryZapytaniaPOST.put("nazwa",login);
                parametryZapytaniaPOST.put("haslo",hasloPodane1);

                // Tworzymy obiekt serwera umozliwiajacy logowanie
                serwer = new Serwer(MainActivity.this,LOGOWANIE_URL, parametryZapytaniaPOST,this,"ml");

                // Wysylamy zapytanie o TOKEN

                    serwer.execute();
                // Odpowiedz przyjdzie do funkcji zwrotnej logowania


                break;
            case R.id.zarejestrujBtn:
                // W przypadku gdy uzytkownik kliknal przycisk rejestracji
                // pobieramy haslo z drugiego pola formularza
                String hasloPodane2 = ((EditText) findViewById(R.id.haslo2Edit)).getText().toString();

                // Sprawdzamy czy oba wprowadzone haslo sa jednakowe
                if(hasloPodane1.equals(hasloPodane2)) {

                // Sprawdzamy poprawnosc wprowadzonego loginu
                    boolean loginPoprawny = login.matches("[A-Za-z0-9]+");
                // Jezeli login jest poprawnie sformatowany
                    if(loginPoprawny){

                        // tworzymy mape wykorzystywana do zapytania POST do serwera
                        parametryZapytaniaPOST = new HashMap<String, String>();

                        // Ustawiamy parametr loginu i hasla uzytkownika
                        parametryZapytaniaPOST.put("nazwa",login);
                        parametryZapytaniaPOST.put("haslo",hasloPodane1);

                        // Tworzymy obiekt serwera umozliwiajacy rejestracje
                        serwer = new Serwer(MainActivity.this,REJESTRACJA_URL, parametryZapytaniaPOST, this, "mr");

                        // Wysylamy zapytanie o to by dodac uzytkownika

                         serwer.execute();



                    }else{
                        pokazAlert("Podany login jest nie poprawny","Login powinien skladac sie ze znakow alfanumerycznych");
                    }
                }else{
                    pokazAlert("Podano dwa rozne hasla","Jako haslo wprowadz dwa identyczne ciagi znakow");

                }
                break;
        }
    }

public void pokazAlert(String temat,String tresc){
    new AlertDialog.Builder(this)
            .setTitle(temat)
            .setMessage(tresc)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
}

/*
    protected void onResume(){
        super.onResume();

        //setContentView(R.layout.empty);

        // Uzyskujemy dostep do preferencji
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Pobieramy zawartosc preferencji TOKEN, jesli nie istnieje w miejsce jej tresci pojawia sie wartosc 'Brak'
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");

        // Sprawdzamy czy TOKEN jest juz zapisany na urzadzeniu
        if(!ustawionyToken.equals("Brak")){

            // Jesli TOKEN jest zapisany tworzymy mape do zapytania POST do serwera
            parametryZapytaniaPOST = new HashMap<String, String>();

            // Ustawiamy parametr kod - jest to wspomniany juz TOKEN
            parametryZapytaniaPOST.put("kod",ustawionyToken);
            // Tworzymy obiekt Serwera - umożliwii on nam odpytanie zdalnego serwera o to czy uzytkownik ma konto
            serwer = new Serwer(MainActivity.this,AUTORYZACJA_URL, parametryZapytaniaPOST,this,"ma");

            String wynikAutoryzacji="0";


            // Odpytujemy serwer o to czy zalogowany uzytkownik ma na pewno konto na serwerze
            serwer.execute();

        }else{
            // Jezeli TOKEN nie jest zapisany w pamieci urzadzenia ustawiamy w obecnej aktywnosci layout logowania i rejestracji
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }

    }
*/

    @Override
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {
        // Sprawdzamy czy uzytkownik ma autoryzacje do korzystania z aplikacji
        if(wynikZserwera.equals("1")) {

            // Jesli uzytkownik ma autoryzacje uruchamiamy aktywnosc z glowna aplikacja
            tablicaActivity = new Intent(this, TablicaActivity.class);
            startActivity(tablicaActivity);
            finish();

        }else {

            // Jezeli uzytkownik nie ma autoryzacji ustawiamy w obecnej aktywnosci layout logowania i rejestracji
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }
    }
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {
        // Sprawdzamy czy token jest poprawny, ma poprawny format
        if(wynikZserwera.length()!=32){

            // Jezeli format tokenu jest nieodpowiedni, na serwerze wystapil blad lub
            // uzytkownik nie posiada konta
            pokazAlert("Blad","Nie posiadasz konta, wpierw musisz je utworzyc!");
        }else {

            // Gdy format tokenu jest poprawny ustawiamy go w preferencjach
            editor.putString(TOKEN, wynikZserwera);
            editor.apply();

            // Uruchamiamy aktywnosc z glownym programem
            tablicaActivity = new Intent(this, TablicaActivity.class);
            startActivity(tablicaActivity);
        }

    }
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {
        if(wynikZserwera.length()!=32){

            // Jezeli format tokenu jest nieodpowiedni, na serwerze wystapil blad lub
            // ktos inny posiada juz wprowadzony login
            pokazAlert("Blad","Ten login jest juz zajety!");
        }else {

            // Gdy format tokenu jest poprawny ustawiamy go w preferencjach
            editor.putString(TOKEN, wynikZserwera);
            editor.apply();

            // Uruchamiamy aktywnosc z glownym programem
            tablicaActivity = new Intent(this, TablicaActivity.class);
            startActivity(tablicaActivity);
        }
    }
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

    }


}
