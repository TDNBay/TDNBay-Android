package com.tcn.tcnbay.background.tasks;

import android.os.AsyncTask;

import com.tcn.tcnbay.background.tasks.service.VideoUploadService;
import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class VideoUploadTask extends AsyncTask<Void, Integer, Void> {

    private InputStream file;

    private WeakReference<VideoUploadService> service;

    private String fileName;
    private String ip;
    private int port;

    public VideoUploadTask(InputStream fileIs, String fileName, VideoUploadService service, String ip, int port) {
        file = fileIs;
        this.fileName = fileName;
        this.service = new WeakReference<>(service);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Connection c = new Connection(ip, port);
        try {
            c.establish();
        } catch (IOException e) {
            c.endSilent();
            e.printStackTrace();
            return null;
        }
        Req req = new Req("fileupload", fileName);
        try {
            c.sendData(Header.JSON_TYPE, req.serialize());
        } catch (IOException e) {
            c.endSilent();
            e.printStackTrace();
            return null;
        }
        OutputStream os;
        try {
            os = c.getOutputStream();
        } catch (IOException e) {
            c.endSilent();
            e.printStackTrace();
            return null;
        }
        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = file.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
        catch (IOException e) {
            c.endSilent();
            e.printStackTrace();
            return null;
        }
        try {
            c.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (service != null && service.get() != null) {
            service.get().onTaskFinished();
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }
}
