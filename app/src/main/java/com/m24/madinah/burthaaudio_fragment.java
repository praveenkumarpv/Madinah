package com.m24.madinah;

import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import Adapters.audioplayadapter;
import helperclass.burthadb;
import io.grpc.Context;

import static android.content.Context.MODE_PRIVATE;


public class burthaaudio_fragment extends Fragment {
    private RecyclerView burthaaudiorecycler;
    private FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirestoreRecyclerAdapter adapteruser,adapteradmin;
    public static String s="";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public burthaaudio_fragment() {
        // Required empty public constructor
    }
    public static burthaaudio_fragment newInstance(String param1, String param2) {
        burthaaudio_fragment fragment = new burthaaudio_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_burthaaudio_fragment, container, false);
        burthaaudiorecycler = view.findViewById(R.id.burthaaudiorecycler);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        SharedPreferences settings = getActivity().getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
       // Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        Query query = FirebaseFirestore.getInstance().collection("Madinahdata").document("Burtha")
                .collection("BurthaAudio").orderBy("date");
        FirestoreRecyclerOptions<burthadb> burthadbFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<burthadb>().
                setQuery(query,burthadb.class).build();
        adapteradmin = new FirestoreRecyclerAdapter<burthadb,baudioholder>(burthadbFirestoreRecyclerOptions){
            @NonNull
            @Override
            public baudioholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audiolayout,parent,false);
                return new baudioholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull baudioholder holder, int position, @NonNull burthadb model) {
             holder.audioname.setText(model.getName());
             audioplayadapter audioplayadapter = new audioplayadapter(getActivity(),model.getFilelink(),model.getName());
             holder.aplay.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     audioplayadapter.startplaying();
                 }
             });
             holder.adelet.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     StorageReference storageReference = storageRef.child("Burtha/BurthaAudio/"+model.getName());
                     storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             db.collection("Madinahdata").document("Burtha").
                                     collection("BurthaAudio").document(model.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(getContext(), "Failed to delet", Toast.LENGTH_SHORT).show();
                         }
                     });

                 }
             });
            }
        };
        adapteruser = new FirestoreRecyclerAdapter<burthadb,baudioholder>(burthadbFirestoreRecyclerOptions){
            @NonNull
            @Override
            public baudioholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audiolayout,parent,false);
                return new baudioholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull baudioholder holder, int position, @NonNull burthadb model) {
             holder.audioname.setText(model.getName());
             holder.adelet.setVisibility(View.GONE);
             holder.aplay.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     audioplayadapter audioplayadapter = new audioplayadapter(getActivity(),model.getFilelink(),model.getName());
                     audioplayadapter.startplaying();
                 }
             });
            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            burthaaudiorecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            burthaaudiorecycler.setItemAnimator(null);
            burthaaudiorecycler.setAdapter(adapteradmin);

        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            burthaaudiorecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            burthaaudiorecycler.setItemAnimator(null);
            burthaaudiorecycler.setAdapter(adapteruser);
        }


        return view;
    }

    private class baudioholder extends RecyclerView.ViewHolder {
        TextView audioname;
        ImageView aplay,adelet;
        public baudioholder(@NonNull View itemView) {
            super(itemView);
            audioname = itemView.findViewById(R.id.audioname);
            aplay = itemView.findViewById(R.id.audioplaybt);
            adelet = itemView.findViewById(R.id.audiodeletbt);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        adapteruser.startListening();
        adapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapteruser.stopListening();
        adapteradmin.stopListening();
    }

}