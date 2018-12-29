package com.agobal.KnyguKeitykla.adapters;

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

import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyBookAdapter extends ArrayAdapter<MyBook> {

    private Context mContext;
    private List<MyBook> myBookList;

    public MyBookAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<MyBook> list) {

        super(context, 0 , list);
        mContext = context;
        myBookList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_mybook,parent,false);

        MyBook currentBook = myBookList.get(position);

        ImageView image = listItem.findViewById(R.id.ivBookCover);
        Log.d("imageUrl", currentBook.getBookImage());
        String imageURL = currentBook.getBookImage();

        if(imageURL.startsWith("https://firebasestorage"))
        {
            Picasso.get().load(currentBook.getBookImage())
                    .rotate(90)
                    .error(R.drawable.ic_nocover)
                    .resize(200,200)
                    .centerCrop()
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

        Switch switchButton = listItem.findViewById(R.id.MyswitchButton);

        Log.d("Tradable", " "+ currentBook.getBookTradable());

/*
        if(currentBook.getBookTradable().equals("true"))
            switchButton.setChecked(true);

        else if (currentBook.getBookTradable().equals(""))
            Log.d("Tradable", " "+ currentBook.getBookTradable());
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
