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

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.SQLiteHandler;
import com.agobal.KnyguKeitykla.helper.SessionManager;

import java.util.HashMap;



/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private SQLiteHandler db;
    private SessionManager session;

    TextView userName1;
    TextView Desc;
    TextView Hobies;
    TextView City;



    public ProfileFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        SQLiteHandler db;
        SessionManager session;



        TextView userName1 = (TextView) v.findViewById(R.id.userName);
        TextView Desc = (TextView) v.findViewById(R.id.desc);
        TextView Hobies = (TextView) v.findViewById(R.id.hobies);
        TextView City= (TextView) v.findViewById(R.id.city);

        db = new SQLiteHandler(getContext());

        HashMap<String, String> user = db.getUserDetails();

        String userName = user.get("userName");
        String email = user.get("email");


        userName1.setText(userName);
        Desc.setText(email);
        Hobies.setText("hobiai");
        City.setText("miestas");




        return v;
    }


}
