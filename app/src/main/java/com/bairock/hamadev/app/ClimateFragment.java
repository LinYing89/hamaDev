package com.bairock.hamadev.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterCollect;
import com.bairock.hamadev.database.CollectPropertyDao;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.Pressure;
import com.bairock.iot.intelDev.user.DevGroup;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class ClimateFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    public static final int REFRESH_VALUE = 1;
    public static final int REFRESH_DEVICE = 2;
    public static final int REFRESH_SORT= 3;

    public static MyHandler handler;

    private ListView listViewPressure;
    private AdapterCollect adapterCollect;
    private List<DevCollect> listDevCollect;

    public ClimateFragment() {
        // Required empty public constructor
    }

    public static ClimateFragment newInstance(int param1) {
        ClimateFragment fragment = new ClimateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_climate, container, false);
        handler = new MyHandler(this);
        listViewPressure = (ListView)view.findViewById(R.id.list_pressure);
        setListener();
        setPressueList();
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        for(DevCollect device : listDevCollect){
            removeDeviceListener(device);
        }
    }

    private void setPressueList(){
        listDevCollect = HamaApp.DEV_GROUP.findListCollectDev();
        Collections.sort(listDevCollect);
        adapterCollect = new AdapterCollect(this.getContext(), listDevCollect);
        listViewPressure.setAdapter(adapterCollect);
        for(DevCollect device : listDevCollect){
            setDeviceListener(device);
        }
    }

    private void setDeviceListener(DevCollect device){
        device.addOnNameChangedListener(onNameChangedListener);
        device.addOnAliasChangedListener(onAliasChangedListener);
        //device.getCollectProperty().setOnCurrentValueChanged(onCurrentValueChangedListener);
    }

    private void removeDeviceListener(DevCollect device){
        device.removeOnNameChangedListener(onNameChangedListener);
        device.removeOnAliasChangedListener(onAliasChangedListener);
        device.getCollectProperty().setOnCurrentValueChanged(null);
    }

    private Device.OnNameChangedListener onNameChangedListener = new Device.OnNameChangedListener() {
        @Override
        public void onNameChanged(Device device, String s) {
            AdapterCollect.handler.obtainMessage(AdapterCollect.NAME, device).sendToTarget();
        }
    };

    private Device.OnAliasChangedListener onAliasChangedListener = new Device.OnAliasChangedListener() {
        @Override
        public void onAliasChanged(Device device, String s) {
            AdapterCollect.handler.obtainMessage(AdapterCollect.ALIAS, device).sendToTarget();
        }
    };

    private DevGroup.OnDeviceCollectionChangedListener onDeviceCollectionChangedListener = new DevGroup.OnDeviceCollectionChangedListener() {
        @Override
        public void onAdded(Device device) {
            addDev(device);
            //handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            removeDev(device);
            //handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }
    };

    private void addDev(Device device){
        if(device instanceof DevCollect){
            DevCollect devCollect = (DevCollect)device;
            listDevCollect.add(devCollect);
            setDeviceListener(devCollect);
            handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                addDev(device1);
            }
        }
    }
    private void removeDev(Device device){
        if(device instanceof DevCollect){
            DevCollect devCollect = (DevCollect)device;
            listDevCollect.remove(devCollect);
            removeDeviceListener(devCollect);
            handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                removeDev(device1);
            }
        }
    }

    private void setListener(){
        listViewPressure.setOnItemLongClickListener(onItemLongClickListener);
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showMaxValueDialog(listDevCollect.get(position));
            return false;
        }
    };

    private void showMaxValueDialog(DevCollect devCollect) {
        if(!(devCollect instanceof Pressure)){
            return;
        }
        final Pressure pressure = (Pressure)devCollect;
        final EditText edit_newName = new EditText(ClimateFragment.this.getContext());
        edit_newName.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(pressure.getCollectProperty().getCrestValue() == null){
            edit_newName.setText("");
        }else {
            edit_newName.setText(String.valueOf(pressure.getCollectProperty().getCrestValue()));
        }
        new AlertDialog.Builder(ClimateFragment.this.getContext())
                .setTitle("输入最大值，单位mm")
                .setView(edit_newName)
                .setPositiveButton(MainActivity.strEnsure,
                        (dialog, which) -> {
                            String value = edit_newName.getText().toString();
                            try{
                                float iValue = Float.parseFloat(value);
                                pressure.getCollectProperty().setCrestValue(iValue);
                                CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(ClimateFragment.this.getActivity());
                                collectPropertyDao.update(pressure.getCollectProperty());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    public static class MyHandler extends Handler {
        WeakReference<ClimateFragment> mActivity;

        MyHandler(ClimateFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            final ClimateFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_VALUE:
                    theActivity.adapterCollect.notifyDataSetChanged();
                    break;
                case REFRESH_DEVICE :
                    theActivity.setPressueList();
                    break;
                case REFRESH_SORT :
                    break;
            }

        }
    }
}
