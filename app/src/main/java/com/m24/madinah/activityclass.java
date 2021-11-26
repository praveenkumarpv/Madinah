package com.m24.madinah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import Adapters.teacheraddpopadapter;
import helperclass.classdatahelperclass;

public class activityclass extends AppCompatActivity {
    FloatingActionButton addmaster;
    RecyclerView teacherlist;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter tadapteruser,tadapteradmin;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityclass);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        db = FirebaseFirestore.getInstance();
        teacherlist = findViewById(R.id.teacherrecycler);
        addmaster = findViewById(R.id.teacheraddfloating);
        SharedPreferences settings = activityclass.this.getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        if (! s.isEmpty()){
            addmaster.setVisibility(View.VISIBLE);
        }
        addmaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacheraddpopadapter teacher = new teacheraddpopadapter(activityclass.this);
                teacher.addmaster();
            }
        });
        Query query =  FirebaseFirestore.getInstance().collection("Class");
        FirestoreRecyclerOptions<classdatahelperclass> teachers = new FirestoreRecyclerOptions.Builder<classdatahelperclass>().
                setQuery(query,classdatahelperclass.class).build();
        tadapteradmin = new FirestoreRecyclerAdapter<classdatahelperclass,teacherviewholde>(teachers){

            @NonNull
            @Override
            public teacherviewholde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacherdisplaylayout,parent,false);
                return new teacherviewholde(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull teacherviewholde holder, int position, @NonNull classdatahelperclass model) {
             holder.mastername.setText(model.getMastername());
             holder.masterdelet.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     db.collection("Class").document(model.getMastername()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             Toast.makeText(activityclass.this, "DeletedSuccessfully", Toast.LENGTH_SHORT).show();
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(activityclass.this, "Failed to delet", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             });
             holder.teacherlayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent i = new Intent(activityclass.this, masteractivity.class);
                     i.putExtra("mastername",model.getMastername());
                     startActivity(i);
                 }
             });
            }
        };
        tadapteruser = new FirestoreRecyclerAdapter<classdatahelperclass,teacherviewholde>(teachers){

            @NonNull
            @Override
            public teacherviewholde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacherdisplaylayout,parent,false);
                return new teacherviewholde(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull teacherviewholde holder, int position, @NonNull classdatahelperclass model) {
             holder.mastername.setText(model.getMastername());
             holder.masterdelet.setVisibility(View.GONE);
             holder.teacherlayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent i = new Intent(activityclass.this, masteractivity.class);
                     i.putExtra("mastername",model.getMastername());
                     startActivity(i);
                 }
             });
            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(activityclass.this);
            teacherlist.setLayoutManager(layoutManager);
            teacherlist.setItemAnimator(null);
            teacherlist.setAdapter(tadapteradmin);
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(activityclass.this);
            teacherlist.setLayoutManager(layoutManager);
            teacherlist.setItemAnimator(null);
            teacherlist.setAdapter(tadapteruser);
        }
    }

    private class teacherviewholde extends RecyclerView.ViewHolder {
        TextView mastername;
        ImageView masterdelet;
        CardView teacherlayout;
        public teacherviewholde(@NonNull View itemView) {
            super(itemView);
            mastername = itemView.findViewById(R.id.mastername);
            masterdelet = itemView.findViewById(R.id.masterdelet);
            teacherlayout = itemView.findViewById(R.id.teachercard);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        tadapteruser.startListening();
        tadapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tadapteruser.stopListening();
        tadapteradmin.stopListening();
    }
}