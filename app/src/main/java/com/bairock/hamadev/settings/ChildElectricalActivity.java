package com.bairock.hamadev.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterChildDevice;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;
import com.bairock.iot.intelDev.user.ErrorCodes;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildElectricalActivity extends AppCompatActivity {

    public static int REFRESH_ELE_LIST = 3;

    public static DevHaveChild controller;
    private List<Device> listShowDevices;
    public static MyHandler handler;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewDevice;
    private RecyclerAdapterChildDevice adapterEle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_electrical);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        handler = new MyHandler(this);

        swipeMenuRecyclerViewDevice = findViewById(R.id.swipeMenuRecyclerViewDevice);
        swipeMenuRecyclerViewDevice.setLayoutManager(new LinearLayoutManager(this));
        swipeMenuRecyclerViewDevice.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewDevice.setSwipeMenuCreator(swipeMenuConditionCreator);

        swipeMenuRecyclerViewDevice.setSwipeItemClickListener(deviceSwipeItemClickListener);
        swipeMenuRecyclerViewDevice.setSwipeMenuItemClickListener(deviceSwipeMenuItemClickListener);

        setChildDeviceList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        controller = null;
        handler = null;
        RecyclerAdapterChildDevice.handler = null;
        super.onDestroy();
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem renameItem = new SwipeMenuItem(this)
                .setBackgroundColor(getResources().getColor(R.color.orange))
                .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width + 10)
                .setHeight(height);
        swipeRightMenu.addMenuItem(renameItem);// 添加菜单到右侧。
        SwipeMenuItem aliasItem = new SwipeMenuItem(this)
                .setBackgroundColor(getResources().getColor(R.color.green_normal))
                .setText("位号")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(aliasItem);// 添加菜单到右侧。
    };

    private void setChildDeviceList() {
        listShowDevices = new ArrayList<>(controller.getListDev());
        Collections.sort(listShowDevices, (device, t1) -> {
            int sort = 0;
            try {
                sort = Integer.parseInt(device.getSubCode()) - Integer.parseInt(t1.getSubCode());
            }catch (Exception e){
                e.printStackTrace();
            }
            return sort;
        });
        adapterEle = new RecyclerAdapterChildDevice(this, listShowDevices);
        swipeMenuRecyclerViewDevice.setAdapter(adapterEle);
    }

    private SwipeItemClickListener deviceSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Device device = listShowDevices.get(position);
            if(device instanceof DevCollect){
                DevCollectSettingActivity.devCollectSignal = (DevCollectSignal) device;
                ChildElectricalActivity.this.startActivity(new Intent(ChildElectricalActivity.this, DevCollectSettingActivity.class));
            }
        }
    };

    private SwipeMenuItemClickListener deviceSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            Device device = controller.getListDev().get(adapterPosition);
            switch (menuBridge.getPosition()){
                case 0:
                    showRenameDialog(device);
                    break;
                case 1:
                    showAliasDialog(device);
                    break;
            }
        }
    };

    private void showRenameDialog(final Device device) {
        final EditText edit_newName = new EditText(ChildElectricalActivity.this);
        edit_newName.setText(device.getName());
        new AlertDialog.Builder(ChildElectricalActivity.this)
                .setTitle(
                        ChildElectricalActivity.this.getString(R.string.input_or_choose_name))
                .setView(edit_newName)
                .setPositiveButton(MainActivity.strEnsure,
                        (dialog, which) -> {
                            String value = edit_newName.getText().toString();
                            if(HamaApp.DEV_GROUP.renameDevice(device, value) == ErrorCodes.DEV_NAME_IS_EXISTS){
                                Toast.makeText(ChildElectricalActivity.this, "与组内其他设备名重复", Toast.LENGTH_SHORT).show();
                            }else{
                                DeviceDao deviceDao = DeviceDao.get(ChildElectricalActivity.this);
                                deviceDao.update(device);
                                adapterEle.notifyDataSetChanged();
                            }
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void showAliasDialog(final Device device) {
        final EditText edit_newName = new EditText(ChildElectricalActivity.this);
        edit_newName.setText(device.getAlias());
        new AlertDialog.Builder(ChildElectricalActivity.this)
                .setTitle("输入位号")
                .setView(edit_newName)
                .setPositiveButton(MainActivity.strEnsure,
                        (dialog, which) -> {
                            String value = edit_newName.getText().toString();
                            device.setAlias(value);
                            DeviceDao deviceDao = DeviceDao.get(ChildElectricalActivity.this);
                            deviceDao.update(device);
                            adapterEle.notifyDataSetChanged();
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    public static class MyHandler extends Handler {
        WeakReference<ChildElectricalActivity> mActivity;

        MyHandler(ChildElectricalActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChildElectricalActivity theActivity = mActivity.get();
            if (msg.arg1 == REFRESH_ELE_LIST) {
                theActivity.setChildDeviceList();
                //theActivity.adapterEle.notifyDataSetChanged();
            }
        }
    }
}
