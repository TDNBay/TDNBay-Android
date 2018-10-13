package com.tcn.tcnbay;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;
import com.tcn.tcnbay.model.Video;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

public class VideoListDownloadTask extends AsyncTask<Void, Void, Void> {

    private IDownloadCallback callback;

    private List<Video> videoList;

    private Integer error;

    public VideoListDownloadTask(IDownloadCallback callback) {
        this.callback = callback;
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        Connection c = Connection.defaultInstance();
        Req req = new Req("filelist", "");
        try {
            c.establish();
        } catch (IOException e) {
            error = 1;
            e.printStackTrace();
            c.endSilent();
            return null;
        }
        try {
            c.sendData(Header.JSON_TYPE, req.serialize());
        } catch (IOException e) {
            error = 2;
            e.printStackTrace();
            c.endSilent();
            return null;
        }
        TDNVideoHeader header;
        try {
            header = TDNVideoHeader.decodeHeader(c.getInputStream());
        } catch (IOException e) {
            error = 3;
            e.printStackTrace();
            c.endSilent();
            return null;
        }
        DataInputStream is;
        try {
            is = new DataInputStream(c.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            error = 4;
            c.endSilent();
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        try {
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            error = 5;
            c.endSilent();
            return null;
        }
        byte[] raw = os.toByteArray();
        String json;
        try {
            json = new String(raw, "utf-8");
        } catch (UnsupportedEncodingException e) {
            error  = 6;
            c.endSilent();
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        Type videoType = new TypeToken<List<Video>>(){}.getType();
        videoList = gson.fromJson(json, videoType);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (error != null) {
            callback.onListDownloadError(error);
            super.onPostExecute(result);
            return;
        }
        callback.onListDownloadFinished(videoList);
        super.onPostExecute(result);
    }
}

