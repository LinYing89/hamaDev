package com.bairock.hamadev.linkage;

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
import com.bairock.hamadev.adapter.RecyclerAdapterCondition;
import com.bairock.hamadev.adapter.RecyclerAdapterEffect;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageConditionDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.SubChain;
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

public class EditChainActivity extends AppCompatActivity {

    public static MyHandler handler;
    public static final int REFRESH_DEVICE_LIST = 1;
    public static final int REFRESH_EVENT_HANDLER_LIST = 2;

    private SubChain subChain;
    public static Effect effect;

    private Button btnAddCondition;
    private Button btnAddEffect;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewCondition;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewEffect;

    private RecyclerAdapterCondition adapterCondition;
    private RecyclerAdapterEffect adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chain);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("编辑连锁");
        }

        findViews();
        setListener();
        subChain = (SubChain) LinkageBaseFragment.Companion.getLINKAGE();
        if (subChain == null) {
            finish();
            return;
        }
        if (actionBar != null) {
            actionBar.setSubtitle(subChain.getName());
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
        subChain = null;
    }

    private void findViews() {
        btnAddCondition = findViewById(R.id.btnAddCondition);
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
        btnAddCondition.setOnClickListener(onClickListener);
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
        SwipeMenuItem deleteItem = new SwipeMenuItem(EditChainActivity.this)
                .setBackgroundColor(Color.RED)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    private void setListViewCondition() {
        adapterCondition = new RecyclerAdapterCondition(this, subChain.getListCondition());
        //adapterCondition.notifyDataSetChanged();
        swipeMenuRecyclerViewCondition.setAdapter(adapterCondition);
    }

    private void setListViewEffect() {
        adapterEffect = new RecyclerAdapterEffect(this, subChain.getListEffect(), true);
        swipeMenuRecyclerViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
    private SwipeItemClickListener conditionSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            LinkageCondition condition = subChain.getListCondition().get(position);
            ConditionActivity.ADD = false;
            ConditionActivity.handler = handler;
            ConditionActivity.condition = condition;
            startActivity(new Intent(EditChainActivity.this, ConditionActivity.class));
        }
    };

    private SwipeMenuItemClickListener conditionSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            LinkageCondition lc = subChain.getListCondition().get(adapterPosition);

            LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(EditChainActivity.this);
            linkageConditionDao.delete(lc);
            subChain.removeCondition(lc);
            adapterCondition.notifyDataSetChanged();
        }
    };

    private SwipeMenuItemClickListener effectSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            effect = subChain.getListEffect().get(adapterPosition);

            EffectDao effectDao = EffectDao.get(EditChainActivity.this);
            effectDao.delete(effect);
            subChain.removeEffect(effect);
            adapterEffect.notifyDataSetChanged();
        }
    };

    private void showDeviceList() {
        List<Device> list = new ArrayList<>();
        for (Device device : HamaApp.DEV_GROUP.findListIStateDev(true)) {
            boolean haved = false;
            for (Effect effect : subChain.getListEffect()) {
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
            subChain.getListEffect().add(effect);
            EffectDao effectDao = EffectDao.get(EditChainActivity.this);
            effectDao.add(effect, subChain.getId());
            adapterEffect.notifyDataSetChanged();
        });
        listDialog.show();
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
                case REFRESH_DEVICE_LIST:
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
                case ConditionActivity.ADD_CONDITION:
                    LinkageCondition lc = (LinkageCondition) msg.obj;
                    theActivity.subChain.addCondition(lc);
                    LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(theActivity);
                    linkageConditionDao.add(lc, theActivity.subChain.getId());
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
