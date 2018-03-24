package com.xiaoliang.wallet.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class QrCodeUtil {
	//生成二维码的方法
    public static void Create2QR2(String content,ImageView imageView) {
        Bitmap bitmap;
        try {
            bitmap = BitmapUtil.createQRCode(content, 50);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
