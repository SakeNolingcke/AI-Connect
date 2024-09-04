package com.example.aicommunication.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aicommunication.DialogueList;
import com.example.aicommunication.tool.DialogueManager;
import com.example.aicommunication.adapter.ListViewAdapter;
import com.example.aicommunication.tool.LoadingDialog;
import com.example.aicommunication.R;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TGA = "HistoryActivity";
    private ListView lv;
    DialogueManager dm;
    private List<DialogueList> lists;
    private ListViewAdapter lva;
    private LoadingDialog dialog;
    private final Handler myHandler = new Handler(Looper.getMainLooper());
    private int sleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lv = findViewById(R.id.listView);
        SharedPreferences sp = getSharedPreferences("AIConnect", Context.MODE_PRIVATE);
        dm = new DialogueManager(this,
                sp.getString("user_id","dialogues.json"));
        lists = dm.getDialogueLists();
        lva = new ListViewAdapter(this, R.layout.list_item, lists, deleteDialogue);
        lv.setAdapter(lva);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < lists.size()) {
                    if (position != dm.position) {
                        dm.setPosition(position);
                        dm.rewriteJsonFile();
                        lists.clear();
                        myHandler.post(deleteDialogueRun);
                    }else {
                        finish();
                    }
                }
            }
        });
        findViewById(R.id.img_bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.delete_all_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dm.deleteDialogue(-1);
                dm.rewriteJsonFile();
                lists.clear();
                lva.notifyDataSetChanged();
                myHandler.post(deleteDialogueRun);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_anim);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lac.setDelay(0.2f);
        lv.setLayoutAnimation(lac);
    }

    View.OnClickListener deleteDialogue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            dm.deleteDialogue(position);
            dm.rewriteJsonFile();
            lists.remove(position);
            lva.notifyDataSetChanged();
            myHandler.post(deleteDialogueRun);
        }
    };

    Runnable deleteDialogueRun = new Runnable() {
        @Override
        public void run() {
            if(dialog==null) {
                dialog = new LoadingDialog(HistoryActivity.this);
                dialog.show();
            }
            if (sleep <= 10) {
                sleep++;
                myHandler.postDelayed(deleteDialogueRun, 100);
                return;
            }
            sleep = 0;
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog=null;
            }
            if (lists.isEmpty())
                finish();
        }
    };
}