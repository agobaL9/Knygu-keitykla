package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.AllBooks;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.activity.adapters.AllBooksAdapter;
import com.agobal.KnyguKeitykla.activity.adapters.MyBookAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookFragment extends Fragment {

    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;
    DatabaseReference mUserBooksUserDatabase;

    private ListView listView;
    private AllBooksAdapter allBooksAdapter;
    ArrayList<AllBooks> AllBookList = new ArrayList<>();

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    String userID;
    String tempID;
    TextView tvEmpty;
    Boolean isUserHaveBooks=false;
    String tempKey;

    public BookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_books, container, false);

        SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        listView = v.findViewById(R.id.listAllBooks);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);

        mUserBookDatabase.keepSynced(true);

        mUserBookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //Log.d("All userID",""+ childDataSnapshot.getKey()); //got all users ID
                        userID = childDataSnapshot.getKey();

                        if (userID.equals(current_uid)) { // ar yra dabartinis vartotojas userbooks šakoje
                            Log.d("ar yra šakoje?", "taip");
                            isUserHaveBooks = true;
                            tempID = userID;
                        }
                    }
                    tvEmpty.setVisibility(View.GONE);
                    fetchBooks();
                }
                else
                    tvEmpty.setVisibility(View.VISIBLE);

                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled"," Suveikė canceled");
            }
        }) ;

        return v;
    }

    private void fetchBooks() {

        AllBookList = new ArrayList<>();

        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");
        mUserBookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.d("All userID",""+ childDataSnapshot.getKey()); //got all users ID

                        userID = childDataSnapshot.getKey();

                        mUserBooksUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(userID);
                        mUserBooksUserDatabase.keepSynced(true);
                        mUserBooksUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //String BookName = dataSnapshot.child("bookName").getValue(String.class);
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    Log.d("get key", "" + childDataSnapshot.getKey());   //displays the key for the node 2 TODO: Gaunu visus knygų ID !!

                                    String path = childDataSnapshot.getRef().toString();
                                    Log.d("path:",path+" ");

                                    tempKey = childDataSnapshot.getKey();

                                    String BookName = childDataSnapshot.child("bookName").getValue(String.class);
                                    String BookAuthor = childDataSnapshot.child("bookAuthor").getValue(String.class);
                                    String Image = childDataSnapshot.child("image").getValue(String.class);
                                    String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                                    AllBookList.add(new AllBooks(BookName, BookAuthor, Image, Tradable));

                                    allBooksAdapter = new AllBooksAdapter(Objects.requireNonNull(getContext()), AllBookList);
                                    listView.setAdapter(allBooksAdapter);
                                    allBooksAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled"," Suveikė canceled2");
            }
        });

    }


}
