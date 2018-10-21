package com.agobal.KnyguKeitykla.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;


public class AddNewBook extends AppCompatActivity {

    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mBookDatabase;

    //ImageButton ivBookCover;
    EditText etBookName;
    EditText etBookAuthor;
    EditText etBookAbout;
    Spinner spinCategory;
    RadioButton rbBookNew;
    RadioButton rbBookGood;
    RadioButton rbBookFair;
    Button btnYear;
    Button btnSave;
    RadioGroup radioGroup;

    ImageView ivPickedImage;

    int BookYear;
    String key;
    String download_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_book);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");

        etBookName = findViewById(R.id.etBookName);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookAbout = findViewById(R.id.etBookAbout);
        spinCategory = findViewById(R.id.spinCategory);
        radioGroup = findViewById(R.id.rbGroup);
        rbBookNew = findViewById(R.id.rbBookNew);
        rbBookGood = findViewById(R.id.rbBookGood);
        rbBookFair = findViewById(R.id.rbBookFair);
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


    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source) {
        RxImagePicker.with(getFragmentManager()).requestImage(source)
                .flatMap(uri -> {

                    //TODO: reikia gauti key
                    key = mBookDatabase.push().getKey();

                    final StorageReference filepath = mImageStorage.child("book_images").child(key + ".jpg");

                    filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            String download_url = uri1.toString();
                            mBookDatabase.child(key).child("image").setValue(download_url);
                        }

                    }));

                            return Observable.just(uri);


                })
                .subscribe(this::onImagePicked, throwable -> Toast.makeText(AddNewBook.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show());





    }

    private void onImagePicked(Object result) {
        Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap) {
            ivPickedImage.setImageBitmap((Bitmap) result);


        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .transition(withCrossFade())
                    .into(ivPickedImage);
        }
    }

    private void selectCategory() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = database.getReference("Category");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(AddNewBook.this, android.R.layout.simple_spinner_item, categories);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveBook() {

        String BookName = etBookName.getText().toString().trim();
        String BookAuthor = etBookAuthor.getText().toString().trim();
        String BookAbout = etBookAbout.getText().toString().trim();

        String CategoryBook = spinCategory.getSelectedItem().toString();


        String bookCondition = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

        mBookDatabase.child(Objects.requireNonNull(key)).child("bookName").setValue(BookName);
        mBookDatabase.child(key).child("bookAuthor").setValue(BookAuthor);
        mBookDatabase.child(key).child("bookAbout").setValue(BookAbout);
        mBookDatabase.child(key).child("categoryBook").setValue(CategoryBook);
        mBookDatabase.child(key).child("bookCondition").setValue(bookCondition);

        mBookDatabase.child(key).child("bookYear").setValue(BookYear);

        //final StorageReference filepath = mImageStorage.child("book_images").child(key + ".jpg");


        //mBookDatabase.child(key).child("image").setValue(download_url);




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
        });


        d.setNegativeButton("Cancel", (dialogInterface, i) -> {
        });

        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

}
