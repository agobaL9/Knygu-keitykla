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
    private SessionManager session;

    TextView userName1;
    TextView Desc;
    TextView Hobies;
    TextView City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName1 = findViewById(R.id.userName);
        Desc = findViewById(R.id.desc);
        Hobies = findViewById(R.id.hobies);
        City = findViewById(R.id.city);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        String userName = user.get("userName");
        String email = user.get("email");

        userName1.setText(userName);
        Desc.setText(email);


    }
}
