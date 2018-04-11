package com.bairock.hamadev.settings;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;

import com.bairock.hamadev.R;
import com.bairock.hamadev.database.CollectPropertyDao;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;

public class DevCollectSettingActivity extends AppCompatActivity {

    public static DevCollectSignal devCollectSignal;
    private CollectProperty collectProperty;

    private EditText etxtWeiHao;
    private EditText etxtName;
    private Spinner spinnerSignalSource;
    private EditText etxtUnit;
    private TableRow tabrow_Aa_Ab;
    private EditText etxtAa;
    private EditText etxtAb;
    private TableRow tabrow_a_b;
    private EditText etxta;
    private EditText etxtb;
    private EditText etxtFormula;
    private EditText etxtCalibration;
    private Button btnCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_collect_setting);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
        if(null == devCollectSignal){
            finish();
            return;
        }
        collectProperty = devCollectSignal.getCollectProperty();

        init();
        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean updateDev = false;
        boolean updatePropety = false;

        String alias = etxtWeiHao.getText().toString();
        String name = etxtName.getText().toString();
        if(!alias.equals(devCollectSignal.getAlias())){
            updateDev = true;
            devCollectSignal.setAlias(alias);
        }
        if(!name.equals(devCollectSignal.getName())){
            updateDev = true;
            devCollectSignal.setName(name);
        }

        String unit = etxtUnit.getText().toString();
        float Aa = Float.parseFloat(etxtAa.getText().toString());
        float Ab = Float.parseFloat(etxtAb.getText().toString());
        float a = Float.parseFloat(etxta.getText().toString());
        float b = Float.parseFloat(etxtb.getText().toString());
        String formula = etxtFormula.getText().toString();
        float calibration = Float.parseFloat(etxtCalibration.getText().toString());
        if(!unit.equals(collectProperty.getUnitSymbol())){
            updatePropety = true;
            collectProperty.setUnitSymbol(unit);
        }
        if(Aa != collectProperty.getLeastReferValue()){
            updatePropety = true;
            collectProperty.setLeastReferValue(Aa);
        }
        if(Ab != collectProperty.getCrestReferValue()){
            updatePropety = true;
            collectProperty.setCrestReferValue(Ab);
        }
        if(a != collectProperty.getLeastValue()){
            updatePropety = true;
            collectProperty.setLeastValue(a);
        }
        if(b != collectProperty.getCrestValue()){
            updatePropety = true;
            collectProperty.setCrestValue(b);
        }
        if(!formula.equals(collectProperty.getFormula())){
            updatePropety = true;
            collectProperty.setFormula(formula);
        }
        if(calibration != collectProperty.getCalibrationValue()){
            updatePropety = true;
            collectProperty.setCalibrationValue(calibration);
        }

        if(updateDev){
            DeviceDao deviceDao = DeviceDao.get(this);
            deviceDao.update(devCollectSignal);
        }
        if(updatePropety){
            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(this);
            collectPropertyDao.update(collectProperty);
        }

        collectProperty = null;
        devCollectSignal = null;
    }

    private void findViews(){
        etxtWeiHao = (EditText)findViewById(R.id.etxtWeiHao);
        etxtName = (EditText)findViewById(R.id.etxtName);
        etxtUnit = (EditText)findViewById(R.id.etxtUnit);
        etxtAa = (EditText)findViewById(R.id.etxtAa);
        etxtAb = (EditText)findViewById(R.id.etxtAb);
        etxta = (EditText)findViewById(R.id.etxta);
        etxtb = (EditText)findViewById(R.id.etxtb);
        etxtFormula = (EditText)findViewById(R.id.etxtFormula);
        etxtCalibration = (EditText)findViewById(R.id.etxtCalibration);
        spinnerSignalSource = (Spinner) findViewById(R.id.spinnerSignalSource);
        btnCalibration = (Button) findViewById(R.id.btnCalibration);

        tabrow_Aa_Ab = (TableRow)findViewById(R.id.tabrow_Aa_Ab);
        tabrow_a_b = (TableRow)findViewById(R.id.tabrow_a_b);
    }

    private void setListener(){
        spinnerSignalSource.setOnItemSelectedListener(onItemSelectedListener);
        btnCalibration.setOnClickListener(onClickListener);
    }

    private void init(){
        etxtWeiHao.setText(devCollectSignal.getAlias());
        etxtName.setText(devCollectSignal.getName());
        etxtUnit.setText(collectProperty.getUnitSymbol());
        etxtAa.setText(String.valueOf(collectProperty.getLeastReferValue()));
        etxtAb.setText(String.valueOf(collectProperty.getCrestReferValue()));
        etxta.setText(String.valueOf(collectProperty.getLeastValue()));
        etxtb.setText(String.valueOf(collectProperty.getCrestValue()));
        etxtCalibration.setText(String.valueOf(collectProperty.getCalibrationValue()));
        etxtFormula.setText(collectProperty.getFormula());
        spinnerSignalSource.setSelection(collectProperty.getCollectSrc().ordinal());

        initSourceLayout();
    }

    private void initSourceLayout(){
        switch (collectProperty.getCollectSrc()){
            case DIGIT:
                tabrow_Aa_Ab.setVisibility(View.VISIBLE);
                tabrow_a_b.setVisibility(View.GONE);
                break;
            case ELECTRIC_CURRENT:
                tabrow_Aa_Ab.setVisibility(View.VISIBLE);
                tabrow_a_b.setVisibility(View.GONE);
                break;
            case VOLTAGE:
                tabrow_Aa_Ab.setVisibility(View.VISIBLE);
                tabrow_a_b.setVisibility(View.VISIBLE);
                break;
            case SWITCH:
                tabrow_Aa_Ab.setVisibility(View.GONE);
                tabrow_a_b.setVisibility(View.GONE);
                break;
        }
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    collectProperty.setCollectSrc(CollectSignalSource.DIGIT);
                    break;
                case 1:
                    collectProperty.setCollectSrc(CollectSignalSource.ELECTRIC_CURRENT);
                    break;
                case 2:
                    collectProperty.setCollectSrc(CollectSignalSource.VOLTAGE);
                    break;
                case 3:
                    collectProperty.setCollectSrc(CollectSignalSource.SWITCH);
                    break;
            }
            initSourceLayout();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onClickListener = v -> {
        String value = etxtCalibration.getText().toString();
        if(value.isEmpty()){
            Snackbar.make(btnCalibration, "标定值不可为空!", Snackbar.LENGTH_SHORT).show();
        }
        try{
            float fValue = Float.parseFloat(value);
            String order = devCollectSignal.createCalibrationOrder(fValue);
            DevChannelBridgeHelper.getIns().sendDevOrder(devCollectSignal, order);
        }catch (Exception e){
            Snackbar.make(btnCalibration, "标定值包含非法字符!", Snackbar.LENGTH_SHORT).show();
        }

    };
}
