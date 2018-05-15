package com.bairock.hamadev.settings;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterBridgeState;
import com.bairock.hamadev.communication.BridgeState;
import com.bairock.iot.intelDev.communication.DevChannelBridge;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BridgesStateActivity extends AppCompatActivity {

    public static MyHandler myHandler;

    private ListView lvBridge;

    private static List<BridgeState> listBridgeState = new ArrayList<>();

    private AdapterBridgeState adapterBridgeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridges_state);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        List<BridgeState> list = new ArrayList<>(listBridgeState);
        for(BridgeState bridgeState : list){
            boolean had = false;
            for(DevChannelBridge db : DevChannelBridgeHelper.getIns().getListDevChannelBridge()){
                if(db.getChannelId().equals(bridgeState.getChannelId())){
                    had = true;
                    break;
                }
            }
            if(!had){
                listBridgeState.remove(bridgeState);
            }
        }
        lvBridge = findViewById(R.id.lvBridge);
        setAdapter();
        setListener();
        myHandler = new MyHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bridge_state, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            case R.id.menu_udp:
                startActivity(new Intent(BridgesStateActivity.this, UdpLogActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }

    private void setAdapter(){
        adapterBridgeState = new AdapterBridgeState(this, listBridgeState);
        lvBridge.setAdapter(adapterBridgeState);
    }

    private void setListener(){
        lvBridge.setOnItemClickListener((parent, view, position, id) -> {
//            BridgeMsgActivity.bridgeState = listBridgeState.get(position);
//            startActivity(new Intent(BridgesStateActivity.this, BridgeMsgActivity.class));

            BridgeMsgTestActivity.bridgeState = listBridgeState.get(position);
            startActivity(new Intent(BridgesStateActivity.this, BridgeMsgTestActivity.class));
        });
    }

    public static void  sendCountAnd(String channelId, int count, String msg){
        for(BridgeState bridgeState : listBridgeState){
            if(bridgeState.getChannelId().equals(channelId)){
                bridgeState.setSendCount(count);
                bridgeState.addMsg(1, msg);
                if(null != myHandler){
                    myHandler.obtainMessage(0).sendToTarget();
                }
                break;
            }
        }
    }
    public static void  recCountAnd(String devCoding, String channelId, int count, String msg){
        for(BridgeState bridgeState : listBridgeState){
            if(bridgeState.getChannelId().equals(channelId)){
                bridgeState.setRecCount(count);
                bridgeState.addMsg(0, msg);
//                if(bridgeState.getDevCoding() == null){
                    bridgeState.setDevCoding(devCoding);
//                }
                if(null != myHandler){
                    myHandler.obtainMessage(0).sendToTarget();
                }
                break;
            }
        }
    }

    public static void addBridge(String channelId){
        BridgeState bridgeState = new BridgeState();
        //bridgeState.setDevCoding(devCoding);
        bridgeState.setChannelId(channelId);
        listBridgeState.add(bridgeState);
        if(null != myHandler){
            myHandler.obtainMessage(0).sendToTarget();
        }
    }

    public static void removeBridge(String channelId){
        for(BridgeState bridgeState : listBridgeState){
            if(bridgeState.getChannelId().equals(channelId)){
                listBridgeState.remove(bridgeState);
                break;
            }
        }
        if(null != myHandler){
            myHandler.obtainMessage(0).sendToTarget();
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<BridgesStateActivity> mActivity;

        MyHandler(BridgesStateActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BridgesStateActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    //theActivity.setAdapter();
                    theActivity.adapterBridgeState.notifyDataSetChanged();
                    break;
            }
        }
    }
}
