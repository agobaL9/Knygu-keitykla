package com.agobal.KnyguKeitykla.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.app.AppConfig;
import com.agobal.KnyguKeitykla.app.AppController;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.ServiceHandler;
//import com.agobal.KnyguKeitykla.helper.SessionManager;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.agobal.KnyguKeitykla.app.AppConfig.URL_CITIES;

public class UserDataActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = UserDataActivity.class.getSimpleName();

    EditText inputName;
    EditText inputLastName;
    Button btnNext;

    private Spinner spinnerCity;
    private ArrayList<Category> citiesList;
    ProgressDialog pDialog;

    private SQLiteHandler db;
    // Url to get all categories

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        inputName = findViewById(R.id.inputName);
        inputLastName = findViewById(R.id.inputLastName);
        btnNext = findViewById(R.id.btnNext);

        spinnerCity = findViewById(R.id.spinCity);

        citiesList = new ArrayList<>();

        // spinner item select listener
        spinnerCity.setOnItemSelectedListener(this);

        // Session manager
        //SessionManager session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        final String email = user.get("email");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = inputName.getText().toString().trim();
                String LastName = inputLastName.getText().toString().trim();
                String CityName = spinnerCity.getSelectedItem().toString(); //gaunu miestą
                int CityID = (int) spinnerCity.getSelectedItemId() +1;

                Log.d("CityID: ", String.valueOf(CityID));// ID +=1  vienu mazesniu duoda

                Log.d("City response",  CityName);

                if (Name.isEmpty()  || LastName.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),
                            "Užpildykite visus laukus!", Toast.LENGTH_LONG)
                            .show();
                }

                else if(!Name.matches("[a-zA-Z.? ]*") || !LastName.matches("[a-zA-Z.? ]*"))
                {
                    Toast.makeText(getApplicationContext(),
                            "Netinka spec simboliai", Toast.LENGTH_LONG).show();
                }

                else
                    WriteToDB(Name, LastName, CityName,  CityID, email);
            }
        });

        new  GetCities().execute();

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        Toast.makeText(getApplicationContext(),
                "Negalima", Toast.LENGTH_LONG).show();
    }

    void WriteToDB(final String Name, final String LastName, final String CityName, final int CityID, final String  Email)
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
                        //String city = userData.getString("city");


                        HashMap<String, String> userEmail = db.getUserDetails();
                        String email = userEmail.get("email");

                        db.addUserData(firstName, lastName, CityName, email);
                        Log.d(TAG, "addUserData " + firstName + lastName +email +CityName);

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
                Map<String, String> params = new HashMap<>();
                params.put("firstName", Name);
                params.put("lastName", LastName);
                params.put("cityID", String.valueOf(CityID)); //err?
                params.put("email", Email);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<>();

        for (int i = 0; i < citiesList.size(); i++) {
            lables.add(citiesList.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerCity.setAdapter(spinnerAdapter);

    }

    /**
     * Async task to get all food categories
     * */
    @SuppressLint("StaticFieldLeak")
    private class GetCities extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserDataActivity.this);
            pDialog.setMessage("Fetching cities..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_CITIES, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray cities = jsonObj
                            .getJSONArray("cities");

                    for (int i = 0; i < cities.length(); i++) {
                        JSONObject cityObj = (JSONObject) cities.get(i);
                        Category cat = new Category(cityObj.getInt("id"),
                                cityObj.getString("cityName"));
                        citiesList.add(cat);
                        Log.e ( "cat: ", "" + cityObj );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Selected" ,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

