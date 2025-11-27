package com.example.admincollegeapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageUtils {

    /**
     * Helper method to get Bitmap from Uri, handling the deprecation of MediaStore.Images.Media.getBitmap
     */
    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } else {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        }
    }
}