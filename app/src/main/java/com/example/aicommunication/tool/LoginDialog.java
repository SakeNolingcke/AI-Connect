package com.example.aicommunication.tool;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aicommunication.R;
import com.example.aicommunication.User;

public class LoginDialog extends Dialog {
    Activity activity;
    SharedPreferences.Editor editor;
    private final String user, pw;

    public LoginDialog(@NonNull Activity activity, String user, String pw) {
        super(activity);
        this.activity = activity;
        SharedPreferences sp = activity.getSharedPreferences("AIConnect", Context.MODE_PRIVATE);
        editor = sp.edit();
        this.user = user;
        this.pw = pw;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        //将背景设置为透明
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        //设置view居中
        if (getWindow() != null) {
            getWindow().setGravity(Gravity.CENTER);
        }

        EditText text = findViewById(R.id.et_jym);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("LJYQM2024")) {
                    editor.putString("user_id", user);
                    editor.commit();
                    User new_user = new User("霏霜雾霞", user, pw, 0);
                    DBDao dbDao = new DBDao(activity,new DBHelper(activity));
                    dbDao.insert(new_user);
                    activity.finish();
                } else {
                    Toast.makeText(activity, "校验码输入错误", Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, "如需获取请资讯管理员", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
