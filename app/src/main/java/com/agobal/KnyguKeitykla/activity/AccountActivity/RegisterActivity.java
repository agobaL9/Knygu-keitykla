package com.agobal.KnyguKeitykla.activity.AccountActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.Entities.UserData;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.agobal.KnyguKeitykla.helper.CustomProgressBar;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

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

        // Check if user is already logged in or not
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // User is logged in
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(view -> {

            final String userName = inputUserName.getText().toString().trim();
            final String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String password2 = inputPassword2.getText().toString().trim();

            SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Prašome palaukti");
            pDialog.setCancelable(false);
            pDialog.show();

            checkRegistrationData(userName, email, password, password2);

            pDialog.dismissWithAnimation();

            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();

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


        if (!isValidEmail(inputEmail.getText().toString()))
            Toast.makeText(getApplicationContext(), "Įveskite galiojantį el. paštą!", Toast.LENGTH_LONG).show();


        else if (userName.isEmpty()  || email.isEmpty() || password.isEmpty() || password2.isEmpty())
            Toast.makeText(getApplicationContext(),"Užpildykite visus laukus!", Toast.LENGTH_LONG).show();


        else if(!userName.matches("[a-zA-Z.? ]*"))
            Toast.makeText(getApplicationContext(),"Netinka specialūs simboliai!", Toast.LENGTH_LONG).show();


        else if  (!password.matches(password2))
            Toast.makeText(getApplicationContext(),"Slaptažodžiai nesutampa!", Toast.LENGTH_LONG).show();


        else if (password.length()<6)
            Toast.makeText(getApplicationContext(),"Slaptažodis per trumpas!", Toast.LENGTH_LONG).show();

        else
            registerUser(userName, email, password);
    }


    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String userName, final String email, final String password) {
        //Google firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registuojantis įvyko klaida!" ,Toast.LENGTH_SHORT).show();
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

                    }
                });
        //firebase
    }
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}