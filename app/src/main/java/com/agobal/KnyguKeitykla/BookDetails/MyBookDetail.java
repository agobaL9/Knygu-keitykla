package com.agobal.KnyguKeitykla.BookDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Books.MyBookEdit;
import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.Fragments.LibraryFragment;
import com.agobal.KnyguKeitykla.MainActivity;
import com.agobal.KnyguKeitykla.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyBookDetail extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvBookYear;
    private TextView tvBookCondition;
    private TextView tvBookCategory;
    private TextView title;

    private String BookName;
    private String BookAuthor;
    private String BookPublisher;
    private String BookPublishYear;
    private String BookCondition;
    private String BookCategory;
    private String BookKey;

    private String imageURL;

    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_detail);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));

        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvBookYear= findViewById(R.id.tvBookYear);
        tvBookCondition= findViewById(R.id.tvBookCondition);
        tvBookCategory= findViewById(R.id.tvBookCategory);

        FloatingActionButton fabEdit = findViewById(R.id.fb_edit);
        FloatingActionButton fabDelete = findViewById(R.id.fb_delete);

        fabEdit.setOnClickListener(view -> {
            Intent intent = new Intent(MyBookDetail.this,MyBookEdit.class);
            intent.putExtra("bookName", BookName);
            intent.putExtra("bookAuthor", BookAuthor);
            intent.putExtra("bookPublisher", BookPublisher);
            intent.putExtra("bookPublishYear", BookPublishYear);
            intent.putExtra("bookCondition", BookCondition);
            intent.putExtra("bookCategory", BookCategory);
            intent.putExtra("bookCover", imageURL);
            intent.putExtra("bookKey", BookKey);
            //intent.putExtra("bookPageCount", BookPageCount);
            startActivity(intent);
        });

        fabDelete.setOnClickListener(view -> new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Dėmėsio!")
                .setContentText("Ar tikrai norite ištrinti šią knygą?")
                .setConfirmText("Taip")
                .setCancelText("Ne")
                .setConfirmClickListener(sDialog -> {
                    deleteMyBook();
                    sDialog.dismissWithAnimation();
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Pavyko!")
                            .setContentText("Knyga panaikinta!")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(MyBookDetail.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .show();
                })
                .show());

        // Use the book to populate the data into our views
        MyBook myBook = (MyBook) getIntent().getSerializableExtra(LibraryFragment.MY_BOOK_DETAIL_KEY);
        BookKey = getIntent().getStringExtra("BOOK_KEY");
        loadBook(myBook);

    }

    private void loadBook(MyBook myBook) {
        //change activity title
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti...");
        pDialog.setCancelable(false);
        pDialog.show();

        title.setText(myBook.getBookName());
        // Populate data
        imageURL= myBook.getBookImage();
        if(imageURL.startsWith("https://firebasestorage"))
        {
            Picasso.get().load(myBook.getBookImage())
                    .rotate(90)
                    .resize(400,600)
                    .centerCrop()
                    .error(R.drawable.ic_nocover)
                    .into(ivBookCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            pDialog.dismissWithAnimation();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        else
        {
            Picasso.get().load(myBook.getBookImage())
                    //.rotate(90)
                    .resize(400,600)
                    .error(R.drawable.ic_nocover)
                    .centerCrop()
                    .into(ivBookCover, new Callback() {
                        @Override
                        public void onSuccess() {
                            pDialog.dismissWithAnimation();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        tvTitle.setText(myBook.getBookName());
        tvAuthor.setText(myBook.getBookAuthor());
        tvPublisher.setText(myBook.getBookPublisher());
        tvBookYear.setText("Išleidimo metai: " + myBook.getBookYear());
        tvBookCondition.setText("Būklė: " + myBook.getBookCondition());
        tvBookCategory.setText("Kategorija: " + myBook.getBookCategory());

        BookName = tvTitle.getText().toString().trim();
        BookAuthor = tvAuthor.getText().toString().trim();
        BookPublisher = tvPublisher.getText().toString().trim();
        BookPublishYear = tvBookYear.getText().toString().trim();
        BookCondition = tvBookCondition.getText().toString().trim();
        BookCategory = tvBookCategory.getText().toString().trim();

    }

    private void deleteMyBook() {

        //DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        DatabaseReference mUserBookDelete = FirebaseDatabase.getInstance().getReference().child("UserBooks").child(current_uid).child(BookKey);
        Log.d("TAG"," "+ BookKey);
        mUserBookDelete.setValue(null);
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
