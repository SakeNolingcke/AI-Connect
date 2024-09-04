package com.example.aicommunication.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "user.db";//数据库名称
    private static final String TABLE_NAME = "users";
    public static final int DB_VERSION = 1;//数据库版本
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSQL = "create table " + TABLE_NAME
                + "(user_ID varchar(15) primary key ,"
                + "user_name text,"
                + "password text,"
                + " tokens_used Integer)";
        db.execSQL(strSQL);
        String sql = "insert into " +TABLE_NAME
                +" values('2021404500529','刘家缘','1831968915','0')";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
