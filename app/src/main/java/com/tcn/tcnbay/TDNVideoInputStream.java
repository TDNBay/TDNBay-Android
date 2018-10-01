package com.tcn.tcnbay;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class TDNVideoInputStream extends InputStream implements IVideoInformation {

    private RandomAccessFile videoFile;
    private long bytesAvailableFile;
    private long totalBytesFile = -1;
    private long available = 0;

    public void loadBuffer() throws IOException {
        File parentFile = new File(Environment.getExternalStorageDirectory().getPath() + "/TDNBay/cache/");
        parentFile.mkdirs();
        File f = new File(parentFile, "videoDownloadCache.tdn");
        f.createNewFile();
        videoFile = new RandomAccessFile(f, "r");
    }

    @Override
    public int available() throws IOException {
        available = bytesAvailableFile - videoFile.getFilePointer();
        if (available < 0)
            return -1;
        while (available == 0) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
            available = bytesAvailableFile - videoFile.getFilePointer();
        }
        return (int) available;
    }

    @Override
    public void close() throws IOException {
        videoFile.close();
        super.close();
    }

    @Override
    public int read() throws IOException {
        throw new IOException("Metodo read() nao implementado");
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        int available = available();
        if (available < 0)
            return -1;
        int count;
        if (available < len)
            count = available;
        else
            count = len;
        int read = videoFile.read(buffer, offset, count);
        this.available -= read;
        return read;
    }

    @Override
    public long skip(long bytes) throws IOException {
        if (bytes < 0)
            return -1;
        long available = available();
        while (available < bytes) {
            available = available();
        }
        int skipped = videoFile.skipBytes((int) bytes);
        this.available -= skipped;
        return skipped;
    }

    @Override
    public void updateAvailableBytes(long bytes) {
        bytesAvailableFile = bytes;
    }

    @Override
    public void updateTotalBytes(long bytes) {
        totalBytesFile = bytes;
    }

    @Override
    public void notifyDownloadFinished(Integer e) {
        if (e != null) {
            Log.i("Task error", "number: " + e);
        }
    }

    public long getTotalBytesFile() {
        while (totalBytesFile == -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return totalBytesFile;
    }
}
