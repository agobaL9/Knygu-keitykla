package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookFragment extends Fragment {

    public static final String BOOK_DETAIL_KEY = "book";
    private DatabaseReference mUserBookDatabase;
    private DatabaseReference mUserBooksUserDatabase;
    private ArrayList<Books> BookList = new ArrayList<>();
    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
    private String userID;
    private String tempID;
    private TextView tvEmpty;
    private Boolean isUserHaveBooks = false;
    private String tempKey;
    private ListView listViewBooks;
    private BooksAdapter booksAdapter;

    private String BookKeyToDetails;
    private String UserKeyToDetails;
    private String queryText;



    public BookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_books, container, false);

        setHasOptionsMenu(true);


        SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
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
                                    Log.d("get key", "" + childDataSnapshot.getKey());   //Gaunu visus knygų ID

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
                                    String BookAbout = childDataSnapshot.child("bookAbout").getValue(String.class);
                                    String Image = childDataSnapshot.child("image").getValue(String.class);
                                    String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                                    String UserID = childDataSnapshot.child("userID").getValue(String.class);
                                    String BookID = childDataSnapshot.child("bookKey").getValue(String.class);


                                    BookList.add(new Books(BookName, BookAuthor, BookPublisher, BookYear, BookCondition, BookCategory, BookAbout, Image, Tradable, UserID, BookID));

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

    private void fetchBooksWithFilter(String queryText) {

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

                        Query query = mUserBooksUserDatabase.orderByChild("bookName").startAt(queryText);


                        mUserBooksUserDatabase.keepSynced(true);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //String BookName = dataSnapshot.child("bookName").getValue(String.class);
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    Log.d("get key", "" + childDataSnapshot.getKey());   //Gaunu visus knygų ID

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
                                    String BookAbout = childDataSnapshot.child("bookAbout").getValue(String.class);
                                    String Image = childDataSnapshot.child("image").getValue(String.class);
                                    String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                                    String UserID = childDataSnapshot.child("userID").getValue(String.class);
                                    String BookID = childDataSnapshot.child("bookKey").getValue(String.class);


                                    BookList.add(new Books(BookName, BookAuthor, BookPublisher, BookYear, BookCondition, BookCategory, BookAbout, Image, Tradable, UserID, BookID));

                                    booksAdapter = new BooksAdapter(Objects.requireNonNull(getContext()), BookList);
                                    listViewBooks.setAdapter(booksAdapter);
                                    booksAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", " Suveikė canceled2");
            }
        });


    }

    private void showFilter() {

                new LovelyTextInputDialog(getContext())
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Paieška")
                        .setMessage("Įrašykite knygos pavadinimą")
                        .setInputFilter("Paieška nepavyko, patikrinkite įvedimo lauką", text -> text.matches("\\w+"))
                        .setConfirmButton(android.R.string.ok, text -> {
                            queryText = text;
                            Log.d("querrytext", queryText + " ");
                            fetchBooksWithFilter(queryText);

                        })
                        .setNegativeButton("Išvalyti filtrą", text -> fetchBooks())
                        .setNegativeButtonColor(R.color.Red)
                        .show();

    }


    private void setupBookSelectedListener() {
        listViewBooks.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(getActivity(), BookDetails.class);
            intent.putExtra(BOOK_DETAIL_KEY, booksAdapter.getItem(position));
            intent.putExtra("BOOK_KEY", BookKeyToDetails);
            intent.putExtra("USER_KEY", UserKeyToDetails);
            startActivity(intent);
            Log.d("NEW_INTENT", "VEIKIA");
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.book_setting:
                Log.d("press", "yes");
                showFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
