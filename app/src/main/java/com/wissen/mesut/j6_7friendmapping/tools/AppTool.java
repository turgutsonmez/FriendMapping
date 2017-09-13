package com.wissen.mesut.j6_7friendmapping.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by TurgutSonmez on 8.09.2017.
 */

public class AppTool {
  public static byte[] resimToByte(ImageView imageView) {
    imageView.setDrawingCacheEnabled(true);
    imageView.buildDrawingCache();
    Bitmap bitmap = imageView.getDrawingCache();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    return stream.toByteArray();
  }

  public static Bitmap stringToBitmap(String base64) {
    byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
  }
}
