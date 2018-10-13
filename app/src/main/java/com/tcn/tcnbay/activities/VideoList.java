package com.tcn.tcnbay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tcn.tcnbay.util.PermissionDialog;
import com.tcn.tcnbay.R;
import com.tcn.tcnbay.adapter.VideoListAdapter;
import com.tcn.tcnbay.background.tasks.VideoListDownloadTask;
import com.tcn.tcnbay.interfaces.IDialogCallback;
import com.tcn.tcnbay.interfaces.IDownloadCallback;
import com.tcn.tcnbay.interfaces.OnListFragmentInteractionListener;
import com.tcn.tcnbay.model.Video;

import java.util.List;

public class VideoList extends AppCompatActivity implements IDownloadCallback, OnListFragmentInteractionListener, IDialogCallback {


    private RecyclerView list;
    private SwipeRefreshLayout swipeContainer;
    private Video pendingVideoForPermission;
    private String ip;
    private int port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView list = findViewById(R.id.videoList);
        final FloatingActionButton fab = findViewById(R.id.videoUploadButton);
        this.list = list;
        SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
        ip = sp.getString("host", "casaamorim.no-ip.biz");
        port = sp.getInt("port", 50000);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoList.this, UploadVideoActivity.class));
            }
        });
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VideoListDownloadTask task = new VideoListDownloadTask(VideoList.this, ip, port);
                task.execute();
            }
        });
        askForPermission(1);
        list.setLayoutManager(new LinearLayoutManager(this));
        swipeContainer.setRefreshing(true);
        VideoListDownloadTask task = new VideoListDownloadTask(this, ip, port);
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_server:
                startActivityForResult(new Intent(this, ServerSetupActivity.class), 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                swipeContainer.setRefreshing(true);
                SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
                ip = sp.getString("host", "casaamorim.no-ip.biz");
                port = sp.getInt("port", 50000);
                VideoListDownloadTask task = new VideoListDownloadTask(this, ip, port);
                task.execute();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onListDownloadFinished(List list) {
        List<Video> videoList = (List<Video>) list;
        if (this.list.getAdapter() == null) {
            SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
            this.list.setAdapter(new VideoListAdapter(this, videoList, this, ip, sp.getInt("httpport", 8000)));
            swipeContainer.setRefreshing(false);
            return;
        }
        VideoListAdapter adapter = (VideoListAdapter) this.list.getAdapter();
        adapter.updateList(videoList);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onListDownloadError(int reason) {
        swipeContainer.setRefreshing(false);
        Log.e("ERROR", "Reason: " + reason);
    }

    @Override
    public void onListFragmentInteraction(Video item) {
        if (!checkForPermission()) {
            this.pendingVideoForPermission = item;
            showPermissionDialog();
            return;
        }
        Intent intent = new Intent(VideoList.this, MainActivity.class);
        intent.putExtra("videoId", item.id);
        startActivity(intent);
    }

    void showPermissionDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        PermissionDialog p = new PermissionDialog();
        p.setCallback(this);
        p.show(ft, "dialog");
    }

    void askForPermission(int code) {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    boolean checkForPermission() {
        return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDialogOk() {
        askForPermission(2);
    }

    @Override
    public void onDialogCancel() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length < 1)
            return;
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 2) {
            onListFragmentInteraction(this.pendingVideoForPermission);
            this.pendingVideoForPermission = null;
        }
    }
}



