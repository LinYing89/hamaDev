package com.bairock.hamadev.communication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 设备连接状态
 * Created by 44489 on 2018/3/8.
 */

public class BridgeState {
    private String devCoding;
    private String channelId;
    private int recCount;
    private int sendCount;
    private List<NetMsgType> listBridgeMsgType = new ArrayList<>();

    private OnCollectionChangedMsgListener onCollectionChangedMsgListener;
    private OnDevCodingChangedListener onDevCodingChangedListener;

    public String getDevCoding() {
        return devCoding;
    }

    public void setDevCoding(String devCoding) {
        if(devCoding == null){
            return;
        }
        if(this.devCoding == null || !this.devCoding.equals(devCoding)) {
            this.devCoding = devCoding;
            if(null != onDevCodingChangedListener){
                onDevCodingChangedListener.OnDevCodingChanged();
            }
        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getRecCount() {
        return recCount;
    }

    public void setRecCount(int recCount) {
        this.recCount = recCount;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public List<NetMsgType> getListBridgeMsgType() {
        return listBridgeMsgType;
    }

    public void setListBridgeMsgType(List<NetMsgType> listBridgeMsgType) {
        this.listBridgeMsgType = listBridgeMsgType;
    }

    public void addMsg(int type, String msg){
        if(listBridgeMsgType.size() > 50){
            NetMsgType nmt = listBridgeMsgType.remove(0);
            if(null != onCollectionChangedMsgListener){
                onCollectionChangedMsgListener.onRemovedMsg(nmt);
            }
        }
        NetMsgType netMsgType = new NetMsgType();
        netMsgType.setType(type);
        netMsgType.setMsg(msg);
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm:ss", Locale.CHINA );
        netMsgType.setTime(dft.format(new Date()));
        listBridgeMsgType.add(netMsgType);
        if(null != onCollectionChangedMsgListener){
            onCollectionChangedMsgListener.onAddMsg(netMsgType);
        }
    }

    public void setOnCollectionChangedMsgListener(OnCollectionChangedMsgListener onCollectionChangedMsgListener) {
        this.onCollectionChangedMsgListener = onCollectionChangedMsgListener;
    }

    public void setOnDevCodingChangedListener(OnDevCodingChangedListener onDevCodingChangedListener) {
        this.onDevCodingChangedListener = onDevCodingChangedListener;
    }

    public interface OnCollectionChangedMsgListener{
        void onAddMsg(NetMsgType netMsgType);
        void onRemovedMsg(NetMsgType netMsgType);
    }

    public interface OnDevCodingChangedListener{
        void OnDevCodingChanged();
    }
}
