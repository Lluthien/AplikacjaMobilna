package com.example.aleksandrasalak.aplikacjamobilna.Logowanie;

/*
 * Aktywnosc glowna, sprawdza czy w urzadzeniu jest juz token uzytkownika - generowany przez serwer podczas rejestracji.
 * Jesli token jest juz zapisany sprawdzana jest jego autentycznosc - obecnosc w bazie serwera.
 * Jesli jest autentyczny wywolywana jest automatycznie aktywnosc tablicy.
 * Jeżeli token nie istnieje lub nie ma autoryzacji ustawiany jest dla obecnej aktywnosci layut umozliwiajacy rejestracje
 * lub logowanie, podczas logowania token jest zapisywany na urzadzeniu, podczas rejestracji jest generowany i nastepnie
 * zapisywany a urządzeniu
 */

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

public class MainActivity extends AppCompatActivity implements ZawolaniaZwrotne {

    // Pole przechowujące referencje do obiektu typu Serwer, obiekt umozliwia
    // komunikacje z serwerem zdalnym
    private Serwer serwer;
    // Kontener przechowujacy referencje do parametrow zapytan potrzebnych do
    // wyslania zapytania do serwera zdalnego
    private HashMap<String, String> parametryZapytaniaPOST;
    // Stala przechowujaca klucz potrzebny do wczytania z preferencji identyfikatora uzytkownika
    private static final String TOKEN = "com.example.arek.TOKEN";
    // Obiekty niezbedne do zarzadzania preferencami
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    // Stale z linkami do API, z ich wykorzystaniem tworzone sa zapytania do serwera zdalnego
    private static final String AUTORYZACJA_URL="http://enecio.heliohost.org/autoryzuj.php/";
    private static final String REJESTRACJA_URL="http://enecio.heliohost.org/rejestracja.php/";
    private static final String LOGOWANIE_URL="http://enecio.heliohost.org/logowanie.php/";
    // Pole przechowujace referencje intencji do stworzenia atywnosci TablicaActivity
    private Intent tablicaActivity;
    // Na poczatku zycia aktywnosci
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Pobranie referencji do obiektow umozliwiajacych dostep do preferencji
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Pobranie zawartosci (Stringa) preferencji ktorej klucz jest pod stala TOKEN,
        // zapisany tam moze byc TOKEN identyfikujacy uzytkownika
        // jesli preferencja nie istnieje w miejsce jej tresci pojawia sie wartosc 'Brak'
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");
        // Sprawdzenie czy TOKEN jest juz zapisany na urzadzeniu
        if(!ustawionyToken.equals("Brak")){
            // Jesli TOKEN jest zapisany tworzona jest mapa do zapytania POST do serwera zdalnego
            parametryZapytaniaPOST = new HashMap<String, String>();
            // Ustawiany jest parametr kod - jest to wspomniany juz TOKEN identyfikujacy uzytkownika
            parametryZapytaniaPOST.put("kod",ustawionyToken);
            // Tworzony jest obiekt Serwera - umożliwii on odpytanie zdalnego serwera o to czy uzytkownik ma konto
            serwer = new Serwer(MainActivity.this,AUTORYZACJA_URL, parametryZapytaniaPOST,this,"ma");
            // Zapytanie o to czy zalogowany uzytkownik ma na pewno konto na serwerze jest wywolywane
            serwer.execute();

            // Odpowiedzia serwera jest wywolanie odpowiedniej funkji callback z interfejsu ZarzadcaListy
            // o rodzaju wywlywanej funkncji zwrotnej decyduje ostatni parametr przekazywany do obiektu
            // Serwer w tym przypadku wywolana zostanie funkcja funkcjaZwrotnaMainAutoryzacja

        // W przeciwny wypadku - jezeli token nie jest ustawiony w urzadzeniu, lub uzytkownik
        // sie wylogowal - co sprowadza sie do tego samego
        }else{
            // Wobecnej aktywnosci ustawiany jest layout logowania i rejestracji, umozliwii on
            // przeprowadzenie tychze procesow
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }
    }

    // W przypadku klikniecia na przycisk Rejestracji lub Logowania
    public void obslugaZdarzenAktywnosciGlownej(View view) {
        // Z elementow EditView layoutu pobierane sa ciagi znakow - login i haslo
        String login = ((EditText) findViewById(R.id.loginEdit)).getText().toString();
        String hasloPodane1 = ((EditText) findViewById(R.id.hasloEdit)).getText().toString();

        // Sprawdzane jest ktory przycisk zostal nacisniety
        switch (view.getId()) {
            // W przypadku nacisniecia przycisku logowania
            case R.id.zalogujBtn:
                // Tworzona jest mapa wykorzystywana do zapytania POST do serwera zdalnego
                parametryZapytaniaPOST = new HashMap<String, String>();
                // Ustawiane sa parametry zapytania - login i haslo uzytkownika
                parametryZapytaniaPOST.put("nazwa",login);
                parametryZapytaniaPOST.put("haslo",hasloPodane1);
                // Tworzony jest obiekt serwera umozliwiajacy logowanie
                serwer = new Serwer(MainActivity.this,LOGOWANIE_URL, parametryZapytaniaPOST,this,"ml");
                // Zapytanie jest wysylane
                serwer.execute();
                // W odpowiedzi serwer wywola odpowiednia metode z interfejsu ZawolaniaZwrotne
                // w tym przypadku bedzie to metoda funkcjaZwrotnaMainLogowanie
                break;
            // Jezeli nacisnieto przycisk rejestracji
            case R.id.zarejestrujBtn:
                // Pobieramne jest haslo z drugiego pola formularza - drugie haslo
                String hasloPodane2 = ((EditText) findViewById(R.id.haslo2Edit)).getText().toString();

                // Sprawdzane jest czy oba wprowadzone hasla - w pierwszym i drugim polu sa jednakowe
                if(hasloPodane1.equals(hasloPodane2)) {

                    // Przeprowadzona zostaje walidacja loginu - powinien skladac sie ze znakow
                    // alfanumerycznych
                    boolean loginPoprawny = login.matches("[A-Za-z0-9]+");
                    // Jezeli login jest poprawnie sformatowany
                    if(loginPoprawny){
                        // Tworzona jest mapa wykorzystywana do zapytania POST do serwera zdalnego
                        parametryZapytaniaPOST = new HashMap<String, String>();
                        // Ustawiane sa parametry loginu i hasla - do zapytania
                        parametryZapytaniaPOST.put("nazwa",login);
                        parametryZapytaniaPOST.put("haslo",hasloPodane1);
                        // Tworziny jest obiekt serwera umozliwiajacy przeslanie zapytania - rejestracje
                        serwer = new Serwer(MainActivity.this,REJESTRACJA_URL, parametryZapytaniaPOST, this, "mr");
                        // Zapytanie zostaje wyslane
                        serwer.execute();
                        // Jego wynik, token uzytkownika trafia do odpowiedniej - wywolywanej przez serwer
                        // metody interfejsu ZapytaniaZwrotne w tym przypadku dla trzeciego parametru serwera
                        // "mr" wolana jest przez serwer funkcja o nazwie funkcjaZwrotnaMainRejestracja

                    // Jezeli podany przez uzytkownika login nie przeszedl walidacji
                    }else{
                        // Wolana jest funkcja wyswietlajaca odpowiedni komunikat
                        pokazAlert("Podany login jest nie poprawny","Login powinien skladac sie ze znakow alfanumerycznych");
                    }
                // Jezeli dwa podane hasla w dwoch polach formularza rejestracji sa rozne wyswietlany
                // jest odpowiedni komunikat
                }else{
                    // Wolana jest funkcja wyswietlajaca odpowiedni komunikat
                    pokazAlert("Podano dwa rozne hasla","Jako haslo wprowadz dwa identyczne ciagi znakow");
                }
                break;
        }
    }

