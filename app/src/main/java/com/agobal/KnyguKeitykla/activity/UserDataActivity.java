package com.agobal.KnyguKeitykla.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.agobal.KnyguKeitykla.app.AppConfig;
import com.agobal.KnyguKeitykla.app.AppController;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.SessionManager;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.agobal.KnyguKeitykla.R;

public class UserDataActivity extends Activity {

    private static final String TAG = UserDataActivity.class.getSimpleName();

    EditText inputName;
    EditText inputLastName;
    EditText inputCity;
    Button btnNext;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        inputName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        inputCity = findViewById(R.id.inputCity);
        btnNext = findViewById(R.id.btnNext);

        // Session manager
        SessionManager session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        final String email = user.get("email");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = inputName.getText().toString().trim();
                String LastName = inputLastName.getText().toString().trim();
                String City = inputCity.getText().toString().trim();

                if (Name.isEmpty()  || LastName.isEmpty() || City.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),
                            "Užpildykite visus laukus!", Toast.LENGTH_LONG)
                            .show();
                }

                else if(!Name.matches("[a-zA-Z.? ]*") || !LastName.matches("[a-zA-Z.? ]*") || !City.matches("[a-zA-Z.? ]*"))
                {
                    Toast.makeText(getApplicationContext(),
                            "Netinka spec simboliai", Toast.LENGTH_LONG).show();
                }

                else
                    WriteToDB(Name, LastName, City, email);
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        Toast.makeText(getApplicationContext(),
                "Negalima", Toast.LENGTH_LONG).show();
    }

    void WriteToDB(final String Name, final String LastName, final String City, final String  Email)
    {
        // Tag used to cancel the request
        String tag_string_req = "req_userData";

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_USERDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "UserData Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response); //
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL

                        //now sqlite
                        JSONObject userData = jObj.getJSONObject("user");
                        String firstName = userData.getString("firstName");
                        String lastName = userData.getString("lastName");
                        String city = userData.getString("city");


                        HashMap<String, String> userEmail = db.getUserDetails();
                        String email = userEmail.get("email");

                        db.addUserData(firstName, lastName, city, email);
                        Log.d(TAG, "addUserData " + firstName + lastName +city  +email);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch main activity
                        Intent intent = new Intent(
                                UserDataActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

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
                Log.d(TAG, "userdata enter Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to user data url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", Name);
                params.put("lastName", LastName);
                params.put("city", City);
                params.put("email", Email);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    }

