package com.example.aicommunication.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aicommunication.R;
import com.example.aicommunication.adapter.ViewPage2Adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private ViewPager2 vp2,vp2fm;
    ViewPage2Adapter vp2A;
    private LinearLayout tabL;
    public Button start_bt;
    List<Integer> images = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public int delay = 3000;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;

    public HomeFragment(ViewPager2 vp2fm) {
        // Required empty public constructor
        this.vp2fm = vp2fm;
    }

    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            vp2 = rootView.findViewById(R.id.viewPager2_fm1);
            tabL = rootView.findViewById(R.id.tabLinear);
            start_bt = rootView.findViewById(R.id.button);
            start_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vp2fm.setCurrentItem(1);
                }
            });
            images.add(R.drawable.image01);
            images.add(R.drawable.image02);
            images.add(R.drawable.image03);
            images.add(R.drawable.image04);
            for (int i = 0; i < images.size(); i++) {
                ImageButton imageBT = getImageButton(i);
                tabL.addView(imageBT);
            }
            vp2A = new ViewPage2Adapter(images);
            vp2.setAdapter(vp2A);
            vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    if (position == images.size()) {
                        if (positionOffset >= 0.9) {
                            vp2.setCurrentItem(1, false);
                        }
                    } else if (position == 0) {
                        if (positionOffset <= 0.1) {
                            vp2.setCurrentItem(images.size(), false);
                        }
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    if (position >= 1 && position <= images.size()) {
                        for (int i = 0; i < tabL.getChildCount(); i++) {
                            tabL.getChildAt(i).setActivated(false);
                        }
                        tabL.getChildAt(position - 1).setActivated(true);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                    if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                        mHandler.removeCallbacks(runnable);
                        mHandler.postDelayed(runnable, delay);
                    } else if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        mHandler.removeCallbacks(runnable);
                    }
                }
            });
            vp2.setCurrentItem(1, false);
            mHandler.postDelayed(runnable, delay);
        }
        return rootView;
    }

    @NonNull
    private ImageButton getImageButton(int i) {
        ImageButton imageBT = new ImageButton(getContext());
        imageBT.setImageResource(R.drawable.indicator);
        imageBT.setBackground(null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                30, 30);
        layoutParams.setMargins(4,0,4,0);
        imageBT.setLayoutParams(layoutParams);
        return imageBT;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    private final Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            if(vp2.getCurrentItem()+1>=vp2A.getItemCount()) {
                vp2.setCurrentItem(1, false);
                mHandler.post(runnable);
            }else {
                vp2.setCurrentItem(vp2.getCurrentItem() + 1, true);
                mHandler.postDelayed(runnable, delay);
            }
        }
    };


}