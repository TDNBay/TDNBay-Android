package com.tcn.tcnbay;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.tcn.tcnbay.App.CHANNEL_ID;

public class VideoUploadService extends Service {

    private ArrayList<VideoUploadTask> jobs;

    private int jobCount = 0;
    private int totJob = 0;

    @Override
    public void onCreate() {
        jobs = new ArrayList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notifIntent = new Intent(this, VideoList.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notifIntent, 0);
        Uri fileUri = intent.getParcelableExtra("uri");
        String fileName = intent.getStringExtra("videoTitle");
        InputStream is;
        try {
            is = getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return START_NOT_STICKY;
        }
        VideoUploadTask task = new VideoUploadTask(is, fileName,this);
        task.execute();
        jobCount += 1;
        totJob += 1;
        Notification n = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.video_upload_notif))
                .setContentText(getResources().getQuantityString(R.plurals.video_upload_progress_notif, jobCount, jobCount))
                .setSmallIcon(R.drawable.ic_videocam)
                .setContentIntent(pi)
                .build();
        startForeground(1, n);
        return START_NOT_STICKY;
    }

    public void onTaskFinished() {
        jobCount -= 1;
        if (jobCount == 0)
            stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
