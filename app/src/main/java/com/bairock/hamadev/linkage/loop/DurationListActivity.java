package com.bairock.hamadev.linkage.loop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bairock.hamadev.R;
import com.bairock.hamadev.adapter.AdapterDurationList;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.LoopDurationDao;
import com.bairock.iot.intelDev.linkage.loop.LoopDuration;

import java.lang.ref.WeakReference;

public class DurationListActivity extends AppCompatActivity {

    public static final int REFRESH_DURATION_LIST = 1;
    public static MyHandler handler;
    private ListView listViewDuration;
    private AdapterDurationList adapterDurationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_list);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
        setListener();
        setListViewDuration();

        handler = new MyHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loop_duration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                break;
            case R.id.act_add:
                startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews(){
        listViewDuration = (ListView)findViewById(R.id.list_duration);
    }

    private void setListener(){
        listViewDuration.setOnItemClickListener(onItemClickListener);
        listViewDuration.setOnItemLongClickListener(onItemLongClickListener);
    }

    private void setListViewDuration(){
        adapterDurationList = new AdapterDurationList(this, EditLoopActivity.zLoop.getListLoopDuration());
        listViewDuration.setAdapter(adapterDurationList);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DurationActivity.duration = EditLoopActivity.zLoop.getListLoopDuration().get(position);
            startActivity(new Intent(DurationListActivity.this, DurationActivity.class));
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
            new AlertDialog.Builder(DurationListActivity.this).setTitle("警告")
                    .setMessage("确定删除该循环时间吗？")
                    .setPositiveButton(MainActivity.strEnsure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoopDuration loopDuration = EditLoopActivity.zLoop.getListLoopDuration().get(position);
                            loopDuration.setDeleted(true);
                            EditLoopActivity.zLoop.removeLoopDuration(position);
                            LoopDurationDao loopDurationDao = LoopDurationDao.get(DurationListActivity.this);
                            //loopDurationDao.update(loopDuration, null);
                            loopDurationDao.delete(loopDuration);
                            adapterDurationList.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(MainActivity.strCancel, null)
                    .show();
            return true;
        }
    };

    public static class MyHandler extends Handler {
        WeakReference<DurationListActivity> mActivity;

        MyHandler(DurationListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            final DurationListActivity theActivity = mActivity.get();
            switch (msg.arg1) {
                case REFRESH_DURATION_LIST:
                    theActivity.adapterDurationList.notifyDataSetChanged();
                    break;
            }

        }
    }
}
