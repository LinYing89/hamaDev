package com.bairock.hamadev.esptouch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.RouterInfo;
import com.bairock.hamadev.app.WelcomeActivity;
import com.bairock.hamadev.communication.MyOnCtrlModelChangedListener;
import com.bairock.hamadev.communication.MyOnGearChangedListener;
import com.bairock.hamadev.communication.MyOnStateChangedListener;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.hamadev.esptouch.task.EsptouchTask;
import com.bairock.hamadev.esptouch.task.IEsptouchResult;
import com.bairock.hamadev.esptouch.task.IEsptouchTask;
import com.bairock.hamadev.esptouch.task.__IEsptouchTask;
import com.bairock.iot.intelDev.communication.DevServer;
import com.bairock.iot.intelDev.communication.UdpServer;
import com.bairock.iot.intelDev.device.CtrlModel;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.OrderHelper;
import com.bairock.iot.intelDev.linkage.LinkageTab;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.IntelDevHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 44489 on 2018/2/1.
 */

public class EspTouchAddDevice {
    public static Device DEVICE;
    public static int RECEIVED_OK_COUNT;
    public static boolean CONFIGING;
    //=ture表示使用udp+tcp方式配置设备，=false表示使用纯udp方式配置设备
    private static boolean TCP_CONFIG_MODEL = true;
    private boolean configOk;
    private EspWifiAdminSimple mWifiAdmin;
    private ProgressDialog mProgressDialog;

    private Context context;

    public boolean moniAdd = false;

    public EspTouchAddDevice(Context context){
        this.context = context;
        mWifiAdmin = new EspWifiAdminSimple(context);
    }

    public String getSsid(){
        String ssid = mWifiAdmin.getWifiConnectedSsid();
        if(ssid == null){
            ssid = "";
        }
        return ssid;
    }

    public void startConfig(){
        CONFIGING = true;
        DEVICE = null;
        RECEIVED_OK_COUNT = 0;
        if(moniAdd) {
            new ConfigDeviceTask(EspTouchAddDevice.this).execute();
            showConfigProgress(null);
        }else{
            String apBssid = mWifiAdmin.getWifiConnectedBssid();
            //Boolean isSsidHidden = false;
            //String isSsidHiddenStr = "NO";
            String isSsidHiddenStr = "YES";
            String taskResultCountStr = Integer.toString(1);
//                if (isSsidHidden){
//                    isSsidHiddenStr = "YES";
//                }
            new EsptouchAsyncTask3(this).execute(RouterInfo.NAME, apBssid, RouterInfo.PSD,
                    isSsidHiddenStr, taskResultCountStr);
        }
    }

    private static class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        WeakReference<EspTouchAddDevice> mActivity;
        private EsptouchAsyncTask3(EspTouchAddDevice activity){
            mActivity = new WeakReference<>(activity);
        }

