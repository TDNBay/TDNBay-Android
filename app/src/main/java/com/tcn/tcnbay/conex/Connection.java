package com.tcn.tcnbay.conex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;

public class Connection {
    private Socket socket;
    private String ip;
    private int port;

    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void establish() throws IOException {
        socket = new Socket(InetAddress.getByName(ip), port);
    }

    public void end() throws IOException {
        socket.close();
    }

    public void sendData(byte header, byte[] data) throws IOException {
        OutputStream out = socket.getOutputStream();
        long len = data.length;
        byte[] lenh = BigInteger.valueOf(len).toByteArray();
        out.write(header);
        out.write(lenh.length);
        out.write(lenh);
        out.write(data);
        out.close();
    }

    public void sendData(byte header, String data) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] arr = data.getBytes();
        long len = arr.length;
        byte[] lenh = BigInteger.valueOf(len).toByteArray();
        out.write(header);
        out.write(lenh.length);
        out.write(lenh);
        out.write(arr);
        out.close();
    }

    // RDT maldito
    public void sendData(byte header, File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        long len = file.length();
        byte[] lenh = BigInteger.valueOf(len).toByteArray();
        OutputStream out = socket.getOutputStream();
        out.write(header);
        out.write(lenh.length);
        out.write(lenh);
        byte[] báfer = new byte[8192];
        while (stream.read(báfer) != -1) {
            out.write(báfer);
        }
        out.close();
        stream.close();
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }


}
