package com.engineering.software.sapi.project;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationViewDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replace ActionBar with Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        drawerToggle = setupDrawerToggle();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationViewDrawer = (NavigationView) findViewById(R.id.navigation_view);
        // Setup drawer view
        setupDrawerContent(navigationViewDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void selectDrawerItem(MenuItem item) {
        android.support.v4.app.Fragment fragment = null;

        Class fragmentClass = null;
        switch (item.getItemId()) {
            case R.id.profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.own_routes:
                fragmentClass = OwnRoutesFragment.class;
                break;
            case R.id.add_route:
                fragmentClass = AddRouteFragment.class;
                break;
            case R.id.search_route:
                fragmentClass = SearchRouteFragment.class;
                break;
            case R.id.log_out:
                fragmentClass = LogOutFragment.class;
                break;
        }

        try {
            if (fragmentClass != null) {
                fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Replace old fragment with the selected one
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        // Highlight selected item
        item.setChecked(true);
        setTitle(item.getTitle());

        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }
}
