package com.bairock.hamadev.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import static android.content.Context.MODE_PRIVATE;

public class SharedHelper {

    private final String NEED_LOGIN = "needLogin";
	public static boolean needLogin = true;
    private static final String DOWNLOAD_ID = "downloadId";
    private static final String SERVER_IP = "serverIp";
    private static final String SERVER_PAD_PORT = "serverPadPort";
    private static final String SERVER_DEV_PORT = "serverDevPort";

    private static final String ROUTER_NAME = "routerName";
    private static final String ROUTER_PSD = "routerPsd";

    /**
     * configuration file name of numbers
     */
    private static final String CONFIG = "config";

	public SharedHelper() {
	}

	private SharedPreferences getSharedFile(String sharedName) {
        return HamaApp.HAMA_CONTEXT.getSharedPreferences(sharedName,
                Context.MODE_PRIVATE);
	}

	public void init(){
        SharedPreferences shared = getSharedFile(CONFIG);
        needLogin = shared.getBoolean(NEED_LOGIN, true);
        HamaApp.SERVER_IP = shared.getString(SERVER_IP, "123.206.104.15");
        HamaApp.SERVER_PAD_PORT = shared.getInt(SERVER_PAD_PORT, 4045);
        HamaApp.SERVER_DEV_PORT = shared.getInt(SERVER_DEV_PORT, 4049);
        RouterInfo.NAME = shared.getString(ROUTER_NAME, "");
        RouterInfo.PSD = shared.getString(ROUTER_PSD, "");
    }

	public void setNeedLogin(boolean needLogin){
        SharedPreferences shared = getSharedFile(CONFIG);
        Editor editor = shared.edit();
        editor.putBoolean(NEED_LOGIN, needLogin);
        editor.apply();
    }

    public void getNeedLogin(){
        SharedPreferences shared = getSharedFile(CONFIG);
		needLogin = shared.getBoolean(NEED_LOGIN, true);
    }

    public void setServerConfig(){
        SharedPreferences shared = getSharedFile(CONFIG);
        Editor editor = shared.edit();
        editor.putString(SERVER_IP, HamaApp.SERVER_IP);
        editor.putInt(SERVER_PAD_PORT, HamaApp.SERVER_PAD_PORT);
        editor.putInt(SERVER_DEV_PORT, HamaApp.SERVER_DEV_PORT);
        editor.apply();
    }

    public void setDownloadId(long id){
        SharedPreferences shared = getSharedFile(CONFIG);
        Editor editor = shared.edit();
        editor.putLong(DOWNLOAD_ID, id);
        editor.apply();
    }

    public void setRouterInfo(){
        SharedPreferences shared = getSharedFile(CONFIG);
        Editor editor = shared.edit();
        editor.putString(ROUTER_NAME, RouterInfo.NAME);
        editor.putString(ROUTER_PSD, RouterInfo.PSD);
        editor.apply();
    }

    public boolean isSerialOpen(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                "com.bairock.hamadev_preferences", MODE_PRIVATE);
        return sp.getBoolean("switch_onoff", false);
    }
}
