package com.agobal.KnyguKeitykla.activity.AccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.AddNewBook;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPasswordActivity extends Activity {

    private EditText inputEmail;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reset_password);

        inputEmail = findViewById(R.id.email);
        Button btnReset = findViewById(R.id.btn_reset_password);
        Button btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(i);
            finish();
        });

        btnReset.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Įveskite prisijungimo informaciją!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            new SweetAlertDialog(this)
                                    .setTitleText("Slaptažodžio pakeitimo nuoroda Jums išsiųsta į el. pašto adresą! ")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .show();

                        } else {
                            new SweetAlertDialog(this)
                                    .setTitleText("Nuorodos su slaptažodžiu išsiųsti nepavyko. Prašome patikrinti duomenis! ")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .show();
                        }

                        progressBar.setVisibility(View.GONE);
                    });
        });
    }

}