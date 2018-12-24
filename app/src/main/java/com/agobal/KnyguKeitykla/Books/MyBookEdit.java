package com.agobal.KnyguKeitykla.Books;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyBookEdit extends AppCompatActivity {

    private StorageReference mImageStorage;
    private DatabaseReference mBookDatabase;
    DatabaseReference mUserDatabase;
    DatabaseReference mUserBookDatabase;

    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    EditText etBookName;
    EditText etBookAuthor;
    EditText etBookAbout;
    EditText etPublisher;
    Spinner spinCategory;
    RadioButton rbBookNew;
    RadioButton rbBookGood;
    RadioButton rbBookFair;
    Button btnYear;
    Button btnSave;
    RadioGroup radioGroup;
    Switch switchButton;

    ImageView ivPickedImage;
    TextView title;

    int BookYear;
    String key;
    String download_url;
    Boolean isPhotoSelected= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_edit);

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("Knygos redagavimas");

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        etBookName = findViewById(R.id.etBookName);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookAbout = findViewById(R.id.etBookAbout);
        etPublisher = findViewById(R.id.etPublisher);
        spinCategory = findViewById(R.id.spinCategory);
        radioGroup = findViewById(R.id.rbGroup);
        rbBookNew = findViewById(R.id.rbBookNew);
        rbBookGood = findViewById(R.id.rbBookGood);
        rbBookFair = findViewById(R.id.rbBookFair);
        switchButton = findViewById(R.id.switchButton);
        switchButton.setChecked(true);
        btnSave = findViewById(R.id.btnSave);
        btnYear = findViewById(R.id.btnYear);

        ivPickedImage = findViewById(R.id.ivPicedImage);

        FloatingActionButton fabCamera = findViewById(R.id.fab_pick_camera);
        FloatingActionButton fabGallery = findViewById(R.id.fab_pick_gallery);

        fabCamera.setOnClickListener(view -> pickImageFromSource(Sources.CAMERA));
        fabGallery.setOnClickListener(view -> pickImageFromSource(Sources.GALLERY));

        btnYear.setOnClickListener(view -> selectYear());
        btnSave.setOnClickListener(view -> saveBook());

        selectCategory();

    }

    void saveBook()
    {

    }

    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source) {
        RxImagePicker.with(getFragmentManager()).requestImage(source).flatMap(uri -> {

            key = mBookDatabase.push().getKey();

            final StorageReference filepath = mImageStorage.child("book_images").child(key + ".jpg");

            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            filepath.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl()
                            .addOnSuccessListener(uri1 ->
                                    download_url = uri1.toString()));

            return Observable.just(uri);

        })
                .subscribe(this::onImagePicked, throwable -> Toast.makeText(MyBookEdit.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show());

        isPhotoSelected=true;
    }

    private void onImagePicked(Object result)
    {
        //Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap)
        {
            ivPickedImage.setImageBitmap((Bitmap) result);
        }
        else
        {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .transition(withCrossFade())
                    .apply(new RequestOptions().centerCrop())
                    .into(ivPickedImage);
        }
    }

    private void selectCategory() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseCategory = database.getReference("Category");
        mDatabaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> categories = new ArrayList<>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("categoryName").getValue(String.class);
                    categories.add(areaName);
                }
                Spinner categorySpinner = findViewById(R.id.spinCategory);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(MyBookEdit.this, android.R.layout.simple_spinner_item, categories);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void selectYear()
    {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("Metai");
        d.setMessage("Knygos išleidimo metai");
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(2019);
        numberPicker.setMinValue(1900);
        numberPicker.setValue(2000);
        numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnValueChangedListener((numberPicker1, i, i1) -> Log.d("TAG", "onValueChange: "));

        d.setPositiveButton("Done", (dialogInterface, i) -> {
            Log.d("TAG1", "onClick: " + numberPicker.getValue());
            BookYear = numberPicker.getValue();

            btnYear.setText("Pasirinkti metai: "+BookYear);

        });


        d.setNegativeButton("Cancel", (dialogInterface, i) -> {
        });

        AlertDialog alertDialog = d.create();
        alertDialog.show();


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
