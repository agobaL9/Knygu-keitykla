package com.agobal.KnyguKeitykla.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.AccountActivity.LoginActivity;
import com.agobal.KnyguKeitykla.activity.LibraryActivity;
import com.agobal.KnyguKeitykla.activity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {


    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_library, container, false);

        FloatingActionButton fab = v.findViewById(R.id.fab);


        fab.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), LibraryActivity.class);
            startActivity(intent);

        });

        return v;
    }

}
