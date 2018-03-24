package com.xiaoliang.wallet.activity;

import com.umeng.analytics.MobclickAgent;
import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.utils.NetworkUtils;
import com.xiaoliang.wallet.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

    protected Activity     activity;
    protected int          mScreenWidth;
    protected int          mScreenHeight;
    private ProgressDialog pd;
    protected static final String TAG = "youmi";
    protected ImageView btn_left,btn_center,btn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        if (!NetworkUtils.isAvailable(this)) {
            ToastUtil.showLong(this, R.string.net_failed);
        }
    }

    public void initTitle( int leftImg, int centerImg, int rightImg) {
        btn_left = (ImageView) findViewById(R.id.leftimg);
        btn_center = (ImageView) findViewById(R.id.centerimg);
        btn_right = (ImageView) findViewById(R.id.rightimg);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        btn_left.setImageResource(leftImg);
        btn_center.setImageResource(centerImg);
        btn_right.setImageResource(rightImg);
        btn_left.setVisibility(View.VISIBLE);
        btn_center.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.VISIBLE);
        txt_title.setVisibility(View.GONE);
        setOnClick(btn_left, btn_center, btn_right);
    }

    public void initTitle(int leftImg,String title ,int rightImg) {
    	btn_left = (ImageView) findViewById(R.id.leftimg);
        btn_center = (ImageView) findViewById(R.id.centerimg);
        btn_right = (ImageView) findViewById(R.id.rightimg);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        btn_left.setImageResource(leftImg);
        btn_right.setImageResource(rightImg);
        txt_title.setText(title);
        btn_left.setVisibility(View.VISIBLE);
        btn_center.setVisibility(View.GONE);
        btn_right.setVisibility(View.VISIBLE);
        txt_title.setVisibility(View.VISIBLE);
        setOnClick(btn_left, btn_center, btn_right);
    }

    private void setOnClick(ImageView left,ImageView center,ImageView right) {
		// TODO Auto-generated method stub
    	if(left!=null){
    		left.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				LeftBtnClick();
    			}
    		});
    	}
    	
    	if(right!=null){
    		right.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				RightBtnClick();
    			}
    		});
    	}
    	
    	if(center!=null){
    		center.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CenterBtnClick();
				}
			});
    	}
	}
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName()); 
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); 
        MobclickAgent.onResume(this);
    }

    //进度条
    public void showProgress(String msg) {
        if (pd == null) {
            pd = new ProgressDialog(activity, ProgressDialog.THEME_HOLO_LIGHT);
            pd.setCanceledOnTouchOutside(false);
        }
        if (msg == null) {
            msg = "加载中...";
        }
        pd.setMessage(msg);
        pd.show();
    }

    public void dimissProgress() {
        if (pd != null) {
            pd.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    protected abstract void RightBtnClick();
    protected abstract void LeftBtnClick();
    protected abstract void CenterBtnClick();
    /**
	 * 打印调试级别日志
	 *
	 * @param format
	 * @param args
	 */
	protected void logDebug(String format, Object... args) {
		logMessage(Log.DEBUG, format, args);
	}

	/**
	 * 打印信息级别日志
	 *
	 * @param format
	 * @param args
	 */
	protected void logInfo(String format, Object... args) {
		logMessage(Log.INFO, format, args);
	}

	/**
	 * 打印错误级别日志
	 *
	 * @param format
	 * @param args
	 */
	protected void logError(String format, Object... args) {
		logMessage(Log.ERROR, format, args);
	}

	/**
	 * 展示短时Toast
	 *
	 * @param format
	 * @param args
	 */
	protected void showShortToast(String format, Object... args) {
		showToast(Toast.LENGTH_SHORT, format, args);
	}

	/**
	 * 展示长时Toast
	 *
	 * @param format
	 * @param args
	 */
	protected void showLongToast(String format, Object... args) {
		showToast(Toast.LENGTH_LONG, format, args);
	}

	/**
	 * 打印日志
	 *
	 * @param level
	 * @param format
	 * @param args
	 */
	private void logMessage(int level, String format, Object... args) {
		String formattedString = String.format(format, args);
		switch (level) {
		case Log.DEBUG:
			Log.d(TAG, formattedString);
			break;
		case Log.INFO:
			Log.i(TAG, formattedString);
			break;
		case Log.ERROR:
			Log.e(TAG, formattedString);
			break;
		}
	}

	/**
	 * 展示Toast
	 *
	 * @param duration
	 * @param format
	 * @param args
	 */
	private void showToast(int duration, String format, Object... args) {
		Toast.makeText(this, String.format(format, args), duration).show();
	}
}
