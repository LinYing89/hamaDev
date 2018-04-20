package com.bairock.hamadev.linkage.guagua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bairock.hamadev.R;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.database.EffectDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.GuaguaMouth;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.ArrayList;
import java.util.List;

public class EditGuaguaEffectActivity extends AppCompatActivity {

    private Effect effectGuagua;

    private List<Device> listOtherDevice;

    public static boolean ADD;

    private Spinner spinnerDevices;
    private EditText etxtSpeakCount;
    private EditText etxtSpeakContent;
    private Button btnOk;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guagua_effect);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListeners();

        //listOtherDevice = new SceneActHelper().getListOtherEle(GuaguaHandler.getIns().getSelectedGuagua().getTrigger());
        listOtherDevice = getListOtherGuanguanMouth();
        if(listOtherDevice.isEmpty()){
            Toast.makeText(this, "无呱呱嘴设备", Toast.LENGTH_SHORT).show();
            btnOk.setEnabled(false);
            return;
        }
        if(!ADD) {
            effectGuagua = EditGuaguaActivity.effect;
            listOtherDevice.add(effectGuagua.getDevice());
            setSpinnerDevices();
            spinnerDevices.setSelection(listOtherDevice.size() - 1);
            etxtSpeakCount.setText(String.valueOf(effectGuagua.getEffectCount()));
            etxtSpeakContent.setText(effectGuagua.getEffectContent());
        }else{
            setSpinnerDevices();
            spinnerDevices.setSelection(0);
        }
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
        spinnerDevices = (Spinner)findViewById(R.id.spinnerDevice);
        etxtSpeakCount = (EditText)findViewById(R.id.etxtSpeakCount);
        etxtSpeakContent = (EditText)findViewById(R.id.etxtSpeakContent);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancel = (Button)findViewById(R.id.btnCancel);
    }

    private void setListeners(){
        spinnerDevices.setOnItemSelectedListener(onItemSelectedListener);
        btnOk.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void setSpinnerDevices(){
        String[] names =new String[listOtherDevice.size()];
        for (int i=0; i< listOtherDevice.size(); i++){
            Device device = listOtherDevice.get(i);
            names[i] = device.getName();
        }
        spinnerDevices.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,names));
    }

    /**
     * 获取trigger中没有的呱呱嘴设备
     * @return
     */
    public List<Device> getListOtherGuanguanMouth(){
        List<GuaguaMouth> listEleAll = HamaApp.DEV_GROUP.findListGuaguaMouth(true);
        List<Device> listOther = new ArrayList<>();
        List<Effect> listEle = EditGuaguaActivity.subChain.getListEffect();
        for (Device device : listEleAll) {
            boolean haved = false;
            for(Effect effect : listEle){
                if(effect.getDevice() == device){
                    haved = true;
                }
            }
            if(!haved){
                listOther.add(device);
            }
        }
        return listOther;
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(null == effectGuagua) {
                effectGuagua = new Effect();
            }
            effectGuagua.setDevice(listOtherDevice.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnOk:
                    if(null == effectGuagua) {
                        effectGuagua = new Effect();
                    }
                    effectGuagua.setDevice(listOtherDevice.get(spinnerDevices.getSelectedItemPosition()));
                    int count = Integer.parseInt(etxtSpeakCount.getText().toString());
                    effectGuagua.setEffectCount(count);
                    String content = etxtSpeakContent.getText().toString();
                    if(content.length() > 20){
                        Toast.makeText(EditGuaguaEffectActivity.this, "播报内容长度不能超过20", Toast.LENGTH_LONG).show();
                        return;
                    }else if(content.isEmpty()){
                        Toast.makeText(EditGuaguaEffectActivity.this, "播报内容不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }
                    effectGuagua.setEffectContent(content);
                    if(ADD){
                        EditGuaguaActivity.subChain.addEffect(effectGuagua);
                        EffectDao.get(EditGuaguaEffectActivity.this).add(effectGuagua, EditGuaguaActivity.subChain.getId());
                    }else{
                        EffectDao.get(EditGuaguaEffectActivity.this).update(effectGuagua, null);
                    }
                    if(EditGuaguaActivity.handler != null){
                        EditGuaguaActivity.handler.obtainMessage(EditGuaguaActivity.REFRESH_DEVICE_LIST).sendToTarget();
                    }
                    finish();
                    break;
                case R.id.btnCancel:
                    finish();
                    break;
            }
        }
    };
}
