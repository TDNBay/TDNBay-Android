package com.tcn.tcnbay.media;

import com.google.android.exoplayer2.upstream.DataSource;

public class TDNFactory implements DataSource.Factory {

    private TDNDataSource ds;


    public TDNFactory(int vid, String ip, int port) {
        this.ds = new TDNDataSource(vid, ip, port);
    }

    @Override
    public DataSource createDataSource() {
        return ds;
    }

    public TDNDataSource getDataSource() {
        return ds;
    }
}
