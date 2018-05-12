package com.bairock.hamadev.linkage.timing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterTimer;
import com.bairock.hamadev.adapter.RecyclerAdapterEffect;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.ZTimerDao;
import com.bairock.hamadev.linkage.LinkageBaseFragment;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.timing.Timing;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;
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

public class EditTimingActivity extends AppCompatActivity {

    public static MyHandler handler;
    public static final int REFRESH_DEVICE_LIST = 1;
    public static final int REFRESH_EVENT_HANDLER_LIST = 2;

    public static Timing timing;
    public static ZTimer zTimer;
    public static Effect effect;

    private Button btnAddTimer;
    private Button btnAddEffect;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewCondition;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewEffect;

    private RecyclerAdapterTimer adapterTimer;
    private RecyclerAdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timing);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("编辑定时");
        }

        findViews();
        setListener();

        timing = (Timing) LinkageBaseFragment.Companion.getLINKAGE();
        if (timing == null) {
            finish();
            return;
        }
        if (actionBar != null) {
            actionBar.setSubtitle(timing.getName());
        }
        setListViewCondition();
        setListViewEffect();

        handler = new MyHandler(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timing = null;
    }

    private void findViews() {
        btnAddTimer = findViewById(R.id.btnAddTimer);
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
        btnAddTimer.setOnClickListener(onClickListener);
        btnAddEffect.setOnClickListener(onClickListener);

        swipeMenuRecyclerViewCondition.setSwipeItemClickListener(conditionSwipeItemClickListener);
        swipeMenuRecyclerViewCondition.setSwipeMenuItemClickListener(conditionSwipeMenuItemClickListener);
        swipeMenuRecyclerViewEffect.setSwipeMenuItemClickListener(effectSwipeMenuItemClickListener);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem deleteItem = new SwipeMenuItem(EditTimingActivity.this)
                .setBackgroundColor(Color.RED)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    private void setListViewCondition() {
        adapterTimer = new RecyclerAdapterTimer(this, timing.getListZTimer());
        swipeMenuRecyclerViewCondition.setAdapter(adapterTimer);
    }

    private void setListViewEffect() {
        adapterEffect = new RecyclerAdapterEffect(this, timing.getListEffect(), false);
        swipeMenuRecyclerViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.btnAddTimer:
                startActivity(new Intent(EditTimingActivity.this, TimerActivity.class));
                break;
            case R.id.btnAddEffect:
                showDeviceList();
                break;
        }
    };

    //条件列表点击事件
    private SwipeItemClickListener conditionSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {

            ZTimer condition = timing.getListZTimer().get(position);
            TimerActivity.timer = condition;
            zTimer = condition;
            startActivity(new Intent(EditTimingActivity.this, TimerActivity.class));
        }
    };

    private SwipeMenuItemClickListener conditionSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            zTimer = timing.getListZTimer().get(adapterPosition);
            timing.removeZTimer(zTimer);
            zTimer.setDeleted(true);
            ZTimerDao zTimerDao = ZTimerDao.get(EditTimingActivity.this);
            zTimerDao.delete(zTimer);
            adapterTimer.notifyDataSetChanged();
        }
    };

    private SwipeMenuItemClickListener effectSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            effect = timing.getListEffect().get(adapterPosition);
            timing.removeEffect(effect);
            effect.setDeleted(true);
            EffectDao effectDao = EffectDao.get(EditTimingActivity.this);
            //effectDao.update(effect,null);
            effectDao.delete(effect);
            adapterEffect.notifyDataSetChanged();
        }
    };

    private void showDeviceList() {
        List<Device> list = new ArrayList<>();
        for (Device device : HamaApp.DEV_GROUP.findListIStateDev(true)) {
            boolean haved = false;
            for (Effect effect : timing.getListEffect()) {
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
            timing.getListEffect().add(effect);
            EffectDao effectDao = EffectDao.get(EditTimingActivity.this);
            effectDao.add(effect, timing.getId());
            adapterEffect.notifyDataSetChanged();
        });
        listDialog.show();
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
                case EditTimingActivity.REFRESH_DEVICE_LIST:
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
            }

        }
    }
}
