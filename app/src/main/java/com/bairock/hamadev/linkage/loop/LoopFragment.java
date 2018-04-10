package com.bairock.hamadev.linkage.loop;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterLoop;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.loop.ZLoop;

import java.lang.ref.WeakReference;

public class LoopFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    public static ZLoop ZLOOP;

    public static MyHandler handler;
    public static final int REFRESH_LIST = 1;

    private CheckBox checkBoxEnable;
    private Button btnAdd;
    private ListView listViewLoop;

    private AdapterLoop adapterLoop;

    public static LoopFragment newInstance(int param1) {
        LoopFragment fragment = new LoopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chain, container, false);
        checkBoxEnable = (CheckBox)view.findViewById(R.id.cbEnable);
        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        listViewLoop = (ListView)view.findViewById(R.id.listViewChain);
        checkBoxEnable.setChecked(HamaApp.DEV_GROUP.getLoopHolder().isEnable());
        setListener();
        setListChain();
        handler= new LoopFragment.MyHandler(this);
        return view;
    }

    private void setListener(){
        checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);
        listViewLoop.setOnItemClickListener(onItemClickListener);
        listViewLoop.setOnItemLongClickListener(onItemLongClickListener);
    }

    private void setListChain(){
        adapterLoop = new AdapterLoop(LoopFragment.this.getContext());
        listViewLoop.setAdapter(adapterLoop);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            HamaApp.DEV_GROUP.getLoopHolder().setEnable(isChecked);
            LinkageHolderDao.get(LoopFragment.this.getContext()).update(HamaApp.DEV_GROUP.getLoopHolder());
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditLoopActivity.ADD = true;
            LoopFragment.this.startActivity(new Intent(LoopFragment.this.getContext(), EditLoopActivity.class));
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ZLOOP = (ZLoop) HamaApp.DEV_GROUP.getLoopHolder().getListLinkage().get(position);
            EditLoopActivity.ADD = false;
            LoopFragment.this.startActivity(new Intent(LoopFragment.this.getContext(), EditLoopActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ZLoop linkageDevValue = (ZLoop)HamaApp.DEV_GROUP.getLoopHolder().getListLinkage().get(position);
            showElectricalPopUp(view, linkageDevValue);
            return true;
        }
    };

    public void showElectricalPopUp(View v, ZLoop zLoop) {
        Button layoutDelete = new Button(this.getContext());
        layoutDelete.setText("删除");
        final PopupWindow popupWindow = new PopupWindow(layoutDelete, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popupWindow.showAsDropDown(v);
        layoutDelete.setOnClickListener(v1 -> {
            popupWindow.dismiss();
            HamaApp.DEV_GROUP.getLoopHolder().removeLinkage(zLoop);
            zLoop.setDeleted(true);
            LinkageDao.get(LoopFragment.this.getContext()).delete(zLoop);
            adapterLoop.notifyDataSetChanged();
        });
    }

    public static class MyHandler extends Handler {
        WeakReference<LoopFragment> mActivity;

        MyHandler(LoopFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            final LoopFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_LIST:
                    theActivity.adapterLoop.notifyDataSetChanged();
                    break;
            }

        }
    }

}
