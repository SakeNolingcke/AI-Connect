package com.example.aicommunication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aicommunication.R;
import com.example.aicommunication.User;
import com.example.aicommunication.tool.DBDao;
import com.example.aicommunication.tool.DBHelper;
import com.example.aicommunication.tool.LoginDialog;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    DBDao dbDao;
    SharedPreferences.Editor editor;
    List<User> users;
    Button lg_button;
    ImageButton img_back;
    public EditText et_lg_user, et_lg_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = getSharedPreferences("AIConnect", MODE_PRIVATE);
        editor = sp.edit();
        dbDao = new DBDao(this, new DBHelper(this));
        users = dbDao.query();
        img_back = findViewById(R.id.img_bt_back_lg);
        lg_button = findViewById(R.id.lg_button);
        et_lg_user = findViewById(R.id.et_lg_user);
        et_lg_pw = findViewById(R.id.et_lg_pw);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lg_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                if (loginSuccess()) {
                    editor.putString("user_id", et_lg_user.getText().toString());
                    editor.putString("user_id", et_lg_user.getText().toString());
                    editor.commit();
                    finish();
                }
            }
        });
    }

    private boolean loginSuccess() {
        String user = et_lg_user.getText().toString();
        String password = et_lg_pw.getText().toString();
        if (!user.isEmpty() && !password.isEmpty()) {
            for (int i = 0; i < users.size(); i++) {
                if (user.equals(users.get(i).getUser_id())) {
                    String pw = users.get(i).getPassword();
                    if (password.length() >= 8 && password.equals(pw)) {
                        return true;
                    } else {
                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            showDialogRegis();
        } else {
            if (user.isEmpty()) {
                et_lg_user.setError("该字段为空");
            }
            if (password.isEmpty()) {
                et_lg_pw.setError("该字段为空");
            }
            Toast.makeText(this, "请输入完整", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void showDialogRegis() {
        LoginDialog ld = new LoginDialog(this,
                et_lg_user.getText().toString(),
                et_lg_pw.getText().toString());
        ld.show();
    }

}