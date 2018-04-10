package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.MainActivity;

/**
 * 检查连接服务器状态
 * Created by 44489 on 2017/12/29.
 */

public class CheckServerConnect extends Thread {

    @Override
    public void run() {
        while(true){
            try {
                if(!MainActivity.IS_ADMIN) {
                    if (!PadClient.getIns().isLinked()) {
                        PadClient.getIns().link();
                    }
                }
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
