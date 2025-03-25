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

import com.esri.arcgisruntime.layers.Layer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.igenesys.R;
import com.igenesys.adapter.ShowTocAdapter;

import java.util.List;

public class ShowTocBottomSheetFragment extends BottomSheetDialogFragment {
    private Activity activity;
    private List<Layer> layers;
    private String intentType;

    public static ShowTocBottomSheetFragment geInstance(Activity activity, List<Layer> layers, String intentType) {
        ShowTocBottomSheetFragment fragment = new ShowTocBottomSheetFragment();
        fragment.setIntentType(intentType);
        fragment.setLayers(layers);
        fragment.setActivity(activity);
        return fragment;
    }

    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_show_toc, null);
        dialog.setContentView(view);
//        TextView tvTitle1 = view.findViewById(R.id.tv_title1);
//        TextView tvTitle2 = view.findViewById(R.id.tv_title2);

//        tvTitle1.setText(getString(R.string.layer_settings));
//        tvTitle2.setText(getString(R.string.select_layers_to_show_on_map));

        RecyclerView showLayerRecyclerView = view.findViewById(R.id.showLayerRecyclerView);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        showLayerRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        ShowTocAdapter adapter = new ShowTocAdapter(activity, layers, intentType);
        showLayerRecyclerView.setAdapter(adapter);

        ivClose.setOnClickListener(v -> dismiss());

    }
}