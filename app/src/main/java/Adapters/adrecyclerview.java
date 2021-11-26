package Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.m24.madinah.Adminpanal;
import com.m24.madinah.R;

import java.util.List;

import helperclass.Ad;
import helperclass.adupload;


public class adrecyclerview extends RecyclerView.Adapter<adrecyclerview.adviewholder> {
    public List<Ad> adList;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public adrecyclerview(List<Ad> adList) {
        this.adList = adList;
    }

    @NonNull
    @Override
    public adviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adviewlayout,parent,false);
        return new adviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adviewholder holder, @SuppressLint("RecyclerView") int position) {
        //context = context.getApplicationContext();
        Glide.with(holder.itemView.getContext()).load(adList.get(position).adlink).into(holder.adimage);
        String adname = adList.get(position).name;
        holder.deletimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference storageReference = storageRef.child("Ad/"+adname);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        adList.remove(position);
                        adupload adupload = new adupload(adList);
                        db.collection("Ad").document("list").set(adupload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(v.getContext(), "Delet Success", Toast.LENGTH_SHORT).show();
                                holder.adlayour.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), "Failed to delet", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), "Failed to delet", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class adviewholder extends RecyclerView.ViewHolder {
        ImageView adimage,deletimage;
        CardView adlayour;
        public adviewholder(@NonNull View itemView) {
            super(itemView);
            adimage = itemView.findViewById(R.id.adimage);
            deletimage = itemView.findViewById(R.id.addeletbt);
            adlayour = itemView.findViewById(R.id.admainlayout);
        }
    }
}
