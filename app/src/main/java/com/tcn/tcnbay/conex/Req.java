package com.tcn.tcnbay.conex;

public class Req {
    public static final String ACTION_STREAM = "stream";
    public String action;
    public String detail;

    public Req(String action, String detail) {
        this.action = action;
        this.detail = detail;
    }
}
