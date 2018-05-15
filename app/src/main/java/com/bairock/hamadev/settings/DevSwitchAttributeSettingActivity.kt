package com.bairock.hamadev.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bairock.hamadev.R
import com.bairock.iot.intelDev.device.Device
import com.bairock.iot.intelDev.device.Gear
import kotlinx.android.synthetic.main.activity_dev_switch_attribute_setting.*

class DevSwitchAttributeSettingActivity : AppCompatActivity() {

    companion object {
        var device : Device? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_switch_attribute_setting)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        init()
        setListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        device = null
    }

    fun init(){
            txtDevCoding.text = device!!.coding
            etxtAlisa.setText(device!!.alias)
            etxtName.setText(device!!.name)
            when (device!!.gear) {
                Gear.KAI -> rbGearKai.isChecked = true
                Gear.GUAN -> rbGearGuan.isChecked = true
                else -> rbGearZiDong.isChecked = true
            }
    }

    fun setListener(){
        btnSave.setOnClickListener {
            val alias = etxtAlisa.text.toString()
            val name = etxtName.text.toString()
            if(device!!.alias != alias){
                device!!.alias = alias
            }
            if(device!!.name != name){
                device!!.name = name
            }
            val gear: Gear = when(rgGear.checkedRadioButtonId){
                R.id.rbGearKai -> Gear.KAI
                R.id.rbGearGuan -> Gear.GUAN
                else -> Gear.ZIDONG
            }

            if(device!!.gear != gear){
                device!!.gear = gear
            }

            finish()
        }

        btnCancel.setOnClickListener{finish()}
    }
}
