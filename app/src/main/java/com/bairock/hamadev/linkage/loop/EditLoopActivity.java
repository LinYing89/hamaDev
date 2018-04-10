package com.bairock.hamadev.linkage.loop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterCondition;
import com.bairock.hamadev.adapter.AdapterEffect;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageConditionDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.linkage.ConditionActivity;
import com.bairock.hamadev.linkage.EditChainActivity;
import com.bairock.hamadev.linkage.timing.EditTimingActivity;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditLoopActivity extends AppCompatActivity {

    public static MyHandler handler;

    public static ZLoop zLoop;
    private LinkageCondition linkageCondition;
    public static Effect effect;
    public static boolean ADD;

    private ActionBar actionBar;
    private Button btnAddConditionHandler;
    private Button btnAddEffect;
    private ListView listViewConditionHandler;
    private ListView listViewEffect;

    private AdapterCondition adapterCondition;
    private AdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_loop);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(ADD){
            actionBar.setTitle("添加循环");
            zLoop = new ZLoop();
            zLoop.setName(getDefaultName());
            HamaApp.DEV_GROUP.getLoopHolder().addLinkage(zLoop);
            LinkageDao.get(this).add(zLoop, HamaApp.DEV_GROUP.getLoopHolder().getId());
        }else{
            actionBar.setTitle("编辑循环");
            zLoop = LoopFragment.ZLOOP;
            if(zLoop == null){
                finish();
                return;
            }
        }
        actionBar.setSubtitle(zLoop.getName());
        setListViewCondition();
        setListViewEffect();

        handler = new MyHandler(this);
        setActionbarSubtitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_loop, menu);
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
            case R.id.action_loop_count:
                showLoopCountDialog();
                break;
            case R.id.action_loop_duration_time:
                startActivity(new Intent(EditLoopActivity.this, DurationListActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != LoopFragment.handler){
            LoopFragment.handler.obtainMessage(LoopFragment.REFRESH_LIST).sendToTarget();
        }
        zLoop = null;
    }

    private void findViews(){
        btnAddConditionHandler = (Button)findViewById(R.id.btnAddConditionHandler);
        btnAddEffect = (Button)findViewById(R.id.btnAddEffect);
        listViewConditionHandler = (ListView)findViewById(R.id.listViewConditionHandler);
        listViewEffect = (ListView)findViewById(R.id.listViewEffect);
    }

    private void setListener(){
        btnAddConditionHandler.setOnClickListener(onClickListener);
        btnAddEffect.setOnClickListener(onClickListener);

        listViewConditionHandler.setOnItemClickListener(eventOnItemClickListener);
        listViewConditionHandler.setOnItemLongClickListener(eventOnItemLongClickListener);
        listViewEffect.setOnItemLongClickListener(deviceOnItemLongClickListener);
    }

    private String getDefaultName(){
        String name = "循环";
        boolean have;
        for(int i=1; i< 1000; i++){
            have = false;
            name = "循环" + i;
            for(Linkage chain : HamaApp.DEV_GROUP.getLoopHolder().getListLinkage()){
                if(chain.getName().equals(name)){
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
        editHour.setText(String.valueOf(zLoop.getName()));
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(editHour)
                .setPositiveButton(
                        MainActivity.strEnsure,
                        (dialog1, which) -> {
                            String strName = String.valueOf(editHour.getText());
                            zLoop.setName(strName);
                            setActionbarSubtitle();
                            LinkageDao.get(EditLoopActivity.this).update(zLoop, HamaApp.DEV_GROUP.getChainHolder().getId());
                        })
                .setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    /**
     * 次数对话框
     */
    private void showLoopCountDialog() {
        View convertView = this.getLayoutInflater().inflate(
                R.layout.dialog_loop_count, null);
        final EditText editLoopCount = (EditText) convertView
                .findViewById(R.id.edit_loop_count);
        final CheckBox checkBoxLoopInfinite = (CheckBox)convertView
                .findViewById(R.id.check_loop_infinite);
        if(zLoop.getLoopCount() == -1){
            editLoopCount.setEnabled(false);
            checkBoxLoopInfinite.setChecked(true);
        }else{
            editLoopCount.setText(String.valueOf(zLoop.getLoopCount()));
            checkBoxLoopInfinite.setChecked(false);
        }
        checkBoxLoopInfinite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editLoopCount.setEnabled(false);
                    zLoop.setLoopCount(-1);
                    LinkageDao.get(EditLoopActivity.this).update(zLoop, null);
                }else{
                    editLoopCount.setEnabled(true);
                }
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(convertView)
                .setPositiveButton(
                        MainActivity.strEnsure,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if(!checkBoxLoopInfinite.isChecked()){
                                    String strHour = String.valueOf(editLoopCount.getText());
                                    try{
                                        zLoop.setLoopCount(Integer.parseInt(strHour));
                                        LinkageDao.get(EditLoopActivity.this).update(zLoop, null);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Snackbar.make(editLoopCount, "格式错误", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                                setActionbarSubtitle();
                            }
                        })
                .setNegativeButton(
                        MainActivity.strCancel,
                        null).create().show();

    }

    private void setActionbarSubtitle(){
        if(zLoop.getLoopCount() == -1){
            actionBar.setSubtitle(zLoop.getName() + " " + "次数:" + "无限");
        }else{
            actionBar.setSubtitle(zLoop.getName() + " " + "次数:" + zLoop.getLoopCount());
        }
    }

    private void setListViewCondition(){
        adapterCondition = new AdapterCondition(this, zLoop.getListCondition());
        listViewConditionHandler.setAdapter(adapterCondition);
    }

    private void setListViewEffect() {
        adapterEffect = new AdapterEffect(this, zLoop.getListEffect(), false);
        listViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddConditionHandler:
                    ConditionActivity.ADD = true;
                    ConditionActivity.handler = handler;
                    startActivity(new Intent(EditLoopActivity.this, ConditionActivity.class));
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
            LinkageCondition condition = zLoop.getListCondition().get(position);
            ConditionActivity.ADD = false;
            ConditionActivity.handler = handler;
            ConditionActivity.condition = condition;
            //linkageCondition = condition;
            startActivity(new Intent(EditLoopActivity.this, ConditionActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            linkageCondition = zLoop.getListCondition().get(position);
            showItemPopup(view, 0);
            return true;
        }
    };

    private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            effect = zLoop.getListEffect().get(position);
            showItemPopup(view, 1);
            return true;
        }
    };

    private void showDeviceList(){
        List<Device> list = new ArrayList<>();
        for(Device device : HamaApp.DEV_GROUP.findListIStateDev(true)){
            boolean haved = false;
            for(Effect effect : zLoop.getListEffect()){
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
        listDialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Effect effect = new Effect();
                effect.setDevice(list.get(which));
                effect.setDsId(DevStateHelper.DS_GUAN);
                zLoop.getListEffect().add(effect);
                EffectDao effectDao = EffectDao.get(EditLoopActivity.this);
                effectDao.add(effect, zLoop.getId());
                adapterEffect.notifyDataSetChanged();
            }
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
                zLoop.removeCondition(linkageCondition);
                linkageCondition.setDeleted(true);
                LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(EditLoopActivity.this);
                linkageConditionDao.delete(linkageCondition);
                adapterCondition.notifyDataSetChanged();
            }else{
                zLoop.removeEffect(effect);
                effect.setDeleted(true);
                EffectDao effectDao = EffectDao.get(EditLoopActivity.this);
                //effectDao.update(effect,null);
                effectDao.delete(effect);
                adapterEffect.notifyDataSetChanged();
            }
        });
    }

        public static class MyHandler extends Handler {
        WeakReference<EditLoopActivity> mActivity;

        MyHandler(EditLoopActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EditLoopActivity theActivity = mActivity.get();
            switch (msg.what) {
                case EditTimingActivity.REFRESH_EVENT_HANDLER_LIST:
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
                case EditTimingActivity.REFRESH_DEVICE_LIST :
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
                case ConditionActivity.ADD_CONDITION :
                    LinkageCondition lc = (LinkageCondition)msg.obj;
                    EditLoopActivity.zLoop.addCondition(lc);
                    LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(theActivity);
                    linkageConditionDao.add(lc, EditLoopActivity.zLoop.getId());
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
                case ConditionActivity.UPDATE_CONDITION :
                    LinkageCondition lc1 = (LinkageCondition)msg.obj;
                    LinkageConditionDao linkageConditionDao1 = LinkageConditionDao.get(theActivity);
                    linkageConditionDao1.update(lc1, null);
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
            }

        }
    }
}
