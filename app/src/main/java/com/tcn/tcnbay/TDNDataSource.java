package com.tcn.tcnbay;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.gson.Gson;
import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class TDNDataSource implements DataSource {

    private Connection c;
    private Req r;
    private InputStream is;
    private String host;
    private int port;
    private long remainingBytes;

    public TDNDataSource(String host, int port, Req r) {
        this.r = r;
        this.host = host;
        this.port = port;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        if (dataSpec.length == C.LENGTH_UNSET) {
            c = new Connection(host, port);
            c.establish();
            Gson gson = new Gson();
            c.sendData(Header.JSON_TYPE, gson.toJson(r));
            is = c.getInputStream();
            int head1 = is.read();
            if (head1 == -1)
                throw new IOException("Primeiro byte cabeçalho faltando na resposta");
            if (head1 != Header.VIDEO_TYPE)
                throw new IOException("Dados recebidos do servidor não são um fluxo de vídeo");
            int contlen_len = is.read();
            if (contlen_len == -1)
                throw new IOException("Segundo byte cabeçalho faltando na resposta");
            byte[] contentLen = new byte[contlen_len];
            int temp;
            for (int i = 0; i < contlen_len; i++) {
                temp = is.read();
                if (temp == -1)
                    throw new IOException("Byte faltando no tamanho do conteudo");
                contentLen[i] = (byte) temp;
            }
            BigInteger big = new BigInteger(contentLen);
            remainingBytes = big.longValue();
            return remainingBytes;
        }
        return dataSpec.length;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        return is.read(buffer, offset, readLength);
    }

    @Nullable
    @Override
    public Uri getUri() {
        return null;
    }

    @Override
    public void close() throws IOException {
        c.end();
    }
}
