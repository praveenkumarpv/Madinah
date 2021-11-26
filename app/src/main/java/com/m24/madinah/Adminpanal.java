package com.m24.madinah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import helperclass.Ad;
import helperclass.adupload;
import Adapters.uploadingadapter;
import Adapters.adrecyclerview;

public class Adminpanal extends AppCompatActivity {
    EditText email,password,adname;
    String emailtxt,passwordtxt,adnametxt;
    Button login;
    TextView logout,selectfileindicator;
    public ConstraintLayout loginform,adform;
    LinearLayout mainlayout;
    CardView adimagecardview;
    ImageView addicon;
    RecyclerView adrecycler;
    int indicator = 0;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    public Uri selimage;
    public List <Ad> adList = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpanal);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginbutton);
        loginform = findViewById(R.id.loginform);
        mainlayout = findViewById(R.id.mainlayout);
        logout = findViewById(R.id.logout);
        adimagecardview = findViewById(R.id.adimagecarview);
        SharedPreferences settings = Adminpanal.this.getSharedPreferences("preference",MODE_PRIVATE);
        if (! settings.getString("uid","").equals("")){
            loginform.setVisibility(View.GONE);
            mainlayout.setVisibility(View.VISIBLE);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtxt = email.getText().toString().trim();
                passwordtxt = password.getText().toString().trim();
                if (emailtxt.isEmpty()){
                    Toast.makeText(Adminpanal.this, "Invalid e-mail id", Toast.LENGTH_SHORT).show();
                }
                else if (passwordtxt.isEmpty()){
                    Toast.makeText(Adminpanal.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }
                else if (!(emailtxt.isEmpty()) && !(passwordtxt.isEmpty())){
                   mAuth.signInWithEmailAndPassword(emailtxt,passwordtxt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           String Uid = authResult.getUser().getUid();
                           SharedPreferences settings = Adminpanal.this.getSharedPreferences("preference", MODE_PRIVATE);
                           SharedPreferences.Editor uid = settings.edit();
                           uid.putString("uid", Uid);
                           uid.commit();
                           loginform.setVisibility(View.GONE);
                           mainlayout.setVisibility(View.VISIBLE);
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(Adminpanal.this, "Login failed", Toast.LENGTH_SHORT).show();
                       }
                   });
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = Adminpanal.this.getSharedPreferences("preference", MODE_PRIVATE);
                SharedPreferences.Editor uid = settings.edit();
                uid.putString("uid", "");
                uid.commit();
                loginform.setVisibility(View.VISIBLE);
                mainlayout.setVisibility(View.GONE);
            }
        });


        ///////////////////////////////////////////mainlayout////////////////////////////////
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        selectfileindicator = findViewById(R.id.adselectindicator);
        addicon = findViewById(R.id.adaddimagebt);
        adname = findViewById(R.id.adname);
        adform = findViewById(R.id.addlayout);
        adrecycler = findViewById(R.id.adviewrecycler);
        db.collection("Ad").document("list").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            adupload adupload = documentSnapshot.toObject(adupload.class);
            adList = adupload.getAdList();
               // adList = (List<Ad>) documentSnapshot.get("adList");
                adrecyclerview adrecyclerview = new adrecyclerview(adList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(Adminpanal.this);
                adrecycler.setLayoutManager(layoutManager);
                adrecycler.setAdapter(adrecyclerview);
            }
        });
        adimagecardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             addicon.setVisibility(View.GONE);
             adform.setVisibility(View.VISIBLE);
            }
        });
        selectfileindicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indicator == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    indicator++;
                    selectfileindicator.setText("Upload");
                }
                else if (indicator == 1) {
                    adnametxt = adname.getText().toString().trim();
                    if (adnametxt.isEmpty()) {
                        Toast.makeText(Adminpanal.this, "Invalied name", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadingadapter up = new uploadingadapter(Adminpanal.this);
                        up.Startuploading();
                        StorageReference storageReference = storageRef.child("Ad/" + adnametxt);
                        storageReference.putFile(selimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       String i = uri.toString();
                                       adList.add(new Ad(adnametxt, i));
                                       adupload adupload = new adupload(adList);
                                       db.collection("Ad").document("list").set(adupload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {
                                               selectfileindicator.setText("Select file");
                                               addicon.setVisibility(View.VISIBLE);
                                               adform.setVisibility(View.GONE);
                                               up.uploadDissmiss();
                                               Toast.makeText(Adminpanal.this, "Upload success", Toast.LENGTH_SHORT).show();
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(Adminpanal.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                               addicon.setVisibility(View.VISIBLE);
                                               adform.setVisibility(View.GONE);
                                               up.uploadDissmiss();
                                           }
                                       });
                                   }
                               });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Adminpanal.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                addicon.setVisibility(View.VISIBLE);
                                adform.setVisibility(View.GONE);
                                up.uploadDissmiss();
                            }
                        });
                    }
                }
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //final int unmasked = requestCode & 0x0000ffff;
        if (requestCode == PICK_IMAGE_REQUEST && data != null){
            selimage = data.getData();
            //Toast.makeText(Adminpanal.this, "In side if result", Toast.LENGTH_SHORT).show();
        }
        else
        {

        }
    }

}