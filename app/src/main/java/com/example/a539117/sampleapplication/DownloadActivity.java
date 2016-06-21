package com.example.a539117.sampleapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadActivity extends AppCompatActivity {

    VideoView mVideoView;
    int currentposition = 0;
    private Handler mHandler;
    MediaController mMediaController;
    ScheduledExecutorService mScheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();
        File folder = new File(getApplicationContext().getFilesDir(),"/videos");
        final String path = folder + "/" +"video.mp4";

        mVideoView = (VideoView)findViewById(R.id.video_view);
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        Log.v("error", "media error unknown");
                        mediaErrorExtra(mp, extra, path);
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        Log.v("error", "media error server died");
                        mediaErrorExtra(mp, extra, path);
                        break;
                }
                return true;
            }
        });
        if(isNetworkAvailable()) {
            File book = new File(path);
            if (book.exists()) {
                book.delete();
            }
            new DownloadTask().execute("http://download.wavetlan.com/SVV/Media/HTTP/MP4/ConvertedFiles/Media-Convert/Unsupported/test7.mp4");
        }
        else {
            mVideoView.setVideoPath(path);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.v("isprepared", "isprepared");
                    mVideoView.start();
                }
            });
        }

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mScheduledExecutorService != null)
                    mScheduledExecutorService.shutdown();
            }
        });

        mScheduledExecutorService =
                new ScheduledExecutor(1);

        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable(){
            @Override
            public void run() {
                mVideoView.post(UpdateCurrentPosition);
            }
        },3000,1000,TimeUnit.MILLISECONDS);
    }

    Runnable UpdateCurrentPosition = new Runnable(){

        @Override
        public void run() {
            currentposition = mVideoView.getCurrentPosition();
        }
    };

    private void mediaErrorExtra(MediaPlayer mp, int extra, String path){
        switch(extra){
            case MediaPlayer.MEDIA_ERROR_IO:

                if(!mp.isPlaying()){
                    mVideoView.setVideoPath(path);
                    mMediaController = new MediaController(this);
                    mVideoView.setMediaController(mMediaController);
                    mVideoView.start();
                    mVideoView.seekTo(currentposition);
                }
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Log.v("error extra","media error malformed");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Log.v("error extra","media error unsupported");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.v("error extra","media error timed out");
                break;
        }
    }

     private class DownloadTask extends AsyncTask<String,String,Void> {

        @Override
        protected Void doInBackground(String[] params) {

            try {
                File folder = new File(getApplicationContext().getFilesDir(),"/videos");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                java.lang.String path = folder +"/" +"video.mp4";
                Log.v("path", path);
                URL url = new URL((String)params[0]);
                URLConnection connection = url.openConnection();
                InputStream inputstream = connection.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(inputstream, 1024 * 5);
                FileOutputStream outStream = new FileOutputStream(path);
                byte[] buff = new byte[5 * 1024];

                //Read bytes (and store them) until there is nothing more to read(-1)
                int filesize = connection.getContentLength();
                int downloaded = 0;
                int len;
                while ((len = inStream.read(buff)) != -1) {
                    outStream.write(buff,0,len);
                    downloaded += len;
                    int percentage = (downloaded * 100)/filesize;
                    Log.v("percentage",Integer.toString(percentage));
                    switch(percentage){
                        case 25:
                            publishProgress(path);
                            break;
                        default:
                            break;
                    }
                }
                //clean up
                outStream.flush();
                outStream.close();
                inStream.close();
            }catch(IOException exception){}
            return null;
        }

         @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);
             String path = values[0];
             mVideoView.setVideoPath(path);
             //mVideoView.start();
             mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(MediaPlayer mp) {
                     Log.v("isprepared", "isprepared");
                     mVideoView.start();
                 }
             });
         }
     }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        NetworkInfo.State network = networkInfo.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
