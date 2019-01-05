package com.agobal.KnyguKeitykla.adapters;


import java.util.List;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.agobal.KnyguKeitykla.Entities.Messages;
import com.agobal.KnyguKeitykla.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vaidas on 2017-12-13.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private String imageURL;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text_layout);
            profileImage = view.findViewById(R.id.message_profile_layout);
            displayName = view.findViewById(R.id.name_text_layout);
            messageImage = view.findViewById(R.id.message_image_layout);


        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages msg = mMessageList.get(i);

        String from_user = msg.getFrom();
        String message_type = msg.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("userName").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.get().load(image)
                        .placeholder(R.drawable.unknown_profile_pic).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            Log.d("c.getmessageTEXT ", msg.getMessage() + " ");
            viewHolder.messageText.setText(msg.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            imageURL = msg.getMessage();
            viewHolder.messageText.setVisibility(View.INVISIBLE);

            Log.d("c.getmessageIMAGE ", imageURL + " ");
            Picasso.get().load(imageURL)
                    .error(R.drawable.error_circle)
                    .resize(800,1200)
                    .rotate(90)
                    //.centerCrop()
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .into(viewHolder.messageImage); //TODO: gauti image i msg.getmesage

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }






}

