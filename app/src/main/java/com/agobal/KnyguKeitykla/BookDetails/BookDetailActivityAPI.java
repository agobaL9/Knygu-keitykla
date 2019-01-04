package com.agobal.KnyguKeitykla.BookDetails;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.API.SearchBookAPI;
import com.agobal.KnyguKeitykla.API.BookEditAPI;
import com.agobal.KnyguKeitykla.Entities.BookAPI;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.BookClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class BookDetailActivityAPI extends AppCompatActivity {

    public static final String BOOK_DETAIL_KEY = "book";

    private StorageReference mImageStorage;
    private DatabaseReference mBookDatabase;
    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    private BookClient client;
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    //private TextView tvPageCount;
    private TextView tvPublishYear;
    String imageURL;
    TextView title;
    Button btnSaveAndEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_api);

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        //tvPageCount = findViewById(R.id.tvPageCount);
        tvPublishYear = findViewById(R.id.tvPublishYear);

        btnSaveAndEdit = findViewById(R.id.btnSaveAndEdit);

        // Use the bookAPI to populate the data into our views
        BookAPI bookAPI = (BookAPI) getIntent().getSerializableExtra(SearchBookAPI.BOOK_DETAIL_KEY);
        loadBook(bookAPI);

        btnSaveAndEdit.setOnClickListener(view -> {
            saveAndEdit();
        });

    }

    private void saveAndEdit() {

        String BookName = tvTitle.getText().toString().trim();
        String BookAuthor = tvAuthor.getText().toString().trim();
        String BookPublisher = tvPublisher.getText().toString().trim();
        String BookPublishYear = tvPublishYear.getText().toString().trim();
        //String BookPageCount = tvPageCount.getText().toString().trim();

        Log.d("saveAndEdit image"," " + imageURL);




        Intent intent = new Intent(BookDetailActivityAPI.this,BookEditAPI.class);
        intent.putExtra("bookName", BookName);
        intent.putExtra("bookAuthor", BookAuthor);
        intent.putExtra("bookPublisher", BookPublisher);
        intent.putExtra("bookPublishYear", BookPublishYear);

        intent.putExtra("bookCover", imageURL);
        //intent.putExtra("bookPageCount", BookPageCount);
        startActivity(intent);

    }

    private void loadBook(BookAPI bookAPI) {
        //change activity title
        title.setText(bookAPI.getTitle());
        // Populate data
        Picasso.get().load(Uri.parse(bookAPI.getLargeCoverUrl())).error(R.drawable.ic_nocover).into(ivBookCover);
        //Picasso.get().load(Uri.parse(bookAPI.getLargeCoverUrl())).into(imageURL);
        imageURL = bookAPI.getLargeCoverUrl();

        Log.d("image"," " + imageURL);
        tvTitle.setText(bookAPI.getTitle());
        tvAuthor.setText(bookAPI.getAuthor());
        // fetch extra bookAPI data from books API
        client = new BookClient();
        client.getExtraBookDetails(bookAPI.getOpenLibraryId(),new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.has("publishers")) {
                        // display comma separated list of publishers
                        final JSONArray publisher = response.getJSONArray("publishers");
                        final int numPublishers = publisher.length();
                        final String[] publishers = new String[numPublishers];
                        for (int i = 0; i < numPublishers; ++i) {
                            publishers[i] = publisher.getString(i);
                        }
                        tvPublisher.setText(TextUtils.join(", ", publishers));
                    }
                    /*
                    if (response.has("number_of_pages")) {
                        tvPageCount.setText("Puslapių skaičius: "+Integer.toString(response.getInt("number_of_pages")));
                    }
                    else
                        tvPageCount.setText("Puslapių skaičius: - ");
                    */
                    if(response.has("publish_date"))
                    {
                        tvPublishYear.setText("Išleidimo metai: "+Integer.toString(response.getInt("publish_date")));
                    }
                    else
                        tvPublishYear.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
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
