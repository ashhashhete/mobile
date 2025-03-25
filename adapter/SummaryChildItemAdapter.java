package com.igenesys.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.databinding.RvItemSummaryChildBinding;
import com.igenesys.fragment.ShowFullScreenAttachment;
import com.igenesys.model.SummaryChildItemModel;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;
import com.igenesys.utils.Utils;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SummaryChildItemAdapter extends RecyclerView.Adapter<SummaryChildItemAdapter.ChildViewHolder> implements AttachmentListItemAdapter.OnItemClickListner {

    Activity activity;
    Integer count =0;
    private ArrayList<SummaryChildItemModel> summaryChildItemModels;

    // Constructor
    SummaryChildItemAdapter(Activity activity, ArrayList<SummaryChildItemModel> summaryChildItemModels) {
        this.activity = activity;
        this.summaryChildItemModels = summaryChildItemModels;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Here we inflate the corresponding layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item_summary_child, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {
        // Create an instance of the ChildItem
        // class for the given position
        SummaryChildItemModel childItem = summaryChildItemModels.get(position);

        if (childItem.getHeader().equals("Attachments")) count++;
        if (childItem.isTopHeader) {
            childViewHolder.binding.txtTopHeader.setVisibility(View.VISIBLE);
            childViewHolder.binding.layHeaderValue.setVisibility(View.GONE);
            childViewHolder.binding.txtTopHeader.setText(childItem.getTopHeader());
        } else {
            childViewHolder.binding.txtTopHeader.setVisibility(View.GONE);
            childViewHolder.binding.layHeaderValue.setVisibility(View.VISIBLE);
            if (count<2) childViewHolder.binding.txtHeader.setText(childItem.getHeader());
            childViewHolder.binding.txtValue.setText(childItem.getValue());
            if(childItem.getHeader().equalsIgnoreCase("Remark")){
                childViewHolder.binding.txtValue.setText("View Remarks");
                childViewHolder.binding.txtValue.setTextColor(Color.parseColor("#367CFF"));
                childViewHolder.binding.txtValue.setPaintFlags(childViewHolder.binding.txtValue.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            }

            if (childItem.getHeader().equals("Attachments")) {
                childViewHolder.binding.txtValue.setVisibility(View.GONE);
                childViewHolder.binding.rvList.setVisibility(View.VISIBLE);

                ArrayList<MediaInfoDataModel> mediaInfoDataModels = Utils.getGson().fromJson(childItem.getValue(),
                        new TypeToken<ArrayList<MediaInfoDataModel>>() {}.getType());

                AttachmentListItemAdapter childItemAdapter = new AttachmentListItemAdapter(mediaInfoDataModels, this);
                childViewHolder.binding.rvList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                childViewHolder.binding.rvList.setAdapter(childItemAdapter);

                // childViewHolder.binding.txtValue.setPaintFlags(childViewHolder.binding.txtValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                // childViewHolder.binding.txtValue.setTextColor(Color.parseColor("#367CFF"));
                // childViewHolder.binding.txtValue.setTextSize(13);
            } else {
                childViewHolder.binding.txtValue.setVisibility(View.VISIBLE);
                childViewHolder.binding.rvList.setVisibility(View.GONE);
            }
        }

        childViewHolder.binding.txtValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(summaryChildItemModels.get(position).getTopHeader()!=null && !summaryChildItemModels.get(position).getTopHeader().equalsIgnoreCase("")){
                    if(summaryChildItemModels.get(position).getValue()!=null && !summaryChildItemModels.get(position).getValue().equalsIgnoreCase("")){
                        showRemarksDialog("Genesys QC Remarks",summaryChildItemModels.get(position).getValue(),summaryChildItemModels.get(position).getTopHeader());
                    }else{
                        Utils.shortToast("No remarks available!", activity);
                    }
                }else if(summaryChildItemModels.get(position).getValue()!=null && !summaryChildItemModels.get(position).getValue().equalsIgnoreCase("")){
                    showRemarksDialog("Remarks",summaryChildItemModels.get(position).getValue(),summaryChildItemModels.get(position).getTopHeader());
                }else{
                    Utils.shortToast("No remarks available!", activity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return summaryChildItemModels.size();
    }

    @Override
    public void onTextClick(MediaInfoDataModel mediaInfoDataModel) {
        try {
            if (mediaInfoDataModel.getItem_url().contains(".pdf")) {
                if (mediaInfoDataModel.getItem_url().contains("http")) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setDataAndType(Uri.parse(mediaInfoDataModel.getItem_url()), "application/pdf");
                        activity.startActivity(browserIntent);
                        // activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mediaInfoDataModel.getItem_url())));
                    } catch (Exception e){
                        Utils.shortToast("Unable to open!", activity);
                    }
                } else if (new File(mediaInfoDataModel.getItem_url()).exists()) {
                    try {
                        CryptoUtilsTest.encryptFileinAES(new File(mediaInfoDataModel.getItem_url()), 2);
                        App.getSharedPreferencesHandler().putString(Constants.pdfFilePath, mediaInfoDataModel.getItem_url());
                        // Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(mediaInfoDataModel.getItem_url()));
                        // Intent intent = new Intent(Intent.ACTION_VIEW);
                        // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        // intent.setDataAndType(uri, activity.getContentResolver().getType(Uri.fromFile(new File(mediaInfoDataModel.getItem_url()))));
                        // if (intent.resolveActivity(activity.getPackageManager()) == null) {
                        //     Utils.shortToast("No app to open!", activity);
                        // } else activity.startActivity(intent);


                        File pdfPathFile;
                        try {
                            pdfPathFile=new File(mediaInfoDataModel.getItem_url());
                            CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                        } catch (CryptoException e) {
                            throw new RuntimeException(e);
                        }

                        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(intent);
                    } catch (CryptoException e) {
                        Utils.shortToast("Unable to open!", activity);
                        throw new RuntimeException(e);
                    }
                } else if (Utils.isNullOrEmpty(mediaInfoDataModel.getItem_url())) {
                    Utils.shortToast("File not available for offline use.", activity);
                } else {
                    Utils.shortToast("File not exist.", activity);
                }
            } else {
                if (mediaInfoDataModel.getItem_url().contains("distometer")) {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bundle extras = new Bundle();
                    extras.putString("video", mediaInfoDataModel.getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }else if (mediaInfoDataModel.getItem_url().contains("https:")) {
                    FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
                    ShowFullScreenAttachment showAttachmentBottomSheetFragment = ShowFullScreenAttachment.newInstance(null, null,
                            mediaInfoDataModel.getItem_url(), mediaInfoDataModel.getFilename());
                    showAttachmentBottomSheetFragment.show(fm, "");
                } else if (new File(mediaInfoDataModel.getItem_url()).exists()) {
                    try {
                        CryptoUtilsTest.encryptFileinAES(new File(mediaInfoDataModel.getItem_url()), 2);
                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(new File(mediaInfoDataModel.getItem_url())));
                        if (bitmap != null) {
                            FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
                            ShowFullScreenAttachment showAttachmentBottomSheetFragment = ShowFullScreenAttachment.newInstance(new File(mediaInfoDataModel.getItem_url()), bitmap, null, mediaInfoDataModel.getFilename());
                            showAttachmentBottomSheetFragment.show(fm, "");
                        }
                    } catch (CryptoException e) {
                        Utils.shortToast("Unable to open!", activity);
                        throw new RuntimeException(e);
                    }
                } else if (Utils.isNullOrEmpty(mediaInfoDataModel.getItem_url())) {
                    Utils.shortToast("File not available for offline use.", activity);
                } else {
                    Utils.shortToast("File not exist.", activity);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        RvItemSummaryChildBinding binding;

        ChildViewHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }


    public void showRemarksDialog(String header, String subHeader,String message) {
        // Create an alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.remarks_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_subHeader = customLayout.findViewById(R.id.txt_subHeader);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_subHeader.setText(subHeader);
        txt_mssage.setText(message);

        ImageView img_close = customLayout.findViewById(R.id.img_close);
//        img_close.setVisibility(View.GONE);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}