package com.agobal.KnyguKeitykla.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    private StorageReference mImageStorage;
    private DatabaseReference mBookDatabase;
    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;
    DatabaseReference mUserBooksDatabase;

    DatabaseReference mUserBooksDatabaseTest;

    private ListView listView;
    private MyBookAdapter myBookAdapter;

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

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        listView = v.findViewById(R.id.listMyBooks);

        /*
        lvBooks = v.findViewById(R.id.listMyBooks);
        ArrayList<Book> aBooks = new ArrayList<>();
        bookAdapter = new BookAdapter(getActivity(), aBooks);
        lvBooks.setAdapter(bookAdapter);
*/


        tvEmpty = v.findViewById(R.id.tvEmpty);


        mUserBookDatabase.keepSynced(true);

        mUserBookDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO: for ciklas

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("All userID",""+ childDataSnapshot.getKey()); //gaunu visus user ID

                    userID  =  childDataSnapshot.getKey().toString();

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
                }
                else
                    tvEmpty.setVisibility(View.GONE);



                fetchBooks();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;



        FloatingActionButton fab = v.findViewById(R.id.fab);

        //Log.d("tempID", " "+tempID); // null

        fab.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), SearchBookActivity.class);
            startActivity(intent);

        });

        return v;
    }

    private void fetchBooks() {

        if(isUserHaveBooks)
        {
            mUserBooksDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(tempID);

            mUserBooksDatabase.keepSynced(true);

            mUserBooksDatabase.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //String BookName = dataSnapshot.child("bookName").getValue(String.class);
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Log.d("get key", "" + childDataSnapshot.getKey());   //displays the key for the node

                        tempKey = childDataSnapshot.getKey();


                        //Log.d("get book name",""+ childDataSnapshot.child("bookName").getValue());   //gives the
                    }

                    mUserBooksDatabaseTest = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(tempID).child(tempKey);

                    mUserBooksDatabaseTest.keepSynced(true);
                    mUserBooksDatabaseTest.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String BookName = dataSnapshot.child("bookName").getValue(String.class);
                            String BookAuthor =  dataSnapshot.child("bookAuthor").getValue(String.class);
                            String Image =  dataSnapshot.child("image").getValue(String.class);

                            Log.d("BookName: ", BookName);
                            Log.d("bookAuthor: ", BookAuthor);
                            Log.d("image: ", Image);

                            //bookAdapter.add(BookName);

                            ArrayList<MyBook> MyBookList = new ArrayList<>();

                            MyBookList.add(new MyBook(BookName, BookAuthor, Image));

                            


                            myBookAdapter = new MyBookAdapter(getContext(), MyBookList);

                            listView.setAdapter(myBookAdapter);

                            myBookAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }




}
