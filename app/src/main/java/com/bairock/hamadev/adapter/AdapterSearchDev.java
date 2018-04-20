package com.bairock.hamadev.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignalContainer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Administrator on 2017/8/27.
 */

public class AdapterSearchDev extends BaseAdapter {

    public static final int CTRL_MODEL = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;

    public static MyHandler handler;

    private Context context;
    private List<Device> listDevice;

    private List<AdapterSearchDev.ViewHolder> listViewHolder;

    public AdapterSearchDev(Context context, List<Device> listDevice){
        this.context = context;
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
    }

    public int getCount() {
        return listDevice.size();
    }

    public Object getItem(int position) {
        return listDevice.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if(convertView == null){
            mViewHolder = new ViewHolder();
            listViewHolder.add(mViewHolder);
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.search_device_list, parent, false);
            mViewHolder.textName  = (TextView) convertView.findViewById(R.id.changer);
            mViewHolder.textCoding  = (TextView) convertView.findViewById(R.id.text_coding);
            mViewHolder.redGreen  = (ImageView) convertView.findViewById(R.id.red_green);
            mViewHolder.textCtrlModel  = (TextView) convertView.findViewById(R.id.txtCtrlModel);
            mViewHolder.cbVisibility  = (CheckBox) convertView.findViewById(R.id.cbVisibility);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.device = listDevice.get(position);
        mViewHolder.init();
        return convertView;
    }

    private static class ViewHolder {
        private Device device;
        private TextView textName;
        private TextView textCoding;
        private ImageView redGreen;
        private TextView textCtrlModel;
        private CheckBox cbVisibility;

        void init(){
            refreshName();
            textCoding.setText(device.getCoding());
            refreshState();
            refreshCtrlModel();
            cbVisibility.setChecked(device.isVisibility());
            cbVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                device.setVisibility(isChecked);
                DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
                deviceDao.update(device);
                if(device instanceof IStateDev){
                    if(null != ElectricalCtrlFragment.handler) {
                        ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
                    }
                }else if(device instanceof DevCollect || device instanceof DevCollectSignalContainer){
                    if(null != ClimateFragment.handler) {
                        ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
                    }
                }
            });
        }

        private void refreshName(){
            textName.setText(device.getName());
        }

        private void refreshState(){
            if(device.isNormal()) {
                redGreen.setBackgroundResource(R.mipmap.normal_green);
            }else {
                redGreen.setBackgroundResource(R.mipmap.abnormal_red);
            }
        }

        private void refreshCtrlModel(){
            if(device.getCtrlModel() != CtrlModel.REMOTE){
                textCtrlModel.setText("本地");
            }else {
                textCtrlModel.setText("远程");
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<AdapterSearchDev> mActivity;

        MyHandler(AdapterSearchDev activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AdapterSearchDev theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for(AdapterSearchDev.ViewHolder vh : theActivity.listViewHolder){
                if(vh.device == dev){
                    switch (msg.what) {
                        case CTRL_MODEL :
                            vh.refreshCtrlModel();
                            break;
                        case STATE:
                            vh.refreshState();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
