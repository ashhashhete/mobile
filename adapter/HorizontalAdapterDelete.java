package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.App;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.squareup.picasso.Picasso;
import com.igenesys.R;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HorizontalAdapterDelete extends RecyclerView.Adapter<HorizontalAdapterDelete.HorizontalViewHolder> {

private int[] items;
    List<MediaInfoDataModel> newMediaInfoDataModels;
    int flag=0;
    boolean isImageFitToScreen;
    private final Activity activity;

    List<MediaInfoDataModel> deleteTotalMediaList=new ArrayList<>();
    LocalSurveyDbViewModel localSurveyDbViewModel;
    private ViewAttachAdapter.OnViewClickListner onViewClickListner;
    public HorizontalAdapterDelete(int[] items, List<MediaInfoDataModel> newMediaInfoDataModels, int flag, Activity activity,ViewAttachAdapter.OnViewClickListner onViewClickListner) {

        this.items = items;
        this.newMediaInfoDataModels = newMediaInfoDataModels;
        this.flag = flag;
        this.activity = activity;
        this.onViewClickListner = onViewClickListner;

    }


@Override
public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_layout_recycler_delete, parent, false);
    return new HorizontalViewHolder(view);
}

@Override
public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        holder.delImageView.setVisibility(View.VISIBLE);
//        Picasso.get().load(newMediaInfoDataModels.get(position).getItem_url()).into(holder.image);
    if (newMediaInfoDataModels.get(position).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)){
        holder.imgView.setVisibility(View.GONE);
        holder.vidView.setVisibility(View.VISIBLE);
        File pdfPathFile;
        try {
            pdfPathFile=new File(newMediaInfoDataModels.get(position).getAttachmentItemLists().get(position).getItem_url());
            CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
//            Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);
        holder.videoView.setVideoURI(Uri.fromFile(pdfPathFile));
        holder.videoView.start();
    }else {
        holder.imgView.setVisibility(View.VISIBLE);
        holder.vidView.setVisibility(View.GONE);
    }

    try {

        if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf")) {
            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
            Drawable res = activity.getResources().getDrawable(imageResource);
            holder.image.setImageDrawable(res);
        }else{
//        Picasso.get().load(newMediaInfoDataModels.get(position).getItem_url()).into(holder.image);
            if(newMediaInfoDataModels.get(position).getItem_url().contains("http")){
                Picasso.get().load(newMediaInfoDataModels.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(holder.image);
            }else{
                File imgFile = new File(newMediaInfoDataModels.get(position).getItem_url());
                if(imgFile.exists()){
                    try {
                        CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                        Bitmap myBitmap = BitmapFactory.decodeFile(newMediaInfoDataModels.get(position).getItem_url());
                        holder.image.setImageBitmap(myBitmap);
                    } catch (CryptoException e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        }
    } catch (Exception exception) {
        AppLog.e(exception.getMessage());
    }

    holder.videoView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(newMediaInfoDataModels.get(0).getAttachmentItemLists().get(position).getFileName().contains(".mp4")){
                Intent intent = new Intent(activity, FullScreenImage.class);
                Bundle extras = new Bundle();
                extras.putString("video", newMediaInfoDataModels.get(0).getAttachmentItemLists().get(position).getItem_url());
                intent.putExtras(extras);
                activity.startActivity(intent);
            }
        }
    });

    holder.image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf") &&
                    !newMediaInfoDataModels.get(position).getItem_url().contains("http")) {
                File pdfPathFile;
                try {
                    pdfPathFile=new File(newMediaInfoDataModels.get(position).getItem_url());
                    CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                } catch (CryptoException | NullPointerException e) {
                    AppLog.e(e.getMessage());
                    throw new RuntimeException(e);
                }
                Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(intent);
                } catch (Exception exception) {
                    AppLog.e(exception.getMessage());
                }
            } else if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf") &&
                    newMediaInfoDataModels.get(position).getItem_url().contains("http")) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(newMediaInfoDataModels.get(position).getItem_url()), "application/pdf");
                    activity.startActivity(browserIntent);
                } catch (Exception exception) {
                    AppLog.e(exception.getMessage());
                }
            } else {
                try {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    holder.image.buildDrawingCache();
                    Bitmap image=  holder.image.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                } catch (Exception exception) {
                    AppLog.e(exception.getMessage());
                }
            }
        }
    });

    holder.delImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this attachment?",
                    "Yes", "No", false,1,position);

//            deleteTotalMediaList=new ArrayList<>();
//            deleteTotalMediaList.add(newMediaInfoDataModels.get(position));
//            onViewClickListner.onAttachmentDeletedClicked(deleteTotalMediaList);

//            localSurveyDbViewModel.updateMediaIsUploadedInfo(infoObjID,true, mediaInfoDataModel.getMediaId());
        }
    });

}

@Override
public int getItemCount() {
    return newMediaInfoDataModels.size();
}

public class HorizontalViewHolder extends RecyclerView.ViewHolder {
    ImageView image,delImageView;
    VideoView videoView;
    RelativeLayout vidView, imgView;
    public HorizontalViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageviewid);
        delImageView = itemView.findViewById(R.id.delImageView);
        videoView = itemView.findViewById(R.id.vidviewid);
        vidView = itemView.findViewById(R.id.delVidLyt);
        imgView = itemView.findViewById(R.id.delimgLyt);

   } }


    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod,int flag,int pos) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_mssage.setText(mssage);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);

        txt_yes.setText(btnYes);
        txt_no.setText(btnNo);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        ImageView img_close = customLayout.findViewById(R.id.img_close);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            if(flag==1){
                deleteTotalMediaList=new ArrayList<>();
                try {
                    deleteTotalMediaList.add(newMediaInfoDataModels.get(pos));
                    onViewClickListner.onAttachmentDeletedClicked(deleteTotalMediaList,1,pos, null, "");
                } catch (Exception exception) {
                    AppLog.e(exception.getMessage());
                }
            }else if(flag==2){
                onViewClickListner.onAttachmentDeletedClicked(newMediaInfoDataModels,2,pos, null, "");
            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedImages(ArrayList<MediaInfoDataModel> attachmentListImageDetails) {
        this.newMediaInfoDataModels = attachmentListImageDetails;
        notifyDataSetChanged();
    }
}