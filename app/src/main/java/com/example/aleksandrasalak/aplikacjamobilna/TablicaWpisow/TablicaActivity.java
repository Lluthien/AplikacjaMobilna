package com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_TYTUL = "EXTRA_TYTUL";
    private static final String EXTRA_TRESC = "EXTRA_TRESC";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_AUTOR = "EXTRA_AUTOR";
    MenuItem opcjaEdycjiWmenu;
    static final String TOKEN = "com.example.arek.TOKEN";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    WpisyAdapter adapter;

    static final String POBIERANIE_WPISOW_URL="http://enecio.heliohost.org/pobierzwpisy.php/";
    static final String ZNAJDOWANIE_WPISOW_URL="http://enecio.heliohost.org/znajdzpostpoid.php/";

    HashMap<String, String> parametryZapytaniaPOST;
    Serwer serwer;
    ZarzadcaListy komunikator;
    ArrayList<Wpis> listaWpisow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_tablica);


        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Ustawienie toolbara
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Menu boczne
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        parametryZapytaniaPOST = new HashMap<String, String>();

        serwer = new Serwer(TablicaActivity.this,POBIERANIE_WPISOW_URL, parametryZapytaniaPOST, this, "tp");


        serwer.execute();



        // Pobranie referencji do wiszacego nad lista plusika
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Przypisanie plusikowi zdarzenia onClick
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dodawanieWpisow = new Intent(TablicaActivity.this,DodajWpisActivity.class);
                int requestCode = 1;
                startActivityForResult(dodawanieWpisow,requestCode);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                String idNowegoWpisu=data.getStringExtra("result");

                // String result=data.getStringExtra("result");
                // ZAPYTANIE O NOWY WPIS
                parametryZapytaniaPOST = new HashMap<String, String>();
                parametryZapytaniaPOST.put("id",idNowegoWpisu);
                serwer = new Serwer(TablicaActivity.this,ZNAJDOWANIE_WPISOW_URL, parametryZapytaniaPOST,this,"tz");

                serwer.execute();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Komunikat o wycofaniu sie z dodawania lub o bledzie
                Snackbar.make(findViewById(R.id.fab), "Anulowano lub brak autoryzacji", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if(requestCode==2){

        }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wpisy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivityForResult(settings,15);

            return true;
        }

        if (id == R.id.wylogujOption){
            editor.putString(TOKEN, "Brak");
            editor.apply();
            // finish();
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        if (id == R.id.szukajOption){
            final MenuItem s = item;

            if(item.getTitle().toString().equals("Filtruj")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TablicaActivity.this);
                alert.setTitle("Wyszukiwanie na liscie");
                alert.setMessage("Wprowadz szukana fraze/nazwe tagu");
                final EditText input = new EditText(TablicaActivity.this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        komunikator.odfiltruj(input.getText().toString());
                        adapter.notifyDataSetChanged();
                        s.setTitle("Cofnij filtracje");
                        Snackbar.make(findViewById(R.id.fab), "Powyzej wyniki", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Snackbar.make(findViewById(R.id.fab), "Anulowano wyszukiwanie", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
                alert.show();
            }else{
                parametryZapytaniaPOST = new HashMap<String, String>();

                serwer = new Serwer(TablicaActivity.this,POBIERANIE_WPISOW_URL, parametryZapytaniaPOST,this,"tp2");
                s.setTitle("Filtruj");
                serwer.execute();


               // odpowiedz w funkcjach zwrotnych


            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int p) {
        Wpis wpis = (Wpis) listaWpisow.get(p);
        Intent in = new Intent(this,TrescWpisuActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EXTRA_TYTUL,wpis.pobierzTemat());
        extras.putString(EXTRA_TRESC,wpis.pobierzTresc());
        extras.putString(EXTRA_AUTOR,wpis.pobierzAutora());
        extras.putString(EXTRA_DATA,wpis.pobierzDate());
        in.putExtra(BUNDLE_EXTRAS, extras);
        startActivity(in);
    }




    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();




        if (id == R.id.nav_tablica) {
            // Nic nie robi - przycisk tej aktwnosci
        } else if (id == R.id.nav_portfel) {
            int requestCode = 2;
            Intent portfelIntent = new Intent(TablicaActivity.this,PortfelActivity.class);
            startActivityForResult(portfelIntent,requestCode);
        } else if (id == R.id.autorzy) {
            int requestCode = 2;
            Intent portfelIntent = new Intent(TablicaActivity.this,PortfelActivity.class);
            startActivityForResult(portfelIntent,requestCode);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Zbedne
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}

    @Override
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        // Uzupelniamy liste wpisow listaWpisow

        komunikator = new ZarzadcaListy();
        listaWpisow = komunikator.stworzListeWpisow(wynikZserwera);

        // Tworzymy adapter z uzupelniona lista wpisow
        adapter = new WpisyAdapter(this, listaWpisow);

        // Ustawiamy adapter w elemencie RecyclerView
        rvContacts.setAdapter(adapter);
        // Ustawiamy jaki manager chcemy używać
        // ewentualnosc to GridLayoutManager i StaggeredGridLayoutManager
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // Ustawiamy jakis element do animowania listy
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        rvContacts.setItemAnimator(animator);
        adapter.setItemClickCallback(this);

    }
    @Override
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {
        boolean czyDodano = komunikator.dodajNaListe(wynikZserwera);

        if(czyDodano){
            adapter.notifyItemInserted(0);
            Snackbar.make(findViewById(R.id.fab), "Post zostal dodany", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            Snackbar.make(findViewById(R.id.fab), "Problem z polaczeniem internetowym!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {
        komunikator.stworzListeWpisow(wynikZserwera);

        adapter.notifyDataSetChanged();
       // opcjaEdycjiWmenu.setTitle("Filtruj");


        Snackbar.make(findViewById(R.id.fab), "Posty zostaly wczytane ponownie", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {

    }

    @Override
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

    }
}