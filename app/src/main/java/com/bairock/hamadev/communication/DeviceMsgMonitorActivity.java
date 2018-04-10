package com.bairock.hamadev.communication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.settings.SearchActivity;

import java.lang.ref.WeakReference;

public class DeviceMsgMonitorActivity extends AppCompatActivity {

    private static StringBuilder SB_DEVICE_MSG = new StringBuilder();

    public static MyHandler handler;
    private TextView txtDeviceMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_msg_monitor);
        txtDeviceMsg = (TextView)findViewById(R.id.txtDeviceMsg);
        handler = new MyHandler(this);
        refreshText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    public static void addMsg(String msg){
        if(SB_DEVICE_MSG.length() > 400){
            SB_DEVICE_MSG.setLength(0);
        }
        SB_DEVICE_MSG.append(msg);
        SB_DEVICE_MSG.append("\n");
        if(null != handler){
            handler.obtainMessage(0).sendToTarget();
        }
    }
    private void refreshText(){
        txtDeviceMsg.setText(SB_DEVICE_MSG.toString());
    }

    private static class MyHandler extends Handler {
        WeakReference<DeviceMsgMonitorActivity> mActivity;
        MyHandler(DeviceMsgMonitorActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceMsgMonitorActivity theActivity = mActivity.get();
            switch (msg.what){
                case 0:
                    theActivity.refreshText();
                    break;
            }
        }
    }
}
