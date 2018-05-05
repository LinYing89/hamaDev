package com.bairock.hamadev.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCollect extends RecyclerView.Adapter<RecyclerAdapterCollect.ViewHolder>{

    public static final int STATE = 0;
    public static final int VALUE = 1;
    public static final int NAME = 2;
    public static final int ALIAS = 3;
    public static final int SRC_NAME = 4;
    public static final int SIMULATOR = 5;
    public static final int SYMBOL = 6;

    public static RecyclerAdapterCollect.MyHandler handler;

    private LayoutInflater mInflater;
    private List<DevCollect> listDevice;
    private List<RecyclerAdapterCollect.ViewHolder> listViewHolder;

    public RecyclerAdapterCollect(Context context, List<DevCollect> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new RecyclerAdapterCollect.MyHandler(this);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }


    @NonNull
    @Override
    public RecyclerAdapterCollect.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterCollect.ViewHolder vh = new RecyclerAdapterCollect.ViewHolder(mInflater.inflate(R.layout.adapter_collect, parent, false));
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterCollect.ViewHolder holder, int position) {
        holder.setData(listDevice.get(position), position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private DevCollect device;
        private View rootView;
        private TextView textSrcName;
        private TextView textSimulator;
        private TextView textAlias;
        private TextView textName;
        private TextView textState;
        private TextView textSymbol;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textSrcName  = itemView.findViewById(R.id.textSrcSignal);
            textSimulator  = itemView.findViewById(R.id.textSimulator);
            textName  = itemView.findViewById(R.id.text_name);
            textName.setSelected(true);
            textAlias  = itemView.findViewById(R.id.text_alias);
            textAlias.setSelected(true);
            textState  = itemView.findViewById(R.id.text_value);
            textSymbol  = itemView.findViewById(R.id.textSymbol);
        }

        public void setData(DevCollect device, int position) {
            this.device = device;
            init(position);
        }

        private void init(int position) {
            textName.setText(device.getName());
            textAlias.setText(device.getAlias());
//            if(position % 2 == 0) {
//                rootView.setBackgroundColor(Color.TRANSPARENT);
//            }else{
//                rootView.setBackgroundColor(HamaApp.HAMA_CONTEXT.getResources().getColor(R.color.back_ground));
//            }
            refreshSrcName();
            refreshSimulator();
            refreshSymbol();
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
            }else {
                textState.setText(device.getCollectProperty().getValueWithSymbol());
            }
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
        private void refreshSymbol(){
            textSymbol.setText(device.getCollectProperty().getUnitSymbol());
        }

        private void refreshSrcName(){
            textSrcName.setText(getSrcName());
        }

        private void refreshSimulator(){
            textSimulator.setText(String.format("  值:%s", String.valueOf(device.getCollectProperty().getSimulatorValue())));
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

        private String floatTrans1(float num){
            if(num % 1.0 == 0){
                return String.valueOf((int)num);
            }
            return String.valueOf(num);
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterCollect> mActivity;

        MyHandler(RecyclerAdapterCollect activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterCollect theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for(RecyclerAdapterCollect.ViewHolder vh : theActivity.listViewHolder){
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
                        case SYMBOL :
                            vh.refreshSymbol();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
