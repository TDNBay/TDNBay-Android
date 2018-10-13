package com.tcn.tcnbay.media;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class TDNVideoHeader {

    public int fileType;
    public long qtBytesLen;
    public long fileLen;


    public static TDNVideoHeader decodeHeader(InputStream is) throws IOException {
        TDNVideoHeader header = new TDNVideoHeader();
        int head1 = is.read();
        if (head1 == -1)
            throw new IOException("Primeiro byte cabeçalho faltando na resposta");
        header.fileType = head1;
        int contlen_len = is.read();
        if (contlen_len == -1)
            throw new IOException("Segundo byte cabeçalho faltando na resposta");
        header.qtBytesLen = contlen_len;
        byte[] contentLen = new byte[contlen_len];
        int temp;
        for (int i = 0; i < contlen_len; i++) {
            temp = is.read();
            if (temp == -1)
                throw new IOException("Byte faltando no tamanho do conteudo");
            contentLen[i] = (byte) temp;
        }
        BigInteger big = new BigInteger(contentLen);
        header.fileLen = big.longValue();
        return header;
    }
}
