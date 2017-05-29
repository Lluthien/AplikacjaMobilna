package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.example.aleksandrasalak.aplikacjamobilna.Logowanie.MainActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.AutorzyActivity;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.SettingsActivity;
import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.TablicaWpisow.ZarzadcaListy;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class PortfelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ZawolaniaZwrotne {

    HashMap<String, String> parametryZapytaniaPOST;
    Serwer serwer;
    ZarzadcaPortfela komunikatorPortfela;
    ArrayList<WpisPortfela> listaWpisowPortfela;


    static final String TOKEN = "com.example.arek.TOKEN";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    PortfelAdapter adapterPortfela;

    static final String POBIERANIE_WPISOW_PORTFELA_URL="http://enecio.heliohost.org/pobierzportfel.php/";
    static final String WYSYLANIE_WPISOW_PORTFELA_URL="http://enecio.heliohost.org/dodajpozycjeportfela.php/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        komunikatorPortfela = new ZarzadcaPortfela();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfel);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String ustawionyToken = sharedPref.getString(TOKEN, "o");

        if(!ustawionyToken.equals("o")) {
            parametryZapytaniaPOST = new HashMap<String, String>();
            parametryZapytaniaPOST.put("idUzytkownik", ustawionyToken);
            serwer = new Serwer(PortfelActivity.this, POBIERANIE_WPISOW_PORTFELA_URL, parametryZapytaniaPOST, this, "ppw");
            serwer.execute();
        }

        // Pobranie referencji do wiszacego nad lista plusika
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);

        // Przypisanie plusikowi zdarzenia onClick
        fab.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                Intent dodawanieWpisow = new Intent(PortfelActivity.this,WezDaneDoPortfela.class);
                int requestCode = 9;
                startActivityForResult(dodawanieWpisow,requestCode);
            }
        });






    }




    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tablica) {
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

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Komunikat o wycofaniu sie z dodawania lub o bledzie
                Snackbar.make(findViewById(R.id.fab), "Anulowano lub brak autoryzacji", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }


    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
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

    @Override
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

    }
}
