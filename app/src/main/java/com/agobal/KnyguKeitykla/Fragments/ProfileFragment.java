package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.Category;
import com.agobal.KnyguKeitykla.helper.RequestHandler;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.agobal.KnyguKeitykla.app.AppConfig.UPLOAD_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

/*
    private SQLiteHandler db;
    private SessionManager session;
    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    private static int RESULT_LOAD_IMG = 1;
*/

    //public static final String UPLOAD_URL = "http://192.168.1.3/android_login_api/profilePicUpload.php";
    public static final String UPLOAD_KEY = "photo";
    //public static final String TAG = "MY MESSAGE";

    private int PICK_IMAGE_REQUEST = 1;


    private Bitmap bitmap;



    public ProfileFragment() {
        // Required empty public constructor

}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        CircleImageView ProfilePic = v.findViewById(R.id.profilePic);

        ArrayList<Category> picID = new ArrayList<>();



        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Profile photo button

                showFileChooser();
                //Profile photo btn
            }
        });


        SQLiteHandler db;
        //SessionManager session;

        TextView T_firstAndLastName = v.findViewById(R.id.firstAndLastName);
        TextView T_Desc = v.findViewById(R.id.desc);
        TextView T_favoriteLiterature = v.findViewById(R.id.favoriteLiterature);
        TextView T_City= v.findViewById(R.id.city);

        db = new SQLiteHandler(getContext());

        HashMap<String, String> user = db.getUserDetails();

        String firstName = user.get("firstName");
        String lastName = user.get("lastName");
        String email = user.get("email");
        String city = user.get("city");
        String userName =  user.get("userName");

        T_firstAndLastName.setText(firstName + " " +lastName);
        T_Desc.setText(email);
        T_City.setText(city);

        return v;
    }
/*
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CircleImageView ProfilePic = getView().findViewById(R.id.profilePic);
        // or  (ImageView) view.findViewById(R.id.foo);
    }
*/
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
        uploadImage();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    private void uploadImage(){
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
                String uploadImage = getStringImage(bitmap); //er

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                return rh.sendPostRequest(UPLOAD_URL,data);
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);


    }
/*
    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if(v == buttonUpload){
            uploadImage();
        }
    }
*/


}
