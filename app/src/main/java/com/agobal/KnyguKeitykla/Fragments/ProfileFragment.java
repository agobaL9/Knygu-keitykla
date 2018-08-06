package com.agobal.KnyguKeitykla.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private SQLiteHandler db;
    private SessionManager session;
    ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        CircleImageView ProfilePic = v.findViewById(R.id.profilePic);

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Profile pic!!",Toast.LENGTH_SHORT).show();
            }
        });

        SQLiteHandler db;
        SessionManager session;

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

        T_firstAndLastName.setText(firstName + " " +lastName);
        T_Desc.setText(email);
        T_City.setText(city);

        return v;
    }


}
