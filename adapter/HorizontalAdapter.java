package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.App;
import com.igenesys.utils.AppLog;
import com.squareup.picasso.Picasso;
import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder> {

private int[] items;
    List<MediaInfoDataModel> newMediaInfoDataModels;
    int flag=0;
    boolean isImageFitToScreen;
    private final Activity activity;
    HorizontalAdapterDelete horizontalAdapterDelete;

    public HorizontalAdapter(int[] items,List<MediaInfoDataModel> newMediaInfoDataModels,int flag,Activity activity) {

        this.items = items;
        this.newMediaInfoDataModels = newMediaInfoDataModels;
        this.flag = flag;
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
//    holder.image.setImageResource(items[position]);
    if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf")) {
        String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

        int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
        Drawable res = activity.getResources().getDrawable(imageResource);
        holder.image.setImageDrawable(res);
    }else{
        if(newMediaInfoDataModels.get(position).getItem_url().contains("http")){
            Picasso.get().load(newMediaInfoDataModels.get(position).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(holder.image);
        }else{
            try {
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
            } catch (Exception exception) {
                AppLog.e(exception.getMessage());
            }
        }
    }


    holder.image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf") && !newMediaInfoDataModels.get(position).getItem_url().contains("http")) {
                File pdfPathFile;
                try {
                    pdfPathFile=new File(newMediaInfoDataModels.get(position).getItem_url());
                    CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                } catch (CryptoException | ArrayIndexOutOfBoundsException e) {
                    AppLog.e(e.getMessage());
                    throw new RuntimeException(e);
                }
                try {
                    Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                } catch (Exception e) {
                    AppLog.e(e.getMessage());
                }
            }else if (newMediaInfoDataModels.get(position).getFilename().contains(".pdf") && newMediaInfoDataModels.get(position).getItem_url().contains("http")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(newMediaInfoDataModels.get(position).getItem_url()), "application/pdf");
                try {
                    activity.startActivity(browserIntent);
                } catch(Exception e) {
                    AppLog.e(e.getMessage());
                }
            }else{
                Intent intent = new Intent(activity, FullScreenImage.class);
//                holder.image.buildDrawingCache();
//                Bitmap image=  holder.image.getDrawingCache();
                Bitmap image=getBitmapFromViewUsingCanvas(holder.image);
                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", image);
                intent.putExtras(extras);
                activity.startActivity(intent);
            }
        }
    });

}

@Override
public int getItemCount() {
    return newMediaInfoDataModels.size();
}

public class HorizontalViewHolder extends RecyclerView.ViewHolder {
    ImageView image,delImageView;

    public HorizontalViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageviewid);
        delImageView = itemView.findViewById(R.id.delImageView);
   } }


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

    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedImages(ArrayList<MediaInfoDataModel> attachmentListImageDetails) {
//        this.newMediaInfoDataModels = attachmentListImageDetails;
//        horizontalAdapterDelete.setUpdatedImages(attachmentListImageDetails);
    }
}