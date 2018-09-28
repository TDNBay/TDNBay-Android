package com.tcn.tcnbay;

import com.google.android.exoplayer2.upstream.DataSource;
import com.tcn.tcnbay.conex.Req;

public class TestFactory implements DataSource.Factory {
    @Override
    public DataSource createDataSource() {
        return new TDNDataSource("192.168.1.161", 50000, new Req(Req.ACTION_STREAM, "0"));
    }
}
