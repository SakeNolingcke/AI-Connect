package com.example.aicommunication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aicommunication.tool.AIApiConnect;
import com.example.aicommunication.tool.DialogueManager;
import com.example.aicommunication.activity.HistoryActivity;
import com.example.aicommunication.Msg;
import com.example.aicommunication.adapter.MsgListAdapter;
import com.example.aicommunication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DialogueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogueFragment extends Fragment {
    public String file_name = "dialogues.json";
    public RecyclerView rc;
    MsgListAdapter mla;
    List<Msg> msg = new ArrayList<>();
    public EditText editText;
    public CardView cv;
    ImageButton imageButton;
    private TextView title_tv;
    AIApiConnect ai = new AIApiConnect();
    DialogueManager dm;
    String text;
    Handler my_handler = new Handler(Looper.getMainLooper());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    private boolean isFirst  =true;
    public boolean isFirstInput = true;

    public DialogueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DialogueFragment newInstance(String param1, String param2) {
        DialogueFragment fragment = new DialogueFragment();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences sp = requireContext()
                .getSharedPreferences("AIConnect", Context.MODE_PRIVATE);
        file_name = sp.getString("user_id", "dialogues.json");

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_dialogue, container, false);
        }
        editText = rootView.findViewById(R.id.editTextText);
        cv = rootView.findViewById(R.id.et_cv);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                imageButton.setEnabled(!s.toString().isEmpty());
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    text = editText.getText().toString();
                    if (text.isEmpty()) {
                        return true;
                    }
                    textExtracted();
                    return true;
                }
                return false;
            }
        });
        imageButton = rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText().toString();
                textExtracted();
            }
        });
        imageButton.setEnabled(false);

        rc = rootView.findViewById(R.id.msg_rcv);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        dm = new DialogueManager(DialogueFragment.this, ai);
        Log.i("", "isNewFile:" + dm.isNewFile);
        if (dm.isNewFile) {
            msg.add(new Msg("你好", Msg.OWNER_USER));
            dm.connectAi();
            isFirstInput = true;
            my_handler.post(connectAi);
        } else {
            setMsg(dm.putListMsg());
            if (msg.get(msg.size() - 1).getOwner() == Msg.OWNER_USER) {
                dm.connectAi();
                my_handler.post(connectAi);
            }
        }
        mla = new MsgListAdapter(requireContext(), msg);
        rc.setLayoutManager(llm);
        rc.setAdapter(mla);
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.my_anim);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.2f);
        rc.setLayoutAnimation(lac);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(300);
        defaultItemAnimator.setRemoveDuration(300);
        rc.setItemAnimator(defaultItemAnimator);

        rootView.findViewById(R.id.img_bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title_tv.getText().toString().contains("新建会话"))
                    Toast.makeText(requireContext(), "当前会话还没有使用！", Toast.LENGTH_SHORT).show();
                else
                    showDialogue(false);
            }
        });

        title_tv = rootView.findViewById(R.id.title_dia_tv);
        title_tv.setText(dm.getUser_id());

        rootView.findViewById(R.id.img_bt_ht).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistory();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            SharedPreferences sp = requireContext()
                    .getSharedPreferences("AIConnect", Context.MODE_PRIVATE);
            file_name = sp.getString("user_id", "dialogues.json");
            dm = new DialogueManager(DialogueFragment.this, ai);
            msg.clear();
            Log.i("", "isNewFile:" + dm.isNewFile);
            if (dm.isNewFile) {
                msg.add(new Msg("你好", Msg.OWNER_USER));
                dm.connectAi();
                isFirstInput = true;
                my_handler.post(connectAi);
            } else {
                setMsg(dm.putListMsg());
                if (msg.get(msg.size() - 1).getOwner() == Msg.OWNER_USER) {
                    dm.connectAi();
                    my_handler.post(connectAi);
                }
            }
            mla = new MsgListAdapter(requireContext(), msg);
            rc.setAdapter(mla);
            title_tv.setText(dm.getUser_id());
        }
        isFirst = false;
    }

    public void openHistory() {
        Intent intent = new Intent(requireContext(), HistoryActivity.class);
        requireContext().startActivity(intent);
    }


    private void textExtracted() {
        if (msg.get(msg.size() - 1).getOwner() == Msg.OWNER_AI
                && msg.get(msg.size() - 1).getContent().equals("加载中...")) {
            Toast.makeText(requireContext(), "正在加载数据，请稍后！"+msg.get(msg.size() - 1).getContent(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!text.isEmpty()) {
            editText.setText("");
            editText.clearFocus();
            hideSoftKeyboard(editText);
            if (dm.isTokensExceeded()) {
                showDialogue(true);
                return;
            }
            msg.add(new Msg(text, Msg.OWNER_USER));
            mla.notifyItemInserted(msg.size() - 1);
            mla.notifyItemChanged(msg.size() - 1);
            rc.scrollToPosition(msg.size() - 1);
            if(isFirstInput) {
                dm.setUser_id(text);
                title_tv.setText(text);
                isFirstInput = false;
            }
            dm.putJsonMsg(msg);
            dm.connectAi();
            my_handler.post(connectAi);
        }
    }

    private void showDialogue(boolean is_tokens_exceeded) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("提示");
        if (is_tokens_exceeded)
            builder.setMessage("这个会话的内容已经很多了，要新建一个吗？");
        else
            builder.setMessage("确定要新建一个会话吗？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dm.newDialogue()) {
                    int msg_length = msg.size();
                    msg.clear();
                    mla.notifyItemRangeRemoved(0, msg_length);
                    mla.notifyItemRangeChanged(0, msg_length);
                    msg.add(new Msg("你好", Msg.OWNER_USER));
                    title_tv.setText(dm.getUser_id());
                    dm.connectAi();
                    isFirstInput=true;
                    my_handler.post(connectAi);
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

    Runnable connectAi = new Runnable() {
        @Override
        public void run() {
            String src = ai.getText();
            int token = ai.getTokens();
            if (msg.get(msg.size() - 1).getOwner() == Msg.OWNER_USER) {
                msg.add(new Msg("加载中...", Msg.OWNER_AI));
                mla.notifyItemInserted(msg.size() - 1);
                mla.notifyItemChanged(msg.size() - 1);
                rc.scrollToPosition(msg.size() - 1);
            }
            if (src.isEmpty()) {
                my_handler.postDelayed(connectAi, 100);
            } else if (msg.get(msg.size() - 1).getOwner() == Msg.OWNER_AI) {
                msg.set(msg.size() - 1, new Msg(src, Msg.OWNER_AI));
                mla.notifyItemChanged(msg.size() - 1);
                rc.scrollToPosition(msg.size() - 1);
                dm.setTokens(token);
                dm.updateUserDB(file_name);
                dm.putJsonMsg(msg);
                dm.rewriteJsonFile();
                ai.setTextNull();
                my_handler.removeCallbacks(connectAi);
            }
        }

    };

    public void setMsg(List<Msg> msg) {
        if (!this.msg.isEmpty()) {
            this.msg.clear();
        }
        this.msg.addAll(msg);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}