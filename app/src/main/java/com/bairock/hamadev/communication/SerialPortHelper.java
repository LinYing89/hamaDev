package com.bairock.hamadev.communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.user.IntelDevHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;

import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 * Created by 44489 on 2018/3/15.
 */

public class SerialPortHelper {
    private static SerialPortHelper ins = new SerialPortHelper();
    private static boolean isReadThreadRuning = false;

    private SerialPort mSerialPort = null;
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private boolean opened = false;

    private SerialMessageAnalysiser analysiser = new SerialMessageAnalysiser();
    private ReadThread readThread;

    private SerialPortHelper(){}

    public static SerialPortHelper getIns(){
        return ins;
    }

    public void openSerialPort(Context context) throws SecurityException,
            IOException, InvalidParameterException{
        closeSerialPort();
        SharedPreferences sp = context.getSharedPreferences(
                "com.bairock.hamadev_preferences", MODE_PRIVATE);
        String path = sp.getString("DEVICE", "");
        int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
        if ( (path.length() == 0) || (baudrate == -1)) {
            throw new InvalidParameterException();
        }

			/* Open the serial port */
        mSerialPort = new SerialPort(new File(path), baudrate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        opened = true;
    }

    public boolean isOpened() {
        return opened;
    }

//    public SerialPort getSerialPort() {
//        return mSerialPort;
//    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        try {
            if (null != mInputStream) {
                mInputStream.close();
                mInputStream = null;
            }
            if (null != mOutputStream) {
                mOutputStream.close();
                mOutputStream = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void send(String msg){
        byte[] mBuffer = msg.getBytes(Charset.forName("GBK"));
        if (mOutputStream != null) {
            try {
                mOutputStream.write(mBuffer);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void startReceiveThread(){
        if(!isReadThreadRuning){
            isReadThreadRuning = true;
            readThread = new ReadThread();
            IntelDevHelper.executeThread(readThread);
        }
    }

    public void stopReceiveThread(){
        opened = false;
        isReadThreadRuning = false;
        if(null != readThread){
            readThread.interrupt();
            readThread = null;
        }
    }

    public class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (isReadThreadRuning && !isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(buffer);
                    if(!isReadThreadRuning){
                        return;
                    }
                    String str = new String(buffer, 0, size, Charset.forName("GBK"));
                    Log.e("SerialPortHelper", str);
                    analysiser.putMsg(str, HamaApp.USER);
//                    if (size > 0) {
//                        onDataReceived(buffer, size);
//                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isReadThreadRuning = false;
        }
    }
}
