package com.agobal.KnyguKeitykla.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.SessionManager;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private SQLiteHandler db;
    //private SessionManager session;

    TextView T_firstAndLastName = findViewById(R.id.firstAndLastName);
    TextView T_Desc =findViewById(R.id.desc);
    TextView T_favoriteLiterature = findViewById(R.id.favoriteLiterature);
    TextView T_City= findViewById(R.id.city);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        T_firstAndLastName = findViewById(R.id.firstAndLastName);
        T_Desc = findViewById(R.id.desc);
        T_favoriteLiterature = findViewById(R.id.favoriteLiterature);
        T_City = findViewById(R.id.city);
*/
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        String firstName = user.get("firstName");
        String lastName = user.get("lastName");
        String email = user.get("email");
        String city = user.get("city");

        T_firstAndLastName.setText(firstName);
        T_Desc.setText(email);
        T_City.setText("NENAUDOTI");

    }
}
