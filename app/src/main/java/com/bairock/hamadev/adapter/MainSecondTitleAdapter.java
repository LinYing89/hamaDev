package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

import java.util.List;

/**
 *
 * Created by Administrator on 2017/8/29.
 */

public class MainSecondTitleAdapter extends BaseAdapter {

    private Context context;
    private List<Device> listElectrical;

    public MainSecondTitleAdapter(Context context, List<Device> listElectrical){
        this.context = context;
        this.listElectrical = listElectrical;
    }
    public int getCount() {
        return listElectrical.size();
    }

    public Object getItem(int arg0) {
        return listElectrical.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if(convertView == null){
            mViewHolder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.adapter_child_electrical, parent, false);
            mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_name);
            mViewHolder.textState  = (TextView) convertView.findViewById(R.id.text_num);
            mViewHolder.cbVisibility  = (CheckBox) convertView.findViewById(R.id.cbVisibility);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.device = listElectrical.get(position);
        mViewHolder.init();
        return convertView;
    }

    static class ViewHolder {
        private TextView textName;
        private TextView textState;
        private CheckBox cbVisibility;
        private Device device;

        private void init(){
            textName.setText(device.getName());
            textState.setText(device.getSubCode());
            cbVisibility.setChecked(device.isVisibility());
            cbVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                device.setVisibility(isChecked);
                DeviceDao deviceDao = DeviceDao.get(HamaApp.HAMA_CONTEXT);
                deviceDao.update(device);
                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
                }
            });
        }
    }
}
