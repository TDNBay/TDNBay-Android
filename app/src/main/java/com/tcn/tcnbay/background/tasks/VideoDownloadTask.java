package com.tcn.tcnbay.background.tasks;

import android.os.AsyncTask;
import android.os.Environment;

import com.tcn.tcnbay.media.TDNVideoHeader;
import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;
import com.tcn.tcnbay.interfaces.IVideoInformation;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoDownloadTask extends AsyncTask<Void, Void, Void> {

    IVideoInformation callback;
    private Integer error = null;
    private Req req;
    private Long totalBytes = null;
    private Long availableBytes = null;
    private String ip;
    private int port;

    private boolean shouldCancelNow = false;

    public VideoDownloadTask(Req req, String ip, int port) {
        this.req = req;
        this.ip = ip;
        this.port = port;
    }

    public void cancelWheneverPossible() {
        this.shouldCancelNow = true;
    }

    public void setCallback(IVideoInformation callback) {
        this.callback = callback;
        if (totalBytes != null)
        callback.updateTotalBytes(totalBytes);
        if (availableBytes != null)
            callback.updateAvailableBytes(availableBytes);
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Connection c = new Connection(ip, port);
        try {
            c.establish();
        } catch (IOException e) {
            error = 1;
            e.printStackTrace();
            c.endSilent();
            return null;
        }
        if (shouldCancelNow) {
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
        if (shouldCancelNow) {
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
        callback.updateTotalBytes(header.fileLen);
        if (shouldCancelNow) {
            c.endSilent();
            return null;
        }
        totalBytes = header.fileLen;
        // TODO: tratar casos de erro no header
        File parentFile = new File(Environment.getExternalStorageDirectory().getPath() + "/TDNBay/cache/");
        parentFile.mkdirs();
        File f = new File(parentFile, "videoDownloadCache.tdn");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            error = 4;
            c.endSilent();
            return null;
        }
        DataInputStream is;
        try {
            is = new DataInputStream(c.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            error = 5;
            c.endSilent();
            return null;
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            error = 5;
            e.printStackTrace();
            c.endSilent();
            return null;
        }
        if (shouldCancelNow) {
            c.endSilent();
            return null;
        }
        int len;
        long tot = 0;
        byte[] buffer = new byte[8192];
        try {
            while ((len = is.read(buffer)) != -1) {
                if (shouldCancelNow) {
                    c.endSilent();
                    return null;
                }
                os.write(buffer, 0, len);
                tot += len;
                callback.updateAvailableBytes(tot);
                availableBytes = tot;
            }
        } catch (IOException e) {
            e.printStackTrace();
            error = 6;
            c.endSilent();
            return null;
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            error = 6;
        }
        try {
            c.end();
        } catch (IOException e) {
            e.printStackTrace();
            error = 7;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        callback.notifyDownloadFinished(error);
        super.onPostExecute(result);
    }

}
