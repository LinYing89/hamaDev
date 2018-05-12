package com.bairock.hamadev.linkage.loop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterCondition;
import com.bairock.hamadev.adapter.RecyclerAdapterEffect;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageConditionDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.linkage.ConditionActivity;
import com.bairock.hamadev.linkage.LinkageBaseFragment;
import com.bairock.hamadev.linkage.timing.EditTimingActivity;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EditLoopActivity extends AppCompatActivity {

    public static MyHandler handler;

    public static ZLoop zLoop;
    public static Effect effect;

    private ActionBar actionBar;
    private Button btnAddConditionHandler;
    private Button btnAddEffect;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewCondition;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewEffect;

    private RecyclerAdapterCondition adapterCondition;
    private RecyclerAdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_loop);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("编辑循环");
        }

        findViews();
        setListener();

        zLoop = (ZLoop) LinkageBaseFragment.Companion.getLINKAGE();
        if (zLoop == null) {
            finish();
            return;
        }
        if (actionBar != null) {
            actionBar.setSubtitle(zLoop.getName());
        }
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
        zLoop = null;
    }

    private void findViews() {
        btnAddConditionHandler = findViewById(R.id.btnAddConditionHandler);
        btnAddEffect = findViewById(R.id.btnAddEffect);
        swipeMenuRecyclerViewCondition = findViewById(R.id.swipeMenuRecyclerViewCondition);
        swipeMenuRecyclerViewCondition.setLayoutManager(new LinearLayoutManager(this));
        swipeMenuRecyclerViewCondition.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewCondition.setSwipeMenuCreator(swipeMenuConditionCreator);

        swipeMenuRecyclerViewEffect = findViewById(R.id.swipeMenuRecyclerViewEffect);
        swipeMenuRecyclerViewEffect.setLayoutManager(new LinearLayoutManager(this));
        swipeMenuRecyclerViewEffect.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewEffect.setSwipeMenuCreator(swipeMenuConditionCreator);
    }

    private void setListener() {
        btnAddConditionHandler.setOnClickListener(onClickListener);
        btnAddEffect.setOnClickListener(onClickListener);

        swipeMenuRecyclerViewCondition.setSwipeItemClickListener(conditionSwipeItemClickListener);
        swipeMenuRecyclerViewCondition.setSwipeMenuItemClickListener(conditionSwipeMenuItemClickListener);
        swipeMenuRecyclerViewEffect.setSwipeMenuItemClickListener(effectSwipeMenuItemClickListener);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem deleteItem = new SwipeMenuItem(EditLoopActivity.this)
                .setBackgroundColor(Color.RED)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    /**
     * 次数对话框
     */
    private void showLoopCountDialog() {
        View convertView = this.getLayoutInflater().inflate(
                R.layout.dialog_loop_count, null);
        final EditText editLoopCount = convertView
                .findViewById(R.id.edit_loop_count);
        final CheckBox checkBoxLoopInfinite = convertView
                .findViewById(R.id.check_loop_infinite);
        if (zLoop.getLoopCount() == -1) {
            editLoopCount.setEnabled(false);
            checkBoxLoopInfinite.setChecked(true);
        } else {
            editLoopCount.setText(String.valueOf(zLoop.getLoopCount()));
            checkBoxLoopInfinite.setChecked(false);
        }
        checkBoxLoopInfinite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editLoopCount.setEnabled(false);
                zLoop.setLoopCount(-1);
                LinkageDao.get(EditLoopActivity.this).update(zLoop, null);
            } else {
                editLoopCount.setEnabled(true);
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(convertView)
                .setPositiveButton(
                        MainActivity.strEnsure,
                        (dialog1, which) -> {
                            if (!checkBoxLoopInfinite.isChecked()) {
                                String strHour = String.valueOf(editLoopCount.getText());
                                try {
                                    zLoop.setLoopCount(Integer.parseInt(strHour));
                                    LinkageDao.get(EditLoopActivity.this).update(zLoop, null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Snackbar.make(editLoopCount, "格式错误", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                            setActionbarSubtitle();
                        })
                .setNegativeButton(
                        MainActivity.strCancel,
                        null).create().show();

    }

    private void setActionbarSubtitle() {
        if (zLoop.getLoopCount() == -1) {
            actionBar.setSubtitle(zLoop.getName() + " " + "次数:" + "无限");
        } else {
            actionBar.setSubtitle(zLoop.getName() + " " + "次数:" + zLoop.getLoopCount());
        }
    }

    private void setListViewCondition() {
        adapterCondition = new RecyclerAdapterCondition(this, zLoop.getListCondition());
        swipeMenuRecyclerViewCondition.setAdapter(adapterCondition);
    }

    private void setListViewEffect() {
        adapterEffect = new RecyclerAdapterEffect(this, zLoop.getListEffect(), false);
        swipeMenuRecyclerViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
    private SwipeItemClickListener conditionSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            LinkageCondition condition = zLoop.getListCondition().get(position);
            ConditionActivity.ADD = false;
            ConditionActivity.handler = handler;
            ConditionActivity.condition = condition;
            //linkageCondition = condition;
            startActivity(new Intent(EditLoopActivity.this, ConditionActivity.class));
        }
    };

    private SwipeMenuItemClickListener conditionSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。

            LinkageCondition linkageCondition = zLoop.getListCondition().get(adapterPosition);
            zLoop.removeCondition(linkageCondition);
            linkageCondition.setDeleted(true);
            LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(EditLoopActivity.this);
            linkageConditionDao.delete(linkageCondition);
            adapterCondition.notifyDataSetChanged();

        }
    };

    private SwipeMenuItemClickListener effectSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            effect = zLoop.getListEffect().get(adapterPosition);
            zLoop.removeEffect(effect);
            effect.setDeleted(true);
            EffectDao effectDao = EffectDao.get(EditLoopActivity.this);
            //effectDao.update(effect,null);
            effectDao.delete(effect);
            adapterEffect.notifyDataSetChanged();
        }
    };

    private void showDeviceList() {
        List<Device> list = new ArrayList<>();
        for (Device device : HamaApp.DEV_GROUP.findListIStateDev(true)) {
            boolean haved = false;
            for (Effect effect : zLoop.getListEffect()) {
                if (device == effect.getDevice()) {
                    haved = true;
                    break;
                }
            }
            if (!haved) {
                list.add(device);
            }
        }

        String[] names = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            names[i] = list.get(i).getName();
        }
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("选择设备");
        listDialog.setItems(names, (dialog, which) -> {
            Effect effect = new Effect();
            effect.setDevice(list.get(which));
            effect.setDsId(DevStateHelper.DS_GUAN);
            zLoop.getListEffect().add(effect);
            EffectDao effectDao = EffectDao.get(EditLoopActivity.this);
            effectDao.add(effect, zLoop.getId());
            adapterEffect.notifyDataSetChanged();
        });
        listDialog.show();
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
                case EditTimingActivity.REFRESH_DEVICE_LIST:
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
                case ConditionActivity.ADD_CONDITION:
                    LinkageCondition lc = (LinkageCondition) msg.obj;
                    EditLoopActivity.zLoop.addCondition(lc);
                    LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(theActivity);
                    linkageConditionDao.add(lc, EditLoopActivity.zLoop.getId());
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
                case ConditionActivity.UPDATE_CONDITION:
                    LinkageCondition lc1 = (LinkageCondition) msg.obj;
                    LinkageConditionDao linkageConditionDao1 = LinkageConditionDao.get(theActivity);
                    linkageConditionDao1.update(lc1, null);
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
            }

        }
    }
}
