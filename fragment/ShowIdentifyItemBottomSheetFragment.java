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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.igenesys.R;
import com.igenesys.model.IdentifyItemListModel;
import com.igenesys.adapter.ShowIdentifyItemAdapter;

import java.util.List;

public class ShowIdentifyItemBottomSheetFragment extends BottomSheetDialogFragment {
    private Activity activity;
    private List<IdentifyItemListModel> identifyItemListModels;

    public static ShowIdentifyItemBottomSheetFragment geInstance(Activity activity, List<IdentifyItemListModel> identifyItemListModels) {
        ShowIdentifyItemBottomSheetFragment fragment = new ShowIdentifyItemBottomSheetFragment();
        fragment.setActivity(activity);
        fragment.setIdentifyItemListModels(identifyItemListModels);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setActivity(Activity activity) {
        this.activity = activity;
    }


    public void setIdentifyItemListModels(List<IdentifyItemListModel> identifyItemListModels) {
        this.identifyItemListModels = identifyItemListModels;
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

        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_identify, null);
        dialog.setContentView(view);

        RecyclerView showLayerRecyclerView = view.findViewById(R.id.showIdentifyItemRecyclerView);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        showLayerRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        ShowIdentifyItemAdapter adapter = new ShowIdentifyItemAdapter(activity, identifyItemListModels);
        showLayerRecyclerView.setAdapter(adapter);

        ivClose.setOnClickListener(v -> dismiss());

    }
}
