package com.bairock.hamadev.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.ContactsContract;

import com.bairock.hamadev.database.DbSb.TabUser;
import com.bairock.iot.intelDev.user.User;

import java.util.Date;

/**
 *
 * Created by Administrator on 2017/8/8.
 */

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        Long id = getLong(getColumnIndex(TabUser.Cols.ID));
        String email = getString(getColumnIndex(TabUser.Cols.EMAIL));
        String name = getString(getColumnIndex(TabUser.Cols.NAME));
        String petName = getString(getColumnIndex(TabUser.Cols.PET_NAME));
        String psd = getString(getColumnIndex(TabUser.Cols.PSD));
        Long date = getLong(getColumnIndex(TabUser.Cols.REGISTER_TIME));
        String tel = getString(getColumnIndex(TabUser.Cols.TEL));
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setPetName(petName);
        user.setPsd(psd);
        user.setRegisterTime(new Date(date));
        user.setTel(tel);
        return user;
    }
}
