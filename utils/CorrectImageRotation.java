package com.igenesys.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

public class CorrectImageRotation {
    public static Bitmap handleSamplingAndRotationBitmap(Activity context, Uri selectedImage) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();
        options.inSampleSize = calculateInSampleSize(options, 750, 750);
        options.inJustDecodeBounds = false;
        return rotateImageIfRequired(context, BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, options), selectedImage);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round(((float) height) / ((float) reqHeight));
            int widthRatio = Math.round(((float) width) / ((float) reqWidth));
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            while (((float) (width * height)) / ((float) (inSampleSize * inSampleSize)) > ((float) (reqWidth * reqHeight * 2))) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        ExifInterface ei;
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ei = new ExifInterface(input);
        switch (ei.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, 1)) {
            case 3:
                return rotateImage(img, 180);
            case 6:
                return rotateImage(img, 90);
            case 8:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
