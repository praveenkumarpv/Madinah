package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.m24.madinah.R;

import helperclass.classdatahelperclass;

public class teacheraddpopadapter {
    Activity activity;
    AlertDialog dialog;
    String sirnametxt;
    TextView add;
    FirebaseFirestore db;
    EditText sirname;

    public teacheraddpopadapter(Activity activity) {
        this.activity = activity;
    }
    public void addmaster(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setCancelable(true);
        View v = inflater.inflate(R.layout.teacheraddpopup,null);
        builder.setView(v);
        db = FirebaseFirestore.getInstance();
        sirname = v.findViewById(R.id.teachername);
        add = v.findViewById(R.id.addteacher);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sirnametxt = sirname.getText().toString().trim();
            if (sirnametxt.isEmpty()){
                Toast.makeText(activity, "Please enter name", Toast.LENGTH_SHORT).show();
            }
            else {
                classdatahelperclass cl = new classdatahelperclass(sirnametxt);
                db.collection("Class").document(sirnametxt).set(cl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            }
        });
        dialog = builder.create();
        dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
    }
}
