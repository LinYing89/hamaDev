package com.bairock.hamadev.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bairock.hamadev.linkage.ChainFragment;
import com.bairock.hamadev.linkage.guagua.GuaguaFragment;
import com.bairock.hamadev.linkage.loop.LoopFragment;
import com.bairock.hamadev.linkage.timing.TimingFragment;

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

        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = ChainFragment.newInstance(position);
                break;
            case 1:
                fragment = TimingFragment.newInstance(position);
                break;
            case 2:
                fragment = LoopFragment.newInstance(position);
                break;
            case 3:
                fragment = GuaguaFragment.newInstance(position);
                break;
            default:
                fragment = ChainFragment.newInstance(position);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
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
