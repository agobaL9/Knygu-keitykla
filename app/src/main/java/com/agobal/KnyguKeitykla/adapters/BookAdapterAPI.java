package com.agobal.KnyguKeitykla.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.BookAPI;
import com.agobal.KnyguKeitykla.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class BookAdapterAPI extends ArrayAdapter<BookAPI> {

    // View lookup cache
    private static class ViewHolder {
        ImageView ivCover;
        public TextView tvTitle;
        public TextView tvAuthor;
    }

    public BookAdapterAPI(Context context, ArrayList<BookAPI> aBookAPIS) {
        super(context, 0, aBookAPIS);
    }

    // Translates a particular `BookAPI` given a position
    // into a relevant row within an AdapterView
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final BookAPI bookAPI = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.item_book, parent, false);
            viewHolder.ivCover = convertView.findViewById(R.id.ivBookCover);
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tvAuthor);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tvTitle.setText(bookAPI.getTitle());
        viewHolder.tvAuthor.setText(bookAPI.getAuthor());
        Picasso.get().load(Uri.parse(bookAPI.getCoverUrl())).error(R.drawable.ic_nocover).into(viewHolder.ivCover);
        // Return the completed view to render on screen
        return convertView;
    }

    public void add(String bookName) {


    }
}