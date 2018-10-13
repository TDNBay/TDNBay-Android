package com.tcn.tcnbay.interfaces;

import java.util.List;

public interface IDownloadCallback {
    void onListDownloadFinished(List list);
    void onListDownloadError(int reason);
}
