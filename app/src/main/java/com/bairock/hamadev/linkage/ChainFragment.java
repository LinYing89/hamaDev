package com.bairock.hamadev.linkage;

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
import com.bairock.hamadev.adapter.AdapterChain;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.Linkage;

import java.lang.ref.WeakReference;

public class ChainFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    public static Linkage LINKAGE;

    public static MyHandler handler;
    public static final int REFRESH_LIST = 1;

    private CheckBox checkBoxEnable;
    private Button btnAdd;
    private ListView listViewChain;

    private AdapterChain adapterChain;

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
        checkBoxEnable = (CheckBox)view.findViewById(R.id.cbEnable);
        btnAdd = (Button)view.findViewById(R.id.btnAdd);
        listViewChain = (ListView)view.findViewById(R.id.listViewChain);
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

    private void setListener(){
        checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);
        listViewChain.setOnItemClickListener(onItemClickListener);
        listViewChain.setOnItemLongClickListener(onItemLongClickListener);
    }

    private void setListChain(){
        adapterChain = new AdapterChain(ChainFragment.this.getContext());
        listViewChain.setAdapter(adapterChain);
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LINKAGE = HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
            EditChainActivity.ADD = false;
            ChainFragment.this.startActivity(new Intent(ChainFragment.this.getContext(), EditChainActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Linkage linkage = HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
            showElectricalPopUp(view, linkage);
            return true;
        }
    };

    public void showElectricalPopUp(View v, Linkage linkage) {
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
            HamaApp.DEV_GROUP.getChainHolder().removeLinkage(linkage);
            linkage.setDeleted(true);
            LinkageDao linkageDevValueDao = LinkageDao.get(ChainFragment.this.getActivity());
            //linkageDevValueDao.update(subChain, null);
            linkageDevValueDao.delete(linkage);
            adapterChain.notifyDataSetChanged();
        });
    }

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
