package com.agobal.KnyguKeitykla.Books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.MainActivity;
import com.agobal.KnyguKeitykla.R;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;


public class AddNewBook extends AppCompatActivity {

    private static final String TAG = "AddNewBookActivity";

    private StorageReference mImageStorage;
    private DatabaseReference mBookDatabase;
    private DatabaseReference mUserBookDatabase;
    private DatabaseReference mUserDatabase;

    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    private EditText etBookName;
    private EditText etBookAuthor;
    private EditText etBookAbout;
    private EditText etPublisher;
    private Spinner spinCategory;
    private Button btnYear;
    private RadioGroup radioGroup;
    private CheckBox cbMoney;
    private CheckBox cbBook;
    //Switch switchButton;

    private ImageView ivPickedImage;

    private int BookYear;
    private String key;
    private String download_url;
    private Boolean isPhotoSelected= false;
    private String cityName;
    private String CheckedChange;
    //private String CheckedMoney;
    //private String CheckedBoth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_book);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("Pridėti naują knygą");

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("cityName");
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cityName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });

        etBookName = findViewById(R.id.etBookName);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookAbout = findViewById(R.id.etBookAbout);
        etPublisher = findViewById(R.id.etPublisher);
        spinCategory = findViewById(R.id.spinCategory);
        radioGroup = findViewById(R.id.rbGroup);
        //RadioButton rbBookNew = findViewById(R.id.rbBookNew);
        //RadioButton rbBookGood = findViewById(R.id.rbBookGood);
        //RadioButton rbBookFair = findViewById(R.id.rbBookFair);
        cbMoney = findViewById(R.id.cbMoney);
        cbBook = findViewById(R.id.cbBook);
        Button btnSave = findViewById(R.id.btnSave);
        btnYear = findViewById(R.id.btnYear);

        ivPickedImage = findViewById(R.id.ivPickedImage);

        FloatingActionButton fabCamera = findViewById(R.id.fab_pick_camera);
        FloatingActionButton fabGallery = findViewById(R.id.fab_pick_gallery);

        fabCamera.setOnClickListener(view -> pickImageFromSource(Sources.CAMERA));
        fabGallery.setOnClickListener(view -> pickImageFromSource(Sources.GALLERY));

        btnYear.setOnClickListener(view -> selectYear());
        btnSave.setOnClickListener(view -> saveBook());

        selectCategory();

    }


    @SuppressWarnings({"ResultOfMethodCallIgnored", "deprecation"})
    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources source) {
        RxImagePicker.with(getFragmentManager()).requestImage(source).flatMap(uri -> {

                    key = mBookDatabase.push().getKey();

                    final StorageReference filepath = mImageStorage.child("book_images").child(key + ".jpg");

                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] data = baos.toByteArray();

                    filepath.putBytes(data).addOnSuccessListener(taskSnapshot ->
                            filepath.getDownloadUrl().addOnSuccessListener(uri1 ->
                                    download_url = uri1.toString()));

                            return Observable.just(uri);

                })
                .subscribe(this::onImagePicked, throwable -> Toast.makeText(AddNewBook.this, String.format("Error: %s", throwable), Toast.LENGTH_LONG).show());

        isPhotoSelected=true;
    }

    private void onImagePicked(Object result)
    {
        //Toast.makeText(this, String.format("Result: %s", result), Toast.LENGTH_LONG).show();
        if (result instanceof Bitmap)
        {
            ivPickedImage.setImageBitmap((Bitmap) result);
            Log.d(TAG, "instance taip");
        }
        else
        {
            Log.d(TAG, "instace1 taip");
            Picasso.get().load(result.toString())
                    .rotate(90)
                    //.fit()
                    .resize(ivPickedImage.getMeasuredWidth(),ivPickedImage.getMeasuredHeight())
                    .centerCrop()
                    .error(R.drawable.ic_nocover)
                    .into(ivPickedImage);
        }
    }

    private void selectCategory() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseCategory = database.getReference("Category");
        mDatabaseCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        String BookCategory = spinCategory.getSelectedItem().toString();
        String BookPublisher = etPublisher.getText().toString().trim();

        if(BookYear==0)
        {
            Toast.makeText(getApplicationContext(), "Pasirinkite knygos išleidimo metus!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isPhotoSelected) {
            Toast.makeText(getApplicationContext(), "Pasirinkite nuotrauką!",
                    Toast.LENGTH_LONG).show();
            return;
        }


        if(TextUtils.isEmpty(BookName)) {
            etBookName.setError("Knygos pavadinimas negali būti tuščias!");
            return;
        }
        if(TextUtils.isEmpty(BookPublisher)) {
            etPublisher.setError("Knygos leidyklos laukas negali būti tuščias!");
            return;
        }

        if(TextUtils.isEmpty(BookAuthor)) {
            etBookAuthor.setError("Knygos autorius negali būti tuščias!");
            return;
        }

        if (radioGroup.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(getApplicationContext(), "Turite pasirinkti knygos būklę!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String bookCondition = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

        if (!cbBook.isChecked() && !cbMoney.isChecked()){
            Toast.makeText(getApplicationContext(), "Turite pasirinkti į ką norite keisti knygą!",
                    Toast.LENGTH_LONG).show();
            return;
        }


        if (cbMoney.isChecked()) {
            CheckedChange = "Domina keitimas į pinigus";
        }
        if (cbBook.isChecked()) {
            CheckedChange = "Domina keitimas į knygą";
        }
        if (cbBook.isChecked() && cbMoney.isChecked())
        {
            CheckedChange = "Domina keitimas ir į knygas ir į pinigus";
        }

        mBookDatabase.child(key).child("bookName").setValue(BookName);
        mBookDatabase.child(key).child("bookAuthor").setValue(BookAuthor);
        mBookDatabase.child(key).child("bookAbout").setValue(BookAbout);
        mBookDatabase.child(key).child("bookPublisher").setValue(BookPublisher);
        mBookDatabase.child(key).child("bookCategory").setValue(BookCategory);
        mBookDatabase.child(key).child("bookCondition").setValue(bookCondition);
        mBookDatabase.child(key).child("bookYear").setValue(BookYear);
        mBookDatabase.child(key).child("image").setValue(download_url);
        mBookDatabase.child(key).child("bookChange").setValue(CheckedChange);

        mUserBookDatabase.child(current_uid).child(key).child("bookName").setValue(BookName);
        mUserBookDatabase.child(current_uid).child(key).child("bookAuthor").setValue(BookAuthor);
        mUserBookDatabase.child(current_uid).child(key).child("bookAbout").setValue(BookAbout);
        mUserBookDatabase.child(current_uid).child(key).child("bookPublisher").setValue(BookPublisher);
        mUserBookDatabase.child(current_uid).child(key).child("bookCategory").setValue(BookCategory);
        mUserBookDatabase.child(current_uid).child(key).child("bookCondition").setValue(bookCondition);
        mUserBookDatabase.child(current_uid).child(key).child("bookYear").setValue(BookYear);
        mUserBookDatabase.child(current_uid).child(key).child("image").setValue(download_url);

        mUserBookDatabase.child(current_uid).child(key).child("userID").setValue(current_uid);
        mUserBookDatabase.child(current_uid).child(key).child("bookKey").setValue(key);

        mUserBookDatabase.child(current_uid).child(key).child("bookCity").setValue(cityName);

        mUserBookDatabase.child(current_uid).child(key).child("bookChange").setValue(CheckedChange);


        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Pavyko!")
                .setContentText("Knygą pridėta!")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent(AddNewBook.this, MainActivity.class);
                    startActivity(intent);
                })
                .show();

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
            Log.d(TAG, "onClick: " + numberPicker.getValue());
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
        else
        {
            super.onBackPressed();
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
