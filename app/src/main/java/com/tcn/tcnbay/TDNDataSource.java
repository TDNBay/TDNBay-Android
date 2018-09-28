package com.tcn.tcnbay;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.gson.Gson;
import com.tcn.tcnbay.conex.Connection;
import com.tcn.tcnbay.conex.Header;
import com.tcn.tcnbay.conex.Req;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class TDNDataSource implements DataSource {

    private Connection c;
    private Req r;
    private DataInputStream is;
    private String host;
    private int port;
    private long totBytes = -1;
    private boolean opened = false;
    private long readBytes = 0;
    private Uri uri;

    public TDNDataSource(String host, int port, Req r) {
        this.r = r;
        this.host = host;
        this.port = port;
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        if (dataSpec.length == C.LENGTH_UNSET) {
            uri = dataSpec.uri;
            if (totBytes != -1) {
                return totBytes - readBytes;
            }
            c = new Connection(host, port);
            c.establish();
            Gson gson = new Gson();
            c.sendData(Header.JSON_TYPE, gson.toJson(r));
            is = c.getInputStream();
            int head1 = is.read();
            Log.i("INFO, HEADER 1", String.valueOf(head1));
            if (head1 == -1)
                throw new IOException("Primeiro byte cabeçalho faltando na resposta");
            if (head1 != Header.VIDEO_TYPE)
                throw new IOException("Dados recebidos do servidor não são um fluxo de vídeo");
            int contlen_len = is.read();
            Log.i("INFO, HEADER 2", String.valueOf(contlen_len));
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
            totBytes = big.longValue();
            return totBytes - readBytes;
        }
        return dataSpec.length;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        Log.i("READ", "Reading " + readLength + " bytes");
        Log.i("OFFSET", "" + offset);
        int read = is.read(buffer, offset, readLength);
        if (read == -1)
            return C.RESULT_END_OF_INPUT;
        readBytes += read;
        return read;
    }

    @Nullable
    @Override
    public Uri getUri() {
        Log.i("DataSource", "Uri get");
        return uri;
    }

    @Override
    public void close() throws IOException {
        c.end();
        Log.i("SOCKET INFO", "CLOSED");
    }
}
