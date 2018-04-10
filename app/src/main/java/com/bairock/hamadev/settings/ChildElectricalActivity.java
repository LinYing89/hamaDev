package com.bairock.hamadev.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.MainSecondTitleAdapter;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.communication.SearchDeviceHelper;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.user.ErrorCodes;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ChildElectricalActivity extends AppCompatActivity {

    public static int REFRESH_ELE_LIST = 3;

    public static DevHaveChild controller;
    public static MyHandler handler;

    private ListView listviewElectrical;
    private MainSecondTitleAdapter adapterEle;

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
        listviewElectrical = (ListView)findViewById(R.id.listElectrical);
        listviewElectrical.setOnItemLongClickListener(remoteOnItemLongClickListener);
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
        super.onDestroy();
    }

    private void setChildDeviceList() {
        List<Device> list = new ArrayList<>();
        list.addAll(controller.getListDev());
        adapterEle = new MainSecondTitleAdapter(this,list);
        listviewElectrical.setAdapter(adapterEle);
    }

    private AdapterView.OnItemLongClickListener remoteOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            Device device = controller.getListDev().get(arg2);
            //controller.setSelectedElectrical(arg2);
            showElectricalPopUp(arg1, device);
            return true;
        }
    };

    public void showElectricalPopUp(View v, Device device) {
        View layout = this.getLayoutInflater()
                .inflate(R.layout.pop_device_long_click, null);
        Button layoutRename = (Button) layout
                .findViewById(R.id.text_rename);
        Button btnAlias = (Button) layout
                .findViewById(R.id.text_alias);
        layout.findViewById(R.id.text_delete).setVisibility(View.GONE);
        final PopupWindow popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popupWindow.showAsDropDown(v);
       // popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
        layoutRename.setOnClickListener(v12 -> {
            popupWindow.dismiss();
            showRenameDialog(device);
        });
        btnAlias.setOnClickListener(v1 -> {
            popupWindow.dismiss();
            showAliasDialog(device);
        });
    }

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
