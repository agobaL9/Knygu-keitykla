package com.agobal.KnyguKeitykla.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends Activity {
    private EditText inputUserName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        inputUserName = findViewById(R.id.userName);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputPassword2 = findViewById(R.id.password2);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);

        auth = FirebaseAuth.getInstance();

        // Register Button Click event
        btnRegister.setOnClickListener(view -> {

            final String userName = inputUserName.getText().toString().trim();
            final String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String password2 = inputPassword2.getText().toString().trim();

            checkRegistrationData(userName, email, password, password2);
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(i);
            finish();
        });

    }

    void checkRegistrationData(String userName, String email, String password, String password2)
    {
        if(TextUtils.isEmpty(userName)) {
            inputUserName.setError("Šis laukas yra privalomas!");
            return;
        }

        if(!userName.matches("[a-zA-Z0-9.? ]*")) { // PauliusII
            inputUserName.setError("Netinka specialūs simboliai!");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            inputEmail.setError("Šis laukas yra privalomas!");
            return;
        }

        if (!isValidEmail(inputEmail.getText().toString())) {
            inputEmail.setError("Įveskite teisingą el. pašto adresą!");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            inputPassword.setError("Šis laukas yra privalomas!");
            return;
        }

        if(TextUtils.isEmpty(password2)) {
            inputPassword2.setError("Šis laukas yra privalomas!");
            return;
        }

        if  (!password.matches(password2)) {
            inputPassword2.setError("Slaptažodžiai nesutampa!");
            return;
        }

        if (password.length()<6){
            inputPassword2.setError("Slaptažodis per trumpas!");
        }

        else
            registerUser(userName, email, password);
    }

    private void registerUser(final String userName, final String email, final String password) {

        SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registuojantis įvyko klaida!" ,Toast.LENGTH_SHORT).show();
                        pDialog.dismissWithAnimation();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Registracija sėkminga!", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase  database = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabaseRef = database.getReference("Users");

                        Map<String, Object> Updates = new HashMap<>();
                        Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/userName", userName);
                        Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/email", email);
                        Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/image", "default");
                        Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/thumb_image", "default");

                        mDatabaseRef.updateChildren(Updates);

                        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
                        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                        pDialog.dismissWithAnimation();

                        mUserDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("cityName").exists()) {
                                    //isUserDataExist = true;
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    startActivity(new Intent(RegisterActivity.this, UserDataActivity.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

    }
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}