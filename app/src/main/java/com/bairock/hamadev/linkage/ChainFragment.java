package com.bairock.hamadev.linkage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterChain;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;

public class ChainFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    public static Linkage LINKAGE;

    public static MyHandler handler;
    public static final int REFRESH_LIST = 1;

    private CheckBox checkBoxEnable;
    private Button btnAdd;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewChain;

    private RecyclerAdapterChain adapterChain;

    public static ChainFragment newInstance(int param1) {
        ChainFragment fragment = new ChainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chain, container, false);
        checkBoxEnable = view.findViewById(R.id.cbEnable);
        btnAdd = view.findViewById(R.id.btnAdd);
        swipeMenuRecyclerViewChain = view.findViewById(R.id.swipeMenuRecyclerViewChain);
        swipeMenuRecyclerViewChain.setLayoutManager(new LinearLayoutManager(this.getContext()));
        swipeMenuRecyclerViewChain.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewChain.setSwipeMenuCreator(swipeMenuConditionCreator);

        checkBoxEnable.setChecked(HamaApp.DEV_GROUP.getChainHolder().isEnable());
        setListener();
        setListChain();
        handler= new MyHandler(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LINKAGE = null;
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        // 添加右侧的，如果不添加，则右侧不会出现菜单。
        SwipeMenuItem deleteItem = new SwipeMenuItem(ChainFragment.this.getContext())
                .setBackgroundColor(getResources().getColor(R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
    };

    private void setListener(){
        checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);

        swipeMenuRecyclerViewChain.setSwipeItemClickListener(linkageSwipeItemClickListener);
        swipeMenuRecyclerViewChain.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener);
    }

    private void setListChain(){
        adapterChain = new RecyclerAdapterChain(ChainFragment.this.getContext());
        swipeMenuRecyclerViewChain.setAdapter(adapterChain);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            HamaApp.DEV_GROUP.getChainHolder().setEnable(isChecked);
            LinkageHolderDao.get(ChainFragment.this.getContext()).update(HamaApp.DEV_GROUP.getChainHolder());
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditChainActivity.ADD = true;
            ChainFragment.this.startActivity(new Intent(ChainFragment.this.getContext(), EditChainActivity.class));
        }
    };

    //条件列表点击事件
    private SwipeItemClickListener linkageSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            LINKAGE = HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
            EditChainActivity.ADD = false;
            ChainFragment.this.startActivity(new Intent(ChainFragment.this.getContext(), EditChainActivity.class));
        }
    };

    private SwipeMenuItemClickListener linkageSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            Linkage linkage = HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(adapterPosition);

            HamaApp.DEV_GROUP.getChainHolder().removeLinkage(linkage);
            linkage.setDeleted(true);
            LinkageDao linkageDevValueDao = LinkageDao.get(ChainFragment.this.getActivity());
            linkageDevValueDao.delete(linkage);
            adapterChain.notifyDataSetChanged();
        }
    };

    public static class MyHandler extends Handler {
        WeakReference<ChainFragment> mActivity;

        MyHandler(ChainFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ChainFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_LIST:
                    theActivity.adapterChain.notifyDataSetChanged();
                    break;
            }

        }
    }
}
