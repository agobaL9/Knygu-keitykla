package com.agobal.KnyguKeitykla.helper;

import android.os.AsyncTask;

class LoadDataTask extends AsyncTask<Object, Object, Object> {

    /* Your object types according to your task. */

    private AsyncTaskCompleteListener callback; // Callback field

    public LoadDataTask(AsyncTaskCompleteListener cb){
        this.callback = cb;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        callback.onTaskComplete(); // Set the Callback
    }
}