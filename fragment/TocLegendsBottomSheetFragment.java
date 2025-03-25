package com.igenesys.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.igenesys.R;
import com.igenesys.adapter.ShowTocLegendsItemAdapter;
import com.igenesys.model.IdentifyItemListModel;
import com.igenesys.model.TocLegendsListModel;

import java.util.List;
import java.util.Objects;

public class TocLegendsBottomSheetFragment extends BottomSheetDialogFragment {
    private Activity activity;
    TabLayout tabLayout;
    ViewPager viewPager;
    RecyclerView recyclerView;
    private List<TocLegendsListModel> tocLegendsListModels;

    public static TocLegendsBottomSheetFragment geInstance(Activity activity, List<TocLegendsListModel> tocLegendsListModels) {
        TocLegendsBottomSheetFragment fragment = new TocLegendsBottomSheetFragment();
        fragment.setActivity(activity);
        fragment.setIdentifyItemListModels(tocLegendsListModels);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setIdentifyItemListModels(List<TocLegendsListModel> tocLegendsListModels) {
        this.tocLegendsListModels = tocLegendsListModels;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SheetDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_toc_legends, null);
        dialog.setContentView(view);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.legendsViewPager);

        tabLayout.addTab(tabLayout.newTab().setText("TOC"));
        tabLayout.addTab(tabLayout.newTab().setText("Legends"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ImageView ivClose = view.findViewById(R.id.iv_close);

        ivClose.setOnClickListener(v -> dismiss());

        RecyclerView showTocLegendsRecylerView = view.findViewById(R.id.showTocLegendsRecyclerView);
        showTocLegendsRecylerView.setLayoutManager(new LinearLayoutManager(activity));
        ShowTocLegendsItemAdapter adapter = new ShowTocLegendsItemAdapter(activity, tocLegendsListModels);
        showTocLegendsRecylerView.setAdapter(adapter);


        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    adapter.setTocLegendsListModels(0, tocLegendsListModels);
                } else {
                    adapter.setTocLegendsListModels(1, tocLegendsListModels);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}