package com.bairock.hamadev.settings;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.SharedHelper;
import com.bairock.hamadev.communication.PadClient;

public class ServerSetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_set);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView txtServerIp = (TextView) findViewById(R.id.txtServerIp);
        TextView txtServerPadPort = (TextView) findViewById(R.id.txtServerPadPort);
        TextView txtServerDevPort = (TextView) findViewById(R.id.txtServerDevPort);

        txtServerIp.setText(HamaApp.SERVER_IP);
        txtServerPadPort.setText(String.valueOf(HamaApp.SERVER_PAD_PORT));
        txtServerDevPort.setText(String.valueOf(HamaApp.SERVER_DEV_PORT));

        findViewById(R.id.btnOk).setOnClickListener(v -> {
            String serverIp = txtServerIp.getText().toString();
            if(serverIp.isEmpty()){
                Snackbar.make(v, "ip地址为空", Snackbar.LENGTH_SHORT).show();
                return;
            }
            int padPort;
            int devPort;
            try{
                String strPort = txtServerPadPort.getText().toString();
                padPort = Integer.parseInt(strPort);
                devPort = Integer.parseInt(txtServerDevPort.getText().toString());
            }catch (Exception e){
                e.printStackTrace();
                Snackbar.make(v, "端口号格式错误", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(!HamaApp.SERVER_IP.equals(serverIp) || HamaApp.SERVER_PAD_PORT != padPort) {
                HamaApp.SERVER_IP = serverIp;
                HamaApp.SERVER_PAD_PORT = padPort;
                PadClient.getIns().closeHandler();
                new SharedHelper().setServerConfig();
            }
            if(HamaApp.SERVER_DEV_PORT != devPort){
                HamaApp.SERVER_DEV_PORT = padPort;
                new SharedHelper().setServerConfig();
            }
            finish();
        });
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
}
