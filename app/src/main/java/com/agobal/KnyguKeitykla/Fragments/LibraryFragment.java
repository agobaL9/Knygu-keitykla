package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.OnGetDataListener;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.BookDetails.MyBookDetail;
import com.agobal.KnyguKeitykla.activity.SearchBookAPI;
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
public class LibraryFragment extends Fragment {

    public static final String MY_BOOK_DETAIL_KEY = "my_book";

    String userID;
    TextView tvEmpty;
    Boolean isUserHaveBooks = false;

    DatabaseReference mUserDatabase;
    DatabaseReference mUserBooksDatabase;
    DatabaseReference mUserBooksUserDatabase;

    ListView listView;
    MyBookAdapter myBookAdapter;
    ArrayList<MyBook> MyBookList = new ArrayList<>();

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library, container, false);

        Log.d("currentID", " " + current_uid);

        SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserBooksDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        listView = v.findViewById(R.id.listMyBooks);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);

        listView.setAdapter(myBookAdapter);
        myBookAdapter = new MyBookAdapter(Objects.requireNonNull(getContext()), MyBookList);

        setupBookSelectedListener();

        IsUserHaveBooks();


        //Log.d("tempID"," "+tempID);
        pDialog.dismissWithAnimation();

        FloatingActionButton fab = v.findViewById(R.id.fab);

        fab.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SearchBookAPI.class);
            startActivity(intent);

        });


        return v;
    }

    private void IsUserHaveBooks() {

        mUserBooksDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //Log.d("All userID",""+ childDataSnapshot.getKey()); //got all users ID
                        userID = childDataSnapshot.getKey();

                        if (userID.equals(current_uid)) { // ar yra dabartinis vartotojas userbooks šakoje
                            Log.d("ar yra šakoje?", "taip");
                            isUserHaveBooks = true;
                        }
                    }

                    if(isUserHaveBooks) //laikinai
                    {
                        fetchMyBooks();
                        tvEmpty.setVisibility(View.GONE);
                    }

                    else
                        tvEmpty.setVisibility(View.VISIBLE);

                }
                else
                    tvEmpty.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled"," Suveikė canceled");
            }
        }) ;
    }


    void setupBookSelectedListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(getActivity(), MyBookDetail.class);
            intent.putExtra(MY_BOOK_DETAIL_KEY, myBookAdapter.getItem(position)); // ? TODO: check
            startActivity(intent);
            Log.d("NEW_INTENT", "VEIKIA");
        });
    }

    private void fetchMyBooks()
    {

        MyBookList = new ArrayList<>();
        mUserBooksDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");
        mUserBooksDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserBooksUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(current_uid);
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

                            String BookName = childDataSnapshot.child("bookName").getValue(String.class);
                            String BookAuthor = childDataSnapshot.child("bookAuthor").getValue(String.class);
                            String BookPublisher = childDataSnapshot.child("bookPublisher").getValue(String.class);
                            Integer BookYear = childDataSnapshot.child("bookYear").getValue(Integer.class);
                            String BookCondition = childDataSnapshot.child("bookCondition").getValue(String.class);
                            String BookCategory = childDataSnapshot.child("bookCategory").getValue(String.class);
                            String Image = childDataSnapshot.child("image").getValue(String.class);
                            String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                            MyBookList.add(new MyBook(BookName, BookAuthor, BookPublisher, BookYear, BookCondition, BookCategory, Image, Tradable));

                            myBookAdapter = new MyBookAdapter(Objects.requireNonNull(getContext()), MyBookList);
                            listView.setAdapter(myBookAdapter);
                            myBookAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled"," Suveikė canceled2");
            }
        });


    }

    public void readData(DatabaseReference mUserBooksUserDatabase, final OnGetDataListener listener) {
        listener.onStart();
        mUserBooksUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();

            }
        });
    }

}
