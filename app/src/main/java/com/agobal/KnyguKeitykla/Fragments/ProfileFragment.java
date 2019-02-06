package com.agobal.KnyguKeitykla.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.agobal.KnyguKeitykla.BookDetails.BookDetails;
import com.agobal.KnyguKeitykla.Entities.Books;
import com.agobal.KnyguKeitykla.Entities.UserData;
import com.agobal.KnyguKeitykla.R;
import com.agobal.KnyguKeitykla.AccountActivity.ProfileEdit;
import com.agobal.KnyguKeitykla.adapters.BooksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.agobal.KnyguKeitykla.Fragments.BookFragment.BOOK_DETAIL_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserFavBooks;
    private DatabaseReference mUserFavBooksUser;
    private static final int GALLERY_PICK = 1;

    private ArrayList<Books> BookList = new ArrayList<>();
    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
    private ListView listAllFavBooks;
    private BooksAdapter booksAdapter;

    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String cityName;
    private String about;

    private String BookKeyToDetails;
    private String UserKeyToDetails;

    private String userID;
    private Boolean isUserHaveFavBooks = false;
    private TextView tvEmpty;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Prašome palaukti");
        pDialog.setCancelable(false);
        pDialog.show();

        ViewCompat.setNestedScrollingEnabled(v, true);

        final TextView T_firstAndLastName = v.findViewById(R.id.firstAndLastName);
        final TextView T_Desc = v.findViewById(R.id.email);
        final TextView T_About = v.findViewById(R.id.about);
        final TextView T_City= v.findViewById(R.id.city);
        final CircleImageView ProfilePic = v.findViewById(R.id.profilePic);
        ImageButton BtnProfileEdit = v.findViewById(R.id.profileEditBtn);

        listAllFavBooks = v.findViewById(R.id.listAllFavBooks);

        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = Objects.requireNonNull(mCurrentUser).getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserFavBooks = FirebaseDatabase.getInstance().getReference().child("UserFavBooks").child(current_uid);

        setupBookSelectedListener();

        ProfilePic.setOnClickListener(view -> {
            //Profile photo button
            showFileChooser();
        });

        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("userName").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                firstName = dataSnapshot.child("firstName").getValue(String.class);
                lastName = dataSnapshot.child("lastName").getValue(String.class);
                String cityName = dataSnapshot.child("cityName").getValue(String.class);
                about = dataSnapshot.child("about").getValue(String.class);
                //String thumb_image = dataSnapshot.child("thumb_image").getValue(String.class);
                final String image = dataSnapshot.child("image").getValue(String.class);

                assert image != null;
                if(!image.equals("default")){
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.unknown_profile_pic)
                            .into(ProfilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            pDialog.dismissWithAnimation();
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.unknown_profile_pic).into(ProfilePic);
                        }

                    });

                }

                Log.d("user info: ", userName +" " + email + " " +firstName+" "+ lastName+ " "+cityName);

                UserData userData = new UserData();

                userData.setFirstName(firstName);
                userData.setLastName(lastName);
                userData.setCityName(cityName);
                userData.setEmail(email);
                userData.setUserName(userName);

                T_firstAndLastName.setText(userData.firstName+" "+userData.lastName);
                T_Desc.setText(userData.email);
                T_City.setText(userData.cityName);
                T_About.setText(about);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

        mUserFavBooks.keepSynced(true);

        mUserFavBooks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        userID = childDataSnapshot.getKey();

                        if (userID != null && userID.equals(current_uid)) { // ar yra dabartinis vartotojas userbooks šakoje
                            Log.d("ar yra šakoje?", "taip");
                            isUserHaveFavBooks = true;
                        }
                    }
                    tvEmpty.setVisibility(View.GONE);
                    fetchFavBooks();
                } else
                    tvEmpty.setVisibility(View.VISIBLE);

                pDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", " Suveikė canceled");
            }
        });

        BtnProfileEdit.setOnClickListener(view -> {
            //Profile edit button
            Intent intent = new Intent(getActivity(), ProfileEdit.class);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("email", email);
            intent.putExtra("userName", userName);
            intent.putExtra("cityName", cityName);
            intent.putExtra("about", about);
            startActivity(intent);
        });

        return v;
    }

    private void fetchFavBooks() {
        Log.d("favbooks", " yes");
        BookList = new ArrayList<>();
        mUserFavBooks = FirebaseDatabase.getInstance().getReference().child("UserFavBooks");
        mUserFavBooks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserFavBooksUser = FirebaseDatabase.getInstance().getReference().child("UserFavBooks").child(current_uid);
                mUserFavBooksUser.keepSynced(true);
                mUserFavBooksUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("get key", "" + childDataSnapshot.getKey());//Gaunu visus knygų ID

                            String path = childDataSnapshot.getRef().toString();
                            Log.d("path:",path+" ");

                            BookKeyToDetails = childDataSnapshot.getKey();

                            UserKeyToDetails = dataSnapshot.getKey();

                            String BookName = childDataSnapshot.child("bookName").getValue(String.class);
                            String BookAuthor = childDataSnapshot.child("bookAuthor").getValue(String.class);
                            String BookPublisher = childDataSnapshot.child("bookPublisher").getValue(String.class);
                            Integer BookYear = childDataSnapshot.child("bookYear").getValue(Integer.class);
                            String BookCondition = childDataSnapshot.child("bookCondition").getValue(String.class);
                            String BookCategory = childDataSnapshot.child("bookCategory").getValue(String.class);
                            String BookAbout = childDataSnapshot.child("bookAbout").getValue(String.class);
                            String Image = childDataSnapshot.child("image").getValue(String.class);
                            String Tradable = childDataSnapshot.child("tradable").getValue(String.class);

                            String UserID = childDataSnapshot.child("userID").getValue(String.class);
                            String BookID = childDataSnapshot.child("bookKey").getValue(String.class);

                            BookList.add(new Books(BookName, BookAuthor, BookPublisher, BookYear, BookCondition, BookCategory, BookAbout, Image, Tradable, UserID, BookID));

                            booksAdapter = new BooksAdapter(Objects.requireNonNull(getContext()), BookList);
                            listAllFavBooks.setAdapter(booksAdapter);
                            booksAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled"," Suveikė canceled2");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            //start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(Objects.requireNonNull(getActivity()),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //progress dialog
                SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Prašome palaukti");
                pDialog.setCancelable(false);
                pDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                String current_user_id = Objects.requireNonNull(user).getUid();

                Bitmap thumb_bitmap = null;

                try {
                    thumb_bitmap = new Compressor(Objects.requireNonNull(getContext()).getApplicationContext())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePath);
                    Log.d("Compresor:", "YES");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Compresor:", "NO");
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (thumb_bitmap != null) {
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                else
                        Log.d("THUMB", "NEPAVYKO");

                //final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                    String download_url = uri.toString();
                    mUserDatabase.child("image").setValue(download_url);
                    pDialog.dismiss();
                }));

                thumb_filepath.putFile(resultUri).addOnSuccessListener(taskSnapshot -> thumb_filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String thumb_download_url = uri.toString();
                    Log.d("URL: ", thumb_download_url);
                    mUserDatabase.child("thumb_image").setValue(thumb_download_url);
                    pDialog.dismiss();

                }));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Log.d("resultCode == error", "YES");
            }
        }
    }

    private void showFileChooser() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "Pasirinkite nuotrauką"), GALLERY_PICK);
    }

    private void setupBookSelectedListener() {
        listAllFavBooks.setOnItemClickListener((parent, view, position, id) -> {
            // Launch the detail view passing book as an extra
            Intent intent = new Intent(getActivity(), BookDetails.class);
            intent.putExtra(BOOK_DETAIL_KEY, booksAdapter.getItem(position));
            intent.putExtra("BOOK_KEY", BookKeyToDetails);
            intent.putExtra("USER_KEY", UserKeyToDetails);
            startActivity(intent);
            Log.d("NEW_INTENT", "VEIKIA");
        });
    }

}


