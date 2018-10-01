package com.agobal.KnyguKeitykla.activity.AccountActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.agobal.KnyguKeitykla.app.AppConfig;
import com.agobal.KnyguKeitykla.app.AppController;
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

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputUserName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private ProgressDialog pDialog;
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

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Check if user is already logged in or not
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // User is logged in
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String userName = inputUserName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String password2 = inputPassword2.getText().toString().trim();

                checkRegistrationData(userName, email, password, password2);

            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String userName, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Vykdoma ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String userName = user.getString("userName");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        Log.d(TAG, "addUserRegistra " + uid +" "+ userName +" "+email );

                    } else {
                        Log.e(TAG, "onResponse: " + userName);
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("userName", userName);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        //Google firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "Registracija sėkminga!", Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registracija nesėkminga!" ,Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            FirebaseDatabase  database = FirebaseDatabase.getInstance();
                            DatabaseReference mDatabaseRef = database.getReference("Users");

                            Map<String, Object> Updates = new HashMap<>();
                            Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/userName", userName);
                            Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/email", email);
                            Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/image", "default");
                            Updates.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+ "/thumb_image", "default");

                            mDatabaseRef.updateChildren(Updates);

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
        //firebase
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


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}