        private IEsptouchTask mEsptouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            EspTouchAddDevice theActivity = mActivity.get();
            theActivity.showConfigProgress(mEsptouchTask);
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            EspTouchAddDevice theActivity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, theActivity.context);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            EspTouchAddDevice theActivity = mActivity.get();
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                //int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                //final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    for (IEsptouchResult resultInList : result) {
                        String ip = resultInList.getInetAddress().getHostAddress();
                        if(!TextUtils.isEmpty(ip)){
                            if(CONFIGING) {
                                new ConfigDeviceTask(theActivity).execute();
                            }
                            //configResult(true);
                        }
                    }
                } else {
                    theActivity.configResult(false, "请检查路由器名称和密码是否正确");
                }
            }
        }
    }

    private final Object mLock = new Object();
    private void showConfigProgress(IEsptouchTask mEsptouchTask){
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("正在配置，请稍等...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(dialog -> {
            synchronized (mLock) {
                if (__IEsptouchTask.DEBUG) {
                    Log.i("EsptouchActivity", "progress dialog is canceled");
                }
                CONFIGING = false;
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }
        });
        mProgressDialog.setMax(100);
        mProgressDialog.setTitle("配置设备");
        mProgressDialog.setIcon(R.drawable.ic_zoom_in_pink_24dp);
        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "稍等...", (dialog, which) -> {
                    if(configOk) {
                        DEVICE = null;
                        //finish();
                    }
                    mProgressDialog.dismiss();
                });
        mProgressDialog.show();
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setEnabled(false);
    }

    private String getSeekOrder(String ip, int port) {
        String order = "S0:n" + ip + "," + port + ":+";
        return OrderHelper.getOrderMsg(order);
    }

    private String getDeviceConfigOkOrder() {
        String order = "S" + DEVICE.getCoding() + ":+ok";
        return OrderHelper.getOrderMsg(order);
    }

    private static class ConfigDeviceTask extends AsyncTask<Void, Integer, Boolean> {

        WeakReference<EspTouchAddDevice> mActivity;
        private ConfigDeviceTask(EspTouchAddDevice activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                EspTouchAddDevice theActivity = mActivity.get();
                //发送寻找正在配置设备的报文
                String ip = IntelDevHelper.getLocalIp();
                String seekOrder = theActivity.getSeekOrder(ip, DevServer.PORT);
                int count = 1;
                publishProgress(0, 0);
                do {
                    if(count >= 10){
                        return false;
                    }
                    UdpServer.getIns().send(seekOrder);
                    Thread.sleep(5000);
                    publishProgress(count * 10);
                    Log.e("EsptouchAct", "DEVICE" + DEVICE);
                    count++;
                }while (DEVICE == null && CONFIGING);
                if(null == DEVICE){
                    return false;
                }
                if(TCP_CONFIG_MODEL){
                    publishProgress(100, 1);
                    return true;
                }

                //下面的是纯udp方式时执行的代码
                Log.e("EsptouchAct", "DEVICE state id " + DEVICE.getDevStateId());
                //发送收到设备编码成功信息
                count = 1;
                String deviceCodingOrder = theActivity.getDeviceConfigOkOrder();
                publishProgress(50, 1);
                do {
                    if(count >= 6 && DEVICE.getDevStateId().equals(DevStateHelper.CONFIGING)){
                        return false;
                    }
                    UdpServer.getIns().send(deviceCodingOrder);
                    //DevChannelBridgeHelper.getIns().sendDevOrder(DEVICE, deviceCodingOrder);
                    Log.e("EsptouchAct", "send config ok order " + deviceCodingOrder);
                    //UdpMsgSender.getIns().send(deviceCodingOrder);
                    Thread.sleep(5000);
                    publishProgress(50 + count * 8);
                    count++;
                }while (DEVICE.getDevStateId().equals(DevStateHelper.CONFIGING) && CONFIGING && RECEIVED_OK_COUNT < 2);
                if(RECEIVED_OK_COUNT >= 2) {
                    //至少收到2次回复，配置成功，再发5次
                    for (int i = 0; i < 5; i++) {
                        UdpServer.getIns().send(deviceCodingOrder);
                        Thread.sleep(1000);
                    }
                }
                Log.e("EsptouchAct", "config ok");
                publishProgress(100);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            EspTouchAddDevice theActivity = mActivity.get();
            theActivity.mProgressDialog.setProgress(values[0]);
            if(values.length >= 2){
                if(values[1] != null){
                    if(values[1] == 1){
                        theActivity.mProgressDialog.setMessage("配置成功的设备:" + DEVICE.getCoding());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            EspTouchAddDevice theActivity = mActivity.get();
            if (success) {
                Device device = DEVICE;
                device.setCtrlModel(CtrlModel.LOCAL);
                Device device2 = HamaApp.DEV_GROUP.findDeviceWithCoding(device.getCoding());
                if(device2 == null) {
                    //配置成功，保存到数据库
                    device.setDevGroup(HamaApp.DEV_GROUP);
                    //先添加到数据库，后添加到用户组，因为添加到数据库后，如果数据库中已有设备的数据信息
                    //则会读取数据信息进行赋值
                    DeviceDao.get(HamaApp.HAMA_CONTEXT).add(device);

                    HamaApp.DEV_GROUP.addDevice(device);
                    //设置设备状态改变监听器
                    WelcomeActivity.setDeviceListener(device, new MyOnStateChangedListener(),
                            new MyOnGearChangedListener(), new MyOnCtrlModelChangedListener());
                    if(TCP_CONFIG_MODEL) {
                        device.setDevStateId(DevStateHelper.DS_ZHENG_CHANG);
                    }else{
                        device.setDevStateId(DevStateHelper.DS_YI_CHANG);
                    }

                    //添加到连锁内存表
                    List<Device> listIStateDev = new ArrayList<>();
                    DevGroup.findListIStateDev(listIStateDev, device, true);
                    for (Device device1 : listIStateDev) {
                        LinkageTab.getIns().addTabRow(device1);
                    }
                }
                if(!TCP_CONFIG_MODEL) {
                    //UDP模式下寻找设备
                    HamaApp.addOfflineDevCoding(device);
                }
                theActivity.configResult(true, device.getCoding());
            }else {
                //设备无返回，配置失败
                theActivity.configResult(false, "设备无响应");
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    private void configResult(boolean result, String message){
        if(null == message){
            message = "";
        }
        CONFIGING = false;
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setEnabled(true);
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定");
        if(result) {
            mProgressDialog.setMessage("配置成功,设备编码:" + message);
            mProgressDialog.setIcon(R.drawable.ic_check_pink_24dp);
            configOk = true;
        }else{
            mProgressDialog.setMessage("配置失败:" + message);
            mProgressDialog.setIcon(R.drawable.ic_close_pink_24dp);
            configOk = false;
        }
        DEVICE = null;
        RECEIVED_OK_COUNT = 0;
    }
}
