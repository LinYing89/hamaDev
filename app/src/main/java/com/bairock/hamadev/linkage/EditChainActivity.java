package com.bairock.hamadev.linkage;

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
import com.bairock.hamadev.adapter.AdapterCondition;
import com.bairock.hamadev.adapter.AdapterEffect;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageConditionDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.SubChain;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditChainActivity extends AppCompatActivity {

    public static MyHandler handler;
    public static final int REFRESH_DEVICE_LIST = 1;
    public static final int REFRESH_EVENT_HANDLER_LIST = 2;

    private SubChain subChain;
    private LinkageCondition linkageCondition;
    public static Effect effect;
    public static boolean ADD;

    private ActionBar actionBar;
    private Button btnAddCondition;
    private Button btnAddEffect;
    private ListView listViewCondition;
    private ListView listViewEffect;

    private AdapterCondition adapterCondition;
    private AdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chain);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(ADD){
            actionBar.setTitle("添加连锁");
            subChain = new SubChain();
            subChain.setName(getDefaultName());
            HamaApp.DEV_GROUP.getChainHolder().addLinkage(subChain);
            LinkageDao linkageDevValueDao = LinkageDao.get(this);
            linkageDevValueDao.add(subChain, HamaApp.DEV_GROUP.getChainHolder().getId());
        }else{
            actionBar.setTitle("编辑连锁");
            subChain = (SubChain) ChainFragment.LINKAGE;
            if(subChain == null){
                finish();
                return;
            }
        }
        actionBar.setSubtitle(subChain.getName());

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
//        if(ADD){
//            HamaApp.DEV_GROUP.getChainHolder().addSubChain(subChain);
//            LinkageDao linkageDevValueDao = LinkageDao.get(this);
//            linkageDevValueDao.add(subChain, HamaApp.DEV_GROUP.getChainHolder().getId());
//        }
        if(null != ChainFragment.handler){
            ChainFragment.handler.obtainMessage(ChainFragment.REFRESH_LIST).sendToTarget();
        }
        subChain = null;
    }

    private void findViews(){
        btnAddCondition = (Button)findViewById(R.id.btnAddCondition);
        btnAddEffect = (Button)findViewById(R.id.btnAddEffect);
        listViewCondition = (ListView)findViewById(R.id.listViewCondition);
        listViewEffect = (ListView)findViewById(R.id.listViewEffect);
    }

    private void setListener(){
        btnAddCondition.setOnClickListener(onClickListener);
        btnAddEffect.setOnClickListener(onClickListener);

        listViewCondition.setOnItemClickListener(eventOnItemClickListener);
        listViewCondition.setOnItemLongClickListener(eventOnItemLongClickListener);
        listViewEffect.setOnItemLongClickListener(deviceOnItemLongClickListener);
    }

    private String getDefaultName(){
        String name = "连锁";
        boolean have;
        for(int i=1; i< 1000; i++){
            have = false;
            name = "连锁" + i;
            for(Linkage chain : HamaApp.DEV_GROUP.getChainHolder().getListLinkage()){
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
        editHour.setText(String.valueOf(subChain.getName()));
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(editHour)
                .setPositiveButton(
                        MainActivity.strEnsure,
                        (dialog1, which) -> {
                            String strName = String.valueOf(editHour.getText());
                            subChain.setName(strName);
                            actionBar.setSubtitle(strName);
                            LinkageDao linkageDevValueDao = LinkageDao.get(EditChainActivity.this);
                            linkageDevValueDao.update(subChain, HamaApp.DEV_GROUP.getChainHolder().getId());
                        })
                .setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void setListViewCondition(){
        adapterCondition = new AdapterCondition(this, subChain.getListCondition());
        listViewCondition.setAdapter(adapterCondition);
    }

    private void setListViewEffect(){
        adapterEffect = new AdapterEffect(this, subChain.getListEffect(), true);
        listViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddCondition:
                    ConditionActivity.ADD = true;
                    ConditionActivity.handler = handler;
                    startActivity(new Intent(EditChainActivity.this, ConditionActivity.class));
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
            LinkageCondition condition = subChain.getListCondition().get(position);
            ConditionActivity.ADD = false;
            ConditionActivity.handler = handler;
            ConditionActivity.condition = condition;
            //linkageCondition = condition;
            startActivity(new Intent(EditChainActivity.this, ConditionActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener eventOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            linkageCondition = subChain.getListCondition().get(position);
            showItemPopup(view, 0);
            return true;
        }
    };

    private AdapterView.OnItemLongClickListener deviceOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            effect = subChain.getListEffect().get(position);
            showItemPopup(view, 1);
            return true;
        }
    };

    private void showDeviceList(){
        List<Device> list = new ArrayList<>();
        for(Device device : HamaApp.DEV_GROUP.findListIStateDev(true)){
            boolean haved = false;
            for(Effect effect : subChain.getListEffect()){
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
            subChain.getListEffect().add(effect);
            EffectDao effectDao = EffectDao.get(EditChainActivity.this);
            effectDao.add(effect, subChain.getId());
            adapterEffect.notifyDataSetChanged();
        });
        listDialog.show();
    }

    private void showItemPopup(View view, int which){
        Button btnDel = new Button(this);
        btnDel.setText("删除");
//        btnDel.setBackgroundColor(Color.BLUE);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        btnDel.setLayoutParams(layoutParams);
//        final PopupWindow popupWindow = new PopupWindow(btnDel, 100, 100);
        final PopupWindow popupWindow = new PopupWindow(btnDel, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(view);
        btnDel.setOnClickListener(v -> {
         popupWindow.dismiss();
            if(which == 0){
                subChain.removeCondition(linkageCondition);
                linkageCondition.setDeleted(true);
                LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(EditChainActivity.this);
                //linkageConditionDao.update(linkageCondition, subChain.getId());
                linkageConditionDao.delete(linkageCondition);
                adapterCondition.notifyDataSetChanged();
            }else{
                subChain.removeEffect(effect);
                effect.setDeleted(true);
                EffectDao effectDao = EffectDao.get(EditChainActivity.this);
                //effectDao.update(effect,subChain.getId());
                effectDao.delete(effect);
                adapterEffect.notifyDataSetChanged();
            }
        });
    }

    public static class MyHandler extends Handler {
        WeakReference<EditChainActivity> mActivity;

        MyHandler(EditChainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EditChainActivity theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_EVENT_HANDLER_LIST:
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
                case REFRESH_DEVICE_LIST :
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
                case ConditionActivity.ADD_CONDITION :
                    LinkageCondition lc = (LinkageCondition)msg.obj;
                    theActivity.subChain.addCondition(lc);
                    LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(theActivity);
                    linkageConditionDao.add(lc, theActivity.subChain.getId());
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
