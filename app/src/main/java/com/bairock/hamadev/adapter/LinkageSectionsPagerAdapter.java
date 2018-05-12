package com.bairock.hamadev.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bairock.hamadev.linkage.ChainFragment2;
import com.bairock.hamadev.linkage.guagua.GuaguaFragment2;
import com.bairock.hamadev.linkage.loop.LoopFragment2;
import com.bairock.hamadev.linkage.timing.TimingFragment2;

/**
 *
 * Created by Administrator on 2017/9/10.
 */

public class LinkageSectionsPagerAdapter extends FragmentPagerAdapter {

    public LinkageSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        switch (position){
            case 0 :
                fragment = ChainFragment2.newInstance(position);
                break;
            case 1:
                fragment = TimingFragment2.newInstance(position);
                break;
            case 2:
                fragment = LoopFragment2.newInstance(position);
                break;
            case 3:
                fragment = GuaguaFragment2.newInstance(position);
                break;
            default:
                fragment = ChainFragment2.newInstance(position);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "连锁";
            case 1:
                return "定时";
            case 2:
                return "循环";
            case 3:
                return "呱呱";
        }
        return null;
    }

}
