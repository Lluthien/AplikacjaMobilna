package com.example.aleksandrasalak.aplikacjamobilna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TablicaActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    static final String TOKEN = "com.example.arek.TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablica);
        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }



    public void wyloguj(View view){
        editor.putString(TOKEN, "Brak");
        editor.apply();
       // finish();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}
