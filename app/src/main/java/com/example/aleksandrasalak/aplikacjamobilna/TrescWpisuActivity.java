package com.example.aleksandrasalak.aplikacjamobilna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TrescWpisuActivity extends AppCompatActivity {
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String EXTRA_TYTUL = "EXTRA_TYTUL";
    private static final String EXTRA_TRESC = "EXTRA_TRESC";
    private static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String EXTRA_AUTOR = "EXTRA_AUTOR";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tresc_wpisu);

        Bundle extras = getIntent().getBundleExtra(BUNDLE_EXTRAS);
        ((TextView)findViewById(R.id.temat1)).setText(extras.getString(EXTRA_TYTUL));
        ((TextView)findViewById(R.id.autor1)).setText(extras.getString(EXTRA_AUTOR));
        ((TextView)findViewById(R.id.data1)).setText(extras.getString(EXTRA_DATA));
        ((TextView)findViewById(R.id.tresc1)).setText(extras.getString(EXTRA_TRESC));

    }
}
