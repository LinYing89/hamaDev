package com.bairock.hamadev.app;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.SectionsPagerAdapter;
import com.bairock.hamadev.communication.CheckServerConnect;
import com.bairock.hamadev.communication.DeviceMsgMonitorActivity;
import com.bairock.hamadev.communication.DownloadClient;
import com.bairock.hamadev.communication.SerialPortHelper;
import com.bairock.hamadev.communication.UploadClient;
import com.bairock.hamadev.linkage.LinkageActivity;
import com.bairock.hamadev.receiver.NetworkConnectChangedReceiver;
import com.bairock.hamadev.settings.BridgesStateActivity;
import com.bairock.hamadev.settings.EsptouchActivity;
import com.bairock.hamadev.settings.SearchActivity;
import com.bairock.hamadev.settings.SettingsActivity;
import com.bairock.hamadev.settings.SortActivity;
import com.bairock.hamadev.settings.UdpLogActivity;
import com.bairock.iot.intelDev.user.IntelDevHelper;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean IS_ADMIN;
    //public static boolean FIRST_LOGIN;
    public static String subTitle = "呱呱物联:智能物联网控制器";
    /**
     * 本地连接是否可用
     */
    public static boolean LOCAL_CONNECTED;

    public static final int UPLOAD_FAIL = 3;
    public static final int UPLOAD_OK = 4;
    public static final int DOWNLOAD_FAIL = 5;
    public static final int DOWNLOAD_OK = 6;
    public static final int REFRESH_TITLE = 8;
    public static MyHandler handler = null;

    public static String strEnsure;
    public static String strCancel;

    private Toolbar toolbar;
    private ProgressDialog progressFileDialog;
    private VersionTask versionTask;
    private PackageInfo packageInfo;

    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_logo_white);
        packageInfo = getAppVersionCode(this);
        //toolbar.setTitle(UserHelper.getUser().getName() + UserHelper.getUser().getPetName());
        if(null != packageInfo){
            subTitle += " v" + packageInfo.versionName;
        }
        toolbar.setTitle(HamaApp.USER.getName() + "-" + HamaApp.DEV_GROUP.getName() + ":" + HamaApp.DEV_GROUP.getPetName());
        toolbar.setSubtitle(subTitle);
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //UserDao userDao = UserDao.get(this);

        View headerView = navigationView.getHeaderView(0);
        //textUser.setText(UserHelper.getUser().getName());
        strEnsure = "确定";
        strCancel = "取消";
        versionTask = new VersionTask(this);
        versionTask.execute((Void)null);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        handler = new MyHandler(MainActivity.this);

        networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(networkConnectChangedReceiver,filter);

        if(!IS_ADMIN) {
            //尝试连接服务器
            IntelDevHelper.executeThread(new CheckServerConnect());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_reset:
                //DeviceChainHelper.getIns().init();
                break;
            case R.id.action_refresh:
                //SendMsgHelper.refreshState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        } else if(id == R.id.nav_set_chain) {
            startActivity(new Intent(MainActivity.this, LinkageActivity.class));
        }else if(id == R.id.nav_system_set) {
            //startActivity(new Intent(MainActivity.this, DeviceMsgMonitorActivity.class));
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_upload) {
            //上传
            showProgressDialog("上传");
            UploadClient uploadClient = new UploadClient();
            uploadClient.link();
        } else if (id == R.id.nav_download) {
            showProgressDialog("下载");
            DownloadClient downloadClient = new DownloadClient();
            downloadClient.link();
        }else if (id == R.id.nav_exit) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("确定退出账号吗")
                    .setNegativeButton(strCancel,null)
                    .setPositiveButton(strEnsure,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    new SharedHelper().setNeedLogin(true);
                                    finish();
                                }
                            }).show();
        }else if(id == R.id.nav_log){
            startActivity(new Intent(MainActivity.this, BridgesStateActivity.class));
            //startActivity(new Intent(MainActivity.this, UdpLogActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("退出程序")
                    .setNegativeButton(strCancel, null)
                    .setPositiveButton(strEnsure,
                            (dialog, which) -> {
                                finish();
                            }).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkConnectChangedReceiver);
        HamaApp.DEV_SERVER.close();
        IntelDevHelper.shutDown();
        SerialPortHelper.getIns().stopReceiveThread();
        SerialPortHelper.getIns().closeSerialPort();
        System.exit(0);
    }

    public static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {
                case UPLOAD_FAIL:
                    Snackbar.make(theActivity.toolbar, "上传失败", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case UPLOAD_OK:
                    Snackbar.make(theActivity.toolbar, "上传成功", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case DOWNLOAD_FAIL:
                    Snackbar.make(theActivity.toolbar, "下载失败", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case DOWNLOAD_OK:
                    Snackbar.make(theActivity.toolbar, "下载成功", Snackbar.LENGTH_SHORT).show();
                    theActivity.closeProgressDialog();
                    break;
                case REFRESH_TITLE:
                    if(!HamaApp.NET_CONNECTED){
                        theActivity.toolbar.setSubtitle(subTitle + "(网络未连接)");
                    }else if(!HamaApp.SERVER_CONNECTED){
                        theActivity.toolbar.setSubtitle(subTitle + "(服务器未连接)");
                    }else{
                        theActivity.toolbar.setSubtitle(subTitle);
                    }
                    break;
            }
        }
    }

    private void showProgressDialog(String title){
        //创建ProgressDialog对象
        progressFileDialog = new ProgressDialog(
                MainActivity.this);
        //设置进度条风格，风格为圆形，旋转的
        progressFileDialog.setProgressStyle(
                ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog 标题
        progressFileDialog.setTitle(title);
        //设置ProgressDialog 提示信息
        progressFileDialog.setMessage("请稍等");
        //设置ProgressDialog 标题图标
        progressFileDialog.setIcon(android.R.drawable.btn_star);
        //设置ProgressDialog 的进度条是否不明确
        progressFileDialog.setIndeterminate(false);
        //设置ProgressDialog 是否可以按退回按键取消
        progressFileDialog.setCancelable(false);
        //设置取消按钮
//        progressFileDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",
//                (dialog, which) -> {
//                    progressFileDialog.dismiss();
//                    //webFileBase.close();
//                });
        // 让ProgressDialog显示
        progressFileDialog.show();
    }

    private void closeProgressDialog(){
        if(null != progressFileDialog && progressFileDialog.isShowing()){
            progressFileDialog.dismiss();
        }
    }

private static class VersionTask extends AsyncTask<Void, Void, Boolean> {
    WeakReference<MainActivity> mActivity;

    VersionTask(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            MainActivity theActivity = mActivity.get();
            if(theActivity.packageInfo != null){
                int version = theActivity.packageInfo.versionCode;
                String s = "";
                //String s = HttpRequest.sendGet(WebClient.getVersionUrl(), "version=" + version);
                Log.e("MainActivity: ", "get:" + s);
                return s.contains("YES");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        MainActivity theActivity = mActivity.get();
        theActivity.versionTask = null;
        if (success) {
            new AlertDialog.Builder(theActivity)
                    .setMessage("有新版本，是否下载更新")
                    .setNegativeButton(strCancel, null)
                    .setPositiveButton(strEnsure,
                            (dialog, whichButton) -> {
                                //下载
                                theActivity.intoDownloadManager();
                                    /*Intent i = new Intent(Intent.ACTION_VIEW , Uri.parse("http://192.168.1.104:8080/ZSHWeb/download/smarthome.apk"));
                                    startActivity(i);*/
                            }).show();
        }
    }

    @Override
    protected void onCancelled() {
        MainActivity theActivity = mActivity.get();
        theActivity.versionTask = null;
    }
}

    /**
     * 返回当前程序版本名
     */
    private PackageInfo getAppVersionCode(Context context) {
        PackageInfo pi = null;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return pi;
    }

    private void intoDownloadManager(){
        try {
            DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            request.setDestinationInExternalPublicDir(MyFileHelper.getZhiBoFile(), "hama.apk");
            request.setDescription("智能物联网控制器新版本下载");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            long refernece = dManager.enqueue(request);
            // 把当前下载的ID保存起来
            new SharedHelper().setDownloadId(refernece);
        }catch (IllegalArgumentException ex){
            Snackbar.make(toolbar, "下载器没有启用", Snackbar.LENGTH_LONG).show();
        }
    }
}
