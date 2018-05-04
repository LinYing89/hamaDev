package com.bairock.hamadev.settings;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.BridgeState;
import com.bairock.hamadev.communication.NetMsgType;

import java.lang.ref.WeakReference;

public class BridgeMsgTestActivity extends AppCompatActivity {

    private TextView tvLogs;

    public static MyHandler myHandler;

    public static BridgeState bridgeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_msg_test);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(bridgeState.getDevCoding() != null) {
                actionBar.setTitle(bridgeState.getDevCoding());
            }
        }

        tvLogs = (TextView)findViewById(R.id.tvLogs);

        StringBuilder sb = new StringBuilder();
        for(NetMsgType netMsgType : bridgeState.getListBridgeMsgType()){
            String type;
            if(netMsgType.getType() == 0){
                type = " <- ";
            }else{
                type = " -> ";
            }
            sb.append(type);

            sb.append(netMsgType.getTime());
            sb.append(" ");
            sb.append(netMsgType.getMsg());
            sb.append("\n");
        }
        String text = sb.toString();
        Spannable sTextSpannable=new SpannableString(text);
        //sTextSpannable.setSpan(new ForegroundColorSpan(Color.RED),1,4,0);

        int index1;
        int index2 = 0;
        while (true){
            index1 = text.indexOf("<", index2);
            if(index1 == -1){
                break;
            }
            index2 = text.indexOf("\n", index1);
            if(index2 == -1){
                break;
            }
            sTextSpannable.setSpan(new ForegroundColorSpan(Color.RED),index1,index2,0);
        }
        tvLogs.setText(sTextSpannable);

        myHandler = new MyHandler(this);
        bridgeState.setOnCollectionChangedMsgListener(new BridgeState.OnCollectionChangedMsgListener() {
            @Override
            public void onAddMsg(NetMsgType netMsgType) {
                    if(null != myHandler) {
                        String type;
                        if(netMsgType.getType() == 0){
                            type = " <- ";
                        }else{
                            type = " -> ";
                        }
                        String str = type + netMsgType.getTime() + " " + netMsgType.getMsg();
                        myHandler.obtainMessage(0, str).sendToTarget();
                    }
            }

            @Override
            public void onRemovedMsg(NetMsgType netMsgType) {
//                if(!isPaused) {
//                    if(null != myHandler) {
//                        myHandler.obtainMessage(0).sendToTarget();
//                    }
//                }
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
            case R.id.clean:
                bridgeState.getListBridgeMsgType().clear();
                tvLogs.setText("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyHandler extends Handler {
        WeakReference<BridgeMsgTestActivity> mActivity;

        MyHandler(BridgeMsgTestActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BridgeMsgTestActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 0:

                    String str = msg.obj.toString() + "\n";
                    if(str.contains("<")){
                        Spannable sTextSpannable=new SpannableString(str);
                        sTextSpannable.setSpan(new ForegroundColorSpan(Color.RED),1,str.length(),0);
//                        theActivity.tvLogs.setText(sTextSpannable);
                        theActivity.tvLogs.append(sTextSpannable);
                    }else {
                        theActivity.tvLogs.append(msg.obj.toString() + "\n");
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
