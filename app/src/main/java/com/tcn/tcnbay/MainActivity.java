package com.tcn.tcnbay;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    FDVideoView vv;
    Button b;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "deu ruim", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        PlayerView playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        MediaSource videoSource = new ExtractorMediaSource.Factory(new TestFactory()).createMediaSource(Uri.parse("data://nothin"));
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
//        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        vv = findViewById(R.id.videoView);
//        b = findViewById(R.id.btn);
//        MediaController mc = new MediaController(this);
//        mc.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                Toast.makeText(getApplicationContext(), "scrolled", Toast.LENGTH_SHORT).show();
//            }
//        });
//        mc.setAnchorView(vv);
//        vv.setMediaController(mc);
//        String ip = "172.20.9.239";
//        int port = 50000;
//        VideoDownloadTask mct = new VideoDownloadTask(ip,port);
//        mct.execute();
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    vv.setVideoFD((new FileInputStream(new File("/sdcard/tempVideo.mp4")).getFD()));
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                vv.seekTo(0);
//                vv.start();
//            }
//        });
    }


}
