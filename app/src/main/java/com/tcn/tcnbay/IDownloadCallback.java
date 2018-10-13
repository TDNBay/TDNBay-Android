package com.tcn.tcnbay;

import java.util.List;

public interface IDownloadCallback {
    void onListDownloadFinished(List list);
    void onListDownloadError(int reason);
}
