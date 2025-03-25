package com.igenesys.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.igenesys.R;
//import com.techaidsolution.gdc_app.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
//import com.techaidsolution.gdc_app.model.IdentifyItemListModel;
//import com.techaidsolution.gdc_app.utils.Constants;
//import com.techaidsolution.gdc_app.utils.Utils;

public class ShowFilterBottomSheetFragment extends BottomSheetDialogFragment {
    String tenementNumber, cluterNumber, status;
    MaterialCardView fltr_cv_inProgress, fltr_cv_completed, fltr_cv_onHold, fltr_cv_notStarted;
    boolean progressClicked = false, completedClicked = false, notStartedClicked = false, holdClicked = false;
    private Activity activity;
    private OnItemClickListner onItemClickListner;

    public static ShowFilterBottomSheetFragment geInstance(Activity activity, String tenementNumber, String cluterNumber, String status, OnItemClickListner onItemClickListner) {
        ShowFilterBottomSheetFragment fragment = new ShowFilterBottomSheetFragment();
        fragment.setActivity(activity);
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragment.setTenementNumber(tenementNumber);
        fragment.setOnItemClickListner(onItemClickListner);
        fragment.setCluterNumber(cluterNumber);
        fragment.setStatus(status);
        return fragment;
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public void setTenementNumber(String tenementNumber) {
        this.tenementNumber = tenementNumber;
    }

    public void setCluterNumber(String cluterNumber) {
        this.cluterNumber = cluterNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private void setActivity(Activity activity) {
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

        View view = LayoutInflater.from(activity).inflate(R.layout.bottom_filter_list, null);
        dialog.setContentView(view);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        EditText et_fiterTenementNumber = view.findViewById(R.id.et_fiterTenementNumber);
        EditText et_fiterClusterNumber = view.findViewById(R.id.et_fiterClusterNumber);
        fltr_cv_inProgress = view.findViewById(R.id.fltr_cv_inProgress);
        fltr_cv_completed = view.findViewById(R.id.fltr_cv_completed);
        fltr_cv_onHold = view.findViewById(R.id.fltr_cv_onHold);
        fltr_cv_notStarted = view.findViewById(R.id.fltr_cv_notStarted);
        MaterialCardView fltr_materialCV_reset = view.findViewById(R.id.fltr_materialCV_reset);
        MaterialCardView fltr_materialCV_apply = view.findViewById(R.id.fltr_materialCV_apply);

        et_fiterTenementNumber.setText(tenementNumber);
        et_fiterClusterNumber.setText(cluterNumber);

        if (status.equalsIgnoreCase(Constants.In_Progress)) {
            inProgressSelected();
        } else if (status.equalsIgnoreCase(Constants.completed)) {
            completedSelected();
        } else if (status.equalsIgnoreCase(Constants.dispute)) { //Constants.Not_Started
            notStartedSelected();
        } else if (status.equalsIgnoreCase(Constants.On_Hold)) {
            onHoldSelected();
        } else
            setCardWidth();

        ivClose.setOnClickListener(v -> dismiss());

        fltr_cv_inProgress.setOnClickListener(view1 -> {
            if (!progressClicked) {
                inProgressSelected();
                progressClicked = true;
                status = Constants.In_Progress;
            } else {
                setCardWidth();
                progressClicked = false;
            }

        });

        fltr_cv_completed.setOnClickListener(view1 -> {
            if (!completedClicked) {
                completedSelected();
                completedClicked = true;
                status = Constants.completed;
            } else {
                setCardWidth();
                completedClicked = false;
            }
        });

        fltr_cv_onHold.setOnClickListener(view1 -> {
            if (!holdClicked) {
                onHoldSelected();
                holdClicked = true;
                status = Constants.On_Hold;
            } else {
                setCardWidth();
                holdClicked = false;
            }
        });

        fltr_cv_notStarted.setOnClickListener(view1 -> {
            if (!notStartedClicked) {
                notStartedSelected();
                notStartedClicked = true;
                status = Constants.Not_Started;
            } else {
                setCardWidth();
                notStartedClicked = false;
            }
        });

        fltr_materialCV_reset.setOnClickListener(view1 -> {
            onItemClickListner.onResetBtnClicked();

            dialog.dismiss();
        });

        fltr_materialCV_apply.setOnClickListener(view1 -> {
            tenementNumber = "";
            cluterNumber = "";

            if (!Utils.isNullOrEmpty(et_fiterTenementNumber.getText().toString().trim())) {
                tenementNumber = et_fiterTenementNumber.getText().toString().trim();
            }
            if (!Utils.isNullOrEmpty(et_fiterClusterNumber.getText().toString().trim())) {
                cluterNumber = et_fiterClusterNumber.getText().toString().trim();
            }

            onItemClickListner.onApplyClicked(tenementNumber, cluterNumber, status);
            dialog.dismiss();
        });

    }

    private void notStartedSelected() {
        setCardWidth();
        status = Constants.Not_Started;
        fltr_cv_notStarted.setStrokeWidth(2);
        notStartedClicked = true;

    }

    private void inProgressSelected() {
        setCardWidth();
        status = Constants.In_Progress;
        fltr_cv_inProgress.setStrokeWidth(2);
        progressClicked = true;

    }

    private void completedSelected() {
        setCardWidth();
        status = Constants.completed;
        fltr_cv_completed.setStrokeWidth(2);
        completedClicked = true;


    }

    private void onHoldSelected() {
        setCardWidth();
        status = Constants.On_Hold;
        fltr_cv_onHold.setStrokeWidth(2);
        holdClicked = true;

    }

    private void setCardWidth() {
        fltr_cv_notStarted.setStrokeWidth(0);
        fltr_cv_onHold.setStrokeWidth(0);
        fltr_cv_completed.setStrokeWidth(0);
        fltr_cv_inProgress.setStrokeWidth(0);

        progressClicked = false;
        completedClicked = false;
        notStartedClicked = false;
        holdClicked = false;
        status = "";

    }

    public interface OnItemClickListner {
        void onResetBtnClicked();

        void onApplyClicked(String tenementNumber, String cluterNumber, String status);

    }
}