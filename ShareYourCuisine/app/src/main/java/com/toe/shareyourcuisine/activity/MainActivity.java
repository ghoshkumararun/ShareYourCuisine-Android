package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.fragment.EventFragment;
import com.toe.shareyourcuisine.fragment.HomeFragment;
import com.toe.shareyourcuisine.fragment.PostFragment;
import com.toe.shareyourcuisine.fragment.RecipeFragment;
import com.toe.shareyourcuisine.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ToeMainActivity:";
    private NavigationView mNavigationView;
    private String mAuthAction = "";
    private TextView mEmailTV;
    private TextView mNameTV;
    private CircleImageView mAvatarCIV;
    public static MaterialSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mEmailTV = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.email_tv);
        mNameTV = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.name_tv);
        mAvatarCIV = (CircleImageView)mNavigationView.getHeaderView(0).findViewById(R.id.avatar_civ);
        mSearchView = (MaterialSearchView)findViewById(R.id.search_view);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    // User is signed in
                    mNavigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(false);
                    mNavigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
                    mNavigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
                    mNavigationView.getMenu().findItem(R.id.nav_favorite).setVisible(true);
                    try {
//                        User user = User.find(User.class, "email = ?", mFirebaseUser.getEmail()).get(0);
                        Picasso.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(mAvatarCIV);
                        mEmailTV.setText(mAuth.getCurrentUser().getEmail());
                        mNameTV.setText(mAuth.getCurrentUser().getDisplayName());
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                        mAuth.signOut();
                    }

                } else {
                    // User is signed out
                    mNavigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(true);
                    mNavigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
                    mNavigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
                    mNavigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
//                    mNavigationView.getMenu().findItem(R.id.nav_msg).setVisible(false);
                    Picasso.with(MainActivity.this).load(R.drawable.avatar).into(mAvatarCIV);
                    mEmailTV.setText("");
                    mNameTV.setText("Guest");

                    if(mAuthAction.equalsIgnoreCase("sign out"))
                        Toast.makeText(MainActivity.this, "Sign out successfully!",
                                Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mAuthAction = "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(menuItem);
        return false;
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
            setTitle(item.getTitle());
        } else if (id == R.id.nav_recipe) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new RecipeFragment()).commit();
            setTitle(item.getTitle());
        } else if (id == R.id.nav_post) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new PostFragment()).commit();
            setTitle(item.getTitle());
//        } else if (id == R.id.nav_event) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.content, new EventFragment()).commit();
//            setTitle(item.getTitle());
        } else if (id == R.id.nav_sign_in) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            mAuthAction = "sign out";
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
//        } else if (id == R.id.nav_msg) {
//            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
