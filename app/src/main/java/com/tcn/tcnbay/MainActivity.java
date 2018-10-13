package com.tcn.tcnbay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

public class MainActivity extends Activity {

    SimpleExoPlayer player;
    TDNDataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int videoId = getIntent().getIntExtra("videoId", 0);
        if (videoId == 0) {
            if (isTaskRoot())
                startActivity(new Intent(MainActivity.this, VideoList.class));
            finish();
        }
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        PlayerView playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        playerView.setShowBuffering(true);
        playerView.setUseController(true);
        TDNFactory factory = new TDNFactory(videoId);
        ds = factory.getDataSource();
        MediaSource videoSource = new ExtractorMediaSource.Factory(factory).createMediaSource(Uri.parse("data://video"));
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        this.player = player;
    }

    @Override
    public void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ds.requestTaskCancellation();
        player.stop();
    }


}
