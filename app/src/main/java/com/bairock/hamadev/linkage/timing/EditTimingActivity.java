package com.bairock.hamadev.linkage.timing;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterEffect;
import com.bairock.hamadev.adapter.AdapterTimer;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.ZTimerDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditTimingActivity extends AppCompatActivity {

    public static MyHandler handler;
    public static final int REFRESH_DEVICE_LIST = 1;
    public static final int REFRESH_EVENT_HANDLER_LIST = 2;

    public static Timing timing;
    public static ZTimer zTimer;
    public static Effect effect;
    public static boolean ADD;

    private ActionBar actionBar;
    private Button btnAddTimer;
    private Button btnAddEffect;
    private ListView listViewTimer;
    private ListView listViewEffect;

    private AdapterTimer adapterTimer;
    private AdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timing);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(ADD){
            actionBar.setTitle("添加定时");
            timing = new Timing();
            timing.setName(getDefaultName());
            HamaApp.DEV_GROUP.getTimingHolder().addTiming(timing);
            LinkageDao.get(this).add(timing, HamaApp.DEV_GROUP.getTimingHolder().getId());
        }else{
            actionBar.setTitle("编辑定时");
            timing = TimingFragment.TIMING;
            if(timing == null){
                finish();
                return;
            }
        }
        actionBar.setSubtitle(timing.getName());
        setListViewCondition();
        setListViewEffect();

        handler = new MyHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_chain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
            case R.id.action_edit_name:
                showNameDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != TimingFragment.handler){
            TimingFragment.handler.obtainMessage(TimingFragment.REFRESH_LIST).sendToTarget();
        }
        timing = null;
    }

    private void findViews(){
        btnAddTimer = (Button)findViewById(R.id.btnAddTimer);
        btnAddEffect = (Button)findViewById(R.id.btnAddEffect);
        listViewTimer = (ListView)findViewById(R.id.listViewTimer);
        listViewEffect = (ListView)findViewById(R.id.listViewEffect);
    }

    private void setListener(){
        btnAddTimer.setOnClickListener(onClickListener);
        btnAddEffect.setOnClickListener(onClickListener);

        listViewTimer.setOnItemClickListener(eventOnItemClickListener);
        listViewTimer.setOnItemLongClickListener(eventOnItemLongClickListener);
        listViewEffect.setOnItemLongClickListener(deviceOnItemLongClickListener);
    }

    private String getDefaultName(){
        String name = "定时";
        boolean have;
        for(int i=1; i< 1000; i++){
            have = false;
            name = "定时" + i;
            for(Linkage linkage : HamaApp.DEV_GROUP.getTimingHolder().getListLinkage()){
                if(linkage.getName().equals(name)){
                    have = true;
                    break;
                }
            }
            if(!have){
                return name;
            }
        }
        return name;
    }

    /**
     * 名称对话框
     */
    private void showNameDialog() {
        final EditText editHour = new EditText(this);
        editHour.setText(String.valueOf(timing.getName()));
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(editHour)
                .setPositiveButton(
                        MainActivity.strEnsure,
                        (dialog1, which) -> {
                            String strName = String.valueOf(editHour.getText());
                            timing.setName(strName);
                            actionBar.setSubtitle(strName);
                            LinkageDao.get(EditTimingActivity.this).update(timing, null);
                        })
                .setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void setListViewCondition(){
        adapterTimer = new AdapterTimer(this, timing.getListZTimer());
        listViewTimer.setAdapter(adapterTimer);
    }

    private void setListViewEffect(){
        adapterEffect = new AdapterEffect(this, timing.getListEffect(), false);
        listViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddTimer:
//                    TimerActivity.isAdd = true;
//                    TimerActivity.handler = handler;
                    startActivity(new Intent(EditTimingActivity.this, TimerActivity.class));
                    break;
                case R.id.btnAddEffect:
                    showDeviceList();
                    break;
            }
        }
    };

    //条件列表点击事件
    private AdapterView.OnItemClickListener eventOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ZTimer condition = timing.getListZTimer().get(position);
            TimerActivity.timer = condition;
            zTimer = condition;
            startActivity(new Intent(EditTimingActivity.this, TimerActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            zTimer = timing.getListZTimer().get(position);
            showItemPopup(view, 0);
            return true;
        }
    };

    private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            effect = timing.getListEffect().get(position);
            showItemPopup(view, 1);
            return true;
        }
    };

    private void showDeviceList(){
        List<Device> list = new ArrayList<>();
        for(Device device : HamaApp.DEV_GROUP.findListIStateDev(true)){
            boolean haved = false;
            for(Effect effect : timing.getListEffect()){
                if(device == effect.getDevice()){
                    haved = true;
                    break;
                }
            }
            if(!haved){
                list.add(device);
            }
        }

        String[] names = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            names[i] = list.get(i).getName();
        }
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("选择设备");
        listDialog.setItems(names, (dialog, which) -> {
            Effect effect = new Effect();
            effect.setDevice(list.get(which));
            effect.setDsId(DevStateHelper.DS_GUAN);
            timing.getListEffect().add(effect);
            EffectDao effectDao = EffectDao.get(EditTimingActivity.this);
            effectDao.add(effect, timing.getId());
            adapterEffect.notifyDataSetChanged();
        });
        listDialog.show();
    }

    private void showItemPopup(View view, int which){
        Button btnDel = new Button(this);
        btnDel.setText("删除");
        final PopupWindow popupWindow = new PopupWindow(btnDel, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(view);
        btnDel.setOnClickListener(v -> {
            popupWindow.dismiss();
            if(which == 0){
                timing.removeZTimer(zTimer);
                zTimer.setDeleted(true);
                ZTimerDao zTimerDao = ZTimerDao.get(EditTimingActivity.this);
                //zTimerDao.update(zTimer, null);
                zTimerDao.delete(zTimer);
                adapterTimer.notifyDataSetChanged();
            }else{
                timing.removeEffect(effect);
                effect.setDeleted(true);
                EffectDao effectDao = EffectDao.get(EditTimingActivity.this);
                //effectDao.update(effect,null);
                effectDao.delete(effect);
                adapterEffect.notifyDataSetChanged();
            }
        });
    }

    public static class MyHandler extends Handler {
        WeakReference<EditTimingActivity> mActivity;

        MyHandler(EditTimingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EditTimingActivity theActivity = mActivity.get();
            switch (msg.what) {
                case EditTimingActivity.REFRESH_EVENT_HANDLER_LIST:
                    theActivity.adapterTimer.notifyDataSetChanged();
                    break;
                case EditTimingActivity.REFRESH_DEVICE_LIST :
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
            }

        }
    }
}
