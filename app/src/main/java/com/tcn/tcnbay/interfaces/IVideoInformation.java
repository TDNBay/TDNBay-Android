package com.tcn.tcnbay.interfaces;

public interface IVideoInformation {
    public void updateAvailableBytes(long bytes);
    public void updateTotalBytes(long bytes);
    public void notifyDownloadFinished(Integer e);
}
