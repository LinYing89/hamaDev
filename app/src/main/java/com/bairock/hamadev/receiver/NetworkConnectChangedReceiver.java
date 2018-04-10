package com.bairock.hamadev.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    //String TAG = "NetworkConnectChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if(null == manager){
                return;
            }
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if(!activeNetwork.isConnected()){
                    setDeviceAbnormal();
                }else{
                    HamaApp.NET_CONNECTED = true;
                    if(null != MainActivity.handler){
                        MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE).sendToTarget();
                    }
                }
//                if (activeNetwork.isConnected()) {
//                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                        // connected to wifi
//                        Log.e(TAG, "当前WiFi连接可用 ");
//                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                        // connected to the mobile provider's data plan
//                        Log.e(TAG, "当前移动网络连接可用 ");
//                    }
//                } else {
//                    setRemoteDeviceAbnormal();
//                }
            } else {   // not connected to the internet
                setDeviceAbnormal();
            }
        }
    }

    private void setDeviceAbnormal(){
        HamaApp.NET_CONNECTED = false;
        if(null != MainActivity.handler){
            MainActivity.handler.obtainMessage(MainActivity.REFRESH_TITLE).sendToTarget();
        }
        for(Device device : HamaApp.DEV_GROUP.getListDevice()){
            device.setDevStateId(DevStateHelper.DS_YI_CHANG);
        }
    }
}
