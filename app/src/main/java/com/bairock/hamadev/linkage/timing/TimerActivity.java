package com.bairock.hamadev.linkage.timing;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bairock.hamadev.R;
import com.bairock.hamadev.database.MyTimeDao;
import com.bairock.hamadev.database.WeekHelperDao;
import com.bairock.hamadev.database.ZTimerDao;
import com.bairock.iot.intelDev.linkage.timing.ZTimer;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    public static ZTimer timer;
    private boolean isAdd = false;

    private EditText editOnHour;
    private EditText editOnMinute;
    private EditText editOnSecond;
    private EditText editOffHour;
    private EditText editOffMinute;
    private EditText editOffSecond;
    private Button btnSave;
    private Button btnCancel;
    private CheckBox checkSun;
    private CheckBox checkMon;
    private CheckBox checkTues;
    private CheckBox checkWed;
    private CheckBox checkThur;
    private CheckBox checkFri;
    private CheckBox checkSat;
    private CheckBox[] checkBoxes;

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

        if(null == timer){
            isAdd = true;
            timer = new ZTimer();
        }

        checkBoxes = new CheckBox[]{checkSun, checkMon, checkTues, checkWed, checkThur, checkFri, checkSat};

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
        timer = null;
    }
    private void findViews(){
        editOnHour = (EditText) findViewById(R.id.edit_on_hour);
        editOnMinute = (EditText)findViewById(R.id.edit_on_minute);
        editOnSecond = (EditText)findViewById(R.id.edit_on_second);
        editOffHour = (EditText) findViewById(R.id.edit_off_hour);
        editOffMinute = (EditText)findViewById(R.id.edit_off_minute);
        editOffSecond = (EditText)findViewById(R.id.edit_off_second);
        checkSun = (CheckBox)findViewById(R.id.check_sun);
        checkMon = (CheckBox)findViewById(R.id.check_mon);
        checkTues = (CheckBox)findViewById(R.id.check_tues);
        checkWed = (CheckBox)findViewById(R.id.check_wed);
        checkThur = (CheckBox)findViewById(R.id.check_thur);
        checkFri = (CheckBox)findViewById(R.id.check_fri);
        checkSat = (CheckBox)findViewById(R.id.check_sat);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
    }

    private void setListener(){
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void init(){
        if(!isAdd){
            editOnHour.setText(String.valueOf(timer.getOnTime().getHour()));
            editOnMinute.setText(String.valueOf(timer.getOnTime().getMinute()));
            editOnSecond.setText(String.valueOf(timer.getOnTime().getSecond()));
            editOffHour.setText(String.valueOf(timer.getOffTime().getHour()));
            editOffMinute.setText(String.valueOf(timer.getOffTime().getMinute()));
            editOffSecond.setText(String.valueOf(timer.getOffTime().getSecond()));
            for(int i = 0; i < 7; i++){
                checkBoxes[i].setChecked(timer.getWeekHelper().isSelected(i));
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_save:
                    String onHour = editOnHour.getText().toString();
                    String onMinute = editOnMinute.getText().toString();
                    if(TextUtils.isEmpty(onMinute)){
                        onMinute = "0";
                    }
                    String onSecond = editOnSecond.getText().toString();
                    if(TextUtils.isEmpty(onSecond)){
                        onSecond = "0";
                    }
                    String offHour = editOffHour.getText().toString();
                    String offMinute = editOffMinute.getText().toString();
                    if(TextUtils.isEmpty(offMinute)){
                        offMinute = "0";
                    }
                    String offSecond = editOffSecond.getText().toString();
                    if(TextUtils.isEmpty(offSecond)){
                        offSecond = "0";
                    }
                    if(TextUtils.isEmpty(onHour) || TextUtils.isEmpty(offHour)){
                        Snackbar.make(btnSave, "小时不可为空", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        timer.getOnTime().setHour(Integer.parseInt(onHour));
                        timer.getOnTime().setMinute(Integer.parseInt(onMinute));
                        timer.getOnTime().setSecond(Integer.parseInt(onSecond));
                        timer.getOffTime().setHour(Integer.parseInt(offHour));
                        timer.getOffTime().setMinute(Integer.parseInt(offMinute));
                        timer.getOffTime().setSecond(Integer.parseInt(offSecond));
                    }catch (Exception e){
                        e.printStackTrace();
                        Snackbar.make(btnSave, "内容必须是整数", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    List<Integer> weeks = new ArrayList<>();
                    boolean haveWeek = false;
                    for(int i=0; i<checkBoxes.length; i++){
                        if(checkBoxes[i].isChecked()){
                            haveWeek = true;
                            weeks.add(i);
                        }
                    }
                    if(!haveWeek){
                        Snackbar.make(btnSave, "星期不可不选", Snackbar.LENGTH_SHORT).show();
                        return;
                    }else{
                        int[] iWeeks = new int[weeks.size()];
                        timer.getWeekHelper().clean();
                        for(int i=0; i<weeks.size(); i++){
                            iWeeks[i] = weeks.get(i);
                        }
                        timer.getWeekHelper().setWeeks(iWeeks);
                    }
                    if(isAdd){
                        EditTimingActivity.timing.addZTimer(timer);
                        ZTimerDao zTimerDao = ZTimerDao.get(TimerActivity.this);
                        zTimerDao.add(timer, EditTimingActivity.timing.getId());
                        MyTimeDao myTimeDao = MyTimeDao.get(TimerActivity.this);
                        myTimeDao.add(timer.getOnTime(), timer.getId());
                        myTimeDao.add(timer.getOffTime(), timer.getId());

                        WeekHelperDao weekHelperDao = WeekHelperDao.get(TimerActivity.this);
                        weekHelperDao.add(timer.getWeekHelper());
                    }else{
                        ZTimerDao zTimerDao = ZTimerDao.get(TimerActivity.this);
                        zTimerDao.update(timer, null);
                        MyTimeDao myTimeDao = MyTimeDao.get(TimerActivity.this);
                        myTimeDao.update(timer.getOnTime(), timer.getId());
                        myTimeDao.update(timer.getOffTime(), timer.getId());
                        WeekHelperDao weekHelperDao = WeekHelperDao.get(TimerActivity.this);
                        weekHelperDao.update(timer.getWeekHelper());
                    }
                    if(null != EditTimingActivity.handler){
                        EditTimingActivity.handler.obtainMessage(EditTimingActivity.REFRESH_EVENT_HANDLER_LIST).sendToTarget();
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
