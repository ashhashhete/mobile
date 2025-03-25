package com.igenesys.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.igenesys.R;
import com.igenesys.databinding.DialogFragmentYesNoBinding;
//import com.techaidsolution.gdc_app.R;
//import com.techaidsolution.gdc_app.databinding.DialogFragmentYesNoBinding;


public class YesNoBottomSheet extends BottomSheetDialogFragment {
    private CardView yesCv;
    private TextView yesCvTv;
    private CardView noCv;
    private TextView noCvTv;
    private TextView yesNoMessageTv;

    private Activity activity;
    private String msg, positiveBtnMsg, negativeBtnMsg;

    private YesNoButton yesNoButton;
    private DialogFragmentYesNoBinding binding;

    public static YesNoBottomSheet geInstance(Activity activity, String msg, String positiveBtnMsg, String negativeBtnMsg, YesNoButton yesNoButton) {

        YesNoBottomSheet fragment = new YesNoBottomSheet();
        fragment.setActivity(activity);
        fragment.setMsg(msg);
        fragment.setPositiveBtnMsg(positiveBtnMsg);
        fragment.setNegativeBtnMsg(negativeBtnMsg);
        fragment.setYesNoButton(yesNoButton);

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void setActivity(Activity activity) {
        this.activity = activity;
    }

    public TextView getYesNoMessageTv() {
        return yesNoMessageTv;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private void setPositiveBtnMsg(String positiveBtnMsg) {
        this.positiveBtnMsg = positiveBtnMsg;
    }

    private void setNegativeBtnMsg(String negativeBtnMsg) {
        this.negativeBtnMsg = negativeBtnMsg;
    }

    private void setYesNoButton(YesNoButton yesNoButton) {
        this.yesNoButton = yesNoButton;
    }

    // Report_130424_092227, Report_160424_092049, Report_130424_094628,
    // Report_130424_102725, Report_130424_124223, Report_130424_111035
    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            AppLog.e("Exception : " + e);
        }
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

        if(activity == null)
            return;

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_yes_no, null);
        dialog.setContentView(view);
        yesCv = view.findViewById(R.id.yesCv);
        yesCvTv = view.findViewById(R.id.yesCvTv);
        noCv = view.findViewById(R.id.noCv);
        noCvTv = view.findViewById(R.id.noCvTv);
        yesNoMessageTv = view.findViewById(R.id.yesNoMessageTv);

        if (!TextUtils.isEmpty(positiveBtnMsg) && !TextUtils.isEmpty(negativeBtnMsg)) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        init();
    }

    private void init() {
        yesNoMessageTv.setText(msg);

        if (!TextUtils.isEmpty(positiveBtnMsg)) {

            yesCv.setVisibility(View.VISIBLE);
            yesCvTv.setText(positiveBtnMsg);
            yesCv.setOnClickListener(view -> {
                dismiss();
                if (yesNoButton != null) {
                    yesNoButton.yesBtn();
                }
            });
        }

        if (!TextUtils.isEmpty(negativeBtnMsg)) {

            noCv.setVisibility(View.VISIBLE);
            noCvTv.setText(negativeBtnMsg);
            noCv.setOnClickListener(view -> {
                dismiss();
                if (yesNoButton != null) {
                    yesNoButton.noBtn();
                }
            });
        }
    }

    public interface YesNoButton {
        void yesBtn();

        void noBtn();
    }
}