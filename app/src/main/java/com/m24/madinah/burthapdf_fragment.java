package com.m24.madinah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import helperclass.burthadb;
import Adapters.audioplayadapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link burthapdf_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class burthapdf_fragment extends Fragment {
    private RecyclerView burthapdfrecycler;
    private FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirestoreRecyclerAdapter adapteruser,adapteradmin;
    public static String s="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public burthapdf_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment burthapdf_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static burthapdf_fragment newInstance(String param1, String param2) {
        burthapdf_fragment fragment = new burthapdf_fragment();
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
        View view = inflater.inflate(R.layout.fragment_burthapdf_fragment, container, false);
        burthapdfrecycler = view.findViewById(R.id.burthapdfrecycler);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        SharedPreferences settings = getActivity().getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        // Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        Query query = FirebaseFirestore.getInstance().collection("Madinahdata").document("Burtha")
                .collection("BurthaPdf").orderBy("date");
        FirestoreRecyclerOptions<burthadb> burthadbFirestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<burthadb>().
                setQuery(query,burthadb.class).build();
        adapteruser = new FirestoreRecyclerAdapter<burthadb,burthapdfholder>(burthadbFirestoreRecyclerOptions) {
            @NonNull
            @Override
            public burthapdfholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdflayout,parent,false);
                return new burthapdfholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull burthapdfholder holder, int position, @NonNull burthadb model) {
              holder.delete.setVisibility(View.GONE);
              holder.pdfname.setText(model.getName());
              holder.pdflayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent i = new Intent(getActivity(), pdfviewerActivity.class);
                      i.putExtra("key",model.getFilelink());
                      startActivity(i);
                  }
              });
            }
        };
        adapteradmin = new FirestoreRecyclerAdapter<burthadb,burthapdfholder>(burthadbFirestoreRecyclerOptions) {
            @NonNull
            @Override
            public burthapdfholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdflayout,parent,false);
                return new burthapdfholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull burthapdfholder holder, int position, @NonNull burthadb model) {
              holder.pdfname.setText(model.getName());
              holder.pdflayout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent i = new Intent(getActivity(), pdfviewerActivity.class);
                      i.putExtra("key",model.getFilelink());
                      startActivity(i);
                  }
              });
              holder.delete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      StorageReference storageReference = storageRef.child("Burtha/BurthaPdf/"+model.getName());
                      storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              db.collection("Madinahdata").document("Burtha").
                                      collection("BurthaPdf").document(model.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            burthapdfrecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            burthapdfrecycler.setItemAnimator(null);
            burthapdfrecycler.setAdapter(adapteradmin);
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            burthapdfrecycler.setLayoutManager(layoutManager);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            burthapdfrecycler.setItemAnimator(null);
            burthapdfrecycler.setAdapter(adapteruser);
        }

        return view;
    }
    private class burthapdfholder extends RecyclerView.ViewHolder {
        TextView pdfname;
        ImageView delete;
        CardView pdflayout;
        public burthapdfholder(@NonNull View itemView) {
            super(itemView);
            pdfname = itemView.findViewById(R.id.pdfname);
            delete = itemView.findViewById(R.id.pdfdeletebt);
            pdflayout = itemView.findViewById(R.id.pdflayout);
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