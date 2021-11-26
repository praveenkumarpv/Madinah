package com.m24.madinah;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.Slide;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import Adapters.SliderAdapterExample;
import Adapters.adrecyclerview;
import helperclass.Ad;
import helperclass.adupload;

public class HomeActivity extends AppCompatActivity {
    ConstraintLayout welcomscreen;
    LinearLayout secondhalf,maincont,burtha,song,date,classview;
    RelativeLayout fristhalf;
    CardView headercard;
    ImageView headerimage,adminpanal;
    SliderView sliderView;
    public List<Ad> mSliderItems = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fristhalf = findViewById(R.id.fristhalf);
        secondhalf = findViewById(R.id.secondhalf);
        welcomscreen = findViewById(R.id.welcomcardview);
        maincont = findViewById(R.id.maincontainer);
        headercard = findViewById(R.id.headercardview);
        headerimage = findViewById(R.id.headerimage);
        sliderView = findViewById(R.id.imageSliderview);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Glide.with(MainActivity.this).load(R.drawable.bluedom).into(headerimage);
                Transition transitions = new Fade();
                transitions.setDuration(600);
                transitions.addTarget(R.id.headerimage);
                TransitionManager.beginDelayedTransition(headercard, transitions);
                headerimage.setVisibility(View.VISIBLE);
                Transition transition = new Slide(Gravity.BOTTOM);
                transition.setDuration(700);
                transition.addTarget(R.id.secondhalf);
                transition.addTarget(R.id.welcomcardview);
                transition.addTarget(R.id.fristhalf);
                TransitionManager.beginDelayedTransition(maincont,transition);
                secondhalf.setVisibility( View.VISIBLE);
                welcomscreen.setVisibility(View.VISIBLE);

            }
        },600);
        db.collection("Ad").document("list").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                adupload adupload = documentSnapshot.toObject(adupload.class);
                mSliderItems = adupload.getAdList();
                SliderAdapterExample adapter = new SliderAdapterExample(HomeActivity.this,mSliderItems);
                sliderView.setSliderAdapter(adapter);
                sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setIndicatorSelectedColor(Color.WHITE);
                sliderView.setIndicatorUnselectedColor(Color.GRAY);
                sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                sliderView.startAutoCycle();
            }
        });



///////////////////////////////////condrolsection////////////////////////////////////

        adminpanal = findViewById(R.id.adminpanel);
        burtha = findViewById(R.id.burtha);
        song = findViewById(R.id.songbt);
        date = findViewById(R.id.dateactivity);
        classview = findViewById(R.id.classview);
        adminpanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent admin = new Intent(HomeActivity.this,Adminpanal.class);
                startActivity(admin);
            }
        });
        burtha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent admin = new Intent(HomeActivity.this,Burtha.class);
                startActivity(admin);
            }
        });
        song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent songintent = new Intent(HomeActivity.this,songsactivity.class);
                startActivity(songintent);
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dateintent = new Intent(HomeActivity.this,madethekiram.class);
                startActivity(dateintent);
            }
        });
        classview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classintent = new Intent(HomeActivity.this,activityclass.class);
                startActivity(classintent);
            }
        });
    }
}