package com.agobal.KnyguKeitykla.activity.AccountActivity;

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

import com.agobal.KnyguKeitykla.Entities.Category;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.agobal.KnyguKeitykla.helper.ServiceHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.agobal.KnyguKeitykla.helper.SessionManager;

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

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = inputName.getText().toString().trim();
                String LastName = inputLastName.getText().toString().trim();
                String CityName = spinnerCity.getSelectedItem().toString();
                int CityID = (int) spinnerCity.getSelectedItemId() +1;

                Log.d("CityID: ", String.valueOf(CityID));// ID +=1

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

                else {
                    FirebaseDatabase  database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabaseRef = database.getReference("Users");

                    Map<String, Object> hopperUpdates = new HashMap<>();

                    hopperUpdates.put(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Name", Name);
                    hopperUpdates.put(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/LastName", LastName);
                    hopperUpdates.put(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/CityName", CityName);
                    mDatabaseRef.updateChildren(hopperUpdates);

                    // Launch main activity
                    Intent intent = new Intent(
                            UserDataActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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

