package com.m24.madinah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Adapters.audioplayadapter;
import helperclass.classdatahelperclass;
import Adapters.uploadingadapter;

public class masteractivity extends AppCompatActivity {
    String mastername,s,caudiotxt = "null",cpdftxt = "null",episodetxt;
    TextView classpdf,classaudio,classupload;
    EditText episode;
    RecyclerView materialrecyclers;
    FloatingActionButton classadfloat;
    ConstraintLayout classforground;
    FirestoreRecyclerAdapter madapteruser,madapteradmin;
    private final int PICK_AUDIO = 1,PICK_PDF = 2;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    uploadingadapter uploadingadapter;
    Uri caudiourls,caudiodownloadurl,cpdfurl,cpdfdownloadurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masteractivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        uploadingadapter = new uploadingadapter(masteractivity.this);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mastername = getIntent().getStringExtra("mastername");
        }
        SharedPreferences settings = masteractivity.this.getSharedPreferences("preference",MODE_PRIVATE);
        s = settings.getString("uid","");
        classaudio = findViewById(R.id.classaudio);
        classpdf = findViewById(R.id.classpdf);
        classupload = findViewById(R.id.classupload);
        episode = findViewById(R.id.episode);
        classadfloat = findViewById(R.id.classaddfloat);
        classforground = findViewById(R.id.classassetaddforground);
        materialrecyclers = findViewById(R.id.classmaterialsrecycler);
        if (s.isEmpty()){
            classforground.setVisibility(View.GONE);
        }
        classadfloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classadfloat.setVisibility(View.GONE);
                classaudio.setVisibility(View.VISIBLE);
                classpdf.setVisibility(View.VISIBLE);
                episode.setVisibility(View.VISIBLE);
                classupload.setVisibility(View.VISIBLE);
                classforground.setBackgroundColor(Color.parseColor("#BE039F39"));
            }
        });
        classaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent caudio = new Intent();
                caudio.setType("audio/*");
                caudio.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(caudio, "Select Audio"), PICK_AUDIO);
            }
        });
        classpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cpdf = new Intent();
                cpdf.setType("application/pdf");
                cpdf.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(cpdf, "Select Pdf"), PICK_PDF);
            }
        });
        classupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             uploadingadapter.Startuploading();
             episodetxt = episode.getText().toString().trim();
             if (episodetxt.isEmpty()){
                 Toast.makeText(masteractivity.this, "Enter episode number", Toast.LENGTH_SHORT).show();
             }


             else if (!caudiotxt.equals("null") && cpdftxt.equals("null")){//audioonly
                 StorageReference storageReference = storageRef.child("Class/"+mastername+"/audio/"+ episodetxt);
                 storageReference.putFile(caudiourls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                            caudiodownloadurl = uri;
                            caudiotxt = String.valueOf(caudiodownloadurl);
                            upload(mastername,episodetxt,caudiotxt,"no");
                          }
                      });
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(masteractivity.this, "Failed to upload audio", Toast.LENGTH_SHORT).show();
                     }
                 });
             }

             else if (caudiotxt.equals("null") && !cpdftxt.equals("null")){//pdfonly
                 StorageReference storageReference = storageRef.child("Class/"+mastername+"/pdf/"+ episodetxt);
                 storageReference.putFile(cpdfurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 cpdfdownloadurl = uri;
                                 cpdftxt = String.valueOf(cpdfdownloadurl);
                                 upload(mastername,episodetxt,"no",cpdftxt);
                             }
                         });
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(masteractivity.this, "Failed to upload pdf", Toast.LENGTH_SHORT).show();
                     }
                 });
             }

             else if (!caudiotxt.equals("null") && !cpdftxt.equals("null")) {//pdf&audioonly
                 StorageReference astorageReference = storageRef.child("Class/" + mastername + "/audio/" + episodetxt);
                 astorageReference.putFile(caudiourls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         astorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                              caudiodownloadurl = uri;
                                 StorageReference pstorageReference = storageRef.child("Class/"+mastername+"/pdf/"+ episodetxt);
                                 pstorageReference.putFile(cpdfurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                     @Override
                                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                      pstorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                          @Override
                                          public void onSuccess(Uri uri) {
                                           caudiotxt = String.valueOf(caudiodownloadurl);
                                           cpdfdownloadurl = uri;
                                           cpdftxt = String.valueOf(cpdfdownloadurl);
                                           upload(mastername,episodetxt,caudiotxt,cpdftxt);
                                          }
                                      });
                                     }
                                 });
                             }
                         });
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(masteractivity.this, "Failed to upload audio & pdf", Toast.LENGTH_SHORT).show();
                     }
                 });
             }


            }
        });

  /////////////////////////////////////////////////recyclerview//////////////////////////////////////////////////////////

        Query query =  FirebaseFirestore.getInstance().collection("Class").document(mastername)
                .collection("materials").orderBy("episodenumber");
        FirestoreRecyclerOptions<classdatahelperclass> materialrecycler = new FirestoreRecyclerOptions.Builder<classdatahelperclass>().
                setQuery(query,classdatahelperclass.class).build();
        madapteradmin = new FirestoreRecyclerAdapter<classdatahelperclass, materialviewholde>(materialrecycler){

            @NonNull
            @Override
            public materialviewholde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classmateriallayout,parent,false);
                return new materialviewholde(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull materialviewholde holder, int position, @NonNull classdatahelperclass model) {
             holder.episodename.setText("Episode-"+model.getEpisodenumber());
             String audio = model.getAudiofilelink();
             String pdf = model.getPdffilelink();
//                Toast.makeText(masteractivity.this, audio, Toast.LENGTH_SHORT).show();
//                Toast.makeText(masteractivity.this,pdf, Toast.LENGTH_SHORT).show();
             holder.materialaudio.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (audio == "no"){
                         Toast.makeText(masteractivity.this, "Audio unavailable", Toast.LENGTH_SHORT).show();
                     }else {
                         audioplayadapter audioplay = new audioplayadapter(masteractivity.this, model.getAudiofilelink(), model.getEpisodenumber());
                         audioplay.startplaying();
                     }
                 }
             });
             holder.materialpdf.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (pdf == "no"){
                         Toast.makeText(masteractivity.this, "Pdf unavailable", Toast.LENGTH_SHORT).show();
                     }else{
                     Intent i = new Intent(masteractivity.this, pdfviewerActivity.class);
                     i.putExtra("key",model.getPdffilelink());
                     startActivity(i);
                     }
                 }
             });
             holder.materialdaelet.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (audio.equals("no")){
                         StorageReference storageReference = storageRef.child("Class/"+mastername+"/pdf/"+ model.getEpisodenumber());
                                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {
                                         deletdb(mastername,model.getEpisodenumber());
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(masteractivity.this, "Deleting audio failed ", Toast.LENGTH_SHORT).show();
                                     }
                                 });
                     }
                     else if (pdf.equals("no")){
                         StorageReference storageReference = storageRef.child("Class/"+mastername+"/audio/"+ model.getEpisodenumber());
                         storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 deletdb(mastername,model.getEpisodenumber());
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(masteractivity.this, "Deleting pdf failed", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                     else if (!audio.equals("no") && !pdf.equals("no")){
                         StorageReference storageReference = storageRef.child("Class/"+mastername+"/audio/"+ model.getEpisodenumber());
                         storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 StorageReference storageReference = storageRef.child("Class/"+mastername+"/pdf/"+ model.getEpisodenumber());
                                 storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {
                                         deletdb(mastername,model.getEpisodenumber());
                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(masteractivity.this, "Deleting both failed", Toast.LENGTH_SHORT).show();
                                     }
                                 });
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(masteractivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 }
             });
            }
        };
        madapteruser = new FirestoreRecyclerAdapter<classdatahelperclass, materialviewholde>(materialrecycler){

            @NonNull
            @Override
            public materialviewholde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classmateriallayout,parent,false);
                return new materialviewholde(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull materialviewholde holder, int position, @NonNull classdatahelperclass model) {
             holder.episodename.setText("Episode-"+model.getEpisodenumber());
             String audio = model.getAudiofilelink();
             String pdf = model.getPdffilelink();
             holder.materialdaelet.setVisibility(View.GONE);
             holder.materialaudio.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (audio.equals("no")){
                         Toast.makeText(masteractivity.this, "Audio unavailable", Toast.LENGTH_SHORT).show();
                     }else {
                         audioplayadapter audioplay = new audioplayadapter(masteractivity.this, model.getAudiofilelink(), model.getEpisodenumber());
                         audioplay.startplaying();
                     }
                 }
             });
             holder.materialpdf.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (pdf.equals("no")){
                         Toast.makeText(masteractivity.this, "Pdf unavailable", Toast.LENGTH_SHORT).show();
                     }else{
                         Intent i = new Intent(masteractivity.this, pdfviewerActivity.class);
                         i.putExtra("key",model.getPdffilelink());
                         startActivity(i);
                     }
                 }
             });
            }
        };
        if (!(s.isEmpty())){
            LinearLayoutManager layoutManager = new LinearLayoutManager(masteractivity.this);
            materialrecyclers.setLayoutManager(layoutManager);
            materialrecyclers.setItemAnimator(null);
            materialrecyclers.setAdapter(madapteradmin);
        }
        else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(masteractivity.this);
            materialrecyclers.setLayoutManager(layoutManager);
            materialrecyclers.setItemAnimator(null);
            materialrecyclers.setAdapter(madapteruser);
        }
    }

    private void deletdb(String masternamepass, String episodenumberpass) {
        db.collection("Class").document(mastername).collection("materials")
                .document(episodenumberpass).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(masteractivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(masteractivity.this, "Deleting failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upload(String master,String pepisodetxt, String audiotxt, String pdftxt) {
        classdatahelperclass data = new classdatahelperclass(pepisodetxt,audiotxt,pdftxt);
        db.collection("Class").document(master).collection("materials")
                .document(pepisodetxt).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              uploadingadapter.uploadDissmiss();
                Toast.makeText(masteractivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                caudiotxt = "null";
                cpdftxt = "null";
                classadfloat.setVisibility(View.VISIBLE);
                classaudio.setVisibility(View.GONE);
                classpdf.setVisibility(View.GONE);
                episode.setVisibility(View.GONE);
                episode.setText("");
                classupload.setVisibility(View.GONE);
                classforground.setBackgroundColor(Color.parseColor("#00039F39"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadingadapter.uploadDissmiss();
                Toast.makeText(masteractivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                caudiotxt = "null";
                cpdftxt = "null";
                classadfloat.setVisibility(View.VISIBLE);
                classaudio.setVisibility(View.GONE);
                classpdf.setVisibility(View.GONE);
                episode.setVisibility(View.GONE);
                episode.setText("");
                classupload.setVisibility(View.GONE);
                classforground.setBackgroundColor(Color.parseColor("#00039F39"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK){
            caudiourls = data.getData();
            caudiotxt = String.valueOf(caudiourls);
        }
        else if (requestCode == PICK_PDF && resultCode == RESULT_OK){
            cpdfurl = data.getData();
            cpdftxt = String.valueOf(cpdfurl);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        madapteruser.startListening();
        madapteradmin.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        madapteruser.stopListening();
        madapteradmin.stopListening();
    }

    private class materialviewholde extends RecyclerView.ViewHolder {
        TextView episodename,materialaudio,materialpdf;
        ImageView materialdaelet;
        public materialviewholde(@NonNull View itemView) {
            super(itemView);
            episodename = itemView.findViewById(R.id.recyclerepisode);
            materialaudio = itemView.findViewById(R.id.materialaudio);
            materialpdf = itemView.findViewById(R.id.materialpdf);
            materialdaelet = itemView.findViewById(R.id.materialdelet);
        }
    }
}