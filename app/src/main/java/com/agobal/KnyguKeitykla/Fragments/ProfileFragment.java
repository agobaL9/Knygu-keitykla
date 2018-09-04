package com.agobal.KnyguKeitykla.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.AccountActivity.UserDataActivity;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Bitmap bitmap;
    private ProgressDialog pDialog;
    private static final String TAG = UserDataActivity.class.getSimpleName();

    public ProfileFragment() {
        // Required empty public constructor
    }


    //TextView T_firstAndLastName1 = Objects.requireNonNull(getActivity()).findViewById(R.id.firstAndLastName);
    //TextView T_Desc = getActivity().findViewById(R.id.desc);
    //TextView T_favoriteLiterature = getActivity().findViewById(R.id.favoriteLiterature);
    //TextView T_City= getActivity().findViewById(R.id.city);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        //T_firstAndLastName1.setText(firstName+ " " + lastName);
        //T_Desc.setText(email);
        //T_City.setText(cityName);

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

    private void showFileChooser() {

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


