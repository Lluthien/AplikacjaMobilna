package com.example.aleksandrasalak.aplikacjamobilna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class TablicaActivity extends AppCompatActivity implements WpisyAdapter.ItemClickCallback{
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_TYTUL = "EXTRA_TYTUL";
    private static final String EXTRA_TRESC = "EXTRA_TRESC";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_AUTOR = "EXTRA_AUTOR";

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

        parametryZapytaniaPOST = new HashMap<String, String>();

        serwer = new Serwer(TablicaActivity.this,POBIERANIE_WPISOW_URL, parametryZapytaniaPOST);

        String wynik="pusty";
        try {
            wynik = serwer.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        // Uzupelniamy liste wpisow listaWpisow

        komunikator = new ZarzadcaListy();
        listaWpisow = komunikator.stworzListeWpisow(wynik);

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

        // Pobranie referencji do wiszacego nad lista plusika
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Przypisanie plusikowi zdarzenia onClick
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dodawanieWpisow = new Intent(TablicaActivity.this,DodajWpisActivity.class);
                int requestCode = 1;

                startActivityForResult(dodawanieWpisow,requestCode);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
                serwer = new Serwer(TablicaActivity.this,ZNAJDOWANIE_WPISOW_URL, parametryZapytaniaPOST);

                String nowyWpis="";
                try {
                    nowyWpis = serwer.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                boolean czyDodano = komunikator.dodajNaListe(nowyWpis);

                if(czyDodano){
                    adapter.notifyItemInserted(0);
                    Snackbar.make(findViewById(R.id.fab), "Post zostal dodany", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(findViewById(R.id.fab), "Problem z polaczeniem internetowym!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                //Komunikat o dodaniu wpisu

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Komunikat o wycofaniu sie z dodawania lub o bledzie
                Snackbar.make(findViewById(R.id.fab), "Anulowano lub brak autoryzacji", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }//onActivityResult

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
}
