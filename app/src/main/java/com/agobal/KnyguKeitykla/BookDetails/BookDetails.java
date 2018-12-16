package com.agobal.KnyguKeitykla.BookDetails;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.Books;
import com.agobal.KnyguKeitykla.Fragments.BookFragment;
import com.agobal.KnyguKeitykla.R;
import com.squareup.picasso.Picasso;

public class BookDetails extends AppCompatActivity {

    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPublisher;
    private TextView tvPageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

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
        // Use the book to populate the data into our views
        Books Book = (Books) getIntent().getSerializableExtra(BookFragment.BOOK_DETAIL_KEY);
        loadBook(Book);

    }

    private void loadBook(Books Book) {
        //change activity title
        this.setTitle(Book.getBookName());
        // Populate data
        Picasso.get().load(Uri.parse(Book.getBookImage())).error(R.drawable.ic_nocover).rotate(90).resize(400,600).centerCrop().into(ivBookCover);
        tvTitle.setText(Book.getBookName());
        tvAuthor.setText(Book.getBookAuthor());

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
