package com.igenesys.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.IOException;

public class ImageUtils {

    public static Bitmap rotateImage(String imagePath) {
        Bitmap rotatedBitmap = null;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
            rotatedBitmap = rotateBitmap(originalBitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        int rotationAngle = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return bitmap;
        }
        return rotateBitmap(bitmap, rotationAngle);
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, float rotationAngle) {
        Bitmap rotatedBitmap = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.setRotate(rotationAngle, width / 2, height / 2);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return rotatedBitmap;
    }
}