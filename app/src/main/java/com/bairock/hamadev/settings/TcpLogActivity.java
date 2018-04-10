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
import com.bairock.hamadev.communication.NetMsgType;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TcpLogActivity extends AppCompatActivity {

    public static List<NetMsgType> listNetMsgType = new ArrayList<>();

    private ListView lvMsg;

    private AdapterBridgeMsg adapterMsg;

    public static MyHandler myHandler;
    private static boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_log);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lvMsg = (ListView)findViewById(R.id.lvMsg);
        setListAdapter();
        myHandler = new MyHandler(this);
        isPaused = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tcp_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
            case R.id.menu_pause:
                isPaused = !isPaused;
                if(isPaused){
                    item.setTitle("启动");
                }else{
                    item.setTitle("暂停");
                }
                break;
            case R.id.menu_clean:
                listNetMsgType.clear();
                adapterMsg.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler = null;
    }

    private void setListAdapter(){
        adapterMsg = new AdapterBridgeMsg(this, listNetMsgType);
        lvMsg.setAdapter(adapterMsg);
    }

    public static void  addRec(String rec){
        addMsg(0, rec);
    }

    public static void  addSend(String send){
        addMsg(1, send);
    }

    private static void addMsg(int type, String msg){
        if(isPaused){
            return;
        }
        if(listNetMsgType.size() > 100){
            listNetMsgType.remove(0);
        }
        NetMsgType netMsgType = new NetMsgType();
        netMsgType.setType(type);
        netMsgType.setMsg(msg);
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm:ss", Locale.CHINA );
        netMsgType.setTime(dft.format(new Date()));
        listNetMsgType.add(netMsgType);
        if(null != myHandler){
            myHandler.obtainMessage(0).sendToTarget();
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<TcpLogActivity> mActivity;

        MyHandler(TcpLogActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            TcpLogActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:
                    if(!isPaused) {
                        theActivity.adapterMsg.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}
