package com.bairock.hamadev.settings;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterBridgeMsg;
import com.bairock.hamadev.communication.BridgeState;
import com.bairock.hamadev.communication.NetMsgType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BridgeMsgActivity extends AppCompatActivity {

    public static MyHandler myHandler;

    public static BridgeState bridgeState;
    private ListView lvBridge;
    private AdapterBridgeMsg adapterBridgeMsg;
    private List<NetMsgType> listNetMsgType;

    private static boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_msg);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(bridgeState.getDevCoding() != null) {
                actionBar.setTitle(bridgeState.getDevCoding());
            }
        }
        lvBridge = (ListView)findViewById(R.id.lvBridgeMsg);
        listNetMsgType = new ArrayList<>(bridgeState.getListBridgeMsgType());
        setAdapter();
        setListener();
        myHandler = new MyHandler(this);
        isPaused = false;
        bridgeState.setOnCollectionChangedMsgListener(new BridgeState.OnCollectionChangedMsgListener() {
            @Override
            public void onAddMsg(NetMsgType netMsgType) {
                if(!isPaused) {
                    listNetMsgType.remove(netMsgType);
                    if(null != myHandler) {
                        myHandler.obtainMessage(0).sendToTarget();
                    }
                }
            }

            @Override
            public void onRemovedMsg(NetMsgType netMsgType) {
                if(!isPaused) {
                    listNetMsgType.add(netMsgType);
                    if(null != myHandler) {
                        myHandler.obtainMessage(0).sendToTarget();
                    }
                }
            }
        });
        bridgeState.setOnDevCodingChangedListener(() -> myHandler.obtainMessage(1).sendToTarget());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bridge_msg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            case R.id.pause:
                isPaused = !isPaused;
                if(isPaused){
                    item.setTitle("启动");
                }else{
                    item.setTitle("暂停");
                }
                break;
            case R.id.clean:
                bridgeState.getListBridgeMsgType().clear();
                listNetMsgType.clear();
                adapterBridgeMsg.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler = null;
        bridgeState.setOnDevCodingChangedListener(null);
        bridgeState.setOnCollectionChangedMsgListener(null);
        listNetMsgType.clear();
    }

    private void setAdapter(){
        adapterBridgeMsg = new AdapterBridgeMsg(this, listNetMsgType);
        lvBridge.setAdapter(adapterBridgeMsg);
    }

    private void setListener(){
        lvBridge.setOnItemClickListener((parent, view, position, id) -> {

        });
    }

    public static class MyHandler extends Handler {
        WeakReference<BridgeMsgActivity> mActivity;

        MyHandler(BridgeMsgActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BridgeMsgActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    if(!isPaused) {
                        theActivity.adapterBridgeMsg.notifyDataSetChanged();
                    }
                    break;
                case 1:
                    if(theActivity.getSupportActionBar() != null) {
                        theActivity.getSupportActionBar().setTitle(bridgeState.getDevCoding());
                    }
                    break;
            }
        }
    }
}
