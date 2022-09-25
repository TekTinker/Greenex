package com.greenexagro.greenex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.greenexagro.greenex.NavigationFragments.AboutUsFragment;
import com.greenexagro.greenex.NavigationFragments.Cart;
import com.greenexagro.greenex.NavigationFragments.ContactUs;
import com.greenexagro.greenex.NavigationFragments.ProductsFragment;
import com.greenexagro.greenex.NavigationFragments.ProfileFragment;
import com.greenexagro.greenex.NavigationFragments.RawFragment;
import com.greenexagro.greenex.app.AppController;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;

    SharedPreferences sharedPreferences;
    FloatingActionButton fab;

    TextView tvName, tvLogin;

    String token;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.navCart);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.content_main, new Cart(), null).commit();
                getSupportActionBar().setTitle("Cart");
                fab.hide();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);

        tvLogin = (TextView) headerView.findViewById(R.id.tvLogin);
        tvName = (TextView) headerView.findViewById(R.id.tvName);

        sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);

        tvName.setText(sharedPreferences.getString("Name", null));
        tvLogin.setText(sharedPreferences.getString("Login", null));

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.content_main, new ProductsFragment(), null).commit();

        token = sharedPreferences.getString("Token", null);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            } else if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Exit Application?");
            builder.setPositiveButton("Yes", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Navigation.super.onBackPressed();
                }

            });

            builder.setNegativeButton("No", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawer.openDrawer(GravityCompat.START);

                }

            });

            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        fab.hide();
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            fab.show();
            fragmentManager.beginTransaction().replace(R.id.content_main, new ProductsFragment(), null).commit();
            getSupportActionBar().setTitle("Products");

        } else if (id == R.id.nav_materials) {
            fab.show();
            fragmentManager.beginTransaction().replace(R.id.content_main, new RawFragment(), null).commit();
            getSupportActionBar().setTitle("Raw Materials");

        } else if (id == R.id.nav_about) {
            fragmentManager.beginTransaction().replace(R.id.content_main, new AboutUsFragment(), null).commit();
            getSupportActionBar().setTitle("About Us");

        } else if (id == R.id.nav_logout) {
            AppController.getInstance().logout();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finishAffinity();
        } else if (id == R.id.nav_contact) {
            fragmentManager.beginTransaction().replace(R.id.content_main, new ContactUs(), null).commit();
            getSupportActionBar().setTitle("Contact Us");

        } else if (id == R.id.nav_profile) {
            fragmentManager.beginTransaction().replace(R.id.content_main, new ProfileFragment(), null).commit();
            getSupportActionBar().setTitle("Profile");
        } else if (id == R.id.nav_cart) {
            fragmentManager.beginTransaction().replace(R.id.content_main, new Cart(), null).commit();
            getSupportActionBar().setTitle("Cart");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
