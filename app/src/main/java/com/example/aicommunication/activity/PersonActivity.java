package com.example.aicommunication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

import java.util.List;

public class PersonActivity extends AppCompatActivity {
    DBDao dbDao;
    List<User> users;
    User user;
    EditText et_un, et_pw, et_pw2;
    TextView tv_tk, tv_ui;
    Button person_done_bt;
    private final View.OnClickListener doneCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!et_un.getText().toString().isEmpty()) {
                user.setUser_name(et_un.getText().toString());
                dbDao.delete(user.getUser_id());
                dbDao.insert(user);
                et_un.setHint(user.getUser_name());
                et_un.setText("");
                Toast.makeText(PersonActivity.this,
                        "昵称修改成功", Toast.LENGTH_SHORT).show();
            } else if (et_pw.getText().toString().isEmpty()
                    && et_pw2.getText().toString().isEmpty()) {
                Toast.makeText(PersonActivity.this,
                        "请输入修改的内容", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!et_pw.getText().toString().isEmpty() && et_pw.getText().toString().equals(
                    et_pw2.getText().toString())) {
                if(et_pw.getText().toString().equals(user.getPassword())){
                    Toast.makeText(PersonActivity.this,
                            "请输入新的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialogSetPW();
            } else
                Toast.makeText(PersonActivity.this,
                        "两次密码输入不一样", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_person);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = (User) getIntent().getSerializableExtra("user");
        initView();
        dbDao = new DBDao(this, new DBHelper(this));
        users = dbDao.query();
    }

    private void initView() {
        et_un = findViewById(R.id.et_un);
        tv_ui = findViewById(R.id.tv_ui);
        et_pw = findViewById(R.id.et_pw);
        et_pw2 = findViewById(R.id.et_pw2);
        tv_tk = findViewById(R.id.tv_tk);
        person_done_bt = findViewById(R.id.person_done_bt);
        person_done_bt.setOnClickListener(doneCL);
        findViewById(R.id.img_bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_un.setHint(user.getUser_name());
        tv_ui.setText(user.getUser_id());
        String src = "tokens:" + user.getTokens_used();
        tv_tk.setText(src);
    }

    private void showDialogSetPW() {
        EditText text = new EditText(this);
        text.setHint("请输入原密码");
        text.setSingleLine(true);
        text.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        text.setMaxEms(20);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确定修改密码吗？");
        builder.setMessage("修改密码需要输入原密码：");
        builder.setView(text);
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (text.getText().toString().equals(user.getPassword())) {
                    user.setPassword(et_pw.getText().toString());
                    dbDao.delete(user.getUser_id());
                    dbDao.insert(user);
                    Toast.makeText(PersonActivity.this,
                            "密码修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonActivity.this,
                            "原密码输入错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();//这个方法可以返回一个alertDialog对象
        alertDialog.show();
    }
}