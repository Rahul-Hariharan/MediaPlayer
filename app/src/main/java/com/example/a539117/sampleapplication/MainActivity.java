package com.example.a539117.sampleapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    VideoView mVideoView;
    VideoReceiver mReceiver;
    public static final String RECEIVER_ACTION = "com.receiver.videoreceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File folder = new File(getApplicationContext().getFilesDir(),"/videos");
        String path = folder + "/" +"video.mp4";
        File book = new File(path);
        if (book.exists()) {
            //book.delete();
        }
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        mReceiver = new VideoReceiver();
        mVideoView = (VideoView)findViewById(R.id.video_view);
    }


    public class VideoReceiver extends BroadcastReceiver{

        VideoReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v("shiot","shoit");
            File folder = new File(getApplicationContext().getFilesDir(),"/videos");
            String path = folder + "/" +"video.mp4";
            mVideoView.setVideoPath(path);
            mVideoView.start();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver,intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
