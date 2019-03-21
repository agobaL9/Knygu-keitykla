package com.agobal.KnyguKeitykla.Books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.agobal.KnyguKeitykla.MainActivity;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyBookEdit extends AppCompatActivity {

    private static final String TAG = "MyBookEditActivity";


    private StorageReference mImageStorage;
    private DatabaseReference mBookDatabase;
    private DatabaseReference mUserBookDatabase;

    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();

    private EditText etBookName;
    private EditText etBookAuthor;
    private EditText etBookAbout;
    private EditText etPublisher;
    private Spinner spinCategory;
    private Button btnYear;
    private RadioGroup radioGroup;

    private ImageView ivPickedImage;

    private String BookName;
    private String BookAuthor;
    private String BookPublisher;
    private String BookPublishYear;
    private String BookCondition;
    private String BookCategory;
    private String BookKey;

    private int BookYear;
    private String key;
    private String download_url;
    private Boolean isPhotoSelected= true;
    private String ImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_edit);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = findViewById(getResources().getIdentifier("action_bar_title", "id", getPackageName()));
        title.setText("Redagavimas");

        mImageStorage = FirebaseStorage.getInstance().getReference();
        //DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mBookDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mUserBookDatabase = FirebaseDatabase.getInstance().getReference().child("UserBooks");

        etBookName = findViewById(R.id.etBookName);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookAbout = findViewById(R.id.etBookAbout);
        etPublisher = findViewById(R.id.etPublisher);
        spinCategory = findViewById(R.id.spinCategory);
        radioGroup = findViewById(R.id.rbGroup);
        //RadioButton rbBookNew = findViewById(R.id.rbBookNew);
        //RadioButton rbBookGood = findViewById(R.id.rbBookGood);
        //RadioButton rbBookFair = findViewById(R.id.rbBookFair);
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

        loadBook();

        Log.d("name ", BookName+ " ");
        Log.d("author ", BookAuthor+ " ");
        Log.d("bookPublisher ", BookPublisher+ " ");
        Log.d("bookPublishYear ", BookPublishYear+ " ");
        Log.d("bookCondition ", BookCondition+ " ");
        Log.d("bookCategory ", BookCategory+ " ");
        Log.d("bookCover ", ImageURL+ " ");
        Log.d("bookKey ", BookKey+ " ");
    }

    private void loadBook()
    {
        BookName= getIntent().getStringExtra("bookName");
        BookAuthor= getIntent().getStringExtra("bookAuthor");
        BookPublisher= getIntent().getStringExtra("bookPublisher");
        BookPublishYear= getIntent().getStringExtra("bookPublishYear");
        BookCondition= getIntent().getStringExtra("bookCondition");
        BookCategory = getIntent().getStringExtra("bookCategory");
        ImageURL= getIntent().getStringExtra("bookCover");
        BookKey= getIntent().getStringExtra("bookKey");


        BookPublishYear = BookPublishYear.replaceAll("\\D+","");

        if(!BookPublishYear.equals(""))
        {
            BookYear = Integer.parseInt(BookPublishYear);
            btnYear.setText("Pasirinkti metai: "+ BookYear);

        }
        else
        {
            BookYear=0;
            btnYear.setText("PASIRINKTI METUS");

        }

        etBookName.setText(BookName);
        etBookAuthor.setText(BookAuthor);
        etPublisher.setText(BookPublisher);

        switch (BookCondition) {
            case "Būklė: Gera":
                Log.d(TAG, "gera yes");
                radioGroup.check(R.id.rbBookGood);
                break;
            case "Būklė: Nauja":
                radioGroup.check(R.id.rbBookNew);
                break;
            case "Būklė: Patenkinama":
                radioGroup.check(R.id.rbBookFair);
                break;
        }

        if(ImageURL.startsWith("https://firebasestorage"))
        {
            Picasso.get().load(ImageURL)
                    .rotate(90)
                    .resize(400,600)
                    .centerCrop()
                    .error(R.drawable.ic_nocover)
                    .into(ivPickedImage);
        }
        else
        {
            Picasso.get().load(ImageURL)
                    //.rotate(90)
                    .resize(400,600)
                    .error(R.drawable.ic_nocover)
                    .centerCrop()
                    .into(ivPickedImage);
            Log.d(" else if", "yes");
        }
    }

    private void saveBook()
    {
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

        mBookDatabase.child(BookKey).child("bookName").setValue(BookName);
        mBookDatabase.child(BookKey).child("bookAuthor").setValue(BookAuthor);
        mBookDatabase.child(BookKey).child("bookAbout").setValue(BookAbout);
        mBookDatabase.child(BookKey).child("bookPublisher").setValue(BookPublisher);
        mBookDatabase.child(BookKey).child("bookCategory").setValue(BookCategory);
        mBookDatabase.child(BookKey).child("bookCondition").setValue(bookCondition);
        mBookDatabase.child(BookKey).child("bookYear").setValue(BookYear);
        mBookDatabase.child(BookKey).child("image").setValue(ImageURL);

        mUserBookDatabase.child(current_uid).child(BookKey).child("bookName").setValue(BookName);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookAuthor").setValue(BookAuthor);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookAbout").setValue(BookAbout);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookPublisher").setValue(BookPublisher);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookCategory").setValue(BookCategory);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookCondition").setValue(bookCondition);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookYear").setValue(BookYear);

        if(download_url!= null)
            mUserBookDatabase.child(current_uid).child(BookKey).child("image").setValue(download_url);
        else
            mUserBookDatabase.child(current_uid).child(BookKey).child("image").setValue(ImageURL);

        mUserBookDatabase.child(current_uid).child(BookKey).child("userID").setValue(current_uid);
        mUserBookDatabase.child(current_uid).child(BookKey).child("bookKey").setValue(key);

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Pavyko!")
                .setContentText("Redagavimas sėkmingas!")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent intent = new Intent(MyBookEdit.this, MainActivity.class);
                    startActivity(intent);
                })
                .show();
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
                //spinCategory = findViewById(R.id.spinCategory);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(MyBookEdit.this, android.R.layout.simple_spinner_item, categories);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCategory.setAdapter(areasAdapter);

                switch (BookCategory) {
                    case "Kategorija: Mokslinė literatūra":
                        spinCategory.setSelection(0);
                        break;
                    case "Kategorija: Fantastinė literatūra":
                        spinCategory.setSelection(1);
                        break;
                    case "Kategorija: Istorinė literatūra":
                        spinCategory.setSelection(2);
                        break;
                    case "Kategorija: Biografinė literatūra":
                        spinCategory.setSelection(3);
                        break;
                    case "Kategorija: Grožinė literatūra":
                        spinCategory.setSelection(4);
                        break;
                    case "Kategorija: Asmens tobulėjimas":
                        spinCategory.setSelection(5);
                        break;
                    case "Kategorija: Psichologija":
                        spinCategory.setSelection(6);
                        break;
                    case "Kategorija: Karjera ir finansai":
                        spinCategory.setSelection(7);
                        break;
                    case "Kategorija: Detektyvai":
                        spinCategory.setSelection(8);
                        break;
                    case "Kategorija: Klasika":
                        spinCategory.setSelection(9);
                        break;
                    default:
                        spinCategory.setSelection(0);
                        break;
                }
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
