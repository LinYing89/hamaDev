package com.bairock.hamadev.settings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.RouterInfo;
import com.bairock.hamadev.app.SharedHelper;
import com.bairock.hamadev.app.WelcomeActivity;
import com.bairock.hamadev.communication.MyOnCtrlModelChangedListener;
import com.bairock.hamadev.communication.MyOnGearChangedListener;
import com.bairock.hamadev.communication.MyOnStateChangedListener;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.hamadev.esptouch.EspWifiAdminSimple;
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

public class EsptouchActivity extends AppCompatActivity {

//    public static Device DEVICE;
//    public static int RECEIVED_OK_COUNT;
//    public static boolean CONFIGING;
    private TextView txtSsid;
    private EditText etxtPsd;
    private Button btnSave;
//    private boolean configOk;

    private EspWifiAdminSimple mWifiAdmin;

//    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esptouch);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWifiAdmin = new EspWifiAdminSimple(this);
        findViews();
        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // display the connected ap's ssid
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        if (apSsid != null) {
            txtSsid.setText(apSsid);
        } else {
            txtSsid.setText("");
        }
        // check whether the wifi is connected
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        btnSave.setEnabled(!isApSsidEmpty);
    }

    private void findViews(){
        txtSsid = (TextView)findViewById(R.id.txtSsid);
        etxtPsd = (EditText)findViewById(R.id.txtPsd);
        etxtPsd.setText(RouterInfo.PSD);
        btnSave = (Button)findViewById(R.id.btnSave);
    }

    private void setListener(){
        btnSave.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnSave) {

                RouterInfo.NAME = txtSsid.getText().toString();
                RouterInfo.PSD = etxtPsd.getText().toString();
                new SharedHelper().setRouterInfo();

                Toast.makeText(EsptouchActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
//                CONFIGING = true;
//                DEVICE = null;
//                RECEIVED_OK_COUNT = 0;
////                new ConfigDeviceTask(EsptouchActivity.this).execute();
////                showConfigProgress(null);
//
//                String apSsid = txtSsid.getText().toString();
//                String apPassword = etxtPsd.getText().toString();
//                String apBssid = mWifiAdmin.getWifiConnectedBssid();
//                //Boolean isSsidHidden = false;
//                //String isSsidHiddenStr = "NO";
//                String isSsidHiddenStr = "YES";
//                String taskResultCountStr = Integer.toString(1);
////                if (isSsidHidden){
////                    isSsidHiddenStr = "YES";
////                }
//                if (__IEsptouchTask.DEBUG) {
//                    Log.d("EsptouchActivity", "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
//                            + ", " + " mEdtApPassword = " + apPassword);
//                }
//                new EsptouchAsyncTask3(EsptouchActivity.this).execute(apSsid, apBssid, apPassword,
//                        isSsidHiddenStr, taskResultCountStr);
            }
        }
    };
}
