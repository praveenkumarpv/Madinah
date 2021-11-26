package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import com.m24.madinah.R;

public class uploadingadapter {
     Activity activity;
     AlertDialog dialog;
     Fragment fragment;

    public uploadingadapter(Activity activity) {
        this.activity = activity;
    }



    public void Startuploading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loadingsccreen,null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog = builder.show();

    }
   public void uploadDissmiss(){
      dialog.dismiss();
   }
}
