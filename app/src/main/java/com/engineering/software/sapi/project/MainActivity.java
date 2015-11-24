package com.engineering.software.sapi.project;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;

public class MainActivity extends AppCompatActivity {

    /*
     * Navigation drawer menu item positions
     */
    private static final int PROFILE = 0;
    private static final int OWN_ROUTES = 1;
    private static final int ADD_ROUTE = 2;
    private static final int SEARCH_ROUTE = 3;
    private static final int LOG_OUT = 4;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationViewDrawer;
    private Toolbar toolbar;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        drawerToggle = setupDrawerToggle();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationViewDrawer = (NavigationView) findViewById(R.id.navigation_view);

        /*
         * Setup drawer view
         */
        setupDrawerContent(navigationViewDrawer);

        /*
         * Set starting frame
         */
        try {
            setStartingFragment(MainFragment.class.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


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
        Fragment fragment = null;

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
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Replace old fragment with the selected one
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        // Highlight the selected item
        item.setChecked(true);
        setActionBarTitle(item);

        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    private void setActionBarTitle(MenuItem item) {
        setTitle(item.getTitle());
    }

    private void setActionBarTitle(Class c) {
        setTitle(c.getSimpleName());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setStartingFragment(Fragment fragment) {
        /*
         * Add starting fragment
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content, fragment).commit();

        /*
         * Sets the action bar title
         */
        setActionBarTitle(navigationViewDrawer.getMenu().getItem(OWN_ROUTES));
    }
}
