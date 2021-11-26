package Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m24.madinah.R;

import java.io.IOException;

public class audioplayadapter   {
    Activity activity;
    AlertDialog dialog;
    String audiolink,audioname;
    TextView audionameview;
    MediaPlayer mediaPlayer;
    ImageView play,pause;
    ProgressBar progressBar;

    public audioplayadapter(Activity activity, String audiolink,String audioname) {
        this.activity = activity;
        this.audiolink = audiolink;
        this.audioname = audioname;
    }
    public void startplaying(){
        mediaPlayer = new MediaPlayer();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setCancelable(true);
        View v = inflater.inflate(R.layout.audioplatlayout,null);
        builder.setView(v);
        play = v.findViewById(R.id.play);
        pause = v.findViewById(R.id.audiopause);
        progressBar = v.findViewById(R.id.audioprogress);
        audionameview = v.findViewById(R.id.popupaudioname);
        audionameview.setText(audioname);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplay();
                play.setVisibility(View.GONE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopplay();
            }
        });
        dialog = builder.create();
        dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void startplay(){
        try {
            progressBar.setVisibility(View.VISIBLE);
            mediaPlayer.setDataSource(audiolink);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (mediaPlayer.isPlaying()){
                progressBar.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopplay(){
        mediaPlayer.pause();
        dialog.dismiss();
    }
}
