package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.BookDetails.BookDetails;
import com.agobal.KnyguKeitykla.Entities.Books;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.adapters.BooksAdapter;

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

    public static final String BOOK_DETAIL_KEY = "book";
    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;
    DatabaseReference mUserBooksUserDatabase;
    ArrayList<Books> BookList = new ArrayList<>();
    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
    String userID;
    String tempID;
    TextView tvEmpty;
    Boolean isUserHaveBooks = false;
    String tempKey;
    private ListView listViewBooks;
    private BooksAdapter booksAdapter;

    String BookKeyToDetails;
    String UserKeyToDetails;



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

        listViewBooks = v.findViewById(R.id.listAllBooks);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);

        setupBookSelectedListener();

        mUserBookDatabase.keepSynced(true);

        mUserBookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //Log.d("All userID",""+ childDataSnapshot.getKey()); //got all users ID
                        userID = childDataSnapshot.getKey();

                        if (userID != null && userID.equals(current_uid)) { // ar yra dabartinis vartotojas userbooks šakoje
                            Log.d("ar yra šakoje?", "taip");
                            isUserHaveBooks = true;
                            tempID = userID;
                        }
                    }
                    tvEmpty.setVisibility(View.GONE);
                    fetchBooks();
                } else
                    tvEmpty.setVisibility(View.VISIBLE);

                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", " Suveikė canceled");
            }
        });

        return v;
    }

    private void fetchBooks() {

        BookList = new ArrayList<>();

        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");
        mUserBookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d("All userID", "" + childDataSnapshot.getKey()); //got all users ID

                    userID = childDataSnapshot.getKey();

                    if (userID != null && !userID.equals(current_uid)) {

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
                                    Log.d("path:", path + " ");

                                    tempKey = childDataSnapshot.getKey();
                                    BookKeyToDetails = childDataSnapshot.getKey();

                                    UserKeyToDetails=dataSnapshot.getKey();

                                    String BookName = childDataSnapshot.child("bookName").getValue(String.class);
                                    String BookAuthor = childDataSnapshot.child("bookAuthor").getValue(String.class);
                                    String BookPublisher = childDataSnapshot.child("bookPublisher").getValue(String.class);
                                    Integer BookYear = childDataSnapshot.child("bookYear").getValue(Integer.class);
                                    String BookCondition = childDataSnapshot.child("bookCondition").getValue(String.class);
                                    String BookCategory = childDataSnapshot.child("bookCategory").getValue(String.class);
                                    String Image = childDataSnapshot.child("image").getValue(String.class);
                                    String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                                    String UserID = childDataSnapshot.child("userID").getValue(String.class);


                                    BookList.add(new Books(BookName, BookAuthor, BookPublisher, BookYear, BookCondition, BookCategory, Image, Tradable, UserID));

                                    booksAdapter = new BooksAdapter(Objects.requireNonNull(getContext()), BookList);
                                    listViewBooks.setAdapter(booksAdapter);
                                    booksAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }//if
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", " Suveikė canceled2");
            }
        });

    }

    void setupBookSelectedListener() {
        listViewBooks.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(getActivity(), BookDetails.class);
            intent.putExtra(BOOK_DETAIL_KEY, booksAdapter.getItem(position)); // ? TODO: check
            intent.putExtra("BOOK_KEY", BookKeyToDetails);
            intent.putExtra("USER_KEY", UserKeyToDetails);
            startActivity(intent);
            Log.d("NEW_INTENT", "VEIKIA");
        });
    }


}
