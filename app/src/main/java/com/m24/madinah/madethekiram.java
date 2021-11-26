package com.m24.madinah;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

import helperclass.burthadb;
import helperclass.dateuploder;
import Adapters.uploadingadapter;

public class madethekiram extends AppCompatActivity {
    TextView datepicker,uploadtxt;
    FloatingActionButton datefloat;
    RecyclerView daterecycler;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    String datest,s;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirestoreRecyclerAdapter dadapteruser,dadapteradmin;
    ConstraintLayout uploadforground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madethekiram);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        datepicker = findViewById(R.id.datepicker);
        uploadtxt = findViewById(R.id.dateupload);
        datefloat = findViewById(R.id.dateadfloat);
        daterecycler = findViewById(R.id.daterecycler);
        uploadforground = findViewById(R.id.dateuploadforground);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        SharedPreferences settings = madethekiram.this.getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        if (! s.isEmpty()){
          datefloat.setVisibility(View.VISIBLE);
        }
        uploadingadapter upload = new uploadingadapter(madethekiram.this);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        datefloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datefloat.setVisibility(View.GONE);
                datepicker.setVisibility(View.VISIBLE);
                uploadtxt.setVisibility(View.VISIBLE);
                uploadforground.setBackgroundColor(Color.parseColor("#BE039F39"));
            }
        });
        datepicker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
             datePickerDialog = new DatePickerDialog(madethekiram.this, new DatePickerDialog.OnDateSetListener() {
                 @Override
                 public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                     datepicker.setText(dayOfMonth + "-"
                             + (month + 1) + "-" + year);
                     datest = dayOfMonth + "-"
                             + (month + 1) + "-" + year;
                 }
             },year,month,day);
             datePickerDialog.show();
            }
        });
        uploadtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(madethekiram.this, datest, Toast.LENGTH_SHORT).show();
                if (datest.isEmpty()){
                    Toast.makeText(madethekiram.this, "Select an date", Toast.LENGTH_SHORT).show();
                }
                else {
                   upload.Startuploading();
                   dateuploder date =new dateuploder(datest);
                   db.collection("Date").document(datest).set(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           datefloat.setVisibility(View.VISIBLE);
                           datepicker.setVisibility(View.GONE);
                           uploadtxt.setVisibility(View.GONE);
                           upload.uploadDissmiss();
                           uploadforground.setBackgroundColor(Color.parseColor("#00039F39"));
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(madethekiram.this, "upload failed", Toast.LENGTH_SHORT).show();
                           upload.uploadDissmiss();
                       }
                   });
                }
            }
        });
        Query query = FirebaseFirestore.getInstance().collection("Date").orderBy("date");
        FirestoreRecyclerOptions<dateuploder>firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<dateuploder>()
                .setQuery(query,dateuploder.class).build();
        dadapteradmin = new FirestoreRecyclerAdapter<dateuploder,dateviewholder>(firestoreRecyclerOptions) {

            @NonNull
            @Override
            public dateviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.datelayout,parent,false);
                return new dateviewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull dateviewholder holder, int position, @NonNull dateuploder model) {
               holder.datev.setText(model.getDate());
               holder.datedel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       db.collection("Date").document(model.getDate()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(madethekiram.this, "Deleted", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(madethekiram.this, "Failed", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               });
            }
        };
        dadapteruser = new FirestoreRecyclerAdapter<dateuploder,dateviewholder>(firestoreRecyclerOptions) {

            @NonNull
            @Override
            public dateviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.datelayout,parent,false);
                return new dateviewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull dateviewholder holder, int position, @NonNull dateuploder model) {
               holder.datev.setText(model.getDate());
               holder.datedel.setVisibility(View.GONE);
            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(madethekiram.this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            daterecycler.setLayoutManager(layoutManager);
            daterecycler.setItemAnimator(null);
            daterecycler.setAdapter(dadapteradmin);

        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(madethekiram.this);
            daterecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            daterecycler.setItemAnimator(null);
            daterecycler.setAdapter(dadapteruser);
        }
    }

    private class dateviewholder extends RecyclerView.ViewHolder {
        TextView datev;
        ImageView datedel;
        public dateviewholder(@NonNull View itemView) {
            super(itemView);
            datev = itemView.findViewById(R.id.dateview);
            datedel = itemView.findViewById(R.id.datedelet);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        dadapteruser.startListening();
        dadapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        dadapteruser.stopListening();
        dadapteradmin.stopListening();
    }
}