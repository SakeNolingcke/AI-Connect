package com.example.aicommunication.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aicommunication.activity.MainActivity;
import com.example.aicommunication.tool.DBDao;
import com.example.aicommunication.tool.DBHelper;
import com.example.aicommunication.tool.DialogueManager;
import com.example.aicommunication.activity.HistoryActivity;
import com.example.aicommunication.activity.LoginActivity;
import com.example.aicommunication.activity.PersonActivity;
import com.example.aicommunication.R;
import com.example.aicommunication.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {
    SharedPreferences sp;
    User user_now;
    String user_id;
    SharedPreferences.Editor editor;
    DBDao dbDao;
    TextView name_tv_ps, tokens_used_tv;
    Button person_bt;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;

    public PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_person, container, false);
        }
        Switch swi = rootView.findViewById(R.id.switch_sp);
        sp = requireContext()
                .getSharedPreferences("AIConnect", Context.MODE_PRIVATE);
        editor = sp.edit();
        swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.switch_sp) {
                    editor.putBoolean("splash", isChecked);
                    editor.apply();
                }
            }
        });
        ((Switch) rootView.findViewById(R.id.switch_sp))
                .setChecked(sp.getBoolean("splash", true));

        rootView.findViewById(R.id.history_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

//        User数据库相关
        user_id = sp.getString("user_id", "");
        dbDao = new DBDao(requireContext(), new DBHelper(requireContext()));
        List<User> users = new ArrayList<>();
        users = dbDao.query();
        if (!user_id.isEmpty()) {
            for (int i = 0; i < users.size(); i++) {
                if (user_id.equals(users.get(i).getUser_id())) {
                    user_now = users.get(i);
                    break;
                }
            }
        }
        rootView.findViewById(R.id.person_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_now == null) {
                    Toast.makeText(requireContext(), "当前为游客账户，无法查看个人资料",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(requireContext(), PersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user_now);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        name_tv_ps = rootView.findViewById(R.id.name_tv_ps);
        tokens_used_tv = rootView.findViewById(R.id.tokens_used_tv);
        person_bt = rootView.findViewById(R.id.exit_button);
        if (user_id.isEmpty()) {
            person_bt.setText("登录");
            person_bt.setTextColor(Color.BLACK);
        } else {
            person_bt.setText("退出登录");
            person_bt.setTextColor(getResources().getColor(R.color.e40000, null));
        }
        person_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user_id.isEmpty())
                    showDialogueExit();
                else {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    private void showDialogueExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("确定要退出登录吗？");
        builder.setMessage("退出登录后将使用游客数据，当前用户数据将保存至本地！");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.remove("user_id");
                editor.commit();
                updateTokensUsed();
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

    public void updateTokensUsed() {
        user_id = sp.getString("user_id", "");
        DialogueManager dm = new DialogueManager(requireContext(), user_id);
        user_now = null;
        if (!user_id.isEmpty()) {
            dbDao = new DBDao(requireContext(), new DBHelper(requireContext()));
            List<User> users;
            users = dbDao.query();
            for (int i = 0; i < users.size(); i++) {
                if (user_id.equals(users.get(i).getUser_id())) {
                    user_now = users.get(i);
                    break;
                }
            }
        }
        if (user_now != null) {
            name_tv_ps.setText(user_now.getUser_name());
            String text = "已使用token数：" + dm.getTokens();
            tokens_used_tv.setText(text);
        } else {
            name_tv_ps.setText("游客");
            String string = "已使用token数：" + dm.getTokens();
            tokens_used_tv.setText(string);
        }
        if (user_id.isEmpty()) {
            person_bt.setText("登录");
            person_bt.setTextColor(Color.BLACK);
        } else {
            person_bt.setText("退出登录");
            person_bt.setTextColor(getResources().getColor(R.color.e40000, null));
        }
        if(dm.isNewFile){
            String src ="已使用token数：0";
            tokens_used_tv.setText(src);
            Toast.makeText(requireContext(), "会话已被清空，正在创建新会话", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity)requireActivity()).setVP2Item(1);
                }
            }).start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTokensUsed();
    }
}