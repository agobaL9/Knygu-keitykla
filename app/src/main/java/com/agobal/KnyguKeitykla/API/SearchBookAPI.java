package com.agobal.KnyguKeitykla.API;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.BookAPI;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.Books.AddNewBook;
import com.agobal.KnyguKeitykla.BookDetails.BookDetailActivityAPI;
import com.agobal.KnyguKeitykla.adapters.BookAdapterAPI;
import com.agobal.KnyguKeitykla.helper.BookClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cn.pedant.SweetAlert.SweetAlertDialog;


import java.util.ArrayList;

public class SearchBookAPI extends AppCompatActivity {

    public static final String BOOK_DETAIL_KEY = "book";
    private BookAdapterAPI bookAdapterAPI;
    private ListView lvBooks;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("Paieška");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lvBooks = findViewById(R.id.listViewBooks);
        ArrayList<BookAPI> aBookAPIS = new ArrayList<>();
        bookAdapterAPI = new BookAdapterAPI(this, aBookAPIS);
        lvBooks.setAdapter(bookAdapterAPI);
        fetchBooks("Lord of");

        setupBookSelectedListener();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(SearchBookAPI.this, AddNewBook.class);
            startActivity(intent);
        });
    }

    public void setupBookSelectedListener() {
        lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(SearchBookAPI.this, BookDetailActivityAPI.class);
            intent.putExtra(BOOK_DETAIL_KEY, bookAdapterAPI.getItem(position));
            startActivity(intent);
        });
    }

    private void fetchBooks(String query) {

        SweetAlertDialog pDialog = new SweetAlertDialog(SearchBookAPI.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        BookClient client = new BookClient();
        client.getBooks(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    pDialog.dismissWithAnimation();

                    JSONArray docs = null;
                    if(response != null) {
                        // Get the docs json array
                        docs = response.getJSONArray("docs");
                        // Parse json array into array of model objects
                        final ArrayList<BookAPI> bookAPIS = BookAPI.fromJson(docs);
                        // Remove all bookAPIS from the adapter
                        bookAdapterAPI.clear();
                        // Load model objects into the adapter
                        for (BookAPI bookAPI : bookAPIS) {
                            bookAdapterAPI.add(bookAPI); // add bookAPI through the adapter
                        }
                        bookAdapterAPI.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pDialog.dismissWithAnimation();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_api_toolbar, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                fetchBooks(query);
                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                // Set activity title to search query
                SearchBookAPI.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }


    @Override
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
