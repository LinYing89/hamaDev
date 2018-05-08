package com.bairock.hamadev.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bairock.hamadev.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById(R.id.tvServer).setOnClickListener(onClickListener);
        findViewById(R.id.tvAbout).setOnClickListener(onClickListener);
        findViewById(R.id.tvSerialSet).setOnClickListener(onClickListener);
        findViewById(R.id.tvNetSet).setOnClickListener(onClickListener);
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

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.tvServer:
                startActivity(new Intent(SettingsActivity.this, ServerSetActivity.class));
                break;
            case R.id.tvNetSet:
                startActivity(new Intent(SettingsActivity.this, EsptouchActivity.class));
                break;
            case R.id.tvSerialSet:
                startActivity(new Intent(SettingsActivity.this, SerialSetActivity.class));
                break;
            case R.id.tvAbout :
                break;
        }
    };
}
