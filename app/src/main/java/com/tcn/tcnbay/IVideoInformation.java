package com.tcn.tcnbay;

public interface IVideoInformation {
    public void updateAvailableBytes(long bytes);
    public void updateTotalBytes(long bytes);
    public void notifyDownloadFinished(Integer e);
}
