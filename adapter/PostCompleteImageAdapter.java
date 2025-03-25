package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.igenesys.utils.DeleteCallBack;
import com.igenesys.utils.FullScreenImage;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostCompleteImageAdapter extends RecyclerView.Adapter<PostCompleteImageAdapter.HorizontalViewHolder> {


    int flag = 0;
    private final Activity activity;

    ArrayList<String> itemList;
    ViewAttachAdapter.OnViewClickListner onViewClickListner;
    DeleteCallBack deleteCallBack;
    MediaInfoDataModel model;

    public PostCompleteImageAdapter(MediaInfoDataModel model, ArrayList<String> itemList, Activity activity, ViewAttachAdapter.OnViewClickListner onViewClickListner, DeleteCallBack deleteCallBack) {
        this.itemList = itemList;
        this.flag = flag;
        this.activity = activity;
        this.onViewClickListner = onViewClickListner;
        this.model = model;
        this.deleteCallBack = deleteCallBack;
    }


    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout_recycler_non_edit, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder, int position) {

        if (itemList.get(position).contains(".pdf")) {
            if (itemList.get(position).contains("http")) {
                holder.delImageView.setVisibility(View.GONE);
            } else {
                holder.delImageView.setVisibility(View.VISIBLE);
            }
            holder.imgLyt.setVisibility(View.VISIBLE);
            holder.vidLyt.setVisibility(View.GONE);
            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
            Drawable res = activity.getResources().getDrawable(imageResource);
            holder.image.setImageDrawable(res);
        } else {
            holder.imgLyt.setVisibility(View.VISIBLE);
            holder.vidLyt.setVisibility(View.GONE);
            if (itemList.get(position).contains("http")) {
                holder.delImageView.setVisibility(View.GONE);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.img_place);
                requestOptions.error(R.drawable.img_place);

                GlideUrl glideUrl = new GlideUrl(itemList.get(position) + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                        new LazyHeaders.Builder()
                                .addHeader("User-Agent", "drppl")
                                .build());

                Glide.with(activity).load(glideUrl).apply(requestOptions).into(holder.image);
                // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(attachmentItemLists.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(holder.image);
            } else {
                holder.delImageView.setVisibility(View.VISIBLE);
                holder.vidLyt.setVisibility(View.GONE);
                File imgFile = new File(itemList.get(position));
                if (imgFile.exists()) {
                    try {
//                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
//                            Bitmap myBitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(new File(attachmentItemLists.get(position).getItem_url())));
//                            Bitmap myBitmap = BitmapFactory.decodeFile(attachmentItemLists.get(position).getItem_url());
                        CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                        holder.image.setImageBitmap(bitmap);
                    } catch (CryptoException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                }
                ;
            }
        }
//        }else{
//            holder.image.setVisibility(View.GONE);
//            holder.delImageView.setVisibility(View.GONE);
//        }


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemList.get(position).contains(".pdf") && !itemList.get(position).contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile = new File(itemList.get(position));
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
                } else if (itemList.get(position).contains(".pdf") && itemList.get(position).contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(itemList.get(position)), "application/pdf");
                    activity.startActivity(browserIntent);
                } else {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    holder.image.buildDrawingCache();
                    Bitmap image = holder.image.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", itemList.get(position));
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete the attachment?",
                        "Delete", "Cancel", false, 1, position, itemList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
                if (model == null) {
                    deleteCallBack.onDelete(itemList.get(pos), null, pos);
                } else {
                    boolean b = false;
                    for (int i = 0; i < model.getAttachmentItemLists().size(); i++) {
                        if (model.getAttachmentItemLists().get(i).item_url.equals(itemUrl)) {
                            b = true;
                        }
                    }
                    if (b) {
                        ArrayList<MediaInfoDataModel> deleteTotalMediaList = new ArrayList<>();
                        deleteTotalMediaList.add(model);
                        if (itemList != null && itemList.size() == 1) {
                            onViewClickListner.onAttachmentDeletedClicked(deleteTotalMediaList, 2, pos, null, "");
                        } else {
                            onViewClickListner.onAttachmentDeletedClicked(deleteTotalMediaList, 1, pos, null, itemUrl);
                        }
                    } else {
                        deleteCallBack.onDelete(itemList.get(pos), null, pos);
                    }

                }

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
    public void setUpdatedList(ArrayList<String> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

}