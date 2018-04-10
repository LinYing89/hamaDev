package com.bairock.hamadev.settings;

import android.content.DialogInterface;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;

import com.bairock.hamadev.R;
import com.bairock.hamadev.communication.SerialPortHelper;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.serialport.SerialPortFinder;

public class SerialSetActivity extends PreferenceActivity {

    SwitchPreference switchOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SerialPortFinder mSerialPortFinder = SerialPortHelper.getIns().mSerialPortFinder;

        addPreferencesFromResource(R.xml.serial_port_preferences);
        final ListPreference devices = (ListPreference) findPreference("DEVICE");
        String[] entries = mSerialPortFinder.getAllDevices();
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
        devices.setEntries(entries);
        devices.setEntryValues(entryValues);
        devices.setSummary(devices.getValue());
        devices.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary((String)newValue);
            return true;
        });

        final ListPreference baudrates = (ListPreference)findPreference("BAUDRATE");
        baudrates.setSummary(baudrates.getValue());
        baudrates.setOnPreferenceChangeListener(((preference, newValue) -> {
            preference.setSummary((String)newValue);
            return true;
        }));

        switchOnOff = (SwitchPreference)findPreference("switch_onoff");
        switchOnOff.setChecked(SerialPortHelper.getIns().isOpened());
        switchOnOff.setOnPreferenceChangeListener((preference, newValue) -> {
            if((boolean)newValue){
                try {
                    SerialPortHelper.getIns().openSerialPort(SerialSetActivity.this);
                    if(SerialPortHelper.getIns().isOpened()) {
                        SerialPortHelper.getIns().startReceiveThread();
                        return true;
                    }
                }catch (SecurityException e) {
                    DisplayError(R.string.error_security);
                } catch (IOException e) {
                    DisplayError(R.string.error_unknown);
                } catch (InvalidParameterException e) {
                    DisplayError(R.string.error_configuration);
                }
                //switchOnOff.setChecked(false);
            }else{
                SerialPortHelper.getIns().stopReceiveThread();
                SerialPortHelper.getIns().closeSerialPort();
            }
            return true;
        });
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("错误");
        b.setMessage(resourceId);
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchOnOff.setChecked(false);
            }
        });
        b.show();
    }
}
