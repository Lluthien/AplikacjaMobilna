package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

/*
 * Aktywnosc portfela zawiera liste wprowadzonych przez uzytkownika wydatkow
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.example.aleksandrasalak.aplikacjamobilna.Logowanie.MainActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.AutorzyActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.SettingsActivity;
import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;
import java.util.ArrayList;
import java.util.HashMap;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class PortfelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ZawolaniaZwrotne {

    // Pole przechowujace referencje do mapy parametrow zapytania
    private HashMap<String, String> parametryZapytaniaPOST;
    // Pole przechowujace referencje do obiektu odpowiedzialnego za komunikacje z serwerem
    private Serwer serwer;
    // Pole odpowiedzialne za przechowywanie referencji do obiektu odpowiedzialnego za zapis,
    // modyfikacje listy wpisow portfela
    private ZarzadcaPortfela komunikatorPortfela;
    // Pole przechowujace referencje do listy wpisow portfela
    private ArrayList<WpisPortfela> listaWpisowPortfela;
    // Stala przechowujaca sciezke do identyfikatora uzytkownika
    private static final String TOKEN = "com.example.arek.TOKEN";
    // Pola przechowujace referencje do obiektow umozliwiajacych zarzadzanie preferencjami
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    // Pole przechowujace adapter reprezentacji listy wpisow portfela w GUI - RecyclerView
    private PortfelAdapter adapterPortfela;
    // Stala przechowujaca adres do serwera konieczny do tworzenia zapytan do pobrania wpisow portfela
    private static final String POBIERANIE_WPISOW_PORTFELA_URL="http://enecio.heliohost.org/pobierzportfel.php/";
    // Pole ktore bedzie przechowywac referencje do tej aktywnosci
    private PortfelActivity taAktywnosc;

    // Podczas tworzenia aktywnosci
    protected void onCreate(Bundle savedInstanceState) {
        // Tworzony jest obiekt zarzadzajacy lista wpisow portfela, posiada on m.in. mozliwosc parsowania
        // XML do programowej reprezentacji wpisu portfela a wiec obiektu klasy WpisPortfela
        // Dodaje poszczegolne obiekty typu WpisPortfela do konkenera je przechowujacego - listaWpisowPortfela
        komunikatorPortfela = new ZarzadcaPortfela();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfel);

        // Przypisanie referencji obecnej aktywnosci polu, potrzebne do wystartowania aktywnosci
        // pobierajacej dane do wpisu portfela
        taAktywnosc=this;
        // Toolbar pobranie referencji do toolbara z GUI i ustawnienie go jako obslugiwanego
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        // Pobranie referencji do menu bocznego i ustawienie jego podstawowej obslugi
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Pobranie referencji do menu w prawym gornym rogu ekranu (navigation view)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        // Ustawienie action listenera, obecna aktywnosc zajmie sie obsluga zdarzen
        navigationView.setNavigationItemSelectedListener(this);
        // Pobranie referencji do preferencji elementow oblugujacych preferencje
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Pobranie ustawionego tokena - identyfikatora uzytkownika z preferencji
        String ustawionyToken = sharedPref.getString(TOKEN, "Brak");
        // Jezeli w istocie identyfikator w preferencjach sie znajduje
        if(!ustawionyToken.equals("Brak")) {
            // Ustawiane sa parametry zapytania z owym identyfikatorem
            parametryZapytaniaPOST = new HashMap<String, String>();
            parametryZapytaniaPOST.put("idUzytkownik", ustawionyToken);
            // Tworzone jest i uruchamiane zapytanie do serwera o wpisy portfela
            serwer = new Serwer(PortfelActivity.this, POBIERANIE_WPISOW_PORTFELA_URL, parametryZapytaniaPOST, this, "ppw");
            serwer.execute();
            // W odpowiedzi serwer wywola odpowiednia metode z interfejsu ZawolaniaZwrotne
            // w tym przypadku bedzie to metoda funkcjaZwrotnaListujWpisyPortfela
        }

        // Pobranie referencji do wiszacego nad lista plusika
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        // Przypisanie plusikowi zdarzenia onClick
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Klikniecie przycisku utworzy intencje aktywnosci odpowiedzialnej za pobranie
                // danych do utworzenia wpisu portfela
                Intent dodawanieWpisow = new Intent(taAktywnosc,DodawanieWpisowPortfelaActivity.class);
                // Ustawiany jest kod zaptania umozliwiajacy identyfikacje danych ktore
                // aktywnosc zwroci
                int requestCode = 9;
                // W celu pobrania danych startowana jest aktywnosc
                startActivityForResult(dodawanieWpisow,requestCode);
            }
        });
    }

    // W momencie nacisniecia w urzadzeniu przycisku wstecz
    public void onBackPressed() {
        // Pobierana jest referencja do layoutu menu bocznego - hamburgera
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        // Jezeli menu jest rozwiniete
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Menu jest zwijane
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Jezeli nie jest rozwiniete wolana jest standardowa obsluga nacisniecia przycisku
            super.onBackPressed();
        }
    }

    // Metoda wywolywana przy tworzeniu menu w prawym gornym rogu ekranu
    public boolean onCreateOptionsMenu(Menu menu) {
        //Metoda dodaje do menu opcje znajdujace sie w pliku menu_wpisy, m.in wyloguj,settings
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Metoda wolana w momencie klikniecia na element menu w prawym gornym menu ekranu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pobierane jest id kliknietego elementu
        int id = item.getItemId();

        // Jezeli Id odpowiada elementowi z pliku menu_wpisy.xml o nazwie action_settings
        if (id == R.id.action_settings) {
            // tworzona jest intencja ustawien
            Intent settings = new Intent(this, SettingsActivity.class);
            // Uruchamiana jest aktywnosc ustawien (w celu uzyskania rezultatu)
            startActivityForResult(settings,15);
            return true;
        }

        // Jezeli kliknieta zostala opcja wylogowania
        if (id == R.id.wylogujOption){
            // Preferencja z tokenem uzytkownika jest czyszcona
            // Powoduje to zakonczenie sesji i koniecznosc ponownego logowania
            editor.putString(TOKEN, "Brak");
            editor.apply();
            // Startowana jest ponownie aktywnosc startowa (AcivityMain)
            startActivity(new Intent(this,MainActivity.class));
            // Dzialanie obecnej aktywnosci jest konczone
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // W momencie klikniecia opcji w menu bocznym - hamburgerze
    public boolean onNavigationItemSelected(MenuItem item) {
        // Pobierane jest id kliknietej opcji
        int id = item.getItemId();
        // Jezeli kliknieto opcje tablicy
        if (id == R.id.nav_tablica) {
            //
            finish();
        } else if (id == R.id.nav_portfel) {
            //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
           // drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.autorzy) {
            int requestCode = 3;
            Intent autorzyIntent = new Intent(PortfelActivity.this,AutorzyActivity.class);
            startActivityForResult(autorzyIntent,requestCode);
        }



        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent dataP) {
        if(requestCode==3){
            finish();
        }

        if (requestCode == 9) {
            if(resultCode == Activity.RESULT_OK){

                String data=dataP.getStringExtra("data");
                String opis=dataP.getStringExtra("opis");
                String wartosc=dataP.getStringExtra("wartosc");


                komunikatorPortfela.dodajDoPortfela(data,opis,wartosc,"xS");
                adapterPortfela.notifyItemInserted(0);
                Snackbar.make(findViewById(R.id.fab1), "Dodano wpis portfela", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Komunikat o wycofaniu sie z dodawania lub o bledzie
                Snackbar.make(findViewById(R.id.fab1), "Anulowano lub brak autoryzacji", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }


    // Funkcje interfejsu ZawolaniaZwrotne - callbacki - wolane przez serwer
    // rodzaj wywolanej funkcji zalezy od podanego przy tworzeniu zapytania do serwera parametru

    // Funkcje wykorzystywane przez inne klasy, nie uzywane w tej aktywnosci
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {}

    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera){

        RecyclerView rvContacts1 = (RecyclerView) findViewById(R.id.rvContacts1);
        // Uzupelniamy liste wpisow listaWpisowPortfela

//        komunikatorPortfela = new ZarzadcaPortfela();
        listaWpisowPortfela = komunikatorPortfela.stworzPortfel(wynikZserwera);

        // Tworzymy adapter z uzupelniona lista wpisow portfela
        adapterPortfela = new PortfelAdapter(this, listaWpisowPortfela);

        // Ustawiamy adapter w elemencie RecyclerView
        rvContacts1.setAdapter(adapterPortfela);
        // Ustawiamy jaki manager chcemy używać
        // ewentualnosc to GridLayoutManager i StaggeredGridLayoutManager
        rvContacts1.setLayoutManager(new LinearLayoutManager(this));
        // Ustawiamy jakis element do animowania listy
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        rvContacts1.setItemAnimator(animator);

    }

}
