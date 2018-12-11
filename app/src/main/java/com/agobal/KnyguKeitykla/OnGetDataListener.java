package com.agobal.KnyguKeitykla;

import com.google.firebase.database.DataSnapshot;

public interface OnGetDataListener {
    //this is for callbacks
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}