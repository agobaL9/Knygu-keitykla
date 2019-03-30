package com.agobal.KnyguKeitykla.BookDetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.Chat.Chat_activity;
import com.agobal.KnyguKeitykla.Entities.Books;
import com.agobal.KnyguKeitykla.Entities.UserData;
import com.agobal.KnyguKeitykla.Fragments.BookFragment;
import com.agobal.KnyguKeitykla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scalified.fab.ActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDetails extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";


    private TextView title;
    private ActionButton fab_star;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String about;
    private String BookKey;
    private String UserID;
    private Boolean isBookSaved;
    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvBookYear;
    private TextView tvBookCondition;
    private TextView tvBookCategory;
    private TextView tvBookAbout;
    private DatabaseReference mUserFavBookKey;

    private DatabaseReference mUserFavBookButton;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserFavBookDelete;
    private DatabaseReference mUserFavBooks;
    ProgressBar spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));

        ActionButton fab_message = findViewById(R.id.fb_message);
        fab_star = findViewById(R.id.fb_star);
        fab_message.setImageResource(R.drawable.ic_message_white_24dp);
        fab_star.setImageResource(R.drawable.ic_star_white_24dp);
        // To set button color for normal state:
        fab_message.setButtonColor(getResources().getColor(R.color.colorAccent));

        fab_star.setButtonColor(getResources().getColor(R.color.colorAccent));
        fab_star.setButtonColorPressed(getResources().getColor(R.color.fab_material_amber_500));


        mUserFavBookDelete = FirebaseDatabase.getInstance().getReference();

        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvBookYear= findViewById(R.id.tvBookYear);
        tvBookCondition= findViewById(R.id.tvBookCondition);
        tvBookCategory= findViewById(R.id.tvBookCategory);
        tvBookAbout= findViewById(R.id.tvBookAbout);

        // Use the book to populate the data into our views
        Books Book = (Books) getIntent().getSerializableExtra(BookFragment.BOOK_DETAIL_KEY);

        Log.d(TAG, "BookKey "+ BookKey);
        Log.d(TAG, "UserKey "+ UserID);

        loadBook(Book);
        loadUserInfo();

        loadFabButton();

        fab_message.setOnClickListener(view -> {
            Intent intent = new Intent(BookDetails.this, Chat_activity.class);
            intent.putExtra("user_id", UserID);
            intent.putExtra("user_name", userName);
            startActivity(intent);
        });

        fab_star.setOnClickListener(view -> {

            if(!isBookSaved) {
                fab_star.setState(ActionButton.State.PRESSED);
                isBookSaved=true;
                saveUserFavBook(Book);
                Toast.makeText(getApplicationContext(), "Knyga išsaugota!", Toast.LENGTH_LONG).show();

            }
            else {
                fab_star.setState(ActionButton.State.NORMAL);
                isBookSaved=false;
                deleteBookFromFav();
            }
        });

    }

    private void loadFabButton() {

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("UserFavBooks"))
                {
                    mUserFavBookButton = mDatabase.child("UserFavBooks");
                    mUserFavBookButton.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(current_uid))
                            {
                                mUserFavBookButton = mDatabase.child("UserFavBooks").child(current_uid);
                                mUserFavBookButton.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(BookKey))
                                        {
                                            isBookSaved = true;
                                            fab_star.setState(ActionButton.State.PRESSED);
                                        }
                                        else
                                        {
                                            fab_star.setState(ActionButton.State.NORMAL);
                                            isBookSaved = false;
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

                        }
                    });
                }
                isBookSaved=false;
                fab_star.setState(ActionButton.State.NORMAL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteBookFromFav() {
        Log.d(TAG, "UserFabBooks "+ mUserFavBookKey);
        mUserFavBookDelete.child("UserFavBooks").child(current_uid).child(BookKey).setValue(null);
        Log.d(TAG, "UserFabBooks1 "+ mUserFavBookKey);
        Toast.makeText(getApplicationContext(), "Knyga panaikinta!", Toast.LENGTH_LONG).show();

    }

    private void saveUserFavBook(Books Book) {

        Log.d(TAG,"currentID "+ current_uid);
        Log.d(TAG, "BookKey "+ BookKey);

        mUserFavBookKey = mDatabase.child("UserFavBooks").child(current_uid);
        mUserFavBooks = mDatabase.child("UserFavBooks").child(current_uid).child(BookKey);



        mUserFavBooks.child("bookName").setValue(Book.getBookName());
        mUserFavBooks.child("bookAuthor").setValue(Book.getBookAuthor());
        mUserFavBooks.child("bookAbout").setValue(Book.getBookAbout());
        mUserFavBooks.child("bookPublisher").setValue(Book.getBookPublisher());
        mUserFavBooks.child("bookCategory").setValue(Book.getBookCategory());
        mUserFavBooks.child("bookCondition").setValue(Book.getBookCondition());
        mUserFavBooks.child("bookYear").setValue(Book.getBookYear());
        mUserFavBooks.child("image").setValue(Book.getBookImage());
        mUserFavBooks.child("bookKey").setValue(Book.getBookID());
        mUserFavBooks.child("userID").setValue(Book.getUserID());

    }

    private void loadBook(Books Book) {
        //change activity title
//        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setTitleText("Prašome palaukti...");
//        pDialog.setCancelable(true);
//        pDialog.show();
        spinner.setVisibility(View.VISIBLE);


        title.setText(Book.getBookName());

        String imageURL = Book.getBookImage();
        Log.d(TAG, "bookDetailImageURL "+ imageURL);
        if(imageURL.startsWith("https://firebasestorage"))
        {
            Picasso.get().load(Book.getBookImage())
                    .rotate(90)
                    .resize(400,600)
                    .centerCrop()
                    .error(R.drawable.ic_nocover)
                    .into(ivBookCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        else
        {
            Picasso.get().load(Book.getBookImage())
                    //.rotate(90)
                    .resize(400,600)
                    .error(R.drawable.ic_nocover)
                    .centerCrop()
                    .into(ivBookCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
            Log.d(" else if", "yes");
        }

        // Populate data
        tvTitle.setText(Book.getBookName());
        tvAuthor.setText(Book.getBookAuthor());
        tvPublisher.setText("Leidykla: "+ Book.getBookPublisher());
        tvBookAbout.setText("Knygos aprašymas: "+ Book.getBookAbout());
        tvBookYear.setText("Išleidimo metai: " + Book.getBookYear());
        tvBookCondition.setText("Būklė: " + Book.getBookCondition());
        tvBookCategory.setText("Kategorija: " + Book.getBookCategory());

        UserID =Book.getUserID();
        BookKey = Book.getBookID();

    }

    private void loadUserInfo() {

        final TextView T_firstAndLastName = findViewById(R.id.firstAndLastName);
        final TextView T_Desc = findViewById(R.id.email);
        final TextView T_About = findViewById(R.id.about);
        final TextView T_City= findViewById(R.id.city);
        final CircleImageView ProfilePic = findViewById(R.id.profilePic);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserInfoInDetais = mDatabase.child("Users").child(UserID);

        Log.d(TAG, "UserID "+ mUserInfoInDetais);

        mUserInfoInDetais.keepSynced(true);

        mUserInfoInDetais.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("userName").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                firstName = dataSnapshot.child("firstName").getValue(String.class);
                lastName = dataSnapshot.child("lastName").getValue(String.class);
                String cityName = dataSnapshot.child("cityName").getValue(String.class);
                about = dataSnapshot.child("about").getValue(String.class);
                //String thumb_image = dataSnapshot.child("thumb_image").getValue(String.class);
                final String image = dataSnapshot.child("image").getValue(String.class);

                Log.d(TAG,"user info "+userName +" " + email + " " +firstName+" "+ lastName+ " "+cityName);
                Log.d(TAG,"Image "+ image);

                Picasso.get().load(image);

                if (image != null && !image.equals("default")) {
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.unknown_profile_pic).into(ProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.unknown_profile_pic).into(ProfilePic);
                        }

                    });
                }
                UserData userData = new UserData();

                userData.setFirstName(firstName);
                userData.setLastName(lastName);
                userData.setCityName(cityName);
                userData.setEmail(email);
                userData.setUserName(userName);
                Log.d(TAG,"user info: " + userName +" " + email + " " +firstName+" "+ lastName+ " "+cityName);

                T_firstAndLastName.setText(userData.firstName+" "+userData.lastName);
                T_Desc.setText(userData.email);
                T_City.setText(userData.cityName);
                T_About.setText(about);

                //hideDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }

    public void onBackPressed() {
        int backstack = getSupportFragmentManager().getBackStackEntryCount();

        if (backstack > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
