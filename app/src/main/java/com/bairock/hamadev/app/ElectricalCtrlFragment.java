package com.bairock.hamadev.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterElectrical3;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.user.DevGroup;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElectricalCtrlFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    public static final int REFRESH_ELE_STATE = 1;
    public static final int REFRESH_ELE = 2;
    public static final int REFRESH_SORT= 3;
    public static final int SHOW_ALERT_DIALOG= 6;
    public static MyHandler handler;

    private RecyclerAdapterElectrical3 adapterElectrical;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewElectrical;

    private List<Device> listIStateDev = new ArrayList<>();

    public ElectricalCtrlFragment() {
        // Required empty public constructor
    }

    public static ElectricalCtrlFragment newInstance(int sectionNumber2) {
        ElectricalCtrlFragment fragment = new ElectricalCtrlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, sectionNumber2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electrical_ctrl, container, false);
        handler = new MyHandler(this);
        swipeMenuRecyclerViewElectrical = view.findViewById(R.id.swipeMenuRecyclerViewElectrical);
//        swipeMenuRecyclerViewElectrical.setLayoutManager(new LinearLayoutManager(this.getContext()));
        swipeMenuRecyclerViewElectrical.setLayoutManager(new GridLayoutManager(this.getContext(), 6));
//        swipeMenuRecyclerViewElectrical.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewElectrical.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        swipeMenuRecyclerViewElectrical.setOnItemMoveListener(onItemMoveListener);// 监听拖拽和侧滑删除，更新UI和数据源。
        swipeMenuRecyclerViewElectrical.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。

        setGridViewElectrical();
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        RecyclerAdapterElectrical3.handler = null;
    }

    public void setGridViewElectrical() {
        if(null != HamaApp.DEV_GROUP) {
            listIStateDev = HamaApp.DEV_GROUP.findListIStateDev(true);
            Collections.sort(listIStateDev);
            for(int i = 0; i < listIStateDev.size(); i++){
                listIStateDev.get(i).setSortIndex(i);
            }
            adapterElectrical = new RecyclerAdapterElectrical3(this.getContext(), listIStateDev);
            swipeMenuRecyclerViewElectrical.setAdapter(adapterElectrical);
        }
    }

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = (viewHolder, actionState) -> {
        if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
            // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
            //viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(ElectricalCtrlFragment.this.getContext()), R.color.drag_background));
            Animation animation= AnimationUtils.loadAnimation(ElectricalCtrlFragment.this.getContext(),R.anim.drag_zoomout);
            viewHolder.itemView.startAnimation(animation);
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
            // 在手松开的时候还原背景。
            //ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(BaseDragActivity.this, R.drawable.select_white));
            Animation animation= AnimationUtils.loadAnimation(ElectricalCtrlFragment.this.getContext(),R.anim.drag_zoomin);
            viewHolder.itemView.startAnimation(animation);

        }
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。

            int fromPosition = srcHolder.getAdapterPosition() - swipeMenuRecyclerViewElectrical.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - swipeMenuRecyclerViewElectrical.getHeaderItemCount();

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    listIStateDev.get(i).setSortIndex(i+1);
                    listIStateDev.get(i + 1).setSortIndex(i);
                    Collections.swap(listIStateDev, i, i + 1);
                }
            }else {
                for (int i = fromPosition; i > toPosition; i--) {
                    listIStateDev.get(i).setSortIndex(i-1);
                    listIStateDev.get(i - 1).setSortIndex(i);
                    Collections.swap(listIStateDev, i, i - 1);
                }
            }

            adapterElectrical.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

        }
    };

    private DevGroup.OnDeviceCollectionChangedListener onDeviceCollectionChangedListener = new DevGroup.OnDeviceCollectionChangedListener() {
        @Override
        public void onAdded(Device device) {
            addDev(device);
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            removeDev(device);
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }
    };

    private void addDev(Device device){
        if(device instanceof IStateDev && device.isVisibility()){
            device.setSortIndex(listIStateDev.size());
            listIStateDev.add(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                addDev(device1);
            }
        }
    }

    private void removeDev(Device device){
        if(device instanceof IStateDev){
            listIStateDev.remove(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                removeDev(device1);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<ElectricalCtrlFragment> mActivity;

        MyHandler(ElectricalCtrlFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            ElectricalCtrlFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_ELE_STATE:
                    theActivity.adapterElectrical.notifyDataSetChanged();
                    break;
                case REFRESH_ELE :
                    theActivity.setGridViewElectrical();
                    break;
                case REFRESH_SORT :
                    break;
                case SHOW_ALERT_DIALOG:
                    break;
            }
        }
    }
}
