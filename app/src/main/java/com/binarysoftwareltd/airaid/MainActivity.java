package com.binarysoftwareltd.airaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Locale locale;
    String appLang;
    MainFragment mf;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            setBundle();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mf,"MainFragment").commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uManual:
                Toast.makeText(getApplicationContext(), "User Manual clicked!", Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.item2:
//                Toast.makeText(getApplicationContext(), "Item 2 selected", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.item3:
//                Toast.makeText(getApplicationContext(), "Item 3 selected", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.subItem1:
//                Toast.makeText(getApplicationContext(), "Sub Item 1 selected", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.subItem2:
//                Toast.makeText(getApplicationContext(), "Sub Item 2 selected", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void languageDialog() {
        //array of languages
        final String[] listItems = {"বাংলা", "English"};
        final AlertDialog.Builder alb = new AlertDialog.Builder(MainActivity.this);
        alb.setTitle(R.string.language_alertdialog_title);
        alb.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    //bengali
                    if (appLang.equals("bn")) {

                    } else {
                        setLocale("bn");
                        refresh();
                    }
                }
                if (i == 1) {
                    //english
                    if (appLang.equals("en")) {

                    } else {
                        setLocale("en");
                        refresh();
                    }
                }
                dialog.dismiss();
            }
        });
        AlertDialog ald = alb.create();
        ald.show();
    }

    private void setLocale(String lang) {
        appLang = lang;
        locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    private void refresh() {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    // load language saved in shared preference
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_lang", "");
        setLocale(language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                setBundle();
                Fragment fm = getSupportFragmentManager().findFragmentByTag("MainFragment");
                if (fm != null && fm.isVisible()) {
                    navigationView.setCheckedItem(R.id.nav_main);
                    break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mf, "MainFragment").commit();
                navigationView.setCheckedItem(R.id.nav_main);
                break;
            case R.id.nav_language:
                languageDialog();
                break;
            case R.id.nav_complain_box:
                Toast.makeText(getApplicationContext(), "Complain Box Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about_us:
                Fragment fau = getSupportFragmentManager().findFragmentByTag("AboutUsFragment");
                if (fau != null && fau.isVisible()) {
                    break;
                }
                AboutUsFragment abf = new AboutUsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, abf,"AboutUsFragment").commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("language", appLang);
        mf = new MainFragment();
        mf.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {
        Fragment fau = getSupportFragmentManager().findFragmentByTag("AboutUsFragment");
        if (fau != null && fau.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mf, "MainFragment").commit();
            navigationView.setCheckedItem(R.id.nav_main);
            return;
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder alb = new AlertDialog.Builder(MainActivity.this);
                alb.setIcon(R.drawable.question);
                alb.setTitle(R.string.exit_title);
                alb.setMessage(R.string.exit_message);
                alb.setPositiveButton(R.string.exit_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alb.setNegativeButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog ald = alb.create();
                ald.show();
            }
        } else {
            getSupportFragmentManager().popBackStack();

        }
    }
}
