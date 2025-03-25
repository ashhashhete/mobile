package com.igenesys.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.igenesys.App;
import com.squareup.picasso.Picasso;
import com.igenesys.R;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.CryptoUtilsTest;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;

public class ShowFullScreenAttachment extends DialogFragment {

    Dialog dialog;
    File file;
    private Bitmap attachmentBitmap;
    private String imageData = "";
    private String imageName = "";
    private Context context;

    public static ShowFullScreenAttachment newInstance(File file, Bitmap attachmentBitmap, String imageData, String imageName) {

        ShowFullScreenAttachment fragmentDialog = new ShowFullScreenAttachment();
        fragmentDialog.setAttachmentBitmap(attachmentBitmap);
        fragmentDialog.setContext(fragmentDialog.context);
        fragmentDialog.setImageData(imageData);
        fragmentDialog.setImageName(imageName);
        fragmentDialog.setFile(file);

        return fragmentDialog;
    }

    public static ShowFullScreenAttachment newInstance(String imageData) {

        ShowFullScreenAttachment fragmentDialog = new ShowFullScreenAttachment();
        fragmentDialog.setImageData(imageData);
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void setAttachmentBitmap(Bitmap attachmentBitmap) {
        this.attachmentBitmap = attachmentBitmap;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
//        if (getDialog() != null) {
//            Window window = getDialog().getWindow();
//            if (window == null) return;
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = getResources().getDimensionPixelSize(R.dimen.popup_width);
//            params.height = getResources().getDimensionPixelSize(R.dimen.popup_height);
//            window.setAttributes(params);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_attachment_preview, container);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            super.onViewCreated(view, savedInstanceState);
            ImageView attachmentPreviewIv = view.findViewById(R.id.attachmentPreviewIv);
            TextView imgNameTxt = view.findViewById(R.id.txt_name);
            ImageView img_close = view.findViewById(R.id.img_close);
            imgNameTxt.setText(imageName);
            img_close.setOnClickListener(view1 -> {
                if (dialog != null)
                    dialog.dismiss();

                try {
                    if (file != null)
                        CryptoUtilsTest.encryptFileinAES(file, 1);
                } catch (CryptoException e) {
                    AppLog.e("Error: Unable to decrypt: " + e.getCause());
                    throw new RuntimeException(e);
                }
            });
            if (attachmentBitmap != null) {
                attachmentPreviewIv.setImageBitmap(attachmentBitmap);
            } else if (imageData != null && !imageData.isEmpty()) {
                Picasso.get().load(imageData + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(attachmentPreviewIv);
            }
        }
    }
}