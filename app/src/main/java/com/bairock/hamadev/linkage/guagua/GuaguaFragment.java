package com.bairock.hamadev.linkage.guagua;

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
import com.bairock.hamadev.adapter.AdapterGuagua;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.SubChain;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuaguaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    public static SubChain SUB_CHAIN;

    public static MyHandler handler;
    public static final int REFRESH_LIST = 1;

    private CheckBox checkBoxEnable;
    private Button btnAdd;
    private ListView listViewChain;

    private AdapterGuagua adapterGuagua;

    public static GuaguaFragment newInstance(int param1) {
        GuaguaFragment fragment = new GuaguaFragment();
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
        checkBoxEnable.setChecked(HamaApp.DEV_GROUP.getGuaguaHolder().isEnable());
        setListener();
        setListChain();
        handler= new MyHandler(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SUB_CHAIN = null;
    }

    private void setListener(){
        checkBoxEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);
        listViewChain.setOnItemClickListener(onItemClickListener);
        listViewChain.setOnItemLongClickListener(onItemLongClickListener);
    }

    private void setListChain(){
        adapterGuagua = new AdapterGuagua(this.getContext());
        listViewChain.setAdapter(adapterGuagua);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            HamaApp.DEV_GROUP.getGuaguaHolder().setEnable(isChecked);
            LinkageHolderDao.get(GuaguaFragment.this.getContext()).update(HamaApp.DEV_GROUP.getGuaguaHolder());
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditGuaguaActivity.ADD = true;
            GuaguaFragment.this.startActivity(new Intent(GuaguaFragment.this.getContext(), EditGuaguaActivity.class));
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SUB_CHAIN = (SubChain) HamaApp.DEV_GROUP.getGuaguaHolder().getListLinkage().get(position);
            EditGuaguaActivity.ADD = false;
            GuaguaFragment.this.startActivity(new Intent(GuaguaFragment.this.getContext(), EditGuaguaActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            SubChain subChain =(SubChain) HamaApp.DEV_GROUP.getGuaguaHolder().getListLinkage().get(position);
            showElectricalPopUp(view, subChain);
            return true;
        }
    };

    public void showElectricalPopUp(View v, SubChain subChain) {
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
            HamaApp.DEV_GROUP.getGuaguaHolder().removeLinkage(subChain);
            subChain.setDeleted(true);
            LinkageDao linkageDevValueDao = LinkageDao.get(GuaguaFragment.this.getActivity());
            //linkageDevValueDao.update(subChain, null);
            linkageDevValueDao.delete(subChain);
            adapterGuagua.notifyDataSetChanged();
        });
    }

    public static class MyHandler extends Handler {
        WeakReference<GuaguaFragment> mActivity;

        MyHandler(GuaguaFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final GuaguaFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_LIST:
                    theActivity.adapterGuagua.notifyDataSetChanged();
                    break;
            }
        }
    }
}
