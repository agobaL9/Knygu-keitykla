package com.agobal.KnyguKeitykla.activity.AccountActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.amitshekhar.DebugDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends Activity {

    private FirebaseAuth auth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebugDB.getAddressLog();
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

        //tikrinimas ar vartotojas prisijungęs
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
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

    void login(final String email, final String password)
    {

        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();
        if (!email.isEmpty() && !password.isEmpty()) {
        //firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful())
                    {
                        //there was an error
                        Toast.makeText(getApplicationContext(), "Prisijungti nepavyko, prašome patikrinti įvestus duomenis", Toast.LENGTH_LONG).show();
                        pDialog.dismissWithAnimation();
                    }
                    else
                    {
                         pDialog.dismissWithAnimation();
                         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                         startActivity(intent);
                         finish();
                    }
                });
        }
        else
        {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(), "Prošome užpildyti laukus!", Toast.LENGTH_LONG)
                    .show();
        }

    }

}