// Funkcja wyswietlajaca okienko - alert - pierwszy parametr tytul, drugi tresc
// wiadomosci ktora alert ma przekazac uzytkownikowi
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

    // Funkcje interfejsu ZawolaniaZwrotne - tzw. callbacki, wolane przez serwer w zaleznosci
    // od parametru ktory zostanie mu podany, zajmuja sie obsluga odpowiedzi serwera

    // Funkcja wolana podczas autoryzacji jako do parametry wynikZserwera trafia odpowiedz
    // serwera na zapytanie - cyfra 0 (zero) jesli nastapila odmowa autoryzacji
    // cyfra 1 (jeden) jezeli uzytkownik posiada autoryzacje
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {
        // Sprawdzane jest czy uzytkownik ma autoryzacje do korzystania z aplikacji
        if(wynikZserwera.equals("1")) {
            // Jesli uzytkownik ma autoryzacje uruchamiamy aktywnosc z glowna aplikacja
            tablicaActivity = new Intent(this, TablicaActivity.class);
            startActivity(tablicaActivity);
            // Konczone jest dzialanie aktywnosci Main
            finish();
        }else {
            // Jezeli uzytkownik nie ma autoryzacji ustawiany jest w obecnej aktywnosci
            // layout umozliwiajacy logowanie i rejestracje
            setContentView(R.layout.main_jesli_nie_zalogowany);
        }
    }

    // Funkcja wolana przez obiekt serwer w momencie zapytania o logowanie, do jej parametru
    // trafia albo skladajacy sie z 32 znakow token autoryzujacy uzytkownika, albo kod bledu
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {
        // Sprawdzane jest czy token jest poprawny, ma poprawna dlugosc
        if(wynikZserwera.length()!=32){
            // Jezeli format tokenu jest nieodpowiedni, na serwerze wystapil blad lub
            // uzytkownik nie posiada konta, o czym jest informowany
            pokazAlert("Blad","Nie posiadasz konta lub serwer napotkal blad!");
        }else {
            // Gdy format tokenu - identyfikatora uzytkownika jest poprawny ustawiany jest on preferencjach
            editor.putString(TOKEN, wynikZserwera);
            editor.apply();
            // Aktywnosc jest uruchamiana ponownie, dzieki temu ponownie sprawdzana jest autoryzacja
            // uzytkownika (wyzej opisana) i jesli ma on autoryzacje trafia do aktywnosci TablicaActivity
            recreate();
        }
    }

    // Funkcja wolana przez obiekt serwer w momencie zapytania o refestracje konta, do jej parametru
    // trafia albo skladajacy sie z 32 znakow token autoryzujacy uzytkownika - o ile konto zostalo z
    // powodzeniem zarejestrowane, albo kod bledu
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {

        // Jezeli format tokenu jest nieodpowiedni, na serwerze wystapil blad lub
        // ktos inny posiada juz wprowadzony login
        if(wynikZserwera.length()!=32){
            // Wyswietlany jest wiec odpowiedni komunikat
            pokazAlert("Blad","Ten login jest juz zajety!");
        }else {
            // Gdy format tokenu jest poprawny ustawiany jest w preferencjach
            editor.putString(TOKEN, wynikZserwera);
            editor.apply();
            // Aktywnosc jest uruchamiana ponownie, dzieki temu ponownie sprawdzana jest autoryzacja
            // uzytkownika (wyzej opisana) i jesli ma on autoryzacje trafia do aktywnosci TablicaActivity
            recreate();
        }
    }

    // Funkcje interfejsu ZawolaniaZwrotne - callbacki - wolane przez serwer
    // rodzaj wywolanej funkcji zalezy od podanego przy tworzeniu zapytania do serwera parametru

    // Funkcje wykorzystywane przez inne klasy, nie uzywane w tej aktywnosci
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {}
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {}
}