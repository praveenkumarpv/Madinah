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

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import helperclass.burthadb;
import Adapters.audioplayadapter;
import helperclass.parayanam;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link parayanam_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class parayanam_fragment extends Fragment {
    RecyclerView parayanamrecycler;
    private FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirestoreRecyclerAdapter padapteruser,padapteradmin;
    public static String s="";
    audioplayadapter audioplayadapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public parayanam_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment parayanam_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static parayanam_fragment newInstance(String param1, String param2) {
        parayanam_fragment fragment = new parayanam_fragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parayanam_fragment, container, false);
        parayanamrecycler = view.findViewById(R.id.parayanamrecycler);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        SharedPreferences settings = getActivity().getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        Query query = FirebaseFirestore.getInstance().collection("Madinahdata").document("Burtha")
                .collection("RehusobaAudio").orderBy("date");
        FirestoreRecyclerOptions<parayanam> parayanamFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<parayanam>().
                setQuery(query,parayanam.class).build();
        padapteradmin = new FirestoreRecyclerAdapter<parayanam,parayanamviewholder>(parayanamFirestoreRecyclerOptions){
            @NonNull
            @Override
            public parayanamviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parayanam_layout,parent,false);
                return new parayanamviewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull parayanamviewholder holder, int position, @NonNull parayanam model) {
              holder.parayanamname.setText(model.getName());
              if (model.getImageindicator() != "no"){
                  Glide.with(getContext()).load(model.getImageindicator()).into(holder.parayanamimage);
              }
              holder.parayanamplay.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     audioplayadapter audioplayadapter = new audioplayadapter(getActivity(),model.getFilelink(), model.getName());
                     audioplayadapter.startplaying();
                  }
              });
              holder.parayanamdel.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if (model.getImageindicator() != "no" || model.getImageindicator() == ""){
                          StorageReference storageReference = storageRef.child("Burtha/RehusobaAudio/" + model.getName());
                          storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  db.collection("Madinahdata").document("Burtha")
                                          .collection("RehusobaAudio")
                                          .document(model.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                      }
                                  }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                              }
                          });

                      }
                      else {
                          Toast.makeText(getContext(), "Deleted inside", Toast.LENGTH_SHORT).show();
                          StorageReference istorageReference = storageRef.child("Burtha/RehusobaImage/" + model.getName());
                          istorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {
                                  StorageReference storageReference = storageRef.child("Burtha/RehusobaAudio/" + model.getName());
                                  storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          db.collection("Madinahdata").document("Burtha")
                                                  .collection("RehusobaAudio")
                                                  .document(model.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void unused) {
                                                  Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                              }
                                          }).addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {
                                                  Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                              }
                                          });
                                      }
                                  }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {

                              }
                          });
                      }
                  }
              });
            }
        };
        padapteruser = new FirestoreRecyclerAdapter<parayanam,parayanamviewholder>(parayanamFirestoreRecyclerOptions){
            @NonNull
            @Override
            public parayanamviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parayanam_layout,parent,false);
                return new parayanamviewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull parayanamviewholder holder, int position, @NonNull parayanam model) {
              holder.parayanamname.setText(model.getName());
              holder.parayanamdel.setVisibility(View.GONE);
              if (model.getImageindicator() != "no"){
                  Glide.with(getContext()).load(model.getImageindicator()).into(holder.parayanamimage);
              }
              holder.parayanamplay.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     audioplayadapter audioplayadapter = new audioplayadapter(getActivity(),model.getFilelink(), model.getName());
                     audioplayadapter.startplaying();
                  }
              });

            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            parayanamrecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            parayanamrecycler.setItemAnimator(null);
            parayanamrecycler.setAdapter(padapteradmin);
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            parayanamrecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            parayanamrecycler.setItemAnimator(null);
            parayanamrecycler.setAdapter(padapteruser);
        }
        return view;
    }
    private class parayanamviewholder extends RecyclerView.ViewHolder {
        ImageView parayanamimage,parayanamplay,parayanamdel;
        TextView parayanamname;
        public parayanamviewholder(@NonNull View itemView) {
            super(itemView);
            parayanamimage = itemView.findViewById(R.id.parayanamimage);
            parayanamname = itemView.findViewById(R.id.parayanname);
            parayanamplay = itemView.findViewById(R.id.parayanamplay);
            parayanamdel = itemView.findViewById(R.id.parayanamdelete);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        padapteruser.startListening();
        padapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        padapteruser.stopListening();
        padapteradmin.stopListening();
    }


}