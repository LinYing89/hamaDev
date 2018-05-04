package com.bairock.hamadev.linkage.guagua;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import android.widget.EditText;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterCondition;
import com.bairock.hamadev.adapter.RecylerAdapterEffectGuagua;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.hamadev.database.LinkageConditionDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.linkage.ConditionActivity;
import com.bairock.iot.intelDev.linkage.Effect;
import com.bairock.iot.intelDev.linkage.Linkage;
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

public class EditGuaguaActivity extends AppCompatActivity {

    public static MyHandler handler;
    public static final int REFRESH_DEVICE_LIST = 1;
    public static final int REFRESH_EVENT_HANDLER_LIST = 2;

    public static SubChain subChain;
    public static Effect effect;
    public static boolean ADD;

    private ActionBar actionBar;
    private Button btnAddCondition;
    private Button btnAddEffect;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewCondition;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewEffect;

    private RecyclerAdapterCondition adapterCondition;
    private RecylerAdapterEffectGuagua adapterEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guagua);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(ADD){
            actionBar.setTitle("添加呱呱");
            subChain = new SubChain();
            subChain.setName(getDefaultName());
            HamaApp.DEV_GROUP.getGuaguaHolder().addLinkage(subChain);
            LinkageDao.get(this).add(subChain, HamaApp.DEV_GROUP.getGuaguaHolder().getId());
        }else{
            actionBar.setTitle("编辑呱呱");
            subChain = GuaguaFragment.SUB_CHAIN;
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
        if(null != GuaguaFragment.handler){
            GuaguaFragment.handler.obtainMessage(GuaguaFragment.REFRESH_LIST).sendToTarget();
        }
        subChain = null;
    }

    private void findViews(){
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

    private void setListener(){
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
        SwipeMenuItem deleteItem = new SwipeMenuItem(EditGuaguaActivity.this)
                .setBackgroundColor(Color.RED)
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    private String getDefaultName(){
        String name = "呱呱";
        boolean have;
        for(int i=1; i< 1000; i++){
            have = false;
            name = "呱呱" + i;
            for(Linkage chain : HamaApp.DEV_GROUP.getGuaguaHolder().getListLinkage()){
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
                            LinkageDao linkageDevValueDao = LinkageDao.get(EditGuaguaActivity.this);
                            linkageDevValueDao.update(subChain, null);
                        })
                .setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void setListViewCondition(){
        adapterCondition = new RecyclerAdapterCondition(this, subChain.getListCondition());
        swipeMenuRecyclerViewCondition.setAdapter(adapterCondition);
    }

    private void setListViewEffect(){
        adapterEffect = new RecylerAdapterEffectGuagua(this, subChain.getListEffect());
        swipeMenuRecyclerViewEffect.setAdapter(adapterEffect);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddCondition:
                    ConditionActivity.ADD = true;
                    ConditionActivity.handler = handler;
                    startActivity(new Intent(EditGuaguaActivity.this, ConditionActivity.class));
                    break;
                case R.id.btnAddEffect:
                    EditGuaguaEffectActivity.ADD = true;
                    startActivity(new Intent(EditGuaguaActivity.this, EditGuaguaEffectActivity.class));
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
            startActivity(new Intent(EditGuaguaActivity.this, ConditionActivity.class));
        }
    };

    private SwipeMenuItemClickListener conditionSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            LinkageCondition lc = subChain.getListCondition().get(adapterPosition);

            LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(EditGuaguaActivity.this);
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

            EffectDao effectDao = EffectDao.get(EditGuaguaActivity.this);
            effectDao.delete(effect);
            subChain.removeEffect(effect);
            adapterEffect.notifyDataSetChanged();
        }
    };

    public static class MyHandler extends Handler {
        WeakReference<EditGuaguaActivity> mActivity;

        MyHandler(EditGuaguaActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final EditGuaguaActivity theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_EVENT_HANDLER_LIST:
                    theActivity.adapterCondition.notifyDataSetChanged();
                    break;
                case REFRESH_DEVICE_LIST :
                    theActivity.adapterEffect.notifyDataSetChanged();
                    break;
                case ConditionActivity.ADD_CONDITION :
                    LinkageCondition lc = (LinkageCondition)msg.obj;
                    EditGuaguaActivity.subChain.addCondition(lc);
                    LinkageConditionDao linkageConditionDao = LinkageConditionDao.get(theActivity);
                    linkageConditionDao.add(lc, EditGuaguaActivity.subChain.getId());
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
