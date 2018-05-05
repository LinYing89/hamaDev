package com.bairock.hamadev.linkage.loop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterLoopDuration;
import com.bairock.hamadev.database.LoopDurationDao;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;

public class DurationListActivity extends AppCompatActivity {

    public static final int REFRESH_DURATION_LIST = 1;
    public static MyHandler handler;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewDuration;
    private RecyclerAdapterLoopDuration adapterDurationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
        setListener();
        setListViewDuration();

        handler = new MyHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loop_duration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
            case R.id.act_add:
                startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews(){
        swipeMenuRecyclerViewDuration = findViewById(R.id.swipeMenuRecyclerViewDuration);
        swipeMenuRecyclerViewDuration.setLayoutManager(new LinearLayoutManager(this));
        swipeMenuRecyclerViewDuration.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewDuration.setSwipeMenuCreator(swipeMenuConditionCreator);
    }

    private void setListener(){
        swipeMenuRecyclerViewDuration.setSwipeItemClickListener(conditionSwipeItemClickListener);
        swipeMenuRecyclerViewDuration.setSwipeMenuItemClickListener(conditionSwipeMenuItemClickListener);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem deleteItem = new SwipeMenuItem(DurationListActivity.this)
                .setBackgroundColor(Color.RED)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    private void setListViewDuration(){
        adapterDurationList = new RecyclerAdapterLoopDuration(this, EditLoopActivity.zLoop.getListLoopDuration());
        swipeMenuRecyclerViewDuration.setAdapter(adapterDurationList);
    }

    //条件列表点击事件
    private SwipeItemClickListener conditionSwipeItemClickListener = (itemView, position) -> {
        DurationActivity.duration = EditLoopActivity.zLoop.getListLoopDuration().get(position);
        startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
    };

    private SwipeMenuItemClickListener conditionSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            LoopDuration loopDuration = EditLoopActivity.zLoop.getListLoopDuration().get(menuBridge.getAdapterPosition());
            loopDuration.setDeleted(true);
            EditLoopActivity.zLoop.removeLoopDuration(loopDuration);
            LoopDurationDao loopDurationDao = LoopDurationDao.get(DurationListActivity.this);
            loopDurationDao.delete(loopDuration);
            adapterDurationList.notifyDataSetChanged();
        }
    };

    public static class MyHandler extends Handler {
        WeakReference<DurationListActivity> mActivity;

        MyHandler(DurationListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            final DurationListActivity theActivity = mActivity.get();
            switch (msg.arg1) {
                case REFRESH_DURATION_LIST:
                    theActivity.adapterDurationList.notifyDataSetChanged();
                    break;
            }

        }
    }
}
