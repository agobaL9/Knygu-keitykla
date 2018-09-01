package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.Entities.UserData;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.LoginActivity;
import com.agobal.KnyguKeitykla.activity.MainActivity;
import com.agobal.KnyguKeitykla.activity.UserDataActivity;
import com.agobal.KnyguKeitykla.app.AppConfig;
import com.agobal.KnyguKeitykla.app.AppController;
import com.agobal.KnyguKeitykla.helper.RequestHandler;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.agobal.KnyguKeitykla.app.AppConfig.UPLOAD_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static final String UPLOAD_KEY = "photo";
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    public SQLiteHandler db;
    public SessionManager session;
    private ProgressDialog pDialog;
    private static final String TAG = UserDataActivity.class.getSimpleName();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(Objects.requireNonNull(getActivity()));

        HashMap<String, String> user = db.getUserDetails();
        final String userName =  user.get("userName");

        ShowDialog();
        getImage(userName);
        sendToServer(userName);

        CircleImageView ProfilePic = v.findViewById(R.id.profilePic);
        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Profile photo button
                showFileChooser();
                }
        });

        return v;
    }

    void sendToServer(final String userName) {
        String tag_string_req = "req_userData";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GETUSERDATA, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "UserData Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response); //
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL

                        JSONObject userData = jObj.getJSONObject("user");
                        String email = userData.getString("email");
                        String firstName = userData.getString("firstName");
                        String lastName = userData.getString("lastName");
                        String cityName = userData.getString("cityName");

                        TextView T_firstAndLastName1 = Objects.requireNonNull(getActivity()).findViewById(R.id.firstAndLastName);
                        TextView T_Desc = getActivity().findViewById(R.id.desc);
                        //TextView T_favoriteLiterature = getActivity().findViewById(R.id.favoriteLiterature);
                        TextView T_City= getActivity().findViewById(R.id.city);

                        T_firstAndLastName1.setText(firstName+ " " + lastName);
                        T_Desc.setText(email);
                        T_City.setText(cityName);

                        Log.d(TAG, "get user data:  " + firstName + lastName +email +cityName);


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getUserdata Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        })

        {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to user data url
                Map<String, String> params = new HashMap<>();
                params.put("userName", userName);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CircleImageView Profile = Objects.requireNonNull(getView()).findViewById(R.id.profilePic);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), filePath);
                Profile.setImageBitmap(bitmap); //er
                /*
                File root = Environment.getExternalStorageDirectory();
                CircleImageView Profile1 = getView().findViewById(R.id.profilePic);
                Bitmap bMap = BitmapFactory.decodeFile(root+"/images/01.jpg");
                Profile1.setImageBitmap(bMap);
                */
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, String> user = db.getUserDetails();
        String userName =  user.get("userName");

        uploadImage(userName);

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadImage(final String userName){
        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Bitmap,Void,String>{

            private ProgressDialog loading;
            private RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Uploading Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(),s,Toast.LENGTH_LONG).show();

                Log.d("php response: ", s);
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("userName", userName);
                Log.d("userName: ", userName);
                return rh.sendPostRequest(UPLOAD_URL,data);
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    private void getImage(final String userName) {

        @SuppressLint("StaticFieldLeak")
        class GetImage extends AsyncTask<String,Void,Bitmap>{
           // ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(ViewImage.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                CircleImageView Profile = Objects.requireNonNull(getView()).findViewById(R.id.profilePic);
                Profile.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {

                String add = AppConfig.URL_GETPROFILEPIC + "?userName="+ userName;
                    URL url;
                    Bitmap image = null;
                    try {
                        url = new URL(add);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(userName);
    }

    void ShowDialog()
    {
        pDialog = ProgressDialog.show(getActivity(), "Updating information", "Please wait...",true,true);
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                pDialog.dismiss();
                t.cancel();
            }
        }, 1000);
    }

}


