package com.igenesys.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ViewListAdapter extends RecyclerView.Adapter<ViewListAdapter.HorizontalViewHolder> {


    List<AttachmentItemList> attachmentItemLists;
    int flag = 0;
    String type = "";
    boolean isImageFitToScreen;
    private final Activity activity;
    HorizontalAdapterDelete horizontalAdapterDelete;

    MediaInfoDataModel model;

    public ViewListAdapter(MediaInfoDataModel model, List<AttachmentItemList> attachmentItemLists, int flag, Activity activity, String type) {
        this.model = model;
        this.attachmentItemLists = attachmentItemLists;
        this.flag = flag;
        this.type = type;
        this.activity = activity;

    }


    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout_recycler, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {
        if (type.equalsIgnoreCase(Constants.video)) {
            holder.imageLayout.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.VISIBLE);

            if(attachmentItemLists == null || attachmentItemLists.isEmpty() || attachmentItemLists.get(position) == null){
                return;
            }

            Log.e("url is:", attachmentItemLists.get(position).getItem_url());
            File pdfPathFile;
            try {
                pdfPathFile = new File(attachmentItemLists.get(position).getItem_url());
                if (attachmentItemLists.get(position).getItem_url().contains("http")) {
                    RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

                    GlideUrl glideUrl = new GlideUrl(attachmentItemLists.get(position).getItem_url(), new LazyHeaders.Builder()
                            .addHeader("User-Agent", "drppl")
                            .build());

                    Glide.with(activity).load(glideUrl).apply(requestOptions).into(holder.thumbnail);

                    holder.videoView.setVideoPath(attachmentItemLists.get(position).getItem_url());

                    try {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {

                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                String time = "0";

                                try {
                                    retriever.setDataSource(attachmentItemLists.get(position).getItem_url(), new HashMap<>());
                                    time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    retriever.release();
                                } catch (Exception e){
                                    AppLog.e(e.getMessage());
                                    try {
                                        FileInputStream fis = new FileInputStream(new File(attachmentItemLists.get(position).getItem_url()).getAbsolutePath());
                                        retriever.setDataSource(fis.getFD());
                                        time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                        retriever.release();
                                    } catch (Exception ee){
                                        AppLog.e(ee.getMessage());
                                    }
                                }

                                if(time == null)
                                    time = "0";

                                long timeInMillisec = Long.parseLong(time);

                                String durations = convertMillieToHMmSs(timeInMillisec); //use this duration
                                activity.runOnUiThread(() -> {
                                    holder.textViewTime.setText(durations);//set in textview
                                });
                            }
                        });
                    } catch (Exception ex) {
                        ex.getCause();
                    }
                } else {
                    CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                    holder.videoView.setVideoURI(Uri.fromFile(pdfPathFile));

                    MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(pdfPathFile.getPath()));
                    if (mp != null) {
                        int duration = mp.getDuration();
                        mp.release();
                        holder.textViewTime.setText(convertMillieToHMmSs(duration));
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
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bundle extras = new Bundle();
                    extras.putString("video", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            });
        } else {
            holder.imageLayout.setVisibility(View.VISIBLE);
            holder.videoLayout.setVisibility(View.GONE);
            if (attachmentItemLists.get(position).getItem_url().contains(".pdf")) {
                String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file
                int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                Drawable res = activity.getResources().getDrawable(imageResource);
                holder.image.setImageDrawable(res);
            } else {
                if (attachmentItemLists.get(position).getItem_url().contains("http")) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.img_place);
                    requestOptions.error(R.drawable.img_place);

                    GlideUrl glideUrl = new GlideUrl(attachmentItemLists.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                            new LazyHeaders.Builder()
                            .addHeader("User-Agent", "drppl")
                            .build());

                    Glide.with(activity).load(glideUrl).apply(requestOptions).into(holder.image);

                    // Glide.with(activity).setDefaultRequestOptions(requestOptions).load().into(holder.image);
                } else {
                    File imgFile = new File(attachmentItemLists.get(position).getItem_url());
                    if (imgFile.exists()) {
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            holder.image.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

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
                    } catch (Exception e){
                        AppLog.e(e.getMessage());
                    }
                } else if (attachmentItemLists.get(position).getItem_url().contains(".mp4")) {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bundle extras = new Bundle();
                    extras.putString("video", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    Bitmap image = getBitmapFromViewUsingCanvas(holder.image);
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", attachmentItemLists.get(position).getItem_url());
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attachmentItemLists.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delImageView, delVideoView;
        ProgressBar progressBar;
        RelativeLayout videoLayout, imageLayout;
        VideoView videoView;
        TextView textViewTime;
        ImageView thumbnail;
        ImageButton btnPlay;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageviewid);
            delImageView = itemView.findViewById(R.id.delImageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            videoLayout = itemView.findViewById(R.id.videoLayout);
            imageLayout = itemView.findViewById(R.id.imageLayout);
            delVideoView = itemView.findViewById(R.id.delVideoView);
            videoView = itemView.findViewById(R.id.videoviewid);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }


    private Bitmap getBitmapFromViewUsingCanvas(View view) {
        // Create a new Bitmap object with the desired width and height
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a new Canvas object using the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Draw the View into the Canvas
        view.draw(canvas);

        // Return the resulting Bitmap
        return bitmap;
    }

    public static String convertMillieToHMmSs(long millie) {

        if(millie == 0)
            return "00:00";

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