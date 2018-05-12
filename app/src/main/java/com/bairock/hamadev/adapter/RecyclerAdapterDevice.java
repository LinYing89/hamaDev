package com.bairock.hamadev.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignalContainer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterDevice extends RecyclerView.Adapter<RecyclerAdapterDevice.ViewHolder> {

    public static final int CTRL_MODEL = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;
    public static final int ALIAS = 4;

    public static RecyclerAdapterDevice.MyHandler handler;

    private LayoutInflater mInflater;
    private List<Device> listDevice;
    private List<RecyclerAdapterDevice.ViewHolder> listViewHolder;

    public RecyclerAdapterDevice(Context context, List<Device> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new RecyclerAdapterDevice.MyHandler(this);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }

    @Override

    public int getItemViewType(int position) {
        Device device = listDevice.get(position);
        int i;
        if (device instanceof DevHaveChild) {
            //不显示位号菜单
            i = 0;
        } else {
            //显示位号菜单
            i = 1;
        }
        i <<= 1;
        if (device.getParent() != null) {
            //不显示模式菜单
            i |= 0;
            i <<= 1;
        } else {
            //显示模式菜单
            i |= 1;
            i <<= 1;
            if (device.getCtrlModel() == CtrlModel.REMOTE) {
                //设为本地模式为0
                i |= 0;
            } else {
                //设为远程模式为1
                i |= 1;
            }
        }
        return i;
    }

    @NonNull
    @Override
    public RecyclerAdapterDevice.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterDevice.ViewHolder vh = new RecyclerAdapterDevice.ViewHolder(mInflater.inflate(R.layout.search_device_list, parent, false));
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterDevice.ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Device device;
        private TextView textName;
        private TextView textCoding;
        private ImageView redGreen;
        private TextView textCtrlModel;
        private CheckBox cbVisibility;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.changer);
            textCoding = itemView.findViewById(R.id.text_coding);
            redGreen = itemView.findViewById(R.id.red_green);
            textCtrlModel = itemView.findViewById(R.id.txtCtrlModel);
            cbVisibility = itemView.findViewById(R.id.cbVisibility);
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init() {
            refreshName();
            refreshAlias();
            refreshState();
            refreshCtrlModel();
            cbVisibility.setChecked(device.isVisibility());
            cbVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                device.setVisibility(isChecked);
                DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
                deviceDao.update(device);
                if (null != ElectricalCtrlFragment.handler) {
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
                }
                if (null != ClimateFragment.handler) {
                    ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
                }
            });
        }

        private void refreshName() {
            textName.setText(device.getName());
        }

        private void refreshAlias() {
            if (device instanceof DevHaveChild) {
                textCoding.setText(device.getCoding());
            } else {
                textCoding.setText(String.format("%s : %s", device.getCoding(), device.getAlias()));
            }
        }

        private void refreshState() {
            if (device.isNormal()) {
                redGreen.setBackgroundResource(R.mipmap.normal_green);
            } else {
                redGreen.setBackgroundResource(R.mipmap.abnormal_red);
            }
        }

        private void refreshCtrlModel() {
            if (device.getCtrlModel() != CtrlModel.REMOTE) {
                textCtrlModel.setText("本地");
            } else {
                textCtrlModel.setText("远程");
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterDevice> mActivity;

        MyHandler(RecyclerAdapterDevice activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterDevice theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (RecyclerAdapterDevice.ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case CTRL_MODEL:
                            vh.refreshCtrlModel();
                            break;
                        case STATE:
                            vh.refreshState();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                        case ALIAS:
                            vh.refreshAlias();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
