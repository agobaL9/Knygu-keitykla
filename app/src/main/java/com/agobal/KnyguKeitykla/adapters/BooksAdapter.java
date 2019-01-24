package com.agobal.KnyguKeitykla.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.Books;
import com.agobal.KnyguKeitykla.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends ArrayAdapter<Books> {

        private final Context mContext;
        private final List<Books> allBookList;

        public BooksAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Books> list) {

            super(context, 0 , list);
            mContext = context;
            allBookList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.item_allbooks,parent,false);

            Books currentBook = allBookList.get(position);

            ImageView image = listItem.findViewById(R.id.ivBookCover);

            String imageURL = currentBook.getBookImage();
            if(imageURL.startsWith("https://firebasestorage"))
            {
                Picasso.get().load(currentBook.getBookImage())
                        .rotate(90)
                        .resize(200,200)
                        .centerCrop()
                        .error(R.drawable.ic_nocover)
                        .into(image);
            }
            else
            {
                Picasso.get().load(currentBook.getBookImage())
                        //.rotate(90)
                        .resize(200,200)
                        .error(R.drawable.ic_nocover)
                        .centerCrop()
                        .into(image);
            }

            TextView name = listItem.findViewById(R.id.tvTitle);
            name.setText(currentBook.getBookName());

            TextView author = listItem.findViewById(R.id.tvAuthor);
            author.setText(currentBook.getBookAuthor());

            return listItem;
        }
    }


