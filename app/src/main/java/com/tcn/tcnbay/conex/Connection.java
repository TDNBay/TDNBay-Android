package com.tcn.tcnbay.conex;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

    public boolean establish() {
        try {
            socket = new Socket(InetAddress.getByName(ip), port);
        } catch (IOException e) {
            e.printStackTrace();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    public boolean sendData(byte header, byte[] data) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(header);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendData(byte header, String data) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(header);
            byte[] arr = data.getBytes();
            out.write(arr);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendData(byte header, File file) {

    }




}
