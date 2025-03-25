package com.igenesys.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.igenesys.R;
import com.igenesys.utils.Utils;

//import com.techaidsolution.gdc_app.R;
//import com.techaidsolution.gdc_app.utils.Utils;

public class ShowFullScreenCrashDetails extends DialogFragment {

    Dialog dialog;
    private String fileName = "";
    private String crashDetails = "";
    private Context context;

    public static ShowFullScreenCrashDetails newInstance(String fileName, String crashDetails) {

        ShowFullScreenCrashDetails fragmentDialog = new ShowFullScreenCrashDetails();
        fragmentDialog.setContext(fragmentDialog.context);
        fragmentDialog.setFileName(fileName);
        fragmentDialog.setCrashDetails(crashDetails);

        return fragmentDialog;
    }


    @Nullable
    @Override
    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCrashDetails() {
        return crashDetails;
    }

    public void setCrashDetails(String crashDetails) {
        this.crashDetails = crashDetails;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialog = super.onCreateDialog(savedInstanceState);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_alert_crash_details, container);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            super.onViewCreated(view, savedInstanceState);
            TextView txt_name = view.findViewById(R.id.txt_name);
            TextView txt_crashDetails = view.findViewById(R.id.txt_crashDetails);
            ImageView img_close = view.findViewById(R.id.img_close);
            txt_name.setText(fileName);
            img_close.setOnClickListener(view1 -> {
                if (dialog != null)
                    dialog.dismiss();
            });
            if (!Utils.isNullOrEmpty(crashDetails)) {
                txt_crashDetails.setText(crashDetails);
            }
        }
    }
}