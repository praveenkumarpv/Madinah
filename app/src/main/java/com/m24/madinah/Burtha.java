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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import helperclass.burthadb;
import Adapters.uploadingadapter;
import helperclass.parayanam;

public class Burtha extends AppCompatActivity {
    private final int PICK_AUDIO = 1,PICK_PDF = 1,PICK_IMAGE = 2;
    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage storage;
    Uri burls,downloadurl,imageurl,imagedownloadurl;
    ViewPager widget,rehunsuba;
    TabLayout tabHost,rehunsubatab;
    Spinner bspinner;
    EditText bname;
    TextView bselect,skip,selectimage;
    int bselectindicator = 0;
    String [] boption = new String[]{"BurthaAudio","BurthaPdf","RehusobaAudio"};
    FloatingActionButton bfloatingActionButton;
    String select,bnametxt,downloadurltxt,iurlstxt;
    LinearLayout nameselectindicatorlayout,burthasliderlayout,rehusubalayout;
    ImageView leftswip,rightswip;
    ConstraintLayout burthaiconforgrounds;
    uploadingadapter uploadingadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burtha);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        widget = findViewById(R.id.burthawidgetviewpager);
        tabHost = findViewById(R.id.burthapdfaudiotab);
        tabHost.addTab(tabHost.newTab().setText("Audio"));
        tabHost.addTab(tabHost.newTab().setText("Pdf"));
        tabHost.addTab(tabHost.newTab().setText("Parayanam"));
        tabHost.setTabGravity(TabLayout.GRAVITY_FILL);
        burtha burtha = new burtha(getSupportFragmentManager(),this,tabHost.getTabCount());
        widget.setAdapter(burtha);
        widget.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabHost));
        tabHost.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                widget.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0 || tab.getPosition()==1 || tab.getPosition() == 2) {
                    burtha.notifyDataSetChanged();
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

////////////////////////////////upload//////////////////

       db = FirebaseFirestore.getInstance();
       storage = FirebaseStorage.getInstance();
       storageRef = storage.getReference();
       List <String> option = new ArrayList<>(Arrays.asList(boption));
       bspinner = findViewById(R.id.burthaseletspinner);
       ArrayAdapter<String> bspinneradapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,option);
       bspinneradapter.setDropDownViewResource(R.layout.spinnervalue);
       bspinner.setAdapter(bspinneradapter);
       bfloatingActionButton = findViewById(R.id.bfloatingActionButton);
       bselect = findViewById(R.id.bselectindicator);
       bname = findViewById(R.id.burthafilename);
       burthaiconforgrounds = findViewById(R.id.burthaiconforground);
       skip = findViewById(R.id.skip);
       selectimage = findViewById(R.id.imageselectindicator);
       uploadingadapter = new uploadingadapter(Burtha.this);
        SharedPreferences settings = Burtha.this.getSharedPreferences("preference",MODE_PRIVATE);
        if (! settings.getString("uid","").equals("")){
            bfloatingActionButton.setVisibility(View.VISIBLE);
        }
       bfloatingActionButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               bfloatingActionButton.setVisibility(View.GONE);
               burthaiconforgrounds.setBackgroundColor(Color.parseColor("#BE039F39"));
               bselect.setVisibility(View.VISIBLE);
               bname.setVisibility(View.VISIBLE);
               bspinner.setVisibility(View.VISIBLE);
           }
       });
       bselect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (bselectindicator == 0){
                   select = bspinner.getSelectedItem().toString().trim();
                   if (select.isEmpty()){
                       Toast.makeText(Burtha.this, "Select file type", Toast.LENGTH_SHORT).show();
                   }
                   else if (select == "BurthaAudio" || select == "RehusobaAudio") {
                       Intent baudio = new Intent();
                       baudio.setType("audio/*");
                       baudio.setAction(Intent.ACTION_OPEN_DOCUMENT);
                       startActivityForResult(Intent.createChooser(baudio, "Select Audio"), PICK_AUDIO);
                   }
                   else if (select == "BurthaPdf"){
                       Intent bpdf = new Intent();
                       bpdf.setType("application/pdf");
                       bpdf.setAction(Intent.ACTION_OPEN_DOCUMENT);
                       startActivityForResult(Intent.createChooser(bpdf, "Select Pdf"), PICK_PDF);
                   }

                   bselectindicator++;
               }
               else if (bselectindicator == 1 && select != "RehusobaAudio" ){
                   Toast.makeText(Burtha.this, "uploadstart", Toast.LENGTH_SHORT).show();
                   uploadingadapter. Startuploading();
                   bnametxt = bname.getText().toString().trim();
                   if (bnametxt.isEmpty()){
                       Toast.makeText(Burtha.this, "Enter the file name", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       StorageReference storageReference = storageRef.child("Burtha/"+ select +"/"+ bnametxt);
                       storageReference.putFile(burls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                       burthadb bdb = new burthadb(bnametxt,formattedDate,downloadurltxt);

                                    db.collection("Madinahdata").document("Burtha")
                                            .collection(select).document(bnametxt).set(bdb).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            bfloatingActionButton.setVisibility(View.VISIBLE);
                                            bselect.setVisibility(View.GONE);
                                            bname.setVisibility(View.GONE);
                                            bspinner.setVisibility(View.GONE);
                                            bselect.setText("Select file");
                                            bselect.setText("");
                                            bname.setText("");
                                            bselect.setText("Select file");
                                            burthaiconforgrounds.setBackgroundColor(Color.parseColor("#00039F39"));
                                            bselectindicator--;
                                            Toast.makeText(Burtha.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                            uploadingadapter.uploadDissmiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Burtha.this, "upload failed", Toast.LENGTH_SHORT).show();
                                            uploadingadapter.uploadDissmiss();
                                        }
                                    });
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(Burtha.this, "Fail to upload resource", Toast.LENGTH_SHORT).show();
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
               else if (bselectindicator == 1 && select == "RehusobaAudio"){
                   Toast.makeText(Burtha.this, "inside rehonsubaupload", Toast.LENGTH_SHORT).show();
                   if (iurlstxt.isEmpty()){
                       Toast.makeText(Burtha.this, "uploadstart", Toast.LENGTH_SHORT).show();
                       uploadingadapter. Startuploading();
                       bnametxt = bname.getText().toString().trim();
                       if (bnametxt.isEmpty()){
                           Toast.makeText(Burtha.this, "Enter the file name", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           StorageReference storageReference = storageRef.child("Burtha/"+ select +"/"+ bnametxt);
                           storageReference.putFile(burls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                           parayanam parayanam = new parayanam(bnametxt,downloadurltxt,formattedDate,"no");
                                           db.collection("Madinahdata").document("Burtha")
                                                   .collection(select).document(bnametxt).set(parayanam).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   bfloatingActionButton.setVisibility(View.VISIBLE);
                                                   bselect.setVisibility(View.GONE);
                                                   bname.setVisibility(View.GONE);
                                                   bspinner.setVisibility(View.GONE);
                                                   bselect.setText("Select file");
                                                   bselect.setText("");
                                                   bname.setText("");
                                                   bselect.setText("Select file");
                                                   burthaiconforgrounds.setBackgroundColor(Color.parseColor("#00039F39"));
                                                   bselectindicator--;
                                                   Toast.makeText(Burtha.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                   uploadingadapter.uploadDissmiss();
                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   Toast.makeText(Burtha.this, "upload failed", Toast.LENGTH_SHORT).show();
                                                   uploadingadapter.uploadDissmiss();
                                               }
                                           });
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(Burtha.this, "Fail to upload resource", Toast.LENGTH_SHORT).show();
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
                   else{
                       uploadingadapter. Startuploading();
                       bnametxt = bname.getText().toString().trim();
                       if (bnametxt.isEmpty()){
                           Toast.makeText(Burtha.this, "Enter the file name", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           StorageReference storageReference = storageRef.child("Burtha/" + select + "/" + bnametxt);
                           storageReference.putFile(burls).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                 storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                     @Override
                                     public void onSuccess(Uri uri) {
                                       downloadurl = uri;
                                         StorageReference istorageReference = storageRef.child("Burtha/RehusobaImage/" + bnametxt);
                                         istorageReference.putFile(imageurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                             @Override
                                             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              istorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                   imagedownloadurl = uri;
                                                   downloadurltxt = String.valueOf(downloadurl);
                                                   iurlstxt = String.valueOf(imagedownloadurl);
                                                      Date c = Calendar.getInstance().getTime();
                                                      System.out.println("Current time => " + c);
                                                      SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                                      String formattedDate = df.format(c);
                                                      parayanam parayanam = new parayanam(bnametxt,downloadurltxt,formattedDate,iurlstxt);
                                                      db.collection("Madinahdata").document("Burtha")
                                                              .collection(select).document(bnametxt).set(parayanam).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void unused) {
                                                              bfloatingActionButton.setVisibility(View.VISIBLE);
                                                              bselect.setVisibility(View.GONE);
                                                              bname.setVisibility(View.GONE);
                                                              bspinner.setVisibility(View.GONE);
                                                              bselect.setText("Select file");
                                                              bselect.setText("");
                                                              bname.setText("");
                                                              bselect.setText("Select file");
                                                              burthaiconforgrounds.setBackgroundColor(Color.parseColor("#00039F39"));
                                                              bselectindicator--;
                                                              Toast.makeText(Burtha.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                              uploadingadapter.uploadDissmiss();
                                                          }
                                                      });
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
                                 uploadingadapter.uploadDissmiss();
                               }
                           });
                       }
                   }
               }
           }
       });
       selectimage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                   intent.setType("image/*");
                   startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
           }
       });
       skip.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               bselect.setText("Upload");
               bselect.setVisibility(View.VISIBLE);
               skip.setVisibility(View.GONE);
               selectimage.setVisibility(View.GONE);
               iurlstxt = "";
           }
       });

    }

 public class burtha extends FragmentPagerAdapter {
        private Context context;
        private int count;

        public burtha(@NonNull FragmentManager fm, Context context, int count) {
            super(fm);
            this.context = context;
            this.count = count;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    burthaaudio_fragment burthaaudio_fragment = new burthaaudio_fragment();
                    return burthaaudio_fragment;
                case 1:
                    burthapdf_fragment burthapdf_fragment = new burthapdf_fragment();
                    return burthapdf_fragment;
                case 2:
                    parayanam_fragment parayanam_fragment = new parayanam_fragment();
                    return parayanam_fragment;
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
        final int unmasked = requestCode & 0x0000ffff;
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            burls = data.getData();
            //  bselectindicator++;
            if (select == "RehusobaAudio" ){
                bselect.setVisibility(View.GONE);
                skip.setVisibility(View.VISIBLE);
                selectimage.setVisibility(View.VISIBLE);
            }
            else{
                bselect.setText("Upload");
            }
        }
        else if (requestCode == PICK_PDF && resultCode == RESULT_OK) {
            burls = data.getData();
            bselect.setText("Upload");
        }
       else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Toast.makeText(Burtha.this, "inside pick image", Toast.LENGTH_SHORT).show();
            imageurl = data.getData();
            iurlstxt = String.valueOf(imageurl);
            bselect.setText("Upload");
            bselect.setVisibility(View.VISIBLE);
            skip.setVisibility(View.GONE);
            selectimage.setVisibility(View.GONE);
            //bselectindicator++;
        }
    }
}