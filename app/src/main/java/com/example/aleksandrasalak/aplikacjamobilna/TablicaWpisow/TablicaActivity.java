package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;

/*
 * Aktywnosc wspoldzielonej przez uzytkownikow tablicy wpisow
 * Uzytkownik moze otworzyc szczegoly wpisu klikajac na dany wpis
 * Jest mozliwosc filtrowania wpisow po slowach kluczowych
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import com.example.aleksandrasalak.aplikacjamobilna.Logowanie.MainActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Portfel.PortfelActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.AutorzyActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.SettingsActivity;
import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class TablicaActivity extends AppCompatActivity
        implements WpisyAdapter.ItemClickCallback,
        NavigationView.OnNavigationItemSelectedListener, ZawolaniaZwrotne{

    // Lacza do API serwisu podawane beda do obiektu serwer klasy Serwer z parametrami zapytania
    // kolejno lacze umozliwiajace pobranie wpisow uzytkownikow
    //static final String POBIERANIE_WPISOW_URL="http://enecio.heliohost.org/pobierzwpisy.php/";
    // i lacze umozliwiajace pobranie danego wpisu - z wykorzystaniem jego id
    //static final String ZNAJDOWANIE_WPISOW_URL="http://enecio.heliohost.org/znajdzpostpoid.php/";
    // Serwer alternatywny
    static final String POBIERANIE_WPISOW_URL="http://enecio.000webhostapp.com/pobierzwpisy.php/";
    static final String ZNAJDOWANIE_WPISOW_URL="http://enecio.000webhostapp.com/znajdzpostpoid.php/";


    // Szereg stalych - klucze ktore przydadza sie pozniej przy przesylaniu danych wpisu
    // do aktywnosci odpowiedzialnej za jego wyswietlenie
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_TYTUL = "EXTRA_TYTUL";
    private static final String EXTRA_TRESC = "EXTRA_TRESC";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_AUTOR = "EXTRA_AUTOR";
    // Stala z kluczem do tokena uzytkownika zapisanego w preferencjach
    static final String TOKEN = "com.example.arek.TOKEN";
    // Pola majace przechowywac obiekty do obslugi preferencji
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    // Adapter wykorzystany dalej, obsluguje reprezentacje listy w GUI aplikacji
    WpisyAdapter adapter;

    // Mapa do ktorej wprowadzone beda parametry zapytania
    HashMap<String, String> parametryZapytaniaPOST;
    // Pole z referencja do klasy serwer, obslugujacej zapytania do serwera
    Serwer serwer;
    // Pole do przechowywania referencji do instancji klasy ZarzadcaListy, klasa wprowadza
    // wpisy na ich programowa reprezentacje listy
    ZarzadcaListy komunikator;
    // Referencja do tworzonej i zarzadzanej przez zarzadce listy wpisow
    ArrayList<Wpis> listaWpisow;

    // Metoda wolana przy tworzeniu aktywnosci
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablica);
        // Strorzeni i pobranie obiektow obslugi prejerencji
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        // Ustawienie toolbara w aktywnosci
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Menu boczne i obsluga jego otwierania/zamykania
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Pobranie referenji do menu w prawym gornym rogu ekranu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Ustawienie oiektu obslugujacego zdarzenia tego menu na obiekt tej aktywnosci
        navigationView.setNavigationItemSelectedListener(this);
        // Tworzona jest pusta mapa parametrow zapytania do serwera
        parametryZapytaniaPOST = new HashMap<String, String>();
        // Zapytanie o liste wpisow nie wymaga parametrow, niemniej konstruktor Serwera
        // wymusza ich obecnosc - wymaga to poprawek

        // Tworzony jest obiekt serwera ze stala zawierajaca lacze do wykonania i parametrami
        // zapytania, ostatni parametr informuje serwer jakiej funkcji zwrotnej z interfejsu
        // ZawolaniaZwrotne uzyc
        serwer = new Serwer(TablicaActivity.this,POBIERANIE_WPISOW_URL, parametryZapytaniaPOST, this, "tp");
        // Wykonanie zapytania do serwera
        serwer.execute();
        // Wynikiem zapytania jest w tym przypadku wywolanie metody funkcjaZwrotnaTablicaPobranieWpisow
        // z obecnego obiektu - zajmie sie ona utworzeniem i wyswietleniem listy wpisow

        // Pobranie referencji do wiszacego nad lista, w layoucie plusika
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Przypisanie plusikowi zdarzenia onClick
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Po klikniecio przycisku tworzona jest intencja dodawania wpisu
                Intent dodawanieWpisow = new Intent(TablicaActivity.this,DodajWpisActivity.class);
                // Ustawiany jest request code który umozliwi identyfikacje
                // ewentualnych nadchodzacych danych zwroconych z wywolanej aktywnosci
                int requestCode = 1;
                // Startuje aktywnosc umozliwiajaca dodanie wpisu, rezultat jej dzialania
                // trafia do funkcji onActivityResult
                startActivityForResult(dodawanieWpisow,requestCode);
            }
        });
    }

    // Funkcja wolana podczas gdy inna aktywnosc zwraca rezultat
    // do obecnej aktywnosc
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Jezeli kod zapytania jest rowny 1 oznacza to, ze dane pochodza z aktywnosci
        // tworzacej nowy wpis do tablicy
        if (requestCode == 1) {
            // Jezeli rezutltat zostal poprawnie zwrocony, uzytkownik sie nie wycofal, brak bledow
            if(resultCode == Activity.RESULT_OK){
                // Pobierane jest z intencji w ktora zostala podana przez aktywnosc zwracajaca dane
                // pole o nazwie podanej do funkcji getStringExtra, tutaj rezultatem jest zwyczajnie
                // id nowego wpisu
                String idNowegoWpisu=data.getStringExtra("result");
                // Majac id tworzona jest mapa zawierajaca klucz o nazwie id z wartoscia
                // zwracona z rzeczonej aktywnosci tj. id owego wpisu, celem jest pobranie tresci tego
                // wpisu z serwera i dodanie na liste
                parametryZapytaniaPOST = new HashMap<String, String>();
                parametryZapytaniaPOST.put("id",idNowegoWpisu);
                // Tworzone jest zapytanie do serwera o wpis o zwroconym id
                serwer = new Serwer(TablicaActivity.this,ZNAJDOWANIE_WPISOW_URL, parametryZapytaniaPOST,this,"tz");
                serwer.execute();
                // Rezultatem wykonania zapytania do serwera jest wywolanie funkcji callback o nazwie
                // funkcjaZwrotnaTablicaZnajdowanieWpisu, o tym ktora funkcja callback zostanie wywolana
                // ostatni parametr podany do serwera - tutaj jest to tz
            }

            // Jezeli uzytkownik zrezygnowal z dodawania wpisu
            if (resultCode == Activity.RESULT_CANCELED) {
                // Komunikat o wycofaniu sie z dodawania lub o bledzie
                Snackbar.make(findViewById(R.id.fab), "Anulowano lub brak autoryzacji", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        // Gdy kod zapytania jest rowny dwa oznacza to, ze aktywnosc zostala wybrana w menu
        } else if(requestCode==2){
            // To jest zwykle jawne pokazanie, ze tak sie dzieje, w przyszlosci
            // mozna rozbudowac ten element o dodatkowe funkcje
        }
    }

    // Metoda wywolywana przy tworzeniu menu w prawym gornym rogu ekranu
    public boolean onCreateOptionsMenu(Menu menu) {
        //Metoda dodaje do menu opcje znajdujace sie w pliku menu_wpisy, m.in wyloguj,filtruj,settings
        getMenuInflater().inflate(R.menu.menu_wpisy, menu);
        return true;
    }

    // Metoda wolana w momencie klikniecia na element menu w prawym gornym menu ekranu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pobierane jest id kliknietego elementu
        int id = item.getItemId();
        // Jezeli Id odpowiada elementowi z pliku menu_wpisy.xml o nazwie action_settings
        if (id == R.id.action_settings) {
            // Tworzona jest intencja z referencja do aktywnosci ustawien
            Intent settings = new Intent(this, SettingsActivity.class);
            // Startowana jest aktywnosc ustawien, z kodem requestCode 15
            // startowanie jej w celu uzyskania rezultatu sprawia, iz obecna aktywnosc
            // co zauwazylem mniej sklonna jest do zatrzymania sie
            // mozna tez ewentualnie z menu ustawien zwrocic pewne parametry dla tej aktywnosci
            startActivityForResult(settings,15);
            return true;
        }

        // Jezeli kliknieta zostala opcja wylogowania
        if (id == R.id.wylogujOption){
            // Preferencja z tokenem uzytkownika jest czyszcona
            // Powoduje to zakonczenie sesji i koniecznosc ponownego logowania
            editor.putString(TOKEN, "Brak");
            editor.apply();
            // Startowana jest ponownie aktywnosc startowa
            startActivity(new Intent(this,MainActivity.class));
            // Dzialanie obecnej aktywnosci jest konczone
            finish();
        }

        // Jezeli kliknieto przycisk szukaj
        if (id == R.id.szukajOption){
            // Referencja przycisku szukaj jest przypisywana do stalej
            // Konieczne jest to, gdyz wartosc tytul modyfikowana bedzie
            // w innymobiekcie - OnClickListener, mozliwe jest to jedynie
            // z wykozystaniem stalej
            final MenuItem szukajItem = item;
            // Sprawdzane jest czy przycisk ma tytul "Filtruj"
            if(item.getTitle().toString().equals("Filtruj")) {
                // Twoorzone jest okienko z opcja wpisania szukanej frazy
                AlertDialog.Builder alert = new AlertDialog.Builder(TablicaActivity.this);
                alert.setTitle("Wyszukiwanie na liscie");
                alert.setMessage("Wprowadz szukana fraze/nazwe tagu/autora");
                final EditText input = new EditText(TablicaActivity.this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Po zatwierdzeniu przyciskiem ok w okienku
                        // wolana jest metoda odfiltruj z obektu komunikator klasy ZarzadcaListy
                        // modyfikuje ona odpowiednio zawartosc listy - kontenera - ArrayList
                        // o nazwie listaWpisow
                        komunikator.odfiltruj(input.getText().toString());
                        // O zmianie zawartosci listy informowany jest adapter
                        adapter.notifyDataSetChanged();
                        // Zmianie ulega tytul przycisku
                        szukajItem.setTitle("Cofnij filtracje");
                        // Wyswietlany jest komunikat o przeprowadzeniu filtrowania
                        Snackbar.make(findViewById(R.id.fab), "Powyzej wyniki", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                // Jezeli anulowano filtrowanie
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Wyswietlany jest odpowiedni komunikat
                        Snackbar.make(findViewById(R.id.fab), "Anulowano wyszukiwanie", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                alert.show();
            // Jezeli tytul przycisku to nie jest 'Filtruj', wide nazywa sie on 'Cofnij filtracje'
            }else{
                // Tworzona jest lista parametrow zapytania do serwera
                parametryZapytaniaPOST = new HashMap<String, String>();
                // Zapytanie o liste wpisow nie wymaga parametrow, niemniej konstruktor Serwera
                // wymusza ich obecnosc - wymaga to poprawek

                // Tworzony jest obiekt serwera ze stala zawierajaca lacze do pobrania i parametrami
                // zapytania
                serwer = new Serwer(TablicaActivity.this,POBIERANIE_WPISOW_URL, parametryZapytaniaPOST,this,"tp2");
                // Zmieniany jest tytul przycisku od filtrowania
                szukajItem.setTitle("Filtruj");
                // Wykonywane jest zapytanie do serwera
                serwer.execute();
                // Odpowiedz w funkcjach zwrotnych - na zapytanie tp2
                // wolana jest funkcja - funkcjaZwrotnaTablicaPobranieWpisow
            }
        }
        // Wolana jest funkcja klasy ktora rozszerza klasa TablicaActivity
        return super.onOptionsItemSelected(item);
    }

    // Przy kliknieciu danego wpisu na liscie wpisow w programie
    public void onItemClick(int indexWpisuNaLiscie) {
        // Pobierana jest referencja do kliknietego wpisu
        Wpis wpis = (Wpis) listaWpisow.get(indexWpisuNaLiscie);
        // Tworzona jest nowa intencja dzieki ktorej wystartowana bedzie aktywnosc z trescia wpisu
        Intent in = new Intent(this,TrescWpisuActivity.class);
        // Tworzony jest obiekt typu bundle, ktory umozliwy przeslanie danych do rzeczonej aktywnosci
        Bundle extras = new Bundle();
        // Do obiektu typu Bundle przekazywane sa kolejno temat, tresc, nazwa autora i data utworzenia wpisu
        extras.putString(EXTRA_TYTUL,wpis.pobierzTemat());
        extras.putString(EXTRA_TRESC,wpis.pobierzTresc());
        extras.putString(EXTRA_AUTOR,wpis.pobierzAutora());
        extras.putString(EXTRA_DATA,wpis.pobierzDate());
        // Obiekt nastepnie umieszczany jest w intencji
        in.putExtra(BUNDLE_EXTRAS, extras);

        RecyclerView rvListaWpisowGUI = (RecyclerView) findViewById(R.id.rvContacts);

        WpisyAdapter.ViewHolder viewHolder = (WpisyAdapter.ViewHolder) rvListaWpisowGUI.findViewHolderForAdapterPosition(indexWpisuNaLiscie);


        Pair<View, String> titlePair = Pair.create(viewHolder.pobierzTematView(), "tematTrans");
        Pair<View, String> datePair = Pair.create(viewHolder.pobierzDataView(), "dataTrans");
        Pair<View, String> bodyPair = Pair.create(viewHolder.pobierzTrescView(), "trescTrans");
        Pair<View, String> autorPair = Pair.create(viewHolder.pobierzAutorView(), "autorTrans");
        viewHolder.pobierzAutorView();

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,titlePair,datePair,bodyPair,autorPair);
        ActivityCompat.startActivity(this, in, options.toBundle());

        // Z wykorzystaniem intencji startowana jest nowa aktywnosc
       // startActivity(in);
    }

    // W momencie nacisniecia w urzadzeniu przycisku wstecz
    public void onBackPressed() {
        // Pobierana jest referencja do layoutu menu bocznego - hamburgera
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Jezeli menu jest rozwiniete
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Menu jest zwijane
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Jezeli nie jest rozwiniete wolana jest standardowa obsluga nacisniecia przycisku
            super.onBackPressed();
        }
    }

    // W momencie klikniecia opcji w menu bocznym - hamburgerze
    public boolean onNavigationItemSelected(MenuItem item) {
        // Pobierane jest id kliknietej opcji
        int id = item.getItemId();
        // Jezeli kliknieto opcje tablicy
        if (id == R.id.nav_tablica) {
            // Nic nie robi - przycisk odpowiadajacy za wywolanie obecnej aktwnosci

        // W przeciwnym wypadku gdy kliknieto przycisk portfela
        } else if (id == R.id.nav_portfel) {
            // Parametr wywolania ustawiany jest na podana wartosc
            int requestCode = 2;
            // Tworzona jest intencja ktora posluzy do wywolania aktywnosci portfela
            Intent portfelIntent = new Intent(TablicaActivity.this,PortfelActivity.class);
            // Startowana jest aktywnosc portfela
            // start w celu uzyskania rezultatu wynika z przyjetego sposobu zarzadzania aktywnociami
            // wzane by android nie zamknal omawianej aktywnosci
            startActivityForResult(portfelIntent,requestCode);
        // Gdy kliknieto przycisk o nazwie autorzy
        } else if (id == R.id.autorzy) {
            // Parametr wywolania ustawiany jest na podana wartosc
            int requestCode = 2;
            // Tworzona jest intencja ktora posluzy do wywolania aktywnosci z informacjami o autorach
            Intent portfelIntent = new Intent(TablicaActivity.this,AutorzyActivity.class);
            // Startowana jest aktywnosc z informacjami o autorach
            startActivityForResult(portfelIntent,requestCode);
        }
        // Pobierana jest referencja do menu opcji i jest ono chowane
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Funkcje interfejsu ZawolaniaZwrotne - callbacki - wolane przez serwer
    // rodzaj wywolanej funkcji zalezy od podanego do serwera parametru

    // Funkcje wykorzystywane przez inne klasy, nie uzywane w tej aktywnosci
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {}
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {}

    // Funckcja zwrotna wolana w tejze aktywnosci celu obslugi dzialan po pobraniu calej
    // listy z serwera zdalnego, obiekt serwer przekazuje do niej pobrany w odpowiedzi na
    // zapytanie kod - cala lista wpisow
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {
        // Pobranie referencji do elementu w layoucie odpowiedzialnego za
        // wyswietlanie listy typu RecyclerView
        RecyclerView rvListaWpisowGUI = (RecyclerView) findViewById(R.id.rvContacts);
        // Utworzenie obiekty typu zarzadca listy - połączenie M i C jesli bylby to
        // wzorzec MVC
        komunikator = new ZarzadcaListy();
        // Utworzenie z wykorzystaniem wspomnianego wyzej obiekty listy wpisow na podstawie kodu
        listaWpisow = komunikator.stworzListeWpisow(wynikZserwera);
        // Tworzymy adapter z uzupelniona lista wpisow, przygotowana przez obiekt typu ZarzadcaListy
        // Adapter konieczny jest liscie tworzonej z wykorzystaniem RecyclerView, wiecej na ten temat
        // w kodzie klasy WpisyAdapter
        adapter = new WpisyAdapter(this, listaWpisow);
        // Przypisywany jest obiekt zajmujacy sie obsluga klikniecia elementu na liscie
        // w tym przypadku jest to obecny obiekt
        adapter.setItemClickCallback(this);
        // Ustawiamy adapter dla elementu typu RecyclerView - reprezentacji listy w aplikacji
        rvListaWpisowGUI.setAdapter(adapter);
        // Ustawiany jest manager - odpowiada on za rozklad elementow w interfejsie
        // ewentualnosc to GridLayoutManager i StaggeredGridLayoutManager
        rvListaWpisowGUI.setLayoutManager(new LinearLayoutManager(this));
        // Tworzony jest prosty obiekt odpowiedzialny za obsluge animacji listy
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        // Animator jest ustawiany dla listy
        rvListaWpisowGUI.setItemAnimator(animator);

    }

    // Funkcja zwrotna - callbacs wolana przez serwer z trescia pojedynczego wpisu, pobranego na
    // podstawie id
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {

        // Wywolywana jest funkcja ZarzadcyListy dodajNaListe, przyjmuje ona xml zawierajacy pojedynczy
        // wpis pobrany z serwera, jesli element zostanie dodany na liste z powodzeniem zwraca
        // prawde w przeciwnym wypadku falsz
        boolean czyDodano = komunikator.dodajNaListe(wynikZserwera);
        // Jezeli dodano wpis na liste
        if(czyDodano){
            // W celu odswiezenia listy w GUI powiadamiany jest o tym adapter listy
            // korzysta z niego RecyclerView
            adapter.notifyItemInserted(0);
            // Wyswietlany jest odpowiedni komunikat
            Snackbar.make(findViewById(R.id.fab), "Post zostal dodany", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        // Jezeli nie udalo sie dodac elementu na liste
        }else{
            // Wyswietlany jest odpowiedni komunikat
            Snackbar.make(findViewById(R.id.fab), "Problem z polaczeniem internetowym!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    // Funkcja callback wolana przez serwer obsluguje odsiezenie listy po cofnieciu filtrowania
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {
        // Programowa reprezentacja listy jest ladowana ponownie
        komunikator.stworzListeWpisow(wynikZserwera);
        // O zmienie zawartosci listy powiadamiany jest adapter eleMentu RecyclerViev - reprezentacji
        // listy w GUI
        adapter.notifyDataSetChanged();
        // Wyswietlany jest komunikat o ponownym wczytaniu calej listy postow
        Snackbar.make(findViewById(R.id.fab), "Posty zostaly wczytane ponownie", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}