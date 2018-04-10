package com.bairock.hamadev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.iot.intelDev.linkage.Linkage;

/**
 * 连锁适配器
 * Created by Administrator on 2017/9/6.
 */

public class AdapterChain extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;

    public AdapterChain(Context context){
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return HamaApp.DEV_GROUP.getChainHolder().getListLinkage().size();
    }

    @Override
    public Object getItem(int position) {
        return HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder myViews;
        final Linkage linkage = HamaApp.DEV_GROUP.getChainHolder().getListLinkage().get(position);
        if (convertView == null){
            myViews = new ViewHolder(context);
            convertView = mInflater.inflate(R.layout.adapter_linkage_holder, null);
            myViews.txtLinkageName = (TextView) convertView.findViewById(R.id.txtLinkageName);
            myViews.switchEnable = (Switch) convertView.findViewById(R.id.switchEnable);
            convertView.setTag(myViews);
        }
        else {
            myViews = (ViewHolder) convertView.getTag();
        }

        myViews.linkage = linkage;
        myViews.init();
        return convertView;
    }

    private static class ViewHolder {
        private Linkage linkage;
        private TextView txtLinkageName;
        private Switch switchEnable;
        private Context context;

        ViewHolder(Context context){
            this.context = context;
        }

        void init(){
            txtLinkageName.setText(linkage.getName());
            switchEnable.setChecked(linkage.isEnable());
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linkage.setEnable(isChecked);
                LinkageDao linkageDevValueDao = LinkageDao.get(context);
                linkageDevValueDao.update(linkage, HamaApp.DEV_GROUP.getChainHolder().getId());
            }
        };
    }
}
