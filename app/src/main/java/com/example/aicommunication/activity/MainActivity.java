package com.example.aicommunication.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aicommunication.R;
import com.example.aicommunication.adapter.Vp2FmAdapter;
import com.example.aicommunication.fragment.DialogueFragment;
import com.example.aicommunication.fragment.HomeFragment;
import com.example.aicommunication.fragment.PersonFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static ViewPager2 vp2fm;
    private CardView cv;
    RecyclerView rc;
    EditText editText;
    Vp2FmAdapter vp2FmA;
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> nav_tags = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vp2fm = findViewById(R.id.viewPager2);
        fragments.add(new HomeFragment(vp2fm));
        DialogueFragment fgm_dlg = new DialogueFragment();
        fragments.add(fgm_dlg);
        PersonFragment fgm_ps = new PersonFragment();
        fragments.add(fgm_ps);
        vp2FmA = new Vp2FmAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        vp2fm.setUserInputEnabled(false);
        vp2fm.setAdapter(vp2FmA);
        LinearLayout status_bar = findViewById(R.id.status_bar);
        vp2fm.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 1)
                    status_bar.setBackgroundColor(getResources()
                            .getColor(R.color.white, null));
                else
                    status_bar.setBackgroundColor(getResources()
                            .getColor(R.color.bk_fgm, null));
            }
        });

        TabLayout tbl = findViewById(R.id.tabLayout_main);
        nav_tags.add("首页");
        nav_tags.add("对话");
        nav_tags.add("个人");
        new TabLayoutMediator(tbl, vp2fm, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                if (i >= 0 && i < nav_tags.size())
                    tab.setText(nav_tags.get(i));
                Log.d("MainActivity", "No." + i + " tab:" + tab.getText());
            }
        }).attach();
        registerHideKeyBoard(fgm_dlg);
    }

    //使editText点击外部时候失去焦点
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) { //点击editText控件外部
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    InputMethodManager imm2 = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm2.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    editText.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            editText = (EditText) v;
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    private int oldDiff = 0;

    private void registerHideKeyBoard(DialogueFragment fgm_dlg) {
        final View activityRoot = getWindow().getDecorView();
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private final Rect r = new Rect();
                    ViewGroup.MarginLayoutParams lp = null;
                    int marginBottom;

                    @Override
                    public void onGlobalLayout() {
                        if (cv == null || rc == null) {
                            cv = fgm_dlg.cv;
                            rc = fgm_dlg.rc;
                        }
                        if (rc != null && lp == null) {
                            lp = (ViewGroup.MarginLayoutParams) rc.getLayoutParams();
                            marginBottom = lp.bottomMargin;
                        }
                        activityRoot.getWindowVisibleDisplayFrame(r);
                        int diff = activityRoot.getRootView().getHeight() - r.height();
                        //键盘是否弹出
                        boolean isOpen = (diff > 200);
                        if (diff != oldDiff) {
                            Log.d("keyboard", "keyboard open: " + isOpen);
                            oldDiff = diff;
                            if (cv != null)
                                if (isOpen) {
                                    cv.setTranslationY(-560);
                                    if (lp != null && rc != null) {
                                        lp.setMargins(lp.getMarginStart(),
                                                lp.topMargin,
                                                lp.getMarginEnd(),
                                                marginBottom + 560);
                                        rc.setLayoutParams(lp);
                                    }
                                } else {
                                    cv.setTranslationY(0);
                                    if (lp != null && rc != null) {
                                        lp.setMargins(lp.getMarginStart(),
                                                lp.topMargin,
                                                lp.getMarginEnd(),
                                                marginBottom);
                                        rc.setLayoutParams(lp);
                                    }
                                }
                        }
                    }
                }
        );
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(),
                        "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setVP2Item(int position){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                vp2fm.setCurrentItem(position,false);
                if(position==1)
                    ((DialogueFragment)fragments.get(1)).isFirstInput = true;
            }
        },1000);

    }

}