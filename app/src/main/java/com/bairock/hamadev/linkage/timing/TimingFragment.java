package com.bairock.hamadev.linkage.timing;

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
import com.bairock.hamadev.adapter.AdapterTiming;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.timing.Timing;

import java.lang.ref.WeakReference;

public class TimingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    public static Timing TIMING;

    public static MyHandler handler;
    public static final int REFRESH_LIST = 1;

    private CheckBox checkBoxEnable;
    private Button btnAdd;
    private ListView listViewTiming;

    private AdapterTiming adapterTiming;

    public static TimingFragment newInstance(int param1) {
        TimingFragment fragment = new TimingFragment();
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
        listViewTiming = (ListView)view.findViewById(R.id.listViewChain);
        checkBoxEnable.setChecked(HamaApp.DEV_GROUP.getTimingHolder().isEnable());
        setListener();
        setListChain();
        handler= new TimingFragment.MyHandler(this);
        return view;
    }

    private void setListener(){
        checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);
        listViewTiming.setOnItemClickListener(onItemClickListener);
        listViewTiming.setOnItemLongClickListener(onItemLongClickListener);
    }

    private void setListChain(){
        adapterTiming = new AdapterTiming(TimingFragment.this.getContext());
        listViewTiming.setAdapter(adapterTiming);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            HamaApp.DEV_GROUP.getTimingHolder().setEnable(isChecked);
            LinkageHolderDao.get(TimingFragment.this.getContext()).update(HamaApp.DEV_GROUP.getTimingHolder());
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditTimingActivity.ADD = true;
            TimingFragment.this.startActivity(new Intent(TimingFragment.this.getContext(), EditTimingActivity.class));
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TIMING = (Timing) HamaApp.DEV_GROUP.getTimingHolder().getListLinkage().get(position);
            EditTimingActivity.ADD = false;
            TimingFragment.this.startActivity(new Intent(TimingFragment.this.getContext(), EditTimingActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Timing timing = (Timing)HamaApp.DEV_GROUP.getTimingHolder().getListLinkage().get(position);
            showElectricalPopUp(view, timing);
            return true;
        }
    };

    public void showElectricalPopUp(View v, Timing timing) {
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
            HamaApp.DEV_GROUP.getTimingHolder().removeTiming(timing);
            timing.setDeleted(true);
            LinkageDao.get(TimingFragment.this.getContext()).delete(timing);
            adapterTiming.notifyDataSetChanged();
        });
    }

    public static class MyHandler extends Handler {
        WeakReference<TimingFragment> mActivity;

        MyHandler(TimingFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final TimingFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_LIST:
                    theActivity.adapterTiming.notifyDataSetChanged();
                    break;
            }

        }
    }
}
