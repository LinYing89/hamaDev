package com.bairock.hamadev.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.RecyclerAdapterDevice;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.app.RouterInfo;
import com.bairock.hamadev.communication.PadClient;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.hamadev.esptouch.EspTouchAddDevice;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.SearchDeviceHelper;
import com.bairock.iot.intelDev.device.Coordinator;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.OrderHelper;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignalContainer;
import com.bairock.iot.intelDev.device.devswitch.DevSwitch;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.ErrorCodes;
import com.bairock.iot.intelDev.user.IntelDevHelper;
import com.bairock.iot.intelDev.user.User;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {

    /** update UI handler */
    public static MyHandler handler;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewDevice;
    private RecyclerAdapterDevice adapterEleHolder;
    private ProgressDialog progressDialog;

    public static DeviceModelHelper deviceModelHelper;
    public SetDevModelTask tSendModel;

    private Device rootDevice;
    private List<Device> listShowDevices;
    private boolean childDevAdding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
        setListener();
        handler = new MyHandler(this);
        //setDeviceList();
        List<Device> list = getRootDevices(rootDevice);
        setDeviceList(list);
        SearchDeviceHelper.getIns().setOnSearchListener(onSearchListener);
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(null == rootDevice){
                    finish();
                }else {
                    List<Device> list = getRootDevices(rootDevice);
                    setDeviceList(list);
                }
                break;
            case R.id.action_add_device :
                if(rootDevice != null && rootDevice instanceof Coordinator){
                    AddDeviceTask addDeviceTask = new AddDeviceTask(this);
                    addDeviceTask.execute();
                }else {
                    EspTouchAddDevice espTouchAddDevice = new EspTouchAddDevice(this);
                    String ssid = espTouchAddDevice.getSsid();
                    if (!ssid.equals(RouterInfo.NAME) && !espTouchAddDevice.moniAdd) {
                        showMessageDialog();
                    } else {
                        espTouchAddDevice.startConfig();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        RecyclerAdapterDevice.handler = null;
        adapterEleHolder = null;
        tSendModel = null;
    }

    private void findViews() {
        swipeMenuRecyclerViewDevice = findViewById(R.id.swipeMenuRecyclerViewDevice);
        swipeMenuRecyclerViewDevice.setLayoutManager(new LinearLayoutManager(this));
        swipeMenuRecyclerViewDevice.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewDevice.setSwipeMenuCreator(swipeMenuConditionCreator);

    }

    private void setListener() {
        swipeMenuRecyclerViewDevice.setSwipeMenuItemClickListener(deviceSwipeMenuItemClickListener);
        swipeMenuRecyclerViewDevice.setSwipeItemClickListener(deviceSwipeItemClickListener);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        if(viewType >> 2 == 1){
            //显示位号菜单
            SwipeMenuItem aliasItem = new SwipeMenuItem(this)
                    .setBackgroundColor(getResources().getColor(R.color.green_normal))
                    .setText("位号")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(aliasItem);
        }

        if((viewType & 2) == 2){
            //显示模式菜单
            String model;
            if((viewType & 1) == 1){
                //远程模式
                model = "设为远程";
            }else{
                //本地模式
                model = "设为本地";
            }
            SwipeMenuItem modelItem = new SwipeMenuItem(this)
                    .setBackgroundColor(getResources().getColor(R.color.menu_blue))
                    .setText(model)
                    .setTextColor(Color.WHITE)
                    .setWidth(width + 20)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(modelItem);
        }

        SwipeMenuItem renameItem = new SwipeMenuItem(this)
                .setBackgroundColor(getResources().getColor(R.color.orange))
                .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width + 10)
                .setHeight(height);
        swipeRightMenu.addMenuItem(renameItem);

        SwipeMenuItem deleteItem = new SwipeMenuItem(this)
                .setBackgroundColor(getResources().getColor(R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);
    };

    public void setDeviceList(List<Device> list) {
        listShowDevices = list;
        adapterEleHolder = new RecyclerAdapterDevice(this, list);
        swipeMenuRecyclerViewDevice.setAdapter(adapterEleHolder);
    }

    private void showMessageDialog(){
        new AlertDialog.Builder(this)
                .setMessage("当前路由器名称与已保存路由器名称不匹配，请检查网络配置")
                .setPositiveButton("确定",null).show();
    }

    private List<Device> getRootDevices(Device rootDevice){
        if(null == rootDevice){
            return HamaApp.DEV_GROUP.getListDevice();
        }
        Device parent = rootDevice.getParent();
        this.rootDevice = parent;
        if(null == parent){
            return HamaApp.DEV_GROUP.getListDevice();
        }
        return ((DevHaveChild)parent).getListDev();
    }

    private void reloadNowDevices(){
        if(null == rootDevice){
            setDeviceList(HamaApp.DEV_GROUP.getListDevice());
        }else{
            setDeviceList(((DevHaveChild)rootDevice).getListDev());
        }
    }

    private DevGroup.OnDeviceCollectionChangedListener onDeviceCollectionChangedListener = new DevGroup.OnDeviceCollectionChangedListener() {
        @Override
        public void onAdded(Device device) {
            handler.obtainMessage(handler.RELOAD_LIST).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            handler.obtainMessage(handler.RELOAD_LIST).sendToTarget();
        }
    };

    private SearchDeviceHelper.OnSearchListener onSearchListener = new SearchDeviceHelper.OnSearchListener() {
        @Override
        public void searchStart() {

        }

        @Override
        public void searchedMsg(String s) {

        }

        @Override
        public void searchFail() {
            handler.obtainMessage(handler.NO_MESSAGE).sendToTarget();
        }

        @Override
        public void searchedAllDevices(List<Device> list) {

        }

        @Override
        public void searchedNewDevices(List<Device> list) {
            DeviceDao deviceDao = DeviceDao.get(SearchActivity.this);
            for(Device device : list){
                if(device.getParent() == null) {
                    HamaApp.DEV_GROUP.addDevice(device);
                }else {
                    HamaApp.DEV_GROUP.sendOnDeviceCollectionChangedListenerAdd(device);
                }
                deviceDao.add(device);
            }
            handler.obtainMessage(handler.SEARCH_OK).sendToTarget();
        }
    };

    private void showRenameDialog(final Device device) {
        final EditText edit_newName = new EditText(SearchActivity.this);
        edit_newName.setText(device.getName());
        new AlertDialog.Builder(SearchActivity.this)
                .setTitle(
                        SearchActivity.this.getString(R.string.rename))
                .setView(edit_newName)
                .setPositiveButton(MainActivity.strEnsure,
                        (dialog, which) -> {
                            String value = edit_newName.getText().toString();
                            if(HamaApp.DEV_GROUP.renameDevice(device, value) == ErrorCodes.DEV_NAME_IS_EXISTS){
                                Toast.makeText(SearchActivity.this, "与组内其他设备名重复", Toast.LENGTH_SHORT).show();
                            }else{
                                DeviceDao deviceDao = DeviceDao.get(SearchActivity.this);
                                deviceDao.update(device);
                                //adapterEleHolder.notifyDataSetChanged();
                            }
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void showAliasDialog(final Device device) {
        final EditText edit_newName = new EditText(SearchActivity.this);
        edit_newName.setText(device.getAlias());
        new AlertDialog.Builder(SearchActivity.this)
                .setTitle(
                        SearchActivity.this.getString(R.string.alias))
                .setView(edit_newName)
                .setPositiveButton(MainActivity.strEnsure,
                        (dialog, which) -> {
                            String value = edit_newName.getText().toString();
                            device.setAlias(value);
                            DeviceDao deviceDao = DeviceDao.get(SearchActivity.this);
                            deviceDao.update(device);
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private SwipeItemClickListener deviceSwipeItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            IntelDevHelper.OPERATE_DEVICE = listShowDevices.get(position);
            if(IntelDevHelper.OPERATE_DEVICE instanceof DevSwitch || IntelDevHelper.OPERATE_DEVICE instanceof DevCollectSignalContainer) {
                ChildElectricalActivity.controller = (DevHaveChild) IntelDevHelper.OPERATE_DEVICE;
                SearchActivity.this.startActivity(new Intent(SearchActivity.this, ChildElectricalActivity.class));
            }else if (IntelDevHelper.OPERATE_DEVICE instanceof DevHaveChild){
                rootDevice = IntelDevHelper.OPERATE_DEVICE;
                setDeviceList(((DevHaveChild)rootDevice).getListDev());
            }else if(IntelDevHelper.OPERATE_DEVICE instanceof DevCollect){
                DevCollectSettingActivity.devCollectSignal = (DevCollectSignal) IntelDevHelper.OPERATE_DEVICE;
                SearchActivity.this.startActivity(new Intent(SearchActivity.this, DevCollectSettingActivity.class));
            }
        }
    };

    private SwipeMenuItemClickListener deviceSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition();
            Device device = listShowDevices.get(adapterPosition);
            IntelDevHelper.OPERATE_DEVICE = device;
            switch (menuBridge.getPosition()){
                case 0:
                    if(device instanceof DevHaveChild){
                        //不是位号菜单，是父设备的不设置位号
                        if(device.getParent() == null){
                            //是模式菜单，没有父设备则为根节点设备，可以设置模式
                            showSetModel(device);
                        }else{
                            //是重命名菜单，有父设备，自己又是父设备的，没有位号和模式菜单
                            showRenameDialog(device);
                        }
                    }else{
                        //是位号菜单
                        //不是父设备，则为根节点设备
                        showAliasDialog(device);
                    }
                    break;
                case 1:
                    if(device instanceof DevHaveChild){
                        //没有位号菜单，是父设备的不设置位号
                        if(device.getParent() == null){
                            //是重命名菜单，没有父设备则为根节点设备，0为设置模式，1为重命名
                            showRenameDialog(device);
                        }else{
                            //是删除菜单，有父设备，自己又是父设备的，没有位号和模式菜单，0为重命名，1为删除
                            deleteDevice(device);
                        }
                    }else{
                        //是模式菜单
                        //不是父设备，则为根节点设备
                        if(device.getParent() == null) {
                            //o为位号，1为模式
                            showSetModel(device);
                        }else{
                            //o为位号，1为重命名
                            showRenameDialog(device);
                        }
                    }
                    break;
                case 2:
                    if(device instanceof DevHaveChild){
                        //不是位号菜单，是父设备的不设置位号
                        if(device.getParent() == null){
                            //是重命名菜单，没有父设备则为根节点设备，0为设置模式，1为重命名,2为删除
                            deleteDevice(device);
                        }
                        //有父设备，自己又是父设备的，没有位号和模式菜单，0为重命名，1为删除
                    }else{
                        //是模式菜单
                        //不是父设备，则为根节点设备，o为位号，1为模式，2为重命名
                        if(device.getParent() == null) {
                            //o为位号，1为模式，2为重命名
                            showRenameDialog(device);
                        }else{
                            //o为位号，1为重命名,2为删除
                            deleteDevice(device);
                        }
                    }
                    break;
                case 3:
                    deleteDevice(device);
                    break;
            }
        }
    };

    private void closeProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<SearchActivity> mActivity;

        final int NO_MESSAGE = 0;
        final int SEARCH_OK = 1;
        public final int UPDATE_LIST = 2;
        public final int CTRL_MODEL_PROGRESS = 3;
        final int RELOAD_LIST = 4;
        public final int DEV_ADD_CHILD = 5;

        MyHandler(SearchActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchActivity theActivity = mActivity.get();
            switch (msg.what){
                case NO_MESSAGE:
                    theActivity.closeProgressDialog();
                    Toast.makeText(theActivity, theActivity.getString(R.string.no_feedback),
                            Toast.LENGTH_LONG).show();
                    break;
                case SEARCH_OK:
                    theActivity.closeProgressDialog();
                    Toast.makeText(theActivity, theActivity.getString(R.string.update_success),
                            Toast.LENGTH_LONG).show();
                    theActivity.adapterEleHolder.notifyDataSetChanged();
                    break;
                case UPDATE_LIST:
                    theActivity.adapterEleHolder.notifyDataSetChanged();
                    break;
                case RELOAD_LIST:
                    theActivity.reloadNowDevices();
                    break;
                case CTRL_MODEL_PROGRESS:
                    Log.e("SearchAct", (int)msg.obj + "");
                    if(null != theActivity.tSendModel) {
                        theActivity.tSendModel.setProgress((int) msg.obj);
                    }
                    break;
                case DEV_ADD_CHILD:
                    theActivity.childDevAdding = false;
                    break;
            }
        }
    }

    private void deleteDevice(Device device){
        device.setDeleted(true);
        DeviceDao deviceDao = DeviceDao.get(SearchActivity.this);
        //deviceDao.update(device);
        deviceDao.delete(device);
        HamaApp.DEV_GROUP.removeDevice(device);
        HamaApp.removeOfflineDevCoding(device);
        adapterEleHolder.notifyDataSetChanged();
    }

    private void showSetModel(Device device){
        deviceModelHelper = new DeviceModelHelper();
        deviceModelHelper.setDevToSet(device);
        if(device.getCtrlModel() == CtrlModel.REMOTE){
            deviceModelHelper.setCtrlModel(CtrlModel.LOCAL);
        }else{
            deviceModelHelper.setCtrlModel(CtrlModel.REMOTE);
        }
        showSetModelWaitDialog(deviceModelHelper.getCtrlModel(), device);
    }

    //显示等待进度对话框
    private void showSetModelWaitDialog(CtrlModel model, Device device){
        String order;
        if(model == CtrlModel.LOCAL) {
            order = device.createTurnLocalModelOrder(IntelDevHelper.getLocalIp(), DevServer.PORT);
        }else{
            order = device.createTurnRemoteModelOrder(HamaApp.SERVER_IP, HamaApp.SERVER_DEV_PORT);
        }

        if(null!= order){
            //创建ProgressDialog对象
            progressDialog = new ProgressDialog(SearchActivity.this);
            //设置进度条风格，风格为圆形，旋转的
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //设置ProgressDialog 标题
            progressDialog.setTitle("正在配置设备...");
            //设置ProgressDialog 提示信息
            progressDialog.setMessage("请稍等");
            progressDialog.setCanceledOnTouchOutside(false);
            //设置ProgressDialog 标题图标
            progressDialog.setIcon(android.R.drawable.btn_star);
            //设置ProgressDialog 的进度条是否不明确
            progressDialog.setIndeterminate(false);
            //设置ProgressDialog 是否可以按退回按键取消
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);

            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "稍等...", (dialog, which) -> progressDialog.dismiss());
            progressDialog.show();
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);

            deviceModelHelper.setOrder(order);
            tSendModel = new SetDevModelTask(this);
            tSendModel.execute();
        }
    }

    private static class SetDevModelTask extends AsyncTask<Void, Integer, Boolean> {

        WeakReference<SearchActivity> mActivity;
        /**
         * 设置模式进度
         * 0:向服务器发送
         * 1:向设备发送
         */
        static int setModelProgressValue = 0;
        private int count;

        private SetDevModelTask(SearchActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        void setProgress(int progress){
            setModelProgressValue = progress;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                while (setModelProgressValue < 3){
                    try {
                        publishProgress(count * 10);
                        //计数加1
                        count++;
                        if(count > 10){
                            //设置失败
                            deviceModelHelper = null;
                            return false;
                        }

                        if(setModelProgressValue == 0) {
                            //第一步 向服务器发送
                            if(deviceModelHelper.getCtrlModel() == CtrlModel.REMOTE) {
                                //PadClient.getIns().send(deviceModelHelper.getOrder());
                                String oldOrder = deviceModelHelper.getOrder().substring(1, deviceModelHelper.getOrder().indexOf("#"));
                                User user = new User();
                                user.setName(HamaApp.USER.getName());
                                DevGroup group = new DevGroup();
                                group.setName(HamaApp.DEV_GROUP.getName());
                                //HamaApp.copyDevice(device, deviceModelHelper.getDevToSet());
                                user.addGroup(group);
                                group.addDevice(deviceModelHelper.getDevToSet());
                                String jsonOrder = HamaApp.getUserJson(user);
                                PadClient.getIns().send(OrderHelper.getOrderMsg(oldOrder + ":" + jsonOrder));
                                deviceModelHelper.getDevToSet().setDevGroup(HamaApp.DEV_GROUP);
                            }else{
                                String oldOrder = deviceModelHelper.getOrder().substring(1, deviceModelHelper.getOrder().indexOf("#"));
                                oldOrder += ":u" + HamaApp.USER.getName() + ":g" + HamaApp.DEV_GROUP.getName();
                                PadClient.getIns().send(OrderHelper.getOrderMsg(oldOrder));
                                //PadClient.getIns().send(deviceModelHelper.getOrder());
                            }
                        }else if(setModelProgressValue == 1){
                            //第二步
                            //如果时设为远程模式，向本地发送报文，
                            // 如果设为本地模式，不需要向本地发，只需向服务器发，收到服务器响应后等待设备本地心跳
                            if(deviceModelHelper.getCtrlModel() == CtrlModel.REMOTE){
                                String oldOrder = deviceModelHelper.getOrder().substring(1, deviceModelHelper.getOrder().indexOf("#"));
                                oldOrder += ":u" + HamaApp.USER.getName() + ":g" + HamaApp.DEV_GROUP.getName();
                                DevChannelBridgeHelper.getIns().sendDevOrder(deviceModelHelper.getDevToSet(), OrderHelper.getOrderMsg(oldOrder), true);
                                //DevChannelBridgeHelper.getIns().sendDevOrder(deviceModelHelper.getDevToSet(), deviceModelHelper.getOrder());
                            }
                        }
                        Thread.sleep(5000);
                    }catch (Exception ex){
                        Log.e("ElectricalCtrlFragment", ex.getMessage());
                        return false;
                    }
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            SearchActivity theActivity = mActivity.get();
            theActivity.progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            SearchActivity theActivity = mActivity.get();
            deviceModelHelper = null;
            theActivity.progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
            theActivity.progressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            if (success) {
                if(null != theActivity.progressDialog && theActivity.progressDialog.isShowing()){
                    theActivity.progressDialog.setProgress(100);
                    theActivity.progressDialog.setMessage("配置成功");
                    theActivity.progressDialog.setIcon(R.drawable.ic_check_pink_24dp);
                }
            }else {
                //设置失败
                String errMsg;
                if(SetDevModelTask.setModelProgressValue == 0){
                    errMsg = "服务器无响应";
                }else{
                    errMsg = "设备或服务器可能无响应";
                }
                if(null != theActivity.progressDialog && theActivity.progressDialog.isShowing()){
                    theActivity.progressDialog.setMessage("配置失败:" + errMsg);
                    theActivity.progressDialog.setIcon(R.drawable.ic_close_pink_24dp);
                }
            }
            SetDevModelTask.setModelProgressValue = 0;
        }

        @Override
        protected void onCancelled() {

        }
    }

    private static class AddDeviceTask extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog progressDialog;

        WeakReference<SearchActivity> mActivity;

        AddDeviceTask(SearchActivity activity) {

            mActivity = new WeakReference<>(activity);
            progressDialog = new ProgressDialog(activity);
            showAddChildDevDialog();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            SearchActivity theAct = mActivity.get();
            ((Coordinator)theAct.rootDevice).setConfigingChildDevice(true);
            theAct.childDevAdding = true;

            int count = 0;
            while (count <= 6 && !this.isCancelled()){
                if(!theAct.childDevAdding){
                    return true;
                }
                count++;
//                DevChannelBridgeHelper.getIns().sendDevOrder(theAct.rootDevice,
//                        OrderHelper.getOrderMsg("S" + theAct.rootDevice.getCoding() + ":+"), true);
                DevChannelBridgeHelper.getIns().sendDevOrder(theAct.rootDevice,
                        OrderHelper.getOrderMsg("?"), true);
                progressDialog.setProgress(count * 10);
                try {
                    TimeUnit.SECONDS.sleep(5);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            addResult(success);
        }

        @Override
        protected void onCancelled() {
            SearchActivity theAct = mActivity.get();
            ((Coordinator)theAct.rootDevice).setConfigingChildDevice(false);
        }

        private void addResult(boolean result){

            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            progressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            if(result){
                progressDialog.setIcon(R.drawable.ic_check_pink_24dp);
                setDialogMessage("添加成功");
            }else{
                progressDialog.setIcon(R.drawable.ic_close_pink_24dp);
                setDialogMessage("添加失败");
            }
            SearchActivity theAct = mActivity.get();
            ((Coordinator)theAct.rootDevice).setConfigingChildDevice(false);
        }

        void setDialogMessage(String msg){
            progressDialog.setMessage(msg);
        }

        private void showAddChildDevDialog(){
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("添加子设备");
            progressDialog.setMessage("请稍等");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(70);
            progressDialog.setIcon(R.drawable.ic_zoom_in_pink_24dp);
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    (dialog, which) -> progressDialog.dismiss());
            //设置取消按钮
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    (dialog, which) -> {
                        this.cancel(true);
                        progressDialog.dismiss();
                    });
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
