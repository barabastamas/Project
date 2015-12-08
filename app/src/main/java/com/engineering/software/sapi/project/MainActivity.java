package com.engineering.software.sapi.project;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;
import com.engineering.software.sapi.project.LoginRegister.GetUserDetails;
import com.engineering.software.sapi.project.Profile.ProfileFragment;
import com.facebook.Profile;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    /*
     * Navigation drawer menu item positions
     */
    private static final int PROFILE = 0;
    private static final int OWN_ROUTES = 1;
    private static final int ADD_ROUTE = 2;
    private static final int SEARCH_ROUTE = 3;
    private static final int LOG_OUT = 4;

    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationViewDrawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        drawerToggle = setupDrawerToggle();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationViewDrawer = (NavigationView) findViewById(R.id.navigation_view);

        /*
         * Add header to navigation drawer
         */
        View headerLayout = navigationViewDrawer.inflateHeaderView(R.layout.navigation_header);

        /*
         * Put current user name to navigation header
         */
        TextView textViewCurrentUserName = (TextView) headerLayout.findViewById(R.id.navigation_drawer_name);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            try {
                textViewCurrentUserName.setText(currentUser.getUsername());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            Snackbar.make(coordinatorLayout, "Session lost. Please log in again!", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        }


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
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    private void setActionBarTitle(MenuItem item) {
        setTitle(item.getTitle());
    }

    private void setActionBarTitle(String title) {
        setTitle(title);
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
        setActionBarTitle("Main");
    }



}
