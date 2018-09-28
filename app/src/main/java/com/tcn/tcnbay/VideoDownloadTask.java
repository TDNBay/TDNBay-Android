package com.tcn.tcnbay;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class VideoDownloadTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    Socket socket = null;
    IVideoInformation callback;
    private Integer error = null;
    private Req req;

    VideoDownloadTask(IVideoInformation callback, Req req) {
        this.callback = callback;
        this.req = req;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Connection c = new Connection("10.0.2.2", 50000);
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
        callback.updateAvailableBytes(header.fileLen);
        // TODO: tratar casos de erro no header
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "TDNBay/cache/videoDownloadCache");
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
        int len;
        long tot = 0;
        byte[] buffer = new byte[1000000];
        try {
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                tot += len;
                callback.updateAvailableBytes(tot);
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
