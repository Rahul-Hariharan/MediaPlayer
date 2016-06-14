package com.example.a539117.sampleapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.a539117.sampleapplication.action.FOO";
    private static final String ACTION_BAZ = "com.example.a539117.sampleapplication.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.a539117.sampleapplication.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.a539117.sampleapplication.extra.PARAM2";

    public DownloadService() {
        super("DownloadService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            /*final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/

            try {

                File folder = new File(getApplicationContext().getFilesDir(),"/videos");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String path = folder +"/" +"video.mp4";
                Log.v("path",path);
                URL url = new URL("http://download.wavetlan.com/SVV/Media/HTTP/MP4/ConvertedFiles/Media-Convert/Unsupported/test7.mp4");
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
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.RECEIVER_ACTION);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            sendBroadcast(broadcastIntent);
                            break;
                        default:
                            break;
                    }
                }
                //clean up
                outStream.flush();
                outStream.close();
                inStream.close();

                /*Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.RECEIVER_ACTION);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadcastIntent);*/

            }catch(IOException exception){}
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
