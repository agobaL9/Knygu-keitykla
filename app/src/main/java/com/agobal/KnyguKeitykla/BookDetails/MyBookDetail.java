package com.agobal.KnyguKeitykla.BookDetails;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.Fragments.LibraryFragment;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.BookClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MyBookDetail extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvBookYear;
    private TextView tvBookCondition;
    private TextView tvBookCategory;

    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_detail);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        /*
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
*/
        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvBookYear= findViewById(R.id.tvBookYear);
        tvBookCondition= findViewById(R.id.tvBookCondition);
        tvBookCategory= findViewById(R.id.tvBookCategory);

        // Use the book to populate the data into our views
        MyBook myBook = (MyBook) getIntent().getSerializableExtra(LibraryFragment.MY_BOOK_DETAIL_KEY);
        loadBook(myBook);

    }

    private void loadBook(MyBook myBook) {
        //change activity title
        title.setText(myBook.getBookName());
        // Populate data
        Picasso.get().load(Uri.parse(myBook.getBookImage())).error(R.drawable.ic_nocover).rotate(90).resize(400,600).centerCrop().into(ivBookCover);
        tvTitle.setText(myBook.getBookName());
        tvAuthor.setText(myBook.getBookAuthor());
        tvPublisher.setText("Leidykla: "+ myBook.getBookPublisher());
        tvBookYear.setText("Išleidimo metai: " + myBook.getBookYear());
        tvBookCondition.setText("Būklė: " + myBook.getBookCondition());
        tvBookCategory.setText("Kategorija: " + myBook.getBookCategory());

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
