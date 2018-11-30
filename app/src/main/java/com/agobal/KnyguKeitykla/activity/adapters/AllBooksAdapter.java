package com.agobal.KnyguKeitykla.activity.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.AllBooks;
import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

    public class AllBooksAdapter extends ArrayAdapter<AllBooks> {

        private Context mContext;
        private List<AllBooks> allBookList;

        public AllBooksAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<AllBooks> list) {

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

            AllBooks currentBook = allBookList.get(position);

            ImageView image = listItem.findViewById(R.id.ivBookCover);

            Picasso.get().load(currentBook.getBookImage())
                    .rotate(90)
                    .resize(200,200)
                    .centerCrop()
                    .into(image);

            TextView name = listItem.findViewById(R.id.tvTitle);
            name.setText(currentBook.getBookName());

            TextView author = listItem.findViewById(R.id.tvAuthor);
            author.setText(currentBook.getBookAuthor());

/*
            Switch switchButton = listItem.findViewById(R.id.switchButton);

            if(currentBook.getBookTradable().equals("true"))
                switchButton.setChecked(true);
            else
                switchButton.setChecked(false);

            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked) {
                    Log.d("isChecked/?", "YES");
                }
                else
                    Log.d("isChecked/?", "NO");

            });
*/

            return listItem;
        }
    }


