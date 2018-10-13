package com.tcn.tcnbay.media;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.tcn.tcnbay.background.tasks.VideoDownloadTask;
import com.tcn.tcnbay.conex.Req;

import java.io.EOFException;
import java.io.IOException;

public class TDNDataSource implements DataSource {


    private TDNVideoInputStream stream;
    private boolean opened = false;
    private Uri uri;
    private String reqDetail = "";
    private VideoDownloadTask task;
    private int vid;
    private long bytesRemaining;
    private String ip;
    private int port;

    public TDNDataSource(int vid, String ip, int port) {
        this.vid = vid;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Req req = new Req("fileget", String.valueOf(vid));
        TDNVideoInputStream stream = new TDNVideoInputStream();
        stream.loadBuffer();
        if (!reqDetail.equals(req.detail)) {
            reqDetail = req.detail;
            VideoDownloadTask task = new VideoDownloadTask(req, ip, port);
            task.setCallback(stream);
            task.execute();
            this.task = task;
        }
        task.setCallback(stream);
        uri = dataSpec.uri;
        this.stream = stream;
        long skipped = stream.skip(dataSpec.position);
        if (skipped < dataSpec.position)
            throw new EOFException();
        if (dataSpec.length != C.LENGTH_UNSET)
            bytesRemaining = dataSpec.length - skipped;
        else {
            bytesRemaining = stream.getTotalBytesFile();
        }
        opened = true;
        return bytesRemaining;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (bytesRemaining == 0)
            return -1;
        int bytesRead;
        int bytesToRead = bytesRemaining == C.LENGTH_UNSET ? readLength
                : (int) Math.min(bytesRemaining, readLength);
        bytesRead = stream.read(buffer, offset, bytesToRead);
        if (bytesRead > 0 && bytesRemaining != C.LENGTH_UNSET) {
            bytesRemaining -= bytesRead;
        }
        return bytesRead;
    }

    @Nullable
    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() throws IOException {
        uri = null;
        if (opened)
            opened = false;
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }

    public void requestTaskCancellation() {
        task.cancelWheneverPossible();
    }
}
