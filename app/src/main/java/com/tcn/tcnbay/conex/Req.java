package com.tcn.tcnbay.conex;

import com.google.gson.Gson;

public class Req {
    public static final String ACTION_STREAM = "stream";
    public String action;
    public String detail;

    public Req(String action, String detail) {
        this.action = action;
        this.detail = detail;
    }

    public String serialize() {
        return new Gson().toJson(this);
    }
}
