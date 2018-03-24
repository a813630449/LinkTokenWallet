package com.xiaoliang.wallet.utils;




import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void showShort(Context context, int resourceId){
		Toast.makeText(context, context.getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
	}
	
	public static void showLong(Context context, int resourceId){
		Toast.makeText(context, context.getResources().getString(resourceId), Toast.LENGTH_LONG).show();
	}
	
	public static void showStringShort(Context context, String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
	
	public static void showStringLong(Context context, String content){
		Toast.makeText(context, content, 10000).show();
	}

}
