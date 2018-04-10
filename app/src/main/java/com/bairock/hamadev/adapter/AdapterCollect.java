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
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;
import com.bairock.iot.intelDev.device.devcollect.Pressure;

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

    public static MyHandler handler;

    private LayoutInflater vi;
    private Context context;
    private List<DevCollect> nameList;
    private List<ViewHolder> listViewHolder;

    public AdapterCollect(Context context, List<DevCollect> nameList){
        this.context = context;
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
        private TextView textAlias;
        private TextView textName;
        private TextView textState;
        private TextView textPer;
        private ProgressBar progressValue;

        private void init() {
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());
        }

        private void refreshValue(){
            textState.setText(device.getCollectProperty().getValueWithSymbol());
            if(device instanceof DevCollectSignal){
                if(device.getCollectProperty().getCollectSrc() == CollectSignalSource.SWITCH){
                    progressValue.setVisibility(View.GONE);
                }else{
                    progressValue.setVisibility(View.VISIBLE);
                    textPer.setText(device.getCollectProperty().getPercentWithSymbol());
                    if(null != device.getCollectProperty().getPercent()) {
                        int value = device.getCollectProperty().getPercent().intValue();
                        progressValue.setProgress(value);
                    }else{
                        progressValue.setProgress(0);
                    }
                }
            }else{
                progressValue.setVisibility(View.GONE);
            }
        }

        private void refreshState(){
            if(!device.isNormal()){
                rootView.setBackgroundColor(Color.parseColor("#E9967A"));
            }else{
                rootView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private void refreshName(){
            textName.setText(device.getName());
        }

        private void refreshAlias(){
            textAlias.setText(device.getAlias());
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
                    }
                    break;
                }
            }

        }
    }
}
