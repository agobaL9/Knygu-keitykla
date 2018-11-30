package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.Book;
import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.SearchBookActivity;
import com.agobal.KnyguKeitykla.activity.adapters.MyBookAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;
    DatabaseReference mUserBooksDatabase;
    DatabaseReference mUserBooksUserDatabase;

    private ListView listView;
    private MyBookAdapter myBookAdapter;
    ArrayList<MyBook> MyBookList = new ArrayList<>();

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    String userID;
    String tempID;
    TextView tvEmpty;
    Boolean isUserHaveBooks=false;
    String tempKey;


    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_library, container, false);

        SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        listView = v.findViewById(R.id.listMyBooks);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);

        mUserBookDatabase.keepSynced(true);

        mUserBookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO: for ciklas

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("All userID",""+ childDataSnapshot.getKey()); //got all users ID

                    userID  = childDataSnapshot.getKey();

                    Log.d("userID", userID);

                    if(userID.equals(current_uid)) {
                        Log.d("ar lygus??", "taip");
                        isUserHaveBooks =true;
                        tempID = userID;
                    }
                }

                Log.d("tempID123"," "+tempID);

                if(!current_uid.equals(tempID))
                {
                    tvEmpty.setVisibility(View.VISIBLE);
                    pDialog.dismissWithAnimation();

                }
                else
                    tvEmpty.setVisibility(View.GONE);

                fetchBooks();
                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

        FloatingActionButton fab = v.findViewById(R.id.fab);

        fab.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SearchBookActivity.class);
            startActivity(intent);

        });

        return v;
    }

    private void fetchBooks() {

        if(isUserHaveBooks)
        {
            mUserBooksDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(tempID); //temp id is user id
            MyBookList = new ArrayList<>();

            mUserBooksDatabase.keepSynced(true);
            mUserBooksDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //String BookName = dataSnapshot.child("bookName").getValue(String.class);
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //Log.d("get key", "" + childDataSnapshot.getKey());   //displays the key for the node 2

                        tempKey = childDataSnapshot.getKey();
                        mUserBooksUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(tempID).child(tempKey);

                        mUserBooksUserDatabase.keepSynced(true);
                        mUserBooksUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String BookName = dataSnapshot.child("bookName").getValue(String.class);
                                String BookAuthor = dataSnapshot.child("bookAuthor").getValue(String.class);
                                String Image = dataSnapshot.child("image").getValue(String.class);
                                String Tradable = dataSnapshot.child("tradable").getValue(String.class);

                                MyBookList.add(new MyBook(BookName, BookAuthor, Image, Tradable));

                                myBookAdapter = new MyBookAdapter(Objects.requireNonNull(getContext()), MyBookList);
                                listView.setAdapter(myBookAdapter);
                                myBookAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
