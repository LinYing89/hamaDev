package com.bairock.hamadev.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.iot.intelDev.linkage.Linkage;

public class RecyclerAdapterLoop extends RecyclerView.Adapter<RecyclerAdapterLoop.ViewHolder>{

    private LayoutInflater mInflater;

    public RecyclerAdapterLoop(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return HamaApp.DEV_GROUP.getLoopHolder().getListLinkage() == null ? 0 : HamaApp.DEV_GROUP.getLoopHolder().getListLinkage().size();
    }

    @NonNull
    @Override
    public RecyclerAdapterLoop.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapterLoop.ViewHolder(mInflater.inflate(R.layout.adapter_linkage_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterLoop.ViewHolder holder, int position) {
        holder.setData(HamaApp.DEV_GROUP.getLoopHolder().getListLinkage().get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Linkage linkage;
        private TextView txtLinkageName;
        private Switch switchEnable;

        public ViewHolder(View itemView) {
            super(itemView);
            txtLinkageName = itemView.findViewById(R.id.txtLinkageName);
            switchEnable = itemView.findViewById(R.id.switchEnable);
        }

        public void setData(Linkage linkage) {
            this.linkage = linkage;
            init();
        }

        private void init(){
            txtLinkageName.setText(linkage.getName());
            switchEnable.setChecked(linkage.isEnable());
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                linkage.setEnable(isChecked);
                LinkageDao linkageDevValueDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
                linkageDevValueDao.update(linkage, null);
            }
        };
    }
}
