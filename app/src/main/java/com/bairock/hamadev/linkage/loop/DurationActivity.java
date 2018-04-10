package com.bairock.hamadev.linkage.loop;

import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.database.LoopDurationDao;
import com.bairock.hamadev.database.MyTimeDao;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;

public class DurationActivity extends AppCompatActivity {

    public static LoopDuration duration;
    private boolean isAdd = false;

    private EditText editOnHour;
    private EditText editOnMinute;
    private EditText editOnSecond;
    private EditText editOffHour;
    private EditText editOffMinute;
    private EditText editOffSecond;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(null == duration){
            isAdd = true;
            duration = new LoopDuration();
        }
        init();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        duration = null;
    }

    private void findViews(){
        editOnHour = (EditText) findViewById(R.id.edit_on_hour);
        editOnMinute = (EditText)findViewById(R.id.edit_on_minute);
        editOnSecond = (EditText)findViewById(R.id.edit_on_second);
        editOffHour = (EditText) findViewById(R.id.edit_off_hour);
        editOffMinute = (EditText)findViewById(R.id.edit_off_minute);
        editOffSecond = (EditText)findViewById(R.id.edit_off_second);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        ((TextView)findViewById(R.id.text_on_time_title)).setText("开启时长");
        ((TextView)findViewById(R.id.text_off_time_title)).setText("关闭时长");
        findViewById(R.id.linear_weeks).setVisibility(View.GONE);
    }

    private void setListener(){
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void init(){
        if(!isAdd){
            editOnHour.setText(String.valueOf(duration.getOnKeepTime().getHour()));
            editOnMinute.setText(String.valueOf(duration.getOnKeepTime().getMinute()));
            editOnSecond.setText(String.valueOf(duration.getOnKeepTime().getSecond()));
            editOffHour.setText(String.valueOf(duration.getOffKeepTime().getHour()));
            editOffMinute.setText(String.valueOf(duration.getOffKeepTime().getMinute()));
            editOffSecond.setText(String.valueOf(duration.getOffKeepTime().getSecond()));
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_save:
                    String onHour = editOnHour.getText().toString();
                    if(TextUtils.isEmpty(onHour)){
                        onHour = "0";
                    }
                    String onMinute = editOnMinute.getText().toString();
                    if(TextUtils.isEmpty(onMinute)){
                        onMinute = "0";

                    }
                    String onSecond = editOnSecond.getText().toString();
                    if(TextUtils.isEmpty(onSecond)){
                        onSecond = "0";
                    }
                    if(onHour.equals("0") && onMinute.equals("0") && onSecond.equals("0")){
                        Snackbar.make(btnSave, "内容不可全为0", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    String offHour = editOffHour.getText().toString();
                    if(TextUtils.isEmpty(offHour)){
                        offHour = "0";
                    }
                    String offMinute = editOffMinute.getText().toString();
                    if(TextUtils.isEmpty(offMinute)){
                        offMinute = "0";
                    }
                    String offSecond = editOffSecond.getText().toString();
                    if(TextUtils.isEmpty(offSecond)){
                        offSecond = "0";
                    }
                    if(offHour.equals("0") && offMinute.equals("0") && offSecond.equals("0")){
                        Snackbar.make(btnSave, "内容不可全为0", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        duration.getOnKeepTime().setHour(Integer.parseInt(onHour));
                        duration.getOnKeepTime().setMinute(Integer.parseInt(onMinute));
                        duration.getOnKeepTime().setSecond(Integer.parseInt(onSecond));
                        duration.getOffKeepTime().setHour(Integer.parseInt(offHour));
                        duration.getOffKeepTime().setMinute(Integer.parseInt(offMinute));
                        duration.getOffKeepTime().setSecond(Integer.parseInt(offSecond));
                    }catch (Exception e){
                        e.printStackTrace();
                        Snackbar.make(btnSave, "内容必须是整数", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if(isAdd){
                        EditLoopActivity.zLoop.addLoopDuration(duration);
                        LoopDurationDao loopDurationDao = LoopDurationDao.get(DurationActivity.this);
                        loopDurationDao.add(duration, EditLoopActivity.zLoop.getId());
                        MyTimeDao myTimeDao = MyTimeDao.get(DurationActivity.this);
                        myTimeDao.add(duration.getOnKeepTime(), duration.getId());
                        myTimeDao.add(duration.getOffKeepTime(), duration.getId());
                    }else{
                        MyTimeDao myTimeDao = MyTimeDao.get(DurationActivity.this);
                        myTimeDao.update(duration.getOnKeepTime(), duration.getId());
                        myTimeDao.update(duration.getOffKeepTime(), duration.getId());
                    }
                    if(null != DurationListActivity.handler){
                        Message message = Message.obtain();
                        message.arg1 = DurationListActivity.REFRESH_DURATION_LIST;
                        DurationListActivity.handler.sendMessage(message);
                    }

                    finish();
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
            }
        }
    };
}
