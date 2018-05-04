package com.bairock.hamadev.settings;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterSortDevice;
import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortActivity extends AppCompatActivity {

    private ListView listViewElectrical;
    private List<Device> listDevice;
    private AdapterSortDevice adapterSortElectrical;
    private boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listDevice = new ArrayList<>();
        listDevice.addAll(HamaApp.DEV_GROUP.findListIStateDev(true));
        listDevice.addAll(HamaApp.DEV_GROUP.findListCollectDev(true));
        Collections.sort(listDevice);
        for(int i = 0; i < listDevice.size(); i++){
            listDevice.get(i).setSortIndex(i);
        }
        //Collections.sort(listDevice);
        findViews();
        setListener();
        setListViewElectrical();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
            case R.id.action_up:
                move(0);
                updated = true;
                break;
            case R.id.action_down:
                move(1);
                updated = true;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findViews(){
        listViewElectrical = (ListView)findViewById(R.id.listViewDevices);
    }

    private void setListener(){
        listViewElectrical.setOnItemClickListener(electricalOnItemClickListener);
    }

    private void setListViewElectrical(){
        Collections.sort(listDevice);
        adapterSortElectrical = new AdapterSortDevice(this, listDevice);
        listViewElectrical.setAdapter(adapterSortElectrical);
    }

    private void move(int forward){
        if(listDevice == null || listDevice.isEmpty()){
            return;
        }

        if(null == adapterSortElectrical || null == adapterSortElectrical.selectedDevice){
            return;
        }

        for(int i = 0; i < listDevice.size(); i++){
            listDevice.get(i).setSortIndex(i);
        }
        int sIndex = adapterSortElectrical.selectedDevice.getSortIndex();

        if(forward == 0){
            moveUp(sIndex);
        }else{
            moveDown(sIndex);
        }

        Collections.sort(listDevice);
        adapterSortElectrical.notifyDataSetChanged();
    }

    private void moveUp(int sIndex){
        if(sIndex == 0){
            Snackbar.make(listViewElectrical, "已经在最前面了", Snackbar.LENGTH_SHORT).show();
            return;
        }
        adapterSortElectrical.selectedDevice.setSortIndex(sIndex - 1);
        Device upDevice = listDevice.get(sIndex - 1);
        upDevice.setSortIndex(upDevice.getSortIndex() + 1);
    }

    private void moveDown(int sIndex){
        if(sIndex == listDevice.size() - 1){
            Snackbar.make(listViewElectrical, "已经在最后面了", Snackbar.LENGTH_SHORT).show();
            return;
        }
        adapterSortElectrical.selectedDevice.setSortIndex(sIndex + 1);
        Device dwonDevice = listDevice.get(sIndex + 1);
        dwonDevice.setSortIndex(dwonDevice.getSortIndex() - 1);
    }

    private AdapterView.OnItemClickListener electricalOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            adapterSortElectrical.selectedDevice = listDevice.get(arg2);
            adapterSortElectrical.notifyDataSetChanged();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(updated){
            DeviceDao deviceDao = DeviceDao.get(SortActivity.this);
            for(Device device : listDevice){
                deviceDao.update(device);
            }
            if(null != ElectricalCtrlFragment.handler){
                ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
            }
            if(null != ClimateFragment.handler){
                ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
            }
        }
    }
}
