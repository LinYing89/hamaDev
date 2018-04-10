package com.bairock.hamadev.communication;

/**
 *
 * Created by 44489 on 2018/3/8.
 */

public class NetMsgType {
    //0 is received, 1 is send
    private int type;
    private String msg;
    private String time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
