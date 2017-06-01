package com.example.aleksandrasalak.aplikacjamobilna.Portfel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.example.aleksandrasalak.aplikacjamobilna.Pozostale.Serwer;
import com.example.aleksandrasalak.aplikacjamobilna.R;
import com.example.aleksandrasalak.aplikacjamobilna.ZawolaniaZwrotne;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DodawanieWpisowPortfelaActivity extends AppCompatActivity implements ZawolaniaZwrotne {
    private static final String WYSYLANIE_WPISOW_PORTFELA_URL="http://enecio.heliohost.org/dodajpozycjeportfela.php/";

    private String wybranaPrzezUzytData;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Serwer serwer;
    private HashMap<String, String> parametryZapytaniaPOST;
    private static final String TOKEN = "com.example.arek.TOKEN";
    EditText dataEdit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wez_dane_do_portfela);
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        dataEdit = (EditText)findViewById(R.id.dataInp);
       // final Button wybierzDateBtn = (Button) findViewById(R.id.dataTx);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // myCalendar.add(Calendar.DATE, 0);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dataEdit.setText(sdf.format(myCalendar.getTime()));
            }
        };

}

    public void wybierzDate(View view){
        final Calendar c = Calendar.getInstance();
        final int mRok = c.get(Calendar.YEAR);
        final int mMiesiac = c.get(Calendar.MONTH);
        final int mDzien = c.get(Calendar.DAY_OF_MONTH);

        // Uruchamia okienko wyboru daty
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int rok,
                                          int miesiacRoku, int dzienMiesiaca) {
                        // Wyswietla wybrana date w okienku

                        if (rok < mRok)
                            view.updateDate(mRok,mMiesiac,mDzien);

                        if (miesiacRoku < mMiesiac && rok == mMiesiac)
                            view.updateDate(mRok,mMiesiac,mDzien);

                        if (dzienMiesiaca < mDzien && rok == mRok && miesiacRoku == mMiesiac)
                            view.updateDate(mRok,mMiesiac,mDzien);

                        dataEdit.setText(rok + "-"+ ++miesiacRoku + "-" + dzienMiesiaca);

                    }
                }, mRok, mMiesiac, mDzien);
        dpd.getDatePicker();
        dpd.show();


    }



    String opis,data,wartosc;
    String ustawionyToken;

    public void dodaj(View view){

        ustawionyToken = sharedPref.getString(TOKEN, "Brak");
        opis = ((EditText)findViewById(R.id.opisTx)).getText().toString();
        wartosc = ((EditText)findViewById(R.id.wartoscTx)).getText().toString();
        data = dataEdit.getText().toString();

        if(!opis.equals("")) {
            if(!data.equals("")) {
                if(!wartosc.equals("")) {
                    parametryZapytaniaPOST = new HashMap<String, String>();
                    parametryZapytaniaPOST.put("kod", ustawionyToken);
                    parametryZapytaniaPOST.put("opis", opis);
                    parametryZapytaniaPOST.put("wartosc", wartosc);
                    parametryZapytaniaPOST.put("data", data);
                    serwer = new Serwer(DodawanieWpisowPortfelaActivity.this, WYSYLANIE_WPISOW_PORTFELA_URL, parametryZapytaniaPOST, DodawanieWpisowPortfelaActivity.this, "ppw2");
                    serwer.execute();
                }else{
                    Snackbar.make(findViewById(R.id.anulujBt), "Nalezy wprowadzic wartosc", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }else{
                Snackbar.make(findViewById(R.id.anulujBt), "Nalezy wybrac date", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }else{
            Snackbar.make(findViewById(R.id.anulujBt), "Nalezy dodac opis", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void anulujDodawanie(View view){
        //  Intent returnIntent = new Intent();
        //  setResult(Activity.RESULT_CANCELED, returnIntent);
        onBackPressed();
    }


    @Override
    public void funkcjaZwrotnaMainAutoryzacja(String wynikZserwera) {}
    public void funkcjaZwrotnaMainLogowanie(String wynikZserwera) {}
    public void funkcjaZwrotnaMainRejestracja(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaZnajdowanieWpisu(String wynikZserwera) {}
    public void funkcjaZwrotnaTablicaPobranieWpisow2(String wynikZserwera) {}
    public void funkcjaZwrotnaDowajWpis(String wynikZserwera) {}
    public void funkcjaZwrotnaListujWpisyPortfela(String wynikZserwera) {}
    public void funkcjaZwrotnaDodajWpisyPortfela(String wynikZserwera) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("wartosc",wartosc);
            returnIntent.putExtra("data",data);
            returnIntent.putExtra("opis",opis);

            setResult(Activity.RESULT_OK,returnIntent);
            finish();

    }
}
