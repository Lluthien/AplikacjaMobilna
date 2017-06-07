package com.example.aleksandrasalak.aplikacjamobilna.Pozostale;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;

        import com.example.aleksandrasalak.aplikacjamobilna.Logowanie.MainActivity;
        import com.example.aleksandrasalak.aplikacjamobilna.Portfel.PortfelActivity;
        import com.example.aleksandrasalak.aplikacjamobilna.R;

public class AutorzyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String TOKEN = "com.example.arek.TOKEN";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorzy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view3);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPref = getSharedPreferences("DANE", Context.MODE_PRIVATE);
        editor = sharedPref.edit();




    }
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
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
            int requestCode = 4;
            Intent portfelIntent = new Intent(this,PortfelActivity.class);
            startActivityForResult(portfelIntent,requestCode);
        } else if (id == R.id.autorzy) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
            drawer.closeDrawer(GravityCompat.START);
        }



        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==4){
            finish();
        }

    }

}
