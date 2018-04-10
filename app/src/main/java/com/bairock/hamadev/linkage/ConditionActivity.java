package com.bairock.hamadev.linkage;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.iot.intelDev.device.CompareSymbol;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.linkage.LinkageCondition;
import com.bairock.iot.intelDev.linkage.ZLogic;

import java.util.ArrayList;
import java.util.List;

public class ConditionActivity extends AppCompatActivity {

    public static final int ADD_CONDITION = 3;
    public static final int UPDATE_CONDITION = 4;

    public static boolean ADD = false;
    public static LinkageCondition condition;
    public static Handler handler;

    private Spinner spinnerLogic;
    private Spinner spinnerDevice;
    private Spinner spinnerSymbol;
    private Spinner spinnerValue;
    private EditText editValue;
    private Button btnSave;
    private Button btnCancel;

    private List<Device> listDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();

        if(ADD){
            condition = new LinkageCondition();
        }

        init();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findViews(){
        spinnerLogic = (Spinner)findViewById(R.id.spinnerLogic);
        spinnerDevice = (Spinner)findViewById(R.id.spinnerDevice);
        spinnerSymbol = (Spinner)findViewById(R.id.spinnerSymbol);
        spinnerValue = (Spinner)findViewById(R.id.spinnerValue);
        editValue = (EditText)findViewById(R.id.etxtValue);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
    }

    private void setListener(){
        spinnerLogic.setOnItemSelectedListener(styleOnItemSelectedListener);
        spinnerDevice.setOnItemSelectedListener(deviceOnItemSelectedListener);
        spinnerSymbol.setOnItemSelectedListener(symbolOnItemSelectedListener);
        spinnerValue.setOnItemSelectedListener(valueOnItemSelectedListener);
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void init(){
        listDevice = new ArrayList<>();
        listDevice.addAll(HamaApp.DEV_GROUP.findListIStateDev(true));
        listDevice.addAll(HamaApp.DEV_GROUP.findListCollectDev());
        List<String> listDeviceName = new ArrayList<>();
        for(Device device : listDevice){
            listDeviceName.add(device.getName());
        }
        spinnerDevice.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,listDeviceName));

        if(condition.getLogic() == ZLogic.AND){
            spinnerLogic.setSelection(0);
        }else{
            spinnerLogic.setSelection(1);
        }

        int iDevice = listDevice.indexOf(condition.getDevice());
        iDevice = iDevice == -1 ? 0 : iDevice;
        spinnerDevice.setSelection(iDevice);

        if(condition.getDevice() instanceof IStateDev){
            showElectricalStyle();
        }else{
            showClimateStyle();
        }
    }

    private void showElectricalStyle(){
        spinnerValue.setVisibility(View.VISIBLE);
        editValue.setVisibility(View.GONE);
        spinnerSymbol.setSelection(1);
        spinnerSymbol.setEnabled(false);
        condition.setCompareSymbol(CompareSymbol.EQUAL);
        condition.setCompareValue(0);
        spinnerValue.setSelection((int)condition.getCompareValue());
    }

    private void showClimateStyle(){
        spinnerValue.setVisibility(View.GONE);
        editValue.setVisibility(View.VISIBLE);
        setSpinnerSymbol();
        spinnerSymbol.setEnabled(true);
        editValue.setText(String.valueOf(condition.getCompareValue()));
    }

    private void setSpinnerSymbol(){
        if(condition.getCompareSymbol() == CompareSymbol.GREAT){
            spinnerSymbol.setSelection(0);
        }else if(condition.getCompareSymbol() == CompareSymbol.EQUAL){
            spinnerSymbol.setSelection(1);
        }else {
            spinnerSymbol.setSelection(2);
        }
    }

    /**
     * 方式选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener styleOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == condition){
                return;
            }
            if(position == 0){
                condition.setLogic(ZLogic.AND);
            }else{
                condition.setLogic(ZLogic.OR);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 设备选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener deviceOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == condition){
                return;
            }
            Device device = listDevice.get(position);
            condition.setDevice(device);
            if(device instanceof IStateDev){
                showElectricalStyle();
            }else{
                showClimateStyle();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 比较符号选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener symbolOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == condition || condition.getDevice() == null){
                return;
            }
            if(condition.getDevice() instanceof IStateDev){
                spinnerSymbol.setSelection(1);
                condition.setCompareSymbol(CompareSymbol.EQUAL);
            }else{
                if(position == 0){
                    condition.setCompareSymbol(CompareSymbol.GREAT);
                }else if(position == 1){
                    condition.setCompareSymbol(CompareSymbol.EQUAL);
                }else {
                    condition.setCompareSymbol(CompareSymbol.LESS);
                }
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * 值选择事件，ADD/OR
     */
    private AdapterView.OnItemSelectedListener valueOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == condition || condition.getDevice() == null){
                return;
            }
            condition.setCompareValue(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_save:
                    if(condition.getDevice() != null){
                        try {
                            if(condition.getDevice() instanceof DevCollect) {
                                condition.setCompareValue(Integer.parseInt(editValue.getText().toString()));
                            }
                            if(ADD){
                                if(null != handler){
                                    handler.obtainMessage(ADD_CONDITION, condition).sendToTarget();
                                }
                            }else{
                                if(null != handler){
                                    handler.obtainMessage(UPDATE_CONDITION, condition).sendToTarget();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finish();
                    }else{
                        Snackbar.make(btnSave, "设备不能为空", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
            }
        }
    };

}
