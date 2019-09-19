package com.agobal.KnyguKeitykla.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.Entities.MyBook;
import com.agobal.KnyguKeitykla.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class MyBookAdapter extends ArrayAdapter<MyBook> {

    private static final String TAG = "MyBookAdapterActivity";


    private final Context mContext;
    private final List<MyBook> myBookList;

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
        Log.d(TAG, "imageUrl " + currentBook.getBookImage());
        String imageURL = currentBook.getBookImage();

        if(imageURL.startsWith("https://firebasestorage"))
        {
            Glide.with(getContext())
                    .load(currentBook.getBookImage())
                    .apply(new RequestOptions().error(R.drawable.ic_nocover).transform(new RotateTransformation(this,90f)))
                    .into(image);
            /*
            Picasso.get().load(currentBook.getBookImage())
                    .rotate(90)
                    .error(R.drawable.ic_nocover)
                    .resize(200,200)
                    .centerCrop()
                    .into(image);
                    */
        }
        else
        {
            Glide.with(getContext())
                    .load(currentBook.getBookImage())
                    .apply(new RequestOptions().error(R.drawable.ic_nocover))
                    .into(image);
            /*
            Picasso.get().load(currentBook.getBookImage())
                    //.rotate(90)
                    .resize(200,200)
                    .error(R.drawable.ic_nocover)
                    .centerCrop()
                    .into(image);
                    */
        }

        TextView name = listItem.findViewById(R.id.tvTitle);
        name.setText(currentBook.getBookName());

        TextView author = listItem.findViewById(R.id.tvAuthor);
        author.setText(currentBook.getBookAuthor());

        Log.d(TAG, "Tradable "+ currentBook.getBookTradable());

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

    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        RotateTransformation(MyBookAdapter context, float rotateRotationAngle) {
            super();

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

}
