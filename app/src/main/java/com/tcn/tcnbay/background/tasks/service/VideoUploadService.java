package com.tcn.tcnbay.background.tasks.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.tcn.tcnbay.R;
import com.tcn.tcnbay.activities.VideoList;
import com.tcn.tcnbay.background.tasks.VideoUploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.tcn.tcnbay.App.CHANNEL_ID;

public class VideoUploadService extends Service {

    private int jobCount = 0;
    private int totJob = 0;


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
        SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
        String ip = sp.getString("host", "casaamorim.no-ip.biz");
        int port = sp.getInt("port", 50000);
        VideoUploadTask task = new VideoUploadTask(is, fileName,this, ip, port);
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
