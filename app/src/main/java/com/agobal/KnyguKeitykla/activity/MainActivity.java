package com.agobal.KnyguKeitykla.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.agobal.KnyguKeitykla.Fragments.LibraryFragment;
import com.agobal.KnyguKeitykla.Fragments.MessagesFragment;
import com.agobal.KnyguKeitykla.Fragments.ProfileFragment;
import com.agobal.KnyguKeitykla.Fragments.BookFragment;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.AccountActivity.LoginActivity;
import com.agobal.KnyguKeitykla.helper.BottomNavigationBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = getSupportActionBar();



        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        // load the store fragment by default
        toolbar.setTitle("Knygos");
        loadFragment(new BookFragment());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_books:
                    toolbar.setTitle("Knygos");
                    fragment = new BookFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_messages:
                    toolbar.setTitle("Pranešimai");
                    fragment = new MessagesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_library:
                    toolbar.setTitle("Biblioteka");
                    fragment = new LibraryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    toolbar.setTitle("Profilis");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logoutUser() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

}
