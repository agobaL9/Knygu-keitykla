package com.agobal.KnyguKeitykla.activity.AccountActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.Entities.Category;
import com.agobal.KnyguKeitykla.Entities.UserData;
import com.agobal.KnyguKeitykla.Fragments.ProfileFragment;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.AddNewBook;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.agobal.KnyguKeitykla.helper.CustomProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEdit extends AppCompatActivity {

    Spinner spinnerCity;
    ArrayList<Category> citiesList;

    EditText E_ProfileInputName;
    EditText E_ProfileInputLastName;
    EditText E_ProfileInputEmail;
    EditText E_ProfileInputUsername;
    EditText E_ProfileInputAbout;

    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        setTitle("Profilio redagavimas");


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        E_ProfileInputName = findViewById(R.id.ProfileInputName);
        E_ProfileInputLastName = findViewById(R.id.ProfileInputLastName);
        E_ProfileInputEmail = findViewById(R.id.ProfileInputEmail);
        E_ProfileInputUsername = findViewById(R.id.ProfileInputUsername);
        E_ProfileInputAbout = findViewById(R.id.ProfileInputAbout);
        spinnerCity = findViewById(R.id.ProfileSpinCity);
        Button BtnProfileSave= findViewById(R.id.btnSave);

        citiesList = new ArrayList<>();

        String firstName= getIntent().getStringExtra("firstName");
        String lastName= getIntent().getStringExtra("lastName");
        String email= getIntent().getStringExtra("email");
        String userName= getIntent().getStringExtra("userName");
        String about = getIntent().getStringExtra("about");
        cityName= getIntent().getStringExtra("cityName");

        getUserData(firstName, lastName, email, userName, about);

        BtnProfileSave.setOnClickListener(view -> {

            String FirstName = E_ProfileInputName.getText().toString().trim();
            String LastName = E_ProfileInputLastName.getText().toString().trim();
            String Email = E_ProfileInputEmail.getText().toString().trim();
            String UserName = E_ProfileInputUsername.getText().toString().trim();
            String About = E_ProfileInputAbout.getText().toString().trim();
            String CityName = spinnerCity.getSelectedItem().toString();

            saveChanges(FirstName, LastName, Email, UserName, About, CityName);
        });

        selectCity();
    }

    private void selectCity() {


        FirebaseDatabase  database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = database.getReference("City");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("cityName").getValue(String.class);
                    areas.add(areaName);
                }

                Spinner areaSpinner = findViewById(R.id.ProfileSpinCity);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(ProfileEdit.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserData(String firstName, String lastName, String email, String userName, String about) {

        E_ProfileInputName.setText(firstName);
        E_ProfileInputLastName.setText(lastName);
        E_ProfileInputEmail.setText(email);
        E_ProfileInputUsername.setText(userName);
        E_ProfileInputAbout.setText(about);

    }

    private void saveChanges(String firstName, String lastName, String email, String userName, String about, String cityName) {

        FirebaseDatabase  database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = database.getReference("Users");

        Map<String, Object> hopperUpdates = new HashMap<>();

        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/firstName", firstName);
        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/lastName", lastName);
        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/cityName", cityName);
        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/email", email);
        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/userName", userName);
        hopperUpdates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/about", about);
        mDatabaseRef.updateChildren(hopperUpdates);

        new SweetAlertDialog(this)
                .setTitleText("Duomenys atnaujinti! ")
                .setConfirmClickListener(sweetAlertDialog -> {

                    Intent intent = new Intent(ProfileEdit.this, MainActivity.class);
                    startActivity(intent);


                })
                .show();

    }

    public void onBackPressed() {
        int backstack = getSupportFragmentManager().getBackStackEntryCount();

        if (backstack > 0)
        {
            getSupportFragmentManager().popBackStack();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
