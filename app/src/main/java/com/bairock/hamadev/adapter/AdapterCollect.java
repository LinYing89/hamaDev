package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 44489 on 2017/9/1.
 */

public class AdapterCollect extends BaseAdapter {

    public static final int STATE = 0;
    public static final int VALUE = 1;
    public static final int NAME = 2;
    public static final int ALIAS = 3;
    public static final int SRC_NAME = 4;
    public static final int SIMULATOR = 5;

    public static MyHandler handler;

    private LayoutInflater vi;
    private List<DevCollect> nameList;
    private List<ViewHolder> listViewHolder;

    public AdapterCollect(Context context, List<DevCollect> nameList){
        this.nameList = nameList;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
    }

    public int getCount() {
        return nameList.size();
    }

    public Object getItem(int position) {
        return nameList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        DevCollect device = nameList.get(position);

        if(convertView == null){
            mViewHolder = new ViewHolder();
            listViewHolder.add(mViewHolder);
            convertView = vi.inflate(R.layout.adapter_collect, parent, false);
            mViewHolder.textSrcName  = (TextView) convertView.findViewById(R.id.textSrcSignal);
            mViewHolder.textSimulator  = (TextView) convertView.findViewById(R.id.textSimulator);
            mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_name);
            mViewHolder.textAlias  = (TextView) convertView.findViewById(R.id.text_alias);
            mViewHolder.textState  = (TextView) convertView.findViewById(R.id.text_value);
            mViewHolder.textPer  = (TextView) convertView.findViewById(R.id.text_per);
            mViewHolder.progressValue  = (ProgressBar) convertView.findViewById(R.id.progress_value);
            mViewHolder.device = nameList.get(position);
            convertView.setTag(mViewHolder);
            mViewHolder.rootView = convertView;
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.device = device;

        mViewHolder.init();
        mViewHolder.refreshValue();
        mViewHolder.refreshState();
        return convertView;
    }

    private static class ViewHolder {
        private DevCollect device;
        private View rootView;
        private TextView textSrcName;
        private TextView textSimulator;
        private TextView textAlias;
        private TextView textName;
        private TextView textState;
        private TextView textPer;
        private ProgressBar progressValue;

        private void init() {
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());
            rootView.setBackgroundColor(Color.TRANSPARENT);
            refreshSrcName();
            refreshSimulator();
        }

        private void refreshValue(){
            if(device.getCollectProperty().getCollectSrc() == CollectSignalSource.SWITCH){
                if(device.getCollectProperty().getCurrentValue() != null) {
                    if (device.getCollectProperty().getCurrentValue() == 1) {
                        rootView.setBackgroundColor(HamaApp.stateKaiColorId);
                        textState.setText("开");
                    } else {
                        rootView.setBackgroundColor(Color.TRANSPARENT);
                        textState.setText("关");
                    }
                }
                //textState.setText(device.getCollectProperty().getValueWithSymbol());
            }else {
                textState.setText(device.getCollectProperty().getValueWithSymbol());
            }

//            if(device instanceof DevCollectSignal){
//                if(device.getCollectProperty().getCollectSrc() != CollectSignalSource.SWITCH){
//                    progressValue.setVisibility(View.VISIBLE);
//                    textPer.setText(device.getCollectProperty().getPercentWithSymbol());
//                    if(null != device.getCollectProperty().getPercent()) {
//                        int value = device.getCollectProperty().getPercent().intValue();
//                        progressValue.setProgress(value);
//                    }else{
//                        progressValue.setProgress(0);
//                    }
//                }else{
//                    textPer.setText("");
//                    progressValue.setVisibility(View.GONE);
//                    if(device.getCollectProperty().getCurrentValue() != null) {
//                        if (device.getCollectProperty().getCurrentValue() == 1) {
//                            rootView.setBackgroundColor(HamaApp.stateKaiColorId);
//                            textPer.setText("开");
//                        } else {
//                            rootView.setBackgroundColor(Color.TRANSPARENT);
//                            textPer.setText("关");
//                        }
//                    }
//                }
//            }else{
//                progressValue.setVisibility(View.GONE);
//            }
        }

        private void refreshState(){
            if(!device.isNormal()){
                rootView.setBackgroundColor(HamaApp.abnormalColorId);
            }else{
                if(device.getCollectProperty().getCollectSrc() != CollectSignalSource.SWITCH) {
                    rootView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }

        private void refreshSrcName(){
            textSrcName.setText(getSrcName());
        }

        private void refreshSimulator(){
            textSimulator.setText("  值:" + String.valueOf(device.getCollectProperty().getSimulatorValue()));
        }

        private void refreshName(){
            textName.setText(device.getName());
        }

        private void refreshAlias(){
            textAlias.setText(device.getAlias());
        }

        private String getSrcName(){
            String srcName = "";
            CollectProperty cp = device.getCollectProperty();
            switch (cp.getCollectSrc()){
                case DIGIT:
                    srcName = "(数字)";
                    break;
                case SWITCH:
                    srcName = "(开关)";
                    break;
                case VOLTAGE:
                    srcName = "(" + floatTrans1(cp.getLeastValue()) + "-" + floatTrans1(cp.getCrestValue()) + "V" + ")";
                    break;
                case ELECTRIC_CURRENT:
                    srcName = "(" + floatTrans1(cp.getLeastValue()) + "-" + floatTrans1(cp.getCrestValue()) + "mA" + ")";
                    break;
            }
            return srcName;
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<AdapterCollect> mActivity;

        MyHandler(AdapterCollect activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AdapterCollect theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for(AdapterCollect.ViewHolder vh : theActivity.listViewHolder){
                if(vh.device == dev){
                    switch (msg.what) {
                        case STATE:
                            vh.refreshState();
                            break;
                        case VALUE :
                            vh.refreshValue();
                            break;
                        case NAME :
                            vh.refreshName();
                            break;
                        case ALIAS :
                            vh.refreshAlias();
                            break;
                        case SRC_NAME :
                            vh.refreshSrcName();
                            break;
                        case SIMULATOR :
                            vh.refreshSimulator();
                            break;
                    }
                    break;
                }
            }

        }
    }

    public static String floatTrans1(float num){
        if(num % 1.0 == 0){
            return String.valueOf((int)num);
        }
        return String.valueOf(num);
    }
}
