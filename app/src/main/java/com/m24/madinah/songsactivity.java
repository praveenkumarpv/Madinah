package com.m24.madinah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.uploadingadapter;
import helperclass.burthadb;

public class songsactivity extends AppCompatActivity {
    private final int PICK_AUDIO = 1,PICK_PDF = 1,PICK_PHOTO = 1;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    Spinner sspinner;
    EditText sname;
    TextView sselect;
    Uri surls,downloadurl;
    int sselectindicator = 0;
    ViewPager widget,rehunsuba;
    TabLayout tabHost,rehunsubatab;
    String [] soption = new String[]{"Audio","Pdf"};
    FloatingActionButton sfloatingActionButton;
    String select,snametxt,downloadurltxt;
    ConstraintLayout songiconforgrounds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songsactivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        widget = findViewById(R.id.songwidgetviewpager);
        tabHost = findViewById(R.id.songpdfaudiotab);
        tabHost.addTab(tabHost.newTab().setText("Audio"));
        tabHost.addTab(tabHost.newTab().setText("Pdf"));
        tabHost.setTabGravity(TabLayout.GRAVITY_FILL);
        song song = new song(getSupportFragmentManager(),this,tabHost.getTabCount());
        widget.setAdapter(song);
        widget.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabHost));
        tabHost.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                widget.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0 || tab.getPosition()==1) {
                    song.notifyDataSetChanged();
                    if (tab.getPosition()==0){
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

////////////////////////////upload///////////////////////////////////////
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sspinner = findViewById(R.id.songseletspinner);
        List<String> option = new ArrayList<>(Arrays.asList(soption));
        ArrayAdapter<String> sspinneradapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,option);
        sspinneradapter.setDropDownViewResource(R.layout.spinnervalue);
        sspinner.setAdapter(sspinneradapter);
        sfloatingActionButton = findViewById(R.id.sfloatingActionButton);
        sselect = findViewById(R.id.sselectindicator);
        sname = findViewById(R.id.songfilename);
        songiconforgrounds = findViewById(R.id.songiconforground);
        SharedPreferences settings = songsactivity.this.getSharedPreferences("preference",MODE_PRIVATE);
        if (! settings.getString("uid","").equals("")){
            sfloatingActionButton.setVisibility(View.VISIBLE);
        }
        sfloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfloatingActionButton.setVisibility(View.GONE);
                sselect.setVisibility(View.VISIBLE);
                sname.setVisibility(View.VISIBLE);
                sspinner.setVisibility(View.VISIBLE);
                songiconforgrounds.setBackgroundColor(Color.parseColor("#BE039F39"));
            }
        });
        sselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sselectindicator == 0){
                    select = sspinner.getSelectedItem().toString().trim();
                    if (select.isEmpty()){
                        Toast.makeText(songsactivity.this, "Select file type", Toast.LENGTH_SHORT).show();
                    }
                    else if (select == "Audio") {
                        Intent baudio = new Intent();
                        baudio.setType("audio/*");
                        baudio.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        startActivityForResult(Intent.createChooser(baudio, "Select Audio"), PICK_AUDIO);
                    }
                    else if (select == "Pdf"){
                        Intent bpdf = new Intent();
                        bpdf.setType("application/pdf");
                        bpdf.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        startActivityForResult(Intent.createChooser(bpdf, "Select Pdf"), PICK_PDF);
                    }

                    sselectindicator++;
                }
                else if (sselectindicator == 1){
                    Toast.makeText(songsactivity.this, "uploadstart", Toast.LENGTH_SHORT).show();
                    uploadingadapter uploadingadapter = new uploadingadapter(songsactivity.this);
                    uploadingadapter. Startuploading();
                    snametxt = sname.getText().toString().trim();
                    if (snametxt.isEmpty()){
                        Toast.makeText(songsactivity.this, "Enter the file name", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        StorageReference storageReference = storageRef.child("mahdesong/"+ select +"/"+ snametxt);
                        storageReference.putFile(surls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Toast.makeText(Burtha.this, "storage", Toast.LENGTH_SHORT).show();
                                        downloadurl = uri;
                                        downloadurltxt = downloadurl.toString();
                                        Date c = Calendar.getInstance().getTime();
                                        System.out.println("Current time => " + c);
                                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                        String formattedDate = df.format(c);
                                        burthadb bdb = new burthadb(snametxt,formattedDate,downloadurltxt);

                                        db.collection("Madinahdata").document("mahdesong")
                                                .collection(select).document(snametxt).set(bdb).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                sfloatingActionButton.setVisibility(View.VISIBLE);
                                                sselect.setVisibility(View.GONE);
                                                sname.setVisibility(View.GONE);
                                                sspinner.setVisibility(View.GONE);
                                                sselect.setText("");
                                                sname.setText("");
                                                sselect.setText("Select file");
                                                sselectindicator--;
                                                songiconforgrounds.setBackgroundColor(Color.parseColor("#00039F39"));
                                                Toast.makeText(songsactivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                uploadingadapter.uploadDissmiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(songsactivity.this, "upload failed", Toast.LENGTH_SHORT).show();
                                                uploadingadapter.uploadDissmiss();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(songsactivity.this, "Fail to upload resource", Toast.LENGTH_SHORT).show();
                                        uploadingadapter.uploadDissmiss();
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

            }
        });
    }
    public class song extends FragmentPagerAdapter {
        private Context context;
        private int count;

        public song(@NonNull FragmentManager fm, Context context, int count) {
            super(fm);
            this.context = context;
            this.count = count;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    songaudiofragment songaaudio_fragment =new songaudiofragment();
                    return songaaudio_fragment;
                case 1:
                    songpdffragment songpdffragment = new songpdffragment();
                    return songpdffragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return count;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            surls = data.getData();
            sselect.setText("Upload");
            //  bselectindicator++;
        }
        if (requestCode == PICK_PDF && resultCode == RESULT_OK) {
            surls = data.getData();
            sselect.setText("Upload");
            //bselectindicator++;
        }
    }
}