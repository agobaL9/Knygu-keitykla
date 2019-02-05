package com.agobal.KnyguKeitykla.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.MainActivity;
import com.agobal.KnyguKeitykla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends Activity {

    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        final EditText inputEmail = findViewById(R.id.email);
        final EditText inputPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLinkToRegister = findViewById(R.id.btnLinkToRegisterScreen);
        TextView btn_reset_password = findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            login(email, password);
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),
                    RegisterActivity.class);
            startActivity(i);
            finish();
        });

        //link to reset password
        btn_reset_password.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
            startActivity(i);
            finish();
        });

    }

    private void login(final String email, final String password)
    {

        if (!email.isEmpty() && !password.isEmpty()) {
        //firebase
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Prašome palaukti...");
            pDialog.setCancelable(false);
            pDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (!task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Prisijungti nepavyko, prašome patikrinti įvestus duomenis", Toast.LENGTH_LONG).show();
                        pDialog.dismissWithAnimation();
                    }
                    else
                    {
                        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
                        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("cityName").exists()) {
                                    //isUserDataExist = true;
                                    pDialog.dismissWithAnimation();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    pDialog.dismissWithAnimation();
                                    startActivity(new Intent(LoginActivity.this, UserDataActivity.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Prošome užpildyti laukus!", Toast.LENGTH_LONG)
                    .show();
        }

    }

}