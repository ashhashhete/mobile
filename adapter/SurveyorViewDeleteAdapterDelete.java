package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;
import com.igenesys.utils.Utils;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyorViewDeleteAdapterDelete extends RecyclerView.Adapter<SurveyorViewDeleteAdapterDelete.HorizontalViewHolder> {

    List<AttachmentItemList> attachmentItemLists;
    int flag = 0;
    boolean isImageFitToScreen;
    private final Activity activity;
    MediaInfoDataModel model;

    List<MediaInfoDataModel> deleteTotalMediaList = new ArrayList<>();
    LocalSurveyDbViewModel localSurveyDbViewModel;
    private SurveyorViewAttachAdapter.OnViewClickListner onViewClickListner;

    private int flagPosition;
    List<MediaInfoDataModel> newMediaInfoDataModels;
    int newPos;

    public SurveyorViewDeleteAdapterDelete(MediaInfoDataModel model, List<AttachmentItemList> attachmentItemLists, int flag, Activity activity, SurveyorViewAttachAdapter.OnViewClickListner onViewClickListner, List<MediaInfoDataModel> newMediaInfoDataModels, int newPos) {
        this.model = model;
        this.attachmentItemLists = attachmentItemLists;
        this.flag = flag;
        this.activity = activity;
        this.onViewClickListner = onViewClickListner;
        this.newMediaInfoDataModels = newMediaInfoDataModels;
        this.newPos = newPos;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout_recycler_delete, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {

        // if(!attachmentItemLists.get(position).isDeleted){
        if (attachmentItemLists.get(position).getItem_url().contains("http")) {
            holder.delImageView.setVisibility(View.INVISIBLE);
        } else {
            holder.delImageView.setVisibility(View.VISIBLE);
        }

        // holder.delImageView.setVisibility(View.VISIBLE);
        if (attachmentItemLists.get(position).getItem_url().contains(".pdf")) {
            holder.imgLyt.setVisibility(View.VISIBLE);
            holder.vidLyt.setVisibility(View.GONE);
            int imageResource = activity.getResources().getIdentifier("@drawable/icon_pdf", null, activity.getPackageName());
            holder.image.setImageDrawable(activity.getResources().getDrawable(imageResource));
        } else if (attachmentItemLists.get(position).getItem_url().contains("disto")) {
            holder.imgLyt.setVisibility(View.GONE);
            holder.vidLyt.setVisibility(View.VISIBLE);

            try {
                if (attachmentItemLists.get(position).getItem_url().contains("http")) {
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

                    GlideUrl glideUrl = new GlideUrl(attachmentItemLists.get(position).getItem_url(), new LazyHeaders.Builder()
                            .addHeader("User-Agent", "drppl").build());

                    Glide.with(activity).load(glideUrl).apply(requestOptions).into(holder.thumbnail);

                    holder.videoView.setVideoPath(attachmentItemLists.get(position).getItem_url());

                    try {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    String time = "0";

                                    try {
                                        retriever.setDataSource(attachmentItemLists.get(position).getItem_url(), new HashMap<>());
                                        time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                        retriever.release();
                                    } catch (Exception e) {
                                        AppLog.e(e.getMessage());
                                        try {
                                            FileInputStream fis = new FileInputStream(new File(attachmentItemLists.get(position).getItem_url()).getAbsolutePath());
                                            retriever.setDataSource(fis.getFD());
                                            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                            retriever.release();
                                        } catch (Exception ee) {
                                            AppLog.e(ee.getMessage());
                                        }
                                    }

                                    long timeInMillisec = Long.parseLong(time);
                                    String durations = convertMillieToHMmSs(timeInMillisec); //use this duration

                                    activity.runOnUiThread(() -> {
                                        holder.textViewTime.setText(durations);//set in textview
                                    });
                                } catch (Exception ex) {
                                    ex.getMessage();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.getCause();
                    }
                } else {
                    File pdfPathFile = new File(attachmentItemLists.get(position).getItem_url());
                    CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                    holder.videoView.setVideoURI(Uri.fromFile(pdfPathFile));
                    MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(pdfPathFile.getPath()));
                    if (mp != null) {
                        int duration = mp.getDuration();
                        mp.release();
                        holder.textViewTime.setText(convertMillieToHMmSs(duration));
                    } else {
                        holder.textViewTime.setText("00:00");
                    }

                    Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(Uri.fromFile(pdfPathFile).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    holder.thumbnail.setImageBitmap(bitmap2);
                }
            } catch (CryptoException e) {
                throw new RuntimeException(e);
            }

            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if(attachmentItemLists.get(position).getFileName().contains(".mp4")){
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bundle extras = new Bundle();
                    extras.putString("video", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                    // }
                }
            });
        } else {
            holder.imgLyt.setVisibility(View.VISIBLE);
            holder.vidLyt.setVisibility(View.GONE);
            if (attachmentItemLists.get(position).getItem_url().contains("http")) {
                RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                GlideUrl glideUrl = new GlideUrl(attachmentItemLists.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                        new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());

                Glide.with(activity).load(glideUrl).apply(requestOptions).into(holder.image);
                // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(attachmentItemLists.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(holder.image);
            } else {
                File imgFile = new File(attachmentItemLists.get(position).getItem_url());
                if (imgFile.exists()) {
                    try {
                        // CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                        // Bitmap myBitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(new File(attachmentItemLists.get(position).getItem_url())));
                        // Bitmap myBitmap = BitmapFactory.decodeFile(attachmentItemLists.get(position).getItem_url());
                        CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                        holder.image.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // }else{
        //     holder.image.setVisibility(View.GONE);
        //     holder.delImageView.setVisibility(View.GONE);
        // }

        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attachmentItemLists.get(position).getItem_url().contains(".mp4")) {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bundle extras = new Bundle();
                    extras.putString("video", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (attachmentItemLists.get(position).getItem_url().contains(".pdf") && !attachmentItemLists.get(position).getItem_url().contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile = new File(attachmentItemLists.get(position).getItem_url());
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
                } else if (attachmentItemLists.get(position).getItem_url().contains(".pdf") && attachmentItemLists.get(position).getItem_url().contains("http")) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setDataAndType(Uri.parse(attachmentItemLists.get(position).getItem_url()), "application/pdf");
                        activity.startActivity(browserIntent);
                    } catch (Exception e) {
                        Utils.shortToast("Something went wrong while opening file. Kindly retry!! Error: " + e.getMessage(), activity);
                    }
                } else {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    holder.image.buildDrawingCache();
                    Bitmap image = holder.image.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete the attachment?",
                        "Delete", "Cancel", false, 1, position, attachmentItemLists.get(position).getItem_url());
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentItemLists.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        ImageView image, delImageView;
        ProgressBar progressBar;
        RelativeLayout imgLyt, vidLyt;
        VideoView videoView;

        TextView textViewTime;
        ImageView thumbnail;
        ImageButton btnPlay;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageviewid);
            delImageView = itemView.findViewById(R.id.delImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            imgLyt = itemView.findViewById(R.id.delimgLyt);
            vidLyt = itemView.findViewById(R.id.delVidLyt);
            videoView = itemView.findViewById(R.id.vidviewid);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }


    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, int pos, String itemUrl) {

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
        btn_yes.getBackground().setColorFilter(activity.getColor(R.color.lighter_red), PorterDuff.Mode.SRC_ATOP);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            if (flag == 1) {
                deleteTotalMediaList = new ArrayList<>();
                deleteTotalMediaList.add(model);
                if (attachmentItemLists != null && attachmentItemLists.size() == 1) {
                    onViewClickListner.onAttachmentDeletedClicked(newMediaInfoDataModels, 2, newPos, null, "");
                } else {
                    onViewClickListner.onAttachmentDeletedClicked(deleteTotalMediaList, 1, pos, attachmentItemLists, itemUrl);
                }

            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> dialog.dismiss());

        img_close.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedImages(List<AttachmentItemList> attachmentListImageDetails) {
        this.attachmentItemLists = attachmentListImageDetails;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedList(List<AttachmentItemList> attachmentListImageDetails) {
        this.attachmentItemLists = attachmentListImageDetails;
        notifyDataSetChanged();
    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }
}