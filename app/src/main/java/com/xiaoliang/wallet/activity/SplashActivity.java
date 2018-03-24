package com.xiaoliang.wallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.utils.SPUtils;

public class SplashActivity extends Activity {
	private static final int sleepTime = 2500;

	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			SPUtils spUtils=new SPUtils(getApplicationContext(), "config");
			boolean first=spUtils.getBoolean("first", true);
			if(first){
				spUtils.putBoolean("first", false);
				Intent intent=new Intent(SplashActivity.this, AboutActivity.class);
				startActivity(intent);
				finish();
			}else{
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setClass(getApplication(), MainActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				finish();
			}
			
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setBackgroundDrawableResource(R.drawable.splash);
		mMainHandler.sendEmptyMessageDelayed(0, 1500);
	}

	// much easier to handle key events
	@Override
	public void onBackPressed() {
	}

}