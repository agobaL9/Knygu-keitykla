package com.agobal.KnyguKeitykla.BookDetails;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scalified.fab.ActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookDetails extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvBookYear;
    private TextView tvBookCondition;
    private TextView tvBookCategory;
    private TextView tvBookAbout;
    TextView title;

    ActionButton fab_message;
    ActionButton fab_star;

    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserFavBooks;
    private DatabaseReference mUserFavBookKey;
    private DatabaseReference mBookDatabase;
    private DatabaseReference mUserBookDatabase;
    private DatabaseReference mUserFavBookButton;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserFavBookDelete;

    String userName;
    String email;
    String firstName;
    String lastName;
    String cityName;
    String about;
    String BookKey;
    String UserID;
    String imageURL;

    //String FavBookKey;

    Boolean isBookSaved;

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));

        fab_message = findViewById(R.id.fb_message);
        fab_star = findViewById(R.id.fb_star);
        fab_message.setImageResource(R.drawable.ic_message_white_24dp);
        fab_star.setImageResource(R.drawable.ic_star_white_24dp);
        // To set button color for normal state:
        fab_message.setButtonColor(getResources().getColor(R.color.colorAccent));

        fab_star.setButtonColor(getResources().getColor(R.color.colorAccent));
        fab_star.setButtonColorPressed(getResources().getColor(R.color.fab_material_amber_500));

        mImageStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
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
        //BookKey = getIntent().getStringExtra("BOOK_KEY");

        Log.d("BookKey", " "+ BookKey);
        Log.d("UserKey", " "+ UserID);

        loadBook(Book);
        loadUserInfo();

        loadFabButton();

        fab_message.setOnClickListener(view -> {
            //Intent intent = new Intent(MyBookDetail.this, test.class);
            //startActivity(intent);
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
        Log.d("UserFabBooks", " "+ mUserFavBookKey);
        mUserFavBookDelete.child("UserFavBooks").child(current_uid).child(BookKey).setValue(null);
        Log.d("UserFabBooks1", " "+ mUserFavBookKey);
        Toast.makeText(getApplicationContext(), "Knyga panaikinta!", Toast.LENGTH_LONG).show();

    }

    private void saveUserFavBook(Books Book) {

        mUserFavBookKey = mDatabase.child("UserFavBooks").child(current_uid);
        //FavBookKey = mUserFavBookKey.push().getKey(); // TODO: reikia kad butu knygos id o ne random
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
        title.setText(Book.getBookName());

        imageURL= Book.getBookImage();
        Log.d("bookDetailImageURL", " "+imageURL);
        if(imageURL.startsWith("https://firebasestorage"))
        {
            Picasso.get().load(Book.getBookImage())
                    .rotate(90)
                    .resize(400,600)
                    .centerCrop()
                    .error(R.drawable.ic_nocover)
                    .into(ivBookCover);
        }
        else
        {
            Picasso.get().load(Book.getBookImage())
                    //.rotate(90)
                    .resize(400,600)
                    .error(R.drawable.ic_nocover)
                    .centerCrop()
                    .into(ivBookCover);
            Log.d(" else if", "yes");
        }

        // Populate data
        //Picasso.get().load(Uri.parse(Book.getBookImage())).error(R.drawable.ic_nocover).rotate(90).resize(400,600).centerCrop().into(ivBookCover);
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

        Log.d("UserID", " "+ mUserInfoInDetais);

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
                String thumb_image = dataSnapshot.child("thumb_image").getValue(String.class);
                final String image = dataSnapshot.child("image").getValue(String.class);

                Log.d("user info", userName +" " + email + " " +firstName+" "+ lastName+ " "+cityName);
                Log.d( "Image"," "+ image);

                Picasso.get().load(image);

                if(!image.equals("default")){
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
                Log.d("user info: ", userName +" " + email + " " +firstName+" "+ lastName+ " "+cityName);

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
            //System.exit(0);
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
