package com.igenesys.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.igenesys.App;
import com.igenesys.R;
import com.squareup.picasso.Picasso;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;

public class FullScreenImage  extends Activity {


@SuppressLint("NewApi")



@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    setContentView(R.layout.layout_full_view);

    Bundle extras = getIntent().getExtras();
    String url ="";
    Bitmap bmp = null;


    ImageView imgDisplay;
    VideoView videoView;
    ImageView btnClose;

    MediaController mediaController=new MediaController(this);
    imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
    videoView = (VideoView) findViewById(R.id.videoDisplay);
//    videoView.setMediaController(new MediaController(this));
    videoView.setMediaController(mediaController);
    btnClose = (ImageView) findViewById(R.id.btnClose);
   TextView fName = (TextView) findViewById(R.id.fileName);
    if (extras.containsKey("video")){
        url = extras.getString("video");
        imgDisplay.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        File pdfPathFile;
        try {
            pdfPathFile=new File(url);
            fName.setText(pdfPathFile.getName());
            if(url.contains("http")){
                videoView.setVideoPath(url);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
            }else{
                CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                videoView.setVideoURI(Uri.fromFile(pdfPathFile));
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
            }

//            videoView.setMediaController(new MediaController(this) {
//                @Override
//                public void hide()
//                {
//                    mediaController.show();
//                }
//
//            });
//            mediaController.setAnchorView(videoView);
//            videoView.setMediaController(mediaController);

        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
//            Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);

    }else {
        imgDisplay.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
         bmp = (Bitmap) extras.getParcelable("imagebitmap");
    }

   btnClose.setOnClickListener(new View.OnClickListener() {            
    public void onClick(View v) {
    FullScreenImage.this.finish();
}
});

    if(extras.containsKey("url") && !extras.getString("url").equals("") && extras.getString("url").contains("http")){
//        Picasso.get().load(extras.getString("url")).into(imgDisplay);
        // Added because attachments not loading due to Esri Token

        GlideUrl glideUrl = new GlideUrl(extras.getString("url") + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                new LazyHeaders.Builder()
                        .addHeader("User-Agent", "drppl")
                        .build());

        Glide.with(this).load(glideUrl).into(imgDisplay);

        // Glide.with(this).load(extras.getString("url") + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(imgDisplay);
        File f = new File(extras.getString("url"));
        fName.setText(f.getName());
    }else{
        try{
            if(extras.containsKey("url") && !extras.getString("url").equals("")){
                File f = new File(extras.getString("url"));
                fName.setText(f.getName());
                Bitmap bitmap=null;
                CryptoUtilsTest.encryptFileinAES(new File(extras.getString("url")), 2);
                bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(this, Uri.fromFile(new File(extras.getString("url"))));
                imgDisplay.setImageBitmap(bitmap);
            }else{
                imgDisplay.setImageBitmap(bmp);
            }
        }catch(Exception ex){
            ex.getCause();
            imgDisplay.setImageBitmap(bmp);
        }
    }


}   


}