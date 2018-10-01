package com.tcn.tcnbay;

import com.google.android.exoplayer2.upstream.DataSource;

public class TestFactory implements DataSource.Factory {
    @Override
    public DataSource createDataSource() {
        return new TDNDataSource();
    }
}
