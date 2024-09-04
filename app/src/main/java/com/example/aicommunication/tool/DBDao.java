package com.example.aicommunication.tool;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.aicommunication.User;

import java.util.ArrayList;
import java.util.List;

public class DBDao {
    private final Context context;
    public final DBHelper dbHelper;

    public DBDao(Context context,DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    /**
     * 数据库插入数据
     *
     * @param bean 实体类
     * @param <T>  T
     */
    public synchronized <T> void insert(T bean) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            if (bean instanceof User) {
                User user = (User) bean;
                ContentValues cv = new ContentValues();
                cv.put("user_name", user.getUser_name());
                cv.put("user_id", user.getUser_id());
                cv.put("password", user.getPassword());
                cv.put("tokens_used", user.getTokens_used());
                db.insert(dbHelper.getTableName(), null, cv);
            }
        } catch (Exception e) {
            Log.e("DBDao.insert", "数据插入失败");
            Toast.makeText(context, "数据添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除表中某行数据
     */
    public synchronized void delete(String user_id) {

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            String sql = "delete from " + dbHelper.getTableName()
                    + " where user_ID =" + user_id;
            db.execSQL(sql);
        } catch (Exception e) {
            Log.e("DBDao.clearAll", "删除某行数据失败");
        }
    }

    /**
     * 查询数据
     *
     * @return List
     */
    @SuppressLint("Range")
    public List<User> query() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> list = new ArrayList<>();
        String querySql = "select * from " + dbHelper.getTableName();
        try {
            try (Cursor cursor = db.rawQuery(querySql, null)) {
                while (cursor.moveToNext()) {
                    User user = new User(
                            cursor.getString(cursor.getColumnIndex("user_name")),
                            cursor.getString(cursor.getColumnIndex("user_ID")),
                            cursor.getString(cursor.getColumnIndex("password")),
                            cursor.getInt(cursor.getColumnIndex("tokens_used"))
                    );
                    list.add(user);
                    Log.e("DBDao.query", "数据:"+user.getUser_name());
                }
            }
        } catch (Exception e) {
            Log.e("DBDao.query", "数据查询失败");
        }
        return list.isEmpty() ? null : list;
    }
}
