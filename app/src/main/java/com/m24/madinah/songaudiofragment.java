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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import helperclass.burthadb;
import Adapters.audioplayadapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link songaudiofragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class songaudiofragment extends Fragment {
    private RecyclerView songaudiorecycler;
    private FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirestoreRecyclerAdapter sadapteruser,sadapteradmin;
    public static String s="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public songaudiofragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment songaudiofragment.
     */
    // TODO: Rename and change types and number of parameters
    public static songaudiofragment newInstance(String param1, String param2) {
        songaudiofragment fragment = new songaudiofragment();
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

        View view = inflater.inflate(R.layout.fragment_songaudiofragment, container, false);
        songaudiorecycler = view.findViewById(R.id.songaudiorecycler);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        SharedPreferences settings = getActivity().getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        Query query = FirebaseFirestore.getInstance().collection("Madinahdata").document("mahdesong")
                .collection("Audio").orderBy("date");
        ////use same dbhelperclass of burtha
        FirestoreRecyclerOptions<burthadb> burthadbFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<burthadb>().
                setQuery(query,burthadb.class).build();
        sadapteradmin = new FirestoreRecyclerAdapter<burthadb, saudioholder>(burthadbFirestoreRecyclerOptions){

            @NonNull
            @Override
            public saudioholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audiolayout,parent,false);
                return new saudioholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull saudioholder holder, int position, @NonNull burthadb model) {
                holder.audioname.setText(model.getName());
                holder.aplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioplayadapter audioplay = new audioplayadapter(getActivity(), model.getFilelink(), model.getName());
                        audioplay.startplaying();
                    }
                });

                holder.adelet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageReference storageReference = storageRef.child("mahdesong/Audio/"+model.getName());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                db.collection("Madinahdata").document("mahdesong").
                                        collection("Audio").document(model.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
        sadapteruser = new FirestoreRecyclerAdapter<burthadb, saudioholder>(burthadbFirestoreRecyclerOptions){

            @NonNull
            @Override
            public saudioholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audiolayout,parent,false);
                return new saudioholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull saudioholder holder, int position, @NonNull burthadb model) {
                holder.audioname.setText(model.getName());
                holder.adelet.setVisibility(View.INVISIBLE);

                holder.aplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioplayadapter audioplay = new audioplayadapter(getActivity(), model.getFilelink(), model.getName());
                        audioplay.startplaying();
                    }
                });
            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            songaudiorecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            songaudiorecycler.setItemAnimator(null);
            songaudiorecycler.setAdapter(sadapteradmin);
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            songaudiorecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            songaudiorecycler.setAdapter(sadapteruser);
            songaudiorecycler.setItemAnimator(null);
        }

        return view;
    }

    private class saudioholder extends RecyclerView.ViewHolder {
        TextView audioname;
        ImageView apus,aplay,adelet;
        public saudioholder(@NonNull View itemView) {
            super(itemView);
            audioname = itemView.findViewById(R.id.audioname);
            aplay = itemView.findViewById(R.id.audioplaybt);
            adelet = itemView.findViewById(R.id.audiodeletbt);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        sadapteruser.startListening();
        sadapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        sadapteruser.stopListening();
        sadapteradmin.stopListening();
    }
}