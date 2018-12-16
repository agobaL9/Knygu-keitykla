package com.agobal.KnyguKeitykla.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.BookAPI;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.helper.BookClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BookDetailActivityAPI extends AppCompatActivity {

    private BookClient client;
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPageCount;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_api);

            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            // Fetch views
            ivBookCover = findViewById(R.id.ivBookCover);
            tvTitle = findViewById(R.id.tvTitle);
            tvAuthor = findViewById(R.id.tvAuthor);
            tvPublisher = findViewById(R.id.tvPublisher);
            tvPageCount = findViewById(R.id.tvPageCount);
            // Use the bookAPI to populate the data into our views
            BookAPI bookAPI = (BookAPI) getIntent().getSerializableExtra(SearchBookAPI.BOOK_DETAIL_KEY);
            loadBook(bookAPI);

    }

    private void loadBook(BookAPI bookAPI) {
        //change activity title
        this.setTitle(bookAPI.getTitle());
        // Populate data
        Picasso.get().load(Uri.parse(bookAPI.getLargeCoverUrl())).error(R.drawable.ic_nocover).into(ivBookCover);
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
                    if (response.has("number_of_pages")) {
                        tvPageCount.setText(Integer.toString(response.getInt("number_of_pages")) + " pages");
                    }
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
