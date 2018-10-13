package com.tcn.tcnbay;

import com.google.android.exoplayer2.upstream.DataSource;

public class TDNFactory implements DataSource.Factory {

    private TDNDataSource ds;


    public TDNFactory(int vid) {
        this.ds = new TDNDataSource(vid);
    }

    @Override
    public DataSource createDataSource() {
        return ds;
    }

    public TDNDataSource getDataSource() {
        return ds;
    }
